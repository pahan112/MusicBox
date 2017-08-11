package com.example.project.musicbox.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Pahan on 08.08.2017.
 */
@Table(database = PlaylistDataBase.class)
public class MusicTextPlayNext extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long idd;
    @Column
    private String track;
    @Column
    private String artist;

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
