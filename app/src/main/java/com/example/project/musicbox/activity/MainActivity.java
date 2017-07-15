package com.example.project.musicbox.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.MusicAdapter;
import com.example.project.musicbox.model.PlayList;
import com.example.project.musicbox.service.MusicService;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnClickMusicItem{

    public static final String LOG_TAG = "myLog";

    private MusicAdapter mMusicAdapter;
    private List<PlayList> mPlayLists = new ArrayList<>();
    private List<PlayList> mPlayListsSearch = new ArrayList<>();
    private boolean bound = false;

    @BindView(R.id.rv_item_music)
    RecyclerView mRecyclerViewMusic;
    @BindView(R.id.sv_music)
    SearchView mSearchViewMusic;
    @BindView(R.id.tv_playing_now)
    TextView mTextViewPlayingNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


        startService(new Intent(this, MusicService.class));

        mMusicAdapter = new MusicAdapter(mPlayLists,this);
        mRecyclerViewMusic.setLayoutManager(new GridLayoutManager(this,5));
        mRecyclerViewMusic.setAdapter(mMusicAdapter);


        initSearch();
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

    private void initSearch() {
        mSearchViewMusic.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    mMusicAdapter.setPlayList(mPlayLists);
                } else {
                    mPlayListsSearch.clear();
                    for (PlayList playList : mPlayLists) {
                        if (playList.getArtist().toLowerCase().contains(newText.toLowerCase()) || playList.getTrack().toLowerCase().contains(newText.toLowerCase())) {
                            mPlayListsSearch.add(playList);
                        }
                        mMusicAdapter.setPlayList(mPlayListsSearch);
                    }
                }
                return false;
            }
        });
    }

    protected ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(LOG_TAG, "onServiceConnected");
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "onServiceDisconnected");
            bound = false;
        }
    };

    private void addTrack(){
        ContentResolver cr = getApplicationContext().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        Delete.tables(PlayList.class);
        if(cur != null)
        {
            count = cur.getCount();

            if(count > 0)
            {
                while(cur.moveToNext())
                {
                    String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String nameTrack = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
//                    long durationInMs = Long.parseLong(cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));
//
//                    double durationInMin = ((double) durationInMs / 1000.0) / 60.0;

                    PlayList playList = new PlayList();
                    playList.setNamePlayList("Day");
                    playList.setData(data);
                    playList.setTrack(nameTrack);
                    playList.setArtist(artist);
                    playList.save();
                }
            }
        }
        cur.close();


        mPlayLists.clear();
        mPlayLists = new Select().from(PlayList.class).queryList();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            getApplicationContext().unbindService(mServerConn);
        }
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    public void obClickMusic(PlayList playList) {
        mTextViewPlayingNow.setText(playList.getArtist() + " - " + playList.getTrack());
        if (bound) {
            getApplicationContext().unbindService(mServerConn);
        }
        stopService(new Intent(this, MusicService.class));

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("key",playList.getData());
        getApplicationContext().bindService( intent, mServerConn, Context.BIND_AUTO_CREATE);

    }
}
