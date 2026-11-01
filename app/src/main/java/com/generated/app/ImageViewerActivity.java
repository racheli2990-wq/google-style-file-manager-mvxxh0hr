package com.generated.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ImageViewerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ImageView ivImage = findViewById(R.id.iv_image);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvInfo = findViewById(R.id.tv_info);
        ImageView btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String path = getIntent().getStringExtra("file_path");
        if (path != null) {
            File file = new File(path);
            tvTitle.setText(file.getName());

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                ivImage.setImageBitmap(bitmap);
                String info = bitmap.getWidth() + "x" + bitmap.getHeight() + " • " + FileUtils.formatFileSize(file.length());
                tvInfo.setText(info);
            } else {
                Toast.makeText(this, "לא ניתן להציג תמונה זו", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
