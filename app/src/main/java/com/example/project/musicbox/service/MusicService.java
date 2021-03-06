package com.example.project.musicbox.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.project.musicbox.activity.MainActivity;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.model.MusicPlayNow;
import com.example.project.musicbox.preferense.PreferencesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pahan on 06.07.2017.
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    public static final String LOG_TAG = "myLog";
    private boolean isPlay = false;
    private MediaPlayer mMp;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<MusicPlayNow> mMusicPlayNow = new ArrayList<>();
    int c = 0;
    int d = 0;

    private Intent intent;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");

        mMusicIdModel.clear();
        mMusicIdModel = new Select().from(MusicIdModel.class).queryList();

        mMusicInfos.clear();
        for (int i = 0; i < mMusicIdModel.size(); i++) {

            mMusicInfos.addAll(SQLite.select().from(MusicInfo.class)
                    .where(MusicInfo_Table.id.is(mMusicIdModel.get(i).getIdMusic())).queryList());
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<MusicInfo>>() {
        }.getType();
        mMusicInfos = gson.fromJson(PreferencesManager.getInstance().getPrefsListMusicInfo(), type);
        if (mMusicInfos == null) {
            mMusicInfos = new ArrayList<>();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        mMp = MediaPlayer.create(this, Uri.parse(mMusicInfos.get(c).getData()));
        mMp.setOnCompletionListener(this);
        mMp.start();

        return new Binder();
    }


    private void play() {

        try {
            mMp.reset();
            if(!mMusicPlayNow.isEmpty()&& isPlay){
                mMp.setDataSource(this, Uri.parse(mMusicPlayNow.get(d).getData()));
                Log.d(LOG_TAG,mMusicPlayNow.get(d).getTrack() + " 123");
                intent.putExtra("start",d);
                sendBroadcast(intent);
                d++;
            }else {
                mMp.setDataSource(this, Uri.parse(mMusicInfos.get(c).getData()));
                Log.d(LOG_TAG,mMusicInfos.get(c).getTrack() + " 567");
                intent.putExtra("finsh",c);
                sendBroadcast(intent);
            }
            mMp.prepare();
            mMp.start();
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        Delete.table(MusicPlayNow.class);

        mMp.pause();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();
        intent = new Intent(MainActivity.BROADCAST_ACTION);
        isPlay = true;
        if(d == mMusicPlayNow.size()&&!mMusicPlayNow.isEmpty()){
            isPlay = false;
            Log.e(LOG_TAG, "gotovo");
            d = 0;
            Delete.table(MusicPlayNow.class);
        }
        if(mMusicPlayNow.isEmpty()|| !isPlay) {
            c++;
        }
        if (c == mMusicInfos.size()) {
            c = 0;
            intent.putExtra("finsh",c);
            sendBroadcast(intent);
        }
        Log.e(LOG_TAG, c + "c service");
        Log.e(LOG_TAG, d + "d service");
        play();
    }
}
