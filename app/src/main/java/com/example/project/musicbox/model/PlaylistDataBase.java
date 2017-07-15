package com.example.project.musicbox.model;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Pahan on 06.07.2017.
 */
@Database(name = PlaylistDataBase.NAME, version = PlaylistDataBase.VERSION)
public class PlaylistDataBase {
    public static final String NAME = "playlistDataBase";
    public static final int VERSION = 1;
}
