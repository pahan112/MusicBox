package com.example.project.musicbox.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.project.musicbox.model.MusicPlayNow;
import com.example.project.musicbox.service.MusicService;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnClickMusicItem {

    public static final String LOG_TAG = "myLog";

    private MusicAdapter mMusicAdapter;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<MusicInfo> mPlayListsSearch = new ArrayList<>();
    private boolean bound = false;

    private BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "com.example.project.musicbox.reciver";
    public final static int STATUS_START = 100;
    public final static int STATUS_FINISH = 200;

    @BindView(R.id.rv_item_music)
    RecyclerView mRecyclerViewMusic;
    @BindView(R.id.sv_music)
    SearchView mSearchViewMusic;
    @BindView(R.id.tv_playing_now)
    TextView mTextViewPlayingNow;
    @BindView(R.id.tv_next_play_main)
    TextView mTextViewNextPlayMain;

    private int d = 0;
    private int l = -1;
    private int k = 1;
    private int x = 1;

    private List<MusicPlayNow> mMusicPlayNow = new ArrayList<>();

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

        Log.e(LOG_TAG, getResources().getDisplayMetrics().density + "");

        final int[] c = {10 / (int) getResources().getDisplayMetrics().density};


        mMusicAdapter = new MusicAdapter(mMusicInfos, this);
        mRecyclerViewMusic.setLayoutManager(new GridLayoutManager(this, c[0]));
        mRecyclerViewMusic.setAdapter(mMusicAdapter);


        initSearch();


        if (!mMusicInfos.isEmpty()) {
            mTextViewPlayingNow.setText(mMusicInfos.get(d).getArtist() + " - " + mMusicInfos.get(d).getTrack());
            startService(new Intent(this, MusicService.class));
            getApplicationContext().bindService(new Intent(this, MusicService.class), mServerConn, Context.BIND_AUTO_CREATE);
        }

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                l = intent.getIntExtra("start", 0);
                d = intent.getIntExtra("finsh", 0);
                mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();

                if (d > 0) {
                    mTextViewPlayingNow.setText(mMusicInfos.get(d).getArtist() + " - " + mMusicInfos.get(d).getTrack());
                }
                if (l >= 0 && !mMusicPlayNow.isEmpty()) {

                    mTextViewPlayingNow.setText(mMusicPlayNow.get(l).getArtist() + " - " + mMusicPlayNow.get(l).getTrack());
                    mTextViewNextPlayMain.setText("");
                    for (k = l+1; k < mMusicPlayNow.size(); k++) {
                        Log.d(LOG_TAG, k + "k");
                        mTextViewNextPlayMain.append(mMusicPlayNow.get(k).getArtist() + " - " + mMusicPlayNow.get(k).getTrack() + ", ");
                    }
//                    if (l < mMusicPlayNow.size()) {
//                        StringBuilder sb = new StringBuilder();
//                        sb.append(mMusicPlayNow.get(k).getArtist() + " - " + mMusicPlayNow.get(k).getTrack() + ", ");
//                        mTextViewNextPlayMain.setText(sb);
//                        k++;
//                        Log.d(LOG_TAG, "zashlo");
//                    }else {
//                        mTextViewNextPlayMain.setText("");
//                        k=0;
//                        Log.d(LOG_TAG, "neshalo");
//                    }
                }
                Log.e(LOG_TAG, l + " l " + d + " d");
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);
    }


    private void nextPlay() {
        mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();
        mTextViewNextPlayMain.setText("");
        for ( x=l+1; x < mMusicPlayNow.size(); x++) {
            mTextViewNextPlayMain.append(mMusicPlayNow.get(x).getArtist() + " - " + mMusicPlayNow.get(x).getTrack() + ", ");
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
        unregisterReceiver(br);
    }

    @Override
    public void obClickMusic(MusicInfo musicInfo) {
        MusicPlayNow musicPlayNow = new MusicPlayNow();
        musicPlayNow.setArtist(musicInfo.getArtist());
        musicPlayNow.setData(musicInfo.getData());
        musicPlayNow.setTrack(musicInfo.getTrack());
        musicPlayNow.save();

        nextPlay();

    }
}
