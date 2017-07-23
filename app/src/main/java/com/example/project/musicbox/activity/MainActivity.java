package com.example.project.musicbox.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.MusicAdapter;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.service.MusicService;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnClickMusicItem{

    public static final String LOG_TAG = "myLog";

    private MusicAdapter mMusicAdapter;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<MusicInfo> mPlayListsSearch = new ArrayList<>();
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
        mMusicIdModel.clear();
        mMusicIdModel = new Select().from(MusicIdModel.class).queryList();

        mMusicInfos.clear();
        for (int i = 0; i < mMusicIdModel.size(); i++) {

            mMusicInfos.addAll(SQLite.select().from(MusicInfo.class)
                    .where(MusicInfo_Table.id.is(mMusicIdModel.get(i).getIdMusic())).queryList());
        }

        Log.e(LOG_TAG,getResources().getDisplayMetrics().density + "");

        int c = 10/(int)getResources().getDisplayMetrics().density;


        mMusicAdapter = new MusicAdapter(mMusicInfos,this);
        mRecyclerViewMusic.setLayoutManager(new GridLayoutManager(this,c));
        mRecyclerViewMusic.setAdapter(mMusicAdapter);


        initSearch();


        if (!mMusicInfos.isEmpty()) {
            startService(new Intent(this, MusicService.class));
            getApplicationContext().bindService(new Intent(this, MusicService.class), mServerConn, Context.BIND_AUTO_CREATE);
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
                    mMusicAdapter.setPlayList(mMusicInfos);
                } else {
                    mPlayListsSearch.clear();
                    for (MusicInfo musicInfo : mMusicInfos) {
                        if (musicInfo.getArtist().toLowerCase().contains(newText.toLowerCase()) || musicInfo.getTrack().toLowerCase().contains(newText.toLowerCase())) {
                            mPlayListsSearch.add(musicInfo);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (bound) {
        if (!mMusicInfos.isEmpty()) {
            getApplicationContext().unbindService(mServerConn);
//        }
            stopService(new Intent(this, MusicService.class));
        }
    }

    @Override
    public void obClickMusic(MusicInfo musicInfo) {

//        mTextViewPlayingNow.setText(musicInfo.getArtist() + " - " + musicInfo.getTrack());
//        if (bound) {
//            getApplicationContext().unbindService(mServerConn);
//        }
//        stopService(new Intent(this, MusicService.class));

//        Intent intent = new Intent(this, MusicService.class);
//        intent.putExtra("key", musicInfo.getData());
//        getApplicationContext().bindService( intent, mServerConn, Context.BIND_AUTO_CREATE);

    }
}
