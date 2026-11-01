package com.generated.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new AppSettings(this);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Switch switchHidden = findViewById(R.id.switch_hidden_files);
        Switch switchThumbnails = findViewById(R.id.switch_thumbnails);

        switchHidden.setChecked(settings.isShowHiddenFiles());
        switchThumbnails.setChecked(settings.isThumbnailsEnabled());

        switchHidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setShowHiddenFiles(isChecked);
            }
        });

        switchThumbnails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setThumbnailsEnabled(isChecked);
            }
        });

        findViewById(R.id.btn_clear_cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File cache = getCacheDir();
                    FileUtils.deleteRecursive(cache);
                    Toast.makeText(SettingsActivity.this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, "שגיאה בניקוי המטמון", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
