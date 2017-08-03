package com.example.project.musicbox.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.MusicAdapter;
import com.example.project.musicbox.fragment.CustomLinearLayoutManager;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.model.MusicPlayNow;
import com.example.project.musicbox.model.PlayListModel;
import com.example.project.musicbox.model.PlayListModel_Table;
import com.example.project.musicbox.service.MusicService;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnClickMusicItem {

    public static final String LOG_TAG = "myLog";

    private MusicAdapter mMusicAdapter;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<MusicInfo> mPlayListsSearch = new ArrayList<>();

    private BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "com.example.project.musicbox.reciver";

    private List<MusicInfo> mMusicInfosAdmin = new ArrayList<>();

    @BindView(R.id.rv_item_music)
    RecyclerView mRecyclerViewMusic;
    @BindView(R.id.sv_music)
    SearchView mSearchViewMusic;
    @BindView(R.id.tv_playing_now)
    TextView mTextViewPlayingNow;
    @BindView(R.id.tv_next_play_main)
    TextView mTextViewNextPlayMain;

    @BindView(R.id.rl_main_screen)
    RelativeLayout mRvTouch;

//    private int msg1;
//    private int msg2;
//    private int msg3;

    private Animation mEnlargeAnimation;


    private int d = 0;
    private int l = -1;
    private int k = 1;
    private int m = 0;
    private int x = 0;
    int i = -1;


    private List<MusicPlayNow> mMusicPlayNow = new ArrayList<>();

    final int speedScroll = 200;
    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        int count = 0;
        boolean flag = true;

        @Override
        public void run() {
            if (count < mMusicAdapter.getItemCount()) {
                if (count == mMusicAdapter.getItemCount() - 1) {
                    flag = false;
                } else if (count == 0) {
                    flag = true;
                }
                if (flag) count++;
                else count--;

                mRecyclerViewMusic.smoothScrollToPosition(count);
                handler.postDelayed(this, speedScroll);
            }
        }
    };


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

        List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("admin")).queryList();
        mMusicInfosAdmin.clear();
        for (int i = 0; i < lm.size(); i++) {

            mMusicInfosAdmin.addAll(SQLite.select().from(MusicInfo.class)
                    .where(MusicInfo_Table.id.is(lm.get(i).getIdTrack())).queryList());
        }

        final int[] c = {10 / (int) getResources().getDisplayMetrics().density};


        mMusicAdapter = new MusicAdapter(mMusicInfosAdmin, this);
        mRecyclerViewMusic.setLayoutManager(new CustomLinearLayoutManager(this, c[0]));
        mRecyclerViewMusic.setAdapter(mMusicAdapter);

        handler.postDelayed(runnable, speedScroll);


        initSearch();


        if (!mMusicInfos.isEmpty()) {
            mTextViewPlayingNow.setText(mMusicInfos.get(d).getArtist() + " - " + mMusicInfos.get(d).getTrack());
            for (m = d + 1; m < mMusicInfos.size(); m++) {
                mTextViewNextPlayMain.append(mMusicInfos.get(m).getArtist() + " - " + mMusicInfos.get(m).getTrack() + ", ");
            }

            startService(new Intent(this, MusicService.class));
            getApplicationContext().bindService(new Intent(this, MusicService.class), mServerConn, Context.BIND_AUTO_CREATE);
        }

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                l = intent.getIntExtra("start", 0);
                d = intent.getIntExtra("finsh", 0);
                mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();

                mTextViewNextPlayMain.setText("");

                if (d >= 0 && mMusicPlayNow.isEmpty()) {
                    mTextViewPlayingNow.setText(mMusicInfos.get(d).getArtist() + " - " + mMusicInfos.get(d).getTrack());
                    for (m = d + 1; m < mMusicInfos.size(); m++) {
                        mTextViewNextPlayMain.append(mMusicInfos.get(m).getArtist() + " - " + mMusicInfos.get(m).getTrack() + ", ");
                    }
                    l = -1;
                }
                if (l >= 0 && !mMusicPlayNow.isEmpty()) {

                    mTextViewPlayingNow.setText(mMusicPlayNow.get(l).getArtist() + " - " + mMusicPlayNow.get(l).getTrack());
                    mTextViewNextPlayMain.setText("");
                    for (k = l + 1; k < mMusicPlayNow.size(); k++) {
                        Log.d(LOG_TAG, k + "k");
                        mTextViewNextPlayMain.append(mMusicPlayNow.get(k).getArtist() + " - " + mMusicPlayNow.get(k).getTrack() + ", ");
                    }
                }
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);


//        Bundle extras = getIntent().getExtras();
//        msg1 = extras.getInt("asdf");
//        msg2 = extras.getInt("asdf1");
//        msg3 = extras.getInt("asdf2");
//        Log.d(LOG_TAG, msg1 + "day " +msg2+ " morning"+ msg3+ "evening");

        }


    private void nextPlay() {
        mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();
        mTextViewNextPlayMain.setText("");
        for (x = l + 1; x < mMusicPlayNow.size(); x++) {
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
                    mMusicAdapter.setPlayList(mMusicInfosAdmin);
                } else {
                    mPlayListsSearch.clear();
                    for (MusicInfo musicInfo : mMusicInfosAdmin) {
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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "onServiceDisconnected");
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mMusicInfos.isEmpty()) {
            getApplicationContext().unbindService(mServerConn);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//   mMusicAdapter.touchScreen(i);
//        i++;

        Animation mEnlargeAnimation = AnimationUtils.loadAnimation(mRecyclerViewMusic.getContext(), R.anim.scale_out_tv);
        mRecyclerViewMusic.startAnimation(mEnlargeAnimation);
        mEnlargeAnimation.setFillAfter(true);
        new CountDownTimer(5000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                handler.postDelayed(runnable, mMusicAdapter.getItemCount());
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onFinish() {
                handler.postDelayed(runnable, speedScroll);
            }
        }.start();
        return super.dispatchTouchEvent(ev);
    }

}
