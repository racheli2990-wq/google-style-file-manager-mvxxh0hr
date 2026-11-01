package com.generated.app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

    private static final String PREF_NAME = "file_manager_prefs";
    private static final String KEY_SHOW_HIDDEN = "show_hidden";
    private static final String KEY_GRID_VIEW = "grid_view";
    private static final String KEY_ENABLE_THUMBNAILS = "enable_thumbnails";

    private final SharedPreferences prefs;

    public AppSettings(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isShowHiddenFiles() {
        return prefs.getBoolean(KEY_SHOW_HIDDEN, false);
    }

    public void setShowHiddenFiles(boolean show) {
        prefs.edit().putBoolean(KEY_SHOW_HIDDEN, show).apply();
    }

    public boolean isGridView() {
        return prefs.getBoolean(KEY_GRID_VIEW, false);
    }

    public void setGridView(boolean isGrid) {
        prefs.edit().putBoolean(KEY_GRID_VIEW, isGrid).apply();
    }

    public boolean isThumbnailsEnabled() {
        return prefs.getBoolean(KEY_ENABLE_THUMBNAILS, true);
    }

    public void setThumbnailsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLE_THUMBNAILS, enabled).apply();
    }
}
