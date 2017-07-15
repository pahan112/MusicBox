package com.example.project.musicbox.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Pahan on 06.07.2017.
 */

public class MusicService extends Service {

    public static final String LOG_TAG = "myLog";
    private boolean isPlay = false;
    private MediaPlayer mMp;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, intent.getStringExtra("key"));

        mMp = new MediaPlayer();
        isPlay = true;
        try {
            mMp.setDataSource(this, Uri.parse(intent.getStringExtra("key")));
            mMp.prepare();
            mMp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Binder();
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
        if(isPlay) {
            isPlay = false;
            mMp.pause();
        }
    }
}
