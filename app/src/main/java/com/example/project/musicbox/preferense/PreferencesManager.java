package com.example.project.musicbox.preferense;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Vladimir on 11.05.2017.
 */

public class PreferencesManager {

    public static final String DEFAULT_STRING_VALUE = "";
    public static final int DEFAULT_INTEGER_VALUE = 0;

    private static final String PREFS_LIST_MUSIC_INFO= "PREFS_LIST_MUSIC_INFO";
    private static final String PREFS_LIST_MUSIC_ID_MODEL= "PREFS_LIST_MUSIC_ID_MODEL";
    private static final String PREFS_LIST_PLAYLIST_MODEL= "PREFS_LIST_PLAYLIST_MODEL";

    private static final String PREFERENCES_NAME = "SETTINGS";

    private static PreferencesManager instance;
    private final SharedPreferences sharedPreferences;


    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeTaskInstance(..) method first.");
        }
        return instance;
    }

    public boolean clear() {
        return sharedPreferences.edit().clear().commit();
    }

    public String getPrefsListMusicInfo() {
        return sharedPreferences.getString(PREFS_LIST_MUSIC_INFO, DEFAULT_STRING_VALUE);
    }

    public <T> void setPrefsListMusicInfo(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        sharedPreferences.edit().putString(PREFS_LIST_MUSIC_INFO, json).apply();
    }

    public String getPrefsListPlayListModel() {
        return sharedPreferences.getString(PREFS_LIST_PLAYLIST_MODEL, DEFAULT_STRING_VALUE);
    }

    public <T> void setPrefsListPlayListModel(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        sharedPreferences.edit().putString(PREFS_LIST_PLAYLIST_MODEL, json).apply();
    }

    public String getPrefsListMusicIdModel() {
        return sharedPreferences.getString(PREFS_LIST_MUSIC_ID_MODEL, DEFAULT_STRING_VALUE);
    }

    public <T> void setPrefsListMusicIdModel(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        sharedPreferences.edit().putString(PREFS_LIST_MUSIC_ID_MODEL, json).apply();
    }

}
