package com.example.project.musicbox.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Pahan on 16.07.2017.
 */
@Table(database = PlaylistDataBase.class)
public class MusicIdModel extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public long idd;
    @Column
    private String idMusic;

    public String getIdMusic() {
        return idMusic;
    }

    public void setIdMusic(String idMusic) {
        this.idMusic = idMusic;
    }

    public long getIdd() {
        return idd;
    }
}
