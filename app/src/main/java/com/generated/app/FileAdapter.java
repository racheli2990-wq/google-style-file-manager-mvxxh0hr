package com.generated.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileAdapter extends BaseAdapter {

    private final Context context;
    private List<FileItem> items;
    private final boolean isGrid;
    private final boolean showThumbnails;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public FileAdapter(Context context, List<FileItem> items, boolean isGrid, boolean showThumbnails) {
        this.context = context;
        this.items = items;
        this.isGrid = isGrid;
        this.showThumbnails = showThumbnails;
    }

    public void updateData(List<FileItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public FileItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int layoutId = isGrid ? R.layout.item_file_grid : R.layout.item_file_list;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.iv_file_icon);
            holder.tvName = convertView.findViewById(R.id.tv_file_name);
            holder.tvDetails = convertView.findViewById(R.id.tv_file_details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileItem item = getItem(position);
        File file = item.getFile();

        holder.tvName.setText(item.getName());

        if (holder.tvDetails != null) {
            if (item.isDirectory()) {
                String countText = String.format(Locale.getDefault(), context.getString(R.string.items_count), item.getItemCount());
                holder.tvDetails.setText(countText);
            } else {
                String size = FileUtils.formatFileSize(item.getLength());
                String date = dateFormat.format(new Date(item.getLastModified()));
                holder.tvDetails.setText(size + " • " + date);
            }
        }

        // Default Icon Setup
        if (item.isDirectory()) {
            holder.ivIcon.setImageResource(R.drawable.ic_folder);
        } else if (FileUtils.isImageFile(file)) {
            holder.ivIcon.setImageResource(R.drawable.ic_image);
            if (showThumbnails) loadThumbnail(file, holder.ivIcon, true);
        } else if (FileUtils.isVideoFile(file)) {
            holder.ivIcon.setImageResource(R.drawable.ic_video);
            if (showThumbnails) loadThumbnail(file, holder.ivIcon, false);
        } else if (FileUtils.isAudioFile(file)) {
            holder.ivIcon.setImageResource(R.drawable.ic_audio);
        } else if (FileUtils.isTextFile(file)) {
            holder.ivIcon.setImageResource(R.drawable.ic_code);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_file);
        }

        return convertView;
    }

    private void loadThumbnail(final File file, final ImageView imageView, final boolean isImage) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    if (isImage) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    } else {
                        bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    }
                } catch (Exception ignored) {
                }

                final Bitmap finalBitmap = bitmap;
                if (finalBitmap != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(finalBitmap);
                        }
                    });
                }
            }
        });
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvDetails;
    }
}
