package com.generated.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TextEditorActivity extends Activity {

    private File currentFile;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        TextView tvTitle = findViewById(R.id.tv_file_title);
        etContent = findViewById(R.id.et_text_content);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFileContent();
            }
        });

        String path = getIntent().getStringExtra("file_path");
        if (path != null) {
            currentFile = new File(path);
            tvTitle.setText(currentFile.getName());
            loadFileContent();
        }
    }

    private void loadFileContent() {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            etContent.setText(builder.toString());
        } catch (Exception e) {
            Toast.makeText(this, "שגיאה שטעינת הקובץ", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileContent() {
        if (currentFile == null) return;
        try (FileWriter writer = new FileWriter(currentFile)) {
            writer.write(etContent.getText().toString());
            Toast.makeText(this, R.string.file_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
        }
    }
}
