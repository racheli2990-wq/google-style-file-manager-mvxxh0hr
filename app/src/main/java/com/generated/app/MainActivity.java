package com.generated.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends Activity {

    private static final int REQ_PERMISSIONS = 101;

    private AppSettings settings;
    private File currentFolder;
    private final Stack<File> pathHistory = new Stack<>();
    private List<FileItem> fileList = new ArrayList<>();
    private List<FileItem> filteredList = new ArrayList<>();

    private TextView tvCurrentPath;
    private ImageView btnBack;
    private ImageView btnSearch;
    private ImageView btnViewMode;
    private ImageView btnSettings;
    private LinearLayout layoutSearchBar;
    private EditText etSearch;
    private ImageView btnCloseSearch;

    private ProgressBar pbStorage;
    private TextView tvStoragePercent;
    private TextView tvStorageUsedRatio;

    private ListView listView;
    private GridView gridView;
    private TextView tvEmptyState;
    private LinearLayout layoutPermissionDenied;

    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = new AppSettings(this);
        initUI();
        checkAndRequestPermissions();
    }

    private void initUI() {
        tvCurrentPath = findViewById(R.id.tv_current_path);
        btnBack = findViewById(R.id.btn_back);
        btnSearch = findViewById(R.id.btn_search);
        btnViewMode = findViewById(R.id.btn_view_mode);
        btnSettings = findViewById(R.id.btn_settings);

        layoutSearchBar = findViewById(R.id.layout_search_bar);
        etSearch = findViewById(R.id.et_search);
        btnCloseSearch = findViewById(R.id.btn_close_search);

        pbStorage = findViewById(R.id.pb_storage);
        tvStoragePercent = findViewById(R.id.tv_storage_percent);
        tvStorageUsedRatio = findViewById(R.id.tv_storage_used_ratio);

        listView = findViewById(R.id.list_files);
        gridView = findViewById(R.id.grid_files);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        layoutPermissionDenied = findViewById(R.id.layout_permission_denied);

        findViewById(R.id.btn_grant_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPermissionSettings();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutSearchBar.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
            }
        });

        btnCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                layoutSearchBar.setVisibility(View.GONE);
            }
        });

        btnViewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newMode = !settings.isGridView();
                settings.setGridView(newMode);
                updateViewModeUI();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.fab_add_folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateFolderDialog();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFiles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileItem item = filteredList.get(position);
                if (item.isDirectory()) {
                    navigateTo(item.getFile());
                } else {
                    openFile(item.getFile());
                }
            }
        };

        AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showFileOptionsMenu(filteredList.get(position));
                return true;
            }
        };

        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);
        gridView.setOnItemClickListener(itemClickListener);
        gridView.setOnItemLongClickListener(itemLongClickListener);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                onPermissionGranted();
            } else {
                layoutPermissionDenied.setVisibility(View.VISIBLE);
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSIONS);
            }
        }
    }

    private void openPermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void onPermissionGranted() {
        layoutPermissionDenied.setVisibility(View.GONE);
        updateStorageCard();
        File root = Environment.getExternalStorageDirectory();
        navigateTo(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutPermissionDenied.getVisibility() == View.GONE && currentFolder != null) {
            loadFolderContent(currentFolder);
            updateStorageCard();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            onPermissionGranted();
        }
    }

    private void updateStorageCard() {
        long[] stats = FileUtils.getStorageStats();
        long used = stats[0];
        long total = stats[1];
        int percent = (int) ((used * 100) / total);

        pbStorage.setProgress(percent);
        tvStoragePercent.setText(percent + "%");
        tvStorageUsedRatio.setText(String.format(getString(R.string.used_storage),
                FileUtils.formatFileSize(used), FileUtils.formatFileSize(total)));
    }

    private void navigateTo(File folder) {
        if (currentFolder != null && !currentFolder.equals(folder)) {
            pathHistory.push(currentFolder);
        }
        currentFolder = folder;
        btnBack.setVisibility(pathHistory.isEmpty() ? View.GONE : View.VISIBLE);
        tvCurrentPath.setText(folder.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())
                ? getString(R.string.internal_storage) : folder.getName());

        loadFolderContent(folder);
    }

    private void loadFolderContent(File folder) {
        fileList.clear();
        File[] files = folder.listFiles();

        if (files != null) {
            boolean showHidden = settings.isShowHiddenFiles();
            for (File f : files) {
                if (!showHidden && f.getName().startsWith(".")) {
                    continue;
                }
                fileList.add(new FileItem(f));
            }

            Collections.sort(fileList, new Comparator<FileItem>() {
                @Override
                public int compare(FileItem o1, FileItem o2) {
                    if (o1.isDirectory() && !o2.isDirectory()) return -1;
                    if (!o1.isDirectory() && o2.isDirectory()) return 1;
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }

        filterFiles(etSearch.getText().toString());
    }

    private void filterFiles(String query) {
        filteredList.clear();
        if (query.trim().isEmpty()) {
            filteredList.addAll(fileList);
        } else {
            String q = query.toLowerCase(Locale.getDefault());
            for (FileItem item : fileList) {
                if (item.getName().toLowerCase(Locale.getDefault()).contains(q)) {
                    filteredList.add(item);
                }
            }
        }

        tvEmptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        updateViewModeUI();
    }

    private void updateViewModeUI() {
        boolean isGrid = settings.isGridView();
        btnViewMode.setImageResource(isGrid ? R.drawable.ic_list : R.drawable.ic_grid);

        adapter = new FileAdapter(this, filteredList, isGrid, settings.isThumbnailsEnabled());
        if (isGrid) {
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(adapter);
        } else {
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
        }
    }

    private void openFile(File file) {
        if (FileUtils.isImageFile(file)) {
            Intent intent = new Intent(this, ImageViewerActivity.class);
            intent.putExtra("file_path", file.getAbsolutePath());
            startActivity(intent);
        } else if (FileUtils.isVideoFile(file)) {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra("file_path", file.getAbsolutePath());
            startActivity(intent);
        } else if (FileUtils.isTextFile(file)) {
            Intent intent = new Intent(this, TextEditorActivity.class);
            intent.putExtra("file_path", file.getAbsolutePath());
            startActivity(intent);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, FileUtils.getMimeType(file));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "אין אפליקציה מתאימה לפתיחת הקובץ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCreateFolderDialog() {
        final EditText input = new EditText(this);
        input.setHint(R.string.new_folder_hint);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle(R.string.new_folder)
                .setView(input)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString().trim();
                        if (!name.isEmpty()) {
                            File newDir = new File(currentFolder, name);
                            if (newDir.mkdir()) {
                                loadFolderContent(currentFolder);
                            } else {
                                Toast.makeText(MainActivity.this, "שגיאה ביצירת התיקייה", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showFileOptionsMenu(final FileItem item) {
        String[] options = new String[]{
                getString(R.string.rename),
                getString(R.string.delete),
                getString(R.string.details)
        };

        new AlertDialog.Builder(this)
                .setTitle(item.getName())
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showRenameDialog(item);
                        } else if (which == 1) {
                            showDeleteConfirmDialog(item);
                        } else if (which == 2) {
                            showDetailsDialog(item);
                        }
                    }
                })
                .show();
    }

    private void showRenameDialog(final FileItem item) {
        final EditText input = new EditText(this);
        input.setText(item.getName());
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename)
                .setView(input)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            File dest = new File(item.getFile().getParent(), newName);
                            if (item.getFile().renameTo(dest)) {
                                loadFolderContent(currentFolder);
                            } else {
                                Toast.makeText(MainActivity.this, "שגיאה בשינוי השם", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDeleteConfirmDialog(final FileItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (FileUtils.deleteRecursive(item.getFile())) {
                            loadFolderContent(currentFolder);
                            updateStorageCard();
                        } else {
                            Toast.makeText(MainActivity.this, "שגיאה במחיקת הקובץ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDetailsDialog(FileItem item) {
        File f = item.getFile();
        String details = "נתיב: " + f.getAbsolutePath() + "\n\n" +
                "גודל: " + FileUtils.formatFileSize(item.getLength()) + "\n\n" +
                "סוג: " + FileUtils.getMimeType(f);

        new AlertDialog.Builder(this)
                .setTitle(R.string.details)
                .setMessage(details)
                .setPositiveButton("סגור", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (layoutSearchBar.getVisibility() == View.VISIBLE) {
            layoutSearchBar.setVisibility(View.GONE);
            etSearch.setText("");
        } else if (!pathHistory.isEmpty()) {
            File previous = pathHistory.pop();
            currentFolder = previous;
            btnBack.setVisibility(pathHistory.isEmpty() ? View.GONE : View.VISIBLE);
            tvCurrentPath.setText(currentFolder.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())
                    ? getString(R.string.internal_storage) : currentFolder.getName());
            loadFolderContent(currentFolder);
        } else {
            super.onBackPressed();
        }
    }
}
