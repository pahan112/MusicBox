package com.example.project.musicbox.model;

/**
 * Created by Pahan on 07.08.2017.
 */

public class ModelSpinerTrack {
    private Integer dayStart;
    private Integer dayFinish;
    private Integer morningStart;
    private Integer morningFinish;
    private Integer eveningStart;
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
