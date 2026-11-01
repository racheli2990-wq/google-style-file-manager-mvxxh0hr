package com.generated.app;

import java.io.File;

public class FileItem {

    private final File file;
    private final String name;
    private final boolean isDirectory;
    private final long length;
    private final long lastModified;
    private final int itemCount;

    public FileItem(File file) {
        this.file = file;
        this.name = file.getName();
        this.isDirectory = file.isDirectory();
        this.length = isDirectory ? 0 : file.length();
        this.lastModified = file.lastModified();

        if (isDirectory) {
            File[] children = file.listFiles();
            this.itemCount = (children != null) ? children.length : 0;
        } else {
            this.itemCount = 0;
        }
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public long getLength() {
        return length;
    }

    public long getLastModified() {
        return lastModified;
    }

    public int getItemCount() {
        return itemCount;
    }
}
