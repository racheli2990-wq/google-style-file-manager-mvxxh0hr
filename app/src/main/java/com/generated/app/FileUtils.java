package com.generated.app;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        if (digitGroups >= units.length) digitGroups = units.length - 1;
        return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    public static String getMimeType(File file) {
        String ext = getFileExtension(file.getName());
        if (ext.isEmpty()) return "*/*";
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return mime != null ? mime : "*/*";
    }

    public static boolean isImageFile(File file) {
        String mime = getMimeType(file);
        return mime.startsWith("image/");
    }

    public static boolean isVideoFile(File file) {
        String mime = getMimeType(file);
        return mime.startsWith("video/");
    }

    public static boolean isAudioFile(File file) {
        String mime = getMimeType(file);
        return mime.startsWith("audio/");
    }

    public static boolean isTextFile(File file) {
        String ext = getFileExtension(file.getName());
        return ext.equals("txt") || ext.equals("json") || ext.equals("xml") ||
                ext.equals("java") || ext.equals("py") || ext.equals("js") ||
                ext.equals("css") || ext.equals("html") || ext.equals("md") ||
                ext.equals("log") || ext.equals("c") || ext.equals("cpp");
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        return fileOrDirectory.delete();
    }

    public static boolean copyFile(File src, File dst) {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static long[] getStorageStats() {
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            long total = totalBlocks * blockSize;
            long free = availableBlocks * blockSize;
            long used = total - free;

            return new long[]{used, total};
        } catch (Exception e) {
            return new long[]{0, 1};
        }
    }
}
