package com.example.project.musicbox.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.MyFragmentPagerAdapter;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;


import com.example.project.musicbox.model.PlayListModel;
import com.example.project.musicbox.model.PlayListModel_Table;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Pahan on 15.07.2017.
 */

public class AdminActivity extends AppCompatActivity {

    public static final String LOG_TAG = "myLog";
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<String> mTracks = new ArrayList<>();
    private List<String> mTracksId = new ArrayList<>();
    private String radio = "day";
    private String[] playArrayName = {"day", "morning", "evening", "admin"};


    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout tab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        FlowManager.init(new FlowConfig.Builder(this).build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {

            }
        } else {
            addTrack();
        }
        mMusicInfos.clear();
        mTracks.clear();
        mTracksId.clear();
        mMusicInfos = new Select().from(MusicInfo.class).queryList();
        for (int i = 0; i < mMusicInfos.size(); i++) {
            mTracksId.add(mMusicInfos.get(i).getId());
            mTracks.add(mMusicInfos.get(i).getArtist() + " - " + mMusicInfos.get(i).getTrack());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playArrayName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setPrompt("PlayList");
        mSpinner.setSelection(1);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        radio = "day";
                        break;
                    case 1:
                        radio = "morning";
                        break;
                    case 2:
                        radio = "evening";
                        break;
                    case 3:
                        radio = "admin";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_tab)));
        tab.setupWithViewPager(mViewPager);
    }

    @OnClick(R.id.bt_add_track_day)
    void onClickAddTrack() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Day");
        adb.setMultiChoiceItems(mTracksId.toArray(new String[mTracksId.size()]), null, myItemsMultiClickListener);
        adb.setMultiChoiceItems(mTracks.toArray(new String[mTracks.size()]), null, myItemsMultiClickListener);
        adb.setPositiveButton("okey", myBtnClickListener);
        adb.create();
        adb.show();
    }

    @OnClick(R.id.bt_start_box)
    void onClickStart() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Set Play List");
        adb.setSingleChoiceItems(playArrayName, -1, myClickListener);
        adb.setPositiveButton("okey", myClickListener);
        adb.create();
        adb.show();
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            ListView lv = ((AlertDialog) dialog).getListView();
            if (which == Dialog.BUTTON_POSITIVE) {
                Log.d(LOG_TAG, "pos = " + lv.getCheckedItemPosition());
                if (lv.getCheckedItemPosition() == 0) {
                    Delete.table(MusicIdModel.class);
                    List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("day")).queryList();
                    for (int i = 0; i < lm.size(); i++) {
                        MusicIdModel musicIdModel = new MusicIdModel();
                        musicIdModel.setIdMusic(lm.get(i).getIdTrack());
                        musicIdModel.save();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if (lv.getCheckedItemPosition() == 1) {
                    Delete.table(MusicIdModel.class);
                    List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("morning")).queryList();
                    for (int i = 0; i < lm.size(); i++) {
                        MusicIdModel musicIdModel = new MusicIdModel();
                        musicIdModel.setIdMusic(lm.get(i).getIdTrack());
                        musicIdModel.save();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if (lv.getCheckedItemPosition() == 2) {
                    Delete.table(MusicIdModel.class);
                    List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("evening")).queryList();
                    for (int i = 0; i < lm.size(); i++) {
                        MusicIdModel musicIdModel = new MusicIdModel();
                        musicIdModel.setIdMusic(lm.get(i).getIdTrack());
                        musicIdModel.save();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if (lv.getCheckedItemPosition() == 3) {
                    Delete.table(MusicIdModel.class);
                    List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("admin")).queryList();
                    for (int i = 0; i < lm.size(); i++) {
                        MusicIdModel musicIdModel = new MusicIdModel();
                        musicIdModel.setIdMusic(lm.get(i).getIdTrack());
                        musicIdModel.save();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            } else
                // выводим в лог позицию нажатого элемента
                Log.d(LOG_TAG, "which = " + which);
        }
    };


    DialogInterface.OnClickListener myBtnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

            SparseBooleanArray sbArray = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
            for (int i = 0; i < sbArray.size(); i++) {
                int key = sbArray.keyAt(i);
                if (sbArray.get(key)) {
                    final PlayListModel playListModel = new PlayListModel();
                    playListModel.setNameList(radio);
                    playListModel.setIdTrack(mTracksId.get(key));
                    playListModel.save();

                }
            }
        }
    };
    DialogInterface.OnMultiChoiceClickListener myItemsMultiClickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            Log.d(LOG_TAG, "which = " + which + ", isChecked = " + isChecked);
        }
    };

    private void addTrack() {
        ContentResolver cr = getApplicationContext().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        Delete.tables(MusicInfo.class);
        if (cur != null) {
            count = cur.getCount();

            if (count > 0) {
                while (cur.moveToNext()) {
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String nameTrack = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));

//                    long durationInMs = Long.parseLong(cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));
//
//                    double durationInMin = ((double) durationInMs / 1000.0) / 60.0;

                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(id);
                    musicInfo.setData(data);
                    musicInfo.setTrack(nameTrack);
                    musicInfo.setArtist(artist);
                    musicInfo.save();

                }
            }
        }
        cur.close();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addTrack();
                } else {
                    Log.e(LOG_TAG, "Denied");
                }
                return;
            }
        }
    }
}
