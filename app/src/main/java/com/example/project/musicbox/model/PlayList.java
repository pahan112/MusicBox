package com.example.project.musicbox.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Pahan on 06.07.2017.
 */
@Table(database = PlaylistDataBase.class)
public class PlayList extends BaseModel implements Serializable{
    @Column
    @PrimaryKey(autoincrement = true)
    public long idd;
    @Column
    private String namePlayList;
    @Column
    private String data;
    @Column
    private String track;
    @Column
    private String artist;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getNamePlayList() {
        return namePlayList;
    }

    public void setNamePlayList(String namePlayList) {
        this.namePlayList = namePlayList;
    }



}
