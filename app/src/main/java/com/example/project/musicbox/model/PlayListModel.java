package com.example.project.musicbox.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by Pahan on 16.07.2017.
 */
@Table(database = PlaylistDataBase.class)
public class PlayListModel extends BaseModel{
    @Column
    @PrimaryKey(autoincrement = true)
    public long idd;
    @Column
    private String nameList;
    @Column
    private String idTrack;

    public String getNameList() {
        return nameList;
    }

    public void setNameList(String nameList) {
        this.nameList = nameList;
    }

    public String getIdTrack() {
        return idTrack;
    }

    public void setIdTrack(String idTrack) {
        this.idTrack = idTrack;
    }
}
