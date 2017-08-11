package com.example.project.musicbox.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.MusicAdapter;
import com.example.project.musicbox.adapter.PlayNextAdapter;
import com.example.project.musicbox.fragment.CustomLinearLayoutManager;
import com.example.project.musicbox.kioskmode.KioskService;
import com.example.project.musicbox.kioskmode.PrefUtils;
import com.example.project.musicbox.model.ModelSpinerTrack;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.model.MusicPlayNow;
import com.example.project.musicbox.model.MusicTextPlayNext;
import com.example.project.musicbox.model.PlayListModel;
import com.example.project.musicbox.model.PlayListModel_Table;
import com.example.project.musicbox.service.MusicService;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnClickMusicItem {

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

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

    @BindView(R.id.rv_play_next)
    RecyclerView mRecyclerPlayNext;

    @BindView(R.id.sv_music)
    SearchView mSearchViewMusic;
    @BindView(R.id.tv_playing_now)
    TextView mTextViewPlayingNow;


    @BindView(R.id.rl_main_screen)
    RelativeLayout mRvTouch;


    private int d = 0;
    private int l = -1;
    private int k = 1;
    private int m = 0;
    private int x = 0;
    int i = 1;


    private boolean isCloseItem = false;
    private boolean isScroll = false;

    private CardView mItemView;
    private Animation mEnlargeAnimation;

    private ImageView mImageView;
    private List<PlayListModel> lm;

    private int currentApiVersion;

    private List<MusicPlayNow> mMusicPlayNow = new ArrayList<>();

    private List<MusicTextPlayNext> mMusicPlayNext = new ArrayList<>();
    private List<MusicInfo> mMusicInfosAdmin2 = new ArrayList<>();

    private PlayNextAdapter mPlayNextAdapter;



    final int speedScroll = 200;
    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (isCloseItem) {
                mEnlargeAnimation = AnimationUtils.loadAnimation(mRecyclerViewMusic.getContext(), R.anim.scale_out_tv);
                mItemView.startAnimation(mEnlargeAnimation);
                /**
                 * здесь нужно сделать удаление
                 */
                mEnlargeAnimation.setFillAfter(true);
                mItemView.bringToFront();
                isCloseItem = false;
                checkRecyclerView();
            }

            mRecyclerViewMusic.smoothScrollToPosition(mMusicAdapter.getItemCount());
            handler.postDelayed(this, speedScroll);
        }
    };

    Runnable runnableTop = new Runnable() {
        @Override
        public void run() {
            mRecyclerViewMusic.smoothScrollToPosition(0);
            handler.removeCallbacks(runnable);
        }
    };

    Runnable runnableBottom = new Runnable() {
        @Override
        public void run() {
            mRecyclerViewMusic.smoothScrollToPosition(mMusicAdapter.getItemCount());
            handler.removeCallbacks(runnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PrefUtils.setKioskModeActive(true, getApplicationContext());

        mMusicIdModel.clear();
        mMusicIdModel = new Select().from(MusicIdModel.class).queryList();



        mPlayNextAdapter = new PlayNextAdapter(mMusicPlayNext);
        mRecyclerPlayNext.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mRecyclerPlayNext.setAdapter(mPlayNextAdapter);

        mMusicInfos.clear();
        for (int i = 0; i < mMusicIdModel.size(); i++) {

            mMusicInfos.addAll(SQLite.select().from(MusicInfo.class)
                    .where(MusicInfo_Table.id.is(mMusicIdModel.get(i).getIdMusic())).queryList());
        }

        lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("admin")).queryList();
        mMusicInfosAdmin.clear();
        for (int i = 0; i < lm.size(); i++) {

            mMusicInfosAdmin.addAll(SQLite.select().from(MusicInfo.class)
                    .where(MusicInfo_Table.id.is(lm.get(i).getIdTrack())).queryList());
        }

        final int[] c = {10 / (int) getResources().getDisplayMetrics().density};

        final CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(this, c[0]);

        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            //and in your imlpementaion of
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // get the viewHolder's and target's positions in your adapter data, swap them
                Collections.swap(mMusicInfosAdmin, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                mMusicAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //TODO
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        });
        ith.attachToRecyclerView(mRecyclerViewMusic);

        mMusicAdapter = new MusicAdapter(mMusicInfosAdmin, this);
        mRecyclerViewMusic.setLayoutManager(layoutManager);
        mRecyclerViewMusic.setAdapter(mMusicAdapter);
        mRecyclerViewMusic.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //check for scroll down
                if (layoutManager.findLastCompletelyVisibleItemPosition() == mMusicAdapter.getItemCount() - 1) {
                    handler.postDelayed(runnableTop, speedScroll);
                    if (isCloseItem) {
                        mEnlargeAnimation = AnimationUtils.loadAnimation(mRecyclerViewMusic.getContext(), R.anim.scale_out_tv);
                        mItemView.startAnimation(mEnlargeAnimation);
                        /**
                         * здесь нужно сделать удаление
                         */

                        mEnlargeAnimation.setFillAfter(true);
                        isCloseItem = false;
                        checkRecyclerView();
                    }
                }
                //check for scroll up
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    handler.postDelayed(runnableBottom, speedScroll);
                }
            }
        });

        handler.postDelayed(runnable, speedScroll);


        initSearch();

        Delete.table(MusicTextPlayNext.class);
        mMusicPlayNext.clear();
        if (!mMusicInfos.isEmpty()) {
            mTextViewPlayingNow.setText(mMusicInfos.get(d).getArtist() + " - " + mMusicInfos.get(d).getTrack());
            for (m = d + 1; m < mMusicInfos.size(); m++) {
//                mTextViewNextPlayMain.append(mMusicInfos.get(m).getArtist() + " - " + mMusicInfos.get(m).getTrack() + ", ");

                MusicTextPlayNext musicTextPlayNext = new MusicTextPlayNext();
                musicTextPlayNext.setTrack(mMusicInfos.get(m).getTrack());
                musicTextPlayNext.setArtist(mMusicInfos.get(m).getArtist());
                musicTextPlayNext.save();

            }

            mMusicPlayNext.addAll(new Select().from(MusicTextPlayNext.class).queryList());
            mPlayNextAdapter.notifyDataSetChanged();

            startService(new Intent(this, MusicService.class));
            getApplicationContext().bindService(new Intent(this, MusicService.class), mServerConn, Context.BIND_AUTO_CREATE);
        }

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                l = intent.getIntExtra("start", 0);
                d = intent.getIntExtra("finsh", 0);
                mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();

//                mTextViewNextPlayMain.setText("");

                if (d >= 0 && mMusicPlayNow.isEmpty()) {
                    Delete.table(MusicTextPlayNext.class);
                    mTextViewPlayingNow.setText(mMusicInfos.get(d).getArtist() + " - " + mMusicInfos.get(d).getTrack());
                    for (m = d + 1; m < mMusicInfos.size(); m++) {
//                        mTextViewNextPlayMain.append(mMusicInfos.get(m).getArtist() + " - " + mMusicInfos.get(m).getTrack() + ", ");


                        MusicTextPlayNext musicTextPlayNext = new MusicTextPlayNext();
                        musicTextPlayNext.setTrack(mMusicInfos.get(m).getTrack());
                        musicTextPlayNext.setArtist(mMusicInfos.get(m).getArtist());
                        musicTextPlayNext.save();

                    }
                    mMusicPlayNext.clear();
                    mMusicPlayNext.addAll(new Select().from(MusicTextPlayNext.class).queryList());
                    mPlayNextAdapter.notifyDataSetChanged();
                    l = -1;
                }
                if (l >= 0 && !mMusicPlayNow.isEmpty()) {

                    mTextViewPlayingNow.setText(mMusicPlayNow.get(l).getArtist() + " - " + mMusicPlayNow.get(l).getTrack());
//                    mTextViewNextPlayMain.setText("");
                    Delete.table(MusicTextPlayNext.class);
                    for (k = l + 1; k < mMusicPlayNow.size(); k++) {
                        Log.d(LOG_TAG, k + "k");
//                        mTextViewNextPlayMain.append(mMusicPlayNow.get(k).getArtist() + " - " + mMusicPlayNow.get(k).getTrack() + ", ");

                        MusicTextPlayNext musicTextPlayNext = new MusicTextPlayNext();
                        musicTextPlayNext.setTrack(mMusicPlayNow.get(k).getTrack());
                        musicTextPlayNext.setArtist(mMusicPlayNow.get(k).getArtist());
                        musicTextPlayNext.save();

                    }
                    for (m = d + 1; m < mMusicInfos.size(); m++) {
//                        mTextViewNextPlayMain.append(mMusicInfos.get(m).getArtist() + " - " + mMusicInfos.get(m).getTrack() + ", ");

                        MusicTextPlayNext musicTextPlayNext = new MusicTextPlayNext();
                        musicTextPlayNext.setTrack(mMusicInfos.get(m).getTrack());
                        musicTextPlayNext.setArtist(mMusicInfos.get(m).getArtist());
                        musicTextPlayNext.save();

                    }
                    mMusicPlayNext.clear();
                    mMusicPlayNext.addAll(new Select().from(MusicTextPlayNext.class).queryList());
                    mPlayNextAdapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);

        startKioskService();


        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }

    }

    private void startKioskService() { // ... and this method
        startService(new Intent(this, KioskService.class));
    }

    private void nextPlay() {
        Delete.table(MusicTextPlayNext.class);
        mMusicPlayNow = new Select().from(MusicPlayNow.class).queryList();
//        mTextViewNextPlayMain.setText("");
        for (x = l + 1; x < mMusicPlayNow.size(); x++) {

//            mTextViewNextPlayMain.append(mMusicPlayNow.get(x).getArtist() + " - " + mMusicPlayNow.get(x).getTrack() + ", ");

            MusicTextPlayNext musicTextPlayNext = new MusicTextPlayNext();
            musicTextPlayNext.setTrack(mMusicPlayNow.get(x).getTrack());
            musicTextPlayNext.setArtist(mMusicPlayNow.get(x).getArtist());
            musicTextPlayNext.save();
        }
        for (m = d + 1; m < mMusicInfos.size(); m++) {
//            mTextViewNextPlayMain.append(mMusicInfos.get(m).getArtist() + " - " + mMusicInfos.get(m).getTrack() + ", ");

            MusicTextPlayNext musicTextPlayNext = new MusicTextPlayNext();
            musicTextPlayNext.setTrack(mMusicInfos.get(m).getTrack() );
            musicTextPlayNext.setArtist(mMusicInfos.get(m).getArtist());
            musicTextPlayNext.save();
        }
        mMusicPlayNext.clear();
        mMusicPlayNext.addAll(new Select().from(MusicTextPlayNext.class).queryList());
        mPlayNextAdapter.notifyDataSetChanged();
    }

    private void initSearch() {
        mSearchViewMusic.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals("admin")) {
                    finishAffinity();
                }
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
        ModelSpinerTrack modelSpinerTrack = new ModelSpinerTrack();
        Log.e(LOG_TAG,modelSpinerTrack.getDayFinish()+ " day");
        Log.e(LOG_TAG,modelSpinerTrack.getDayStart()+ " day");
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
    protected void onResume() {
        super.onResume();
//        FullScreencall();
//        View decor_View = getWindow().getDecorView();
//
//        int ui_Options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        decor_View.setSystemUiVisibility(ui_Options);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mMusicInfos.isEmpty()) {
            getApplicationContext().unbindService(mServerConn);
            stopService(new Intent(this, MusicService.class));
        }
        unregisterReceiver(br);
        stopService(new Intent(this, KioskService.class));
        Delete.table(MusicPlayNow.class);
        Delete.table(MusicTextPlayNext.class);
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
    public void onClickItem(CardView itemView) {
        i++;
        mItemView = itemView;
        isCloseItem = true;

        itemView.bringToFront();
        ((View)itemView.getParent()).invalidate();
        mEnlargeAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.enlarge);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.setTranslationZ(i);
            itemView.invalidate();
        }



        itemView.startAnimation(mEnlargeAnimation);

        mEnlargeAnimation.setFillAfter(true);
        mMusicAdapter.setItem(false);
        /**
         * здесь нужно сделать добавление
         */
        checkRecyclerView();
    }

    private void checkRecyclerView() {
        for (int i = 0; i < mMusicInfosAdmin.size(); i++) {
            View convertView = mRecyclerViewMusic.getChildAt(i);
            if (convertView != null) {
                ImageView addImage = (ImageView) convertView.findViewById(R.id.image_add_button);
                ImageView deleteImage = (ImageView) convertView.findViewById(R.id.image_delete_button);

                addImage.setVisibility(View.GONE);
                deleteImage.setVisibility(View.GONE);

                mRecyclerViewMusic.invalidate();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        if (imm.isAcceptingText()) {
//            View decor_View = getWindow().getDecorView();
//
//        int ui_Options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        decor_View.setSystemUiVisibility(ui_Options);
//        } else {
//
//        }

        if (isCloseItem) {

            mEnlargeAnimation = AnimationUtils.loadAnimation(mRecyclerViewMusic.getContext(), R.anim.scale_out_tv);
            mItemView.startAnimation(mEnlargeAnimation);
            mItemView.bringToFront();
            mEnlargeAnimation.setFillAfter(true);
            /**
             * здесь нужно сделать удаление
             */

            isCloseItem = false;
            mMusicAdapter.setItem(true);

        }

        new CountDownTimer(3000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                handler.postDelayed(runnable, mMusicAdapter.getItemCount());
                handler.removeCallbacks(runnable);
                isScroll = false;
            }

            @Override
            public void onFinish() {
                handler.postDelayed(runnable, speedScroll);

            }
        }.start();
        return super.dispatchTouchEvent(ev);
    }

    /**
     * kiosk mode start.
     */
    @Override
    public void onBackPressed() {

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }
    /**
     * kiosk mode end.
     */
//    public void FullScreencall() {
//        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
//            View v = this.getWindow().getDecorView();
//            v.setSystemUiVisibility(View.GONE);
//        } else if(Build.VERSION.SDK_INT >= 19) {
//            //for new api versions.
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
//    }



}
