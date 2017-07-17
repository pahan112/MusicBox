package com.example.project.musicbox.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.project.musicbox.fragment.FragmentAdmin;
import com.example.project.musicbox.fragment.FragmentDay;
import com.example.project.musicbox.fragment.FragmentEvening;
import com.example.project.musicbox.fragment.FragmentMorning;

/**
 * Created by Pahan on 17.07.2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTableTitles;

    public MyFragmentPagerAdapter(FragmentManager fm, String[] mTableTitles) {
        super(fm);
        this.mTableTitles = mTableTitles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentMorning();
            case 1:
                return new FragmentDay();
            case 2:
                return new FragmentEvening();
            case 3:
                return new FragmentAdmin();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.mTableTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mTableTitles[position];
    }
}
