package com.example.project.musicbox.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Created by Pahan on 07.08.2017.
 */
@Table(database = PlaylistDataBase.class)
public class ModelSpinerTrack extends BaseModel implements Serializable{
    @Column
    @PrimaryKey(autoincrement = true)
    public long idd;
    @Column
    private Integer dayStart;
    @Column
    private Integer dayFinish;
    @Column
    private Integer morningStart;
    @Column
    private Integer morningFinish;
    @Column
    private Integer eveningStart;
    @Column
    private Integer eveningFinish;

    public Integer getDayStart() {
        return dayStart;
    }

    public void setDayStart(Integer dayStart) {
        this.dayStart = dayStart;
    }

    public Integer getDayFinish() {
        return dayFinish;
    }

    public void setDayFinish(Integer dayFinish) {
        this.dayFinish = dayFinish;
    }

    public Integer getMorningStart() {
        return morningStart;
    }

    public void setMorningStart(Integer morningStart) {
        this.morningStart = morningStart;
    }

    public Integer getMorningFinish() {
        return morningFinish;
    }

    public void setMorningFinish(Integer morningFinish) {
        this.morningFinish = morningFinish;
    }

    public Integer getEveningStart() {
        return eveningStart;
    }

    public void setEveningStart(Integer eveningStart) {
        this.eveningStart = eveningStart;
    }

    public Integer getEveningFinish() {
        return eveningFinish;
    }

    public void setEveningFinish(Integer eveningFinish) {
        this.eveningFinish = eveningFinish;
    }
}
