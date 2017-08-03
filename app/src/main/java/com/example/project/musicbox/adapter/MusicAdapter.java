package com.example.project.musicbox.adapter;


import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.activity.MainActivity;
import com.example.project.musicbox.model.MusicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 07.07.2017.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<MusicInfo> mMusicInfos;
    private OnClickMusicItem mOnClickMusicItem;
    private int s;

    public MusicAdapter(List<MusicInfo> mMusicInfos, OnClickMusicItem mOnClickMusicItem) {
        this.mMusicInfos = mMusicInfos;
        this.mOnClickMusicItem = mOnClickMusicItem;
    }
    public MusicAdapter() {

    }

    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.MusicViewHolder holder, int position) {
        holder.bind(mMusicInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicInfos.size();
    }

    public void setPlayList(List<MusicInfo> mMusicInfos) {
        this.mMusicInfos = mMusicInfos;
        notifyDataSetChanged();
    }

//    public Integer getS() {
//        return s;
//    }

//    public void touchScreen(int s){
//        Log.e("myLog",s+ " hfghhf");
//        this.s = s;
//    }
    public class MusicViewHolder extends RecyclerView.ViewHolder {
        int[] androidColors;
        @BindView(R.id.tv_artist)
        TextView mTextViewArtist;
        @BindView(R.id.tv_track)
        TextView mTextViewTrack;
        @BindView(R.id.tv_timer)
        TextView mTextViewTimer;
        @BindView(R.id.textView)
        TextView mTextViewAdd;
        @BindView(R.id.cv_item_music)
        CardView cardView;
        @BindView(R.id.ll_item_music)
        RelativeLayout linearLayoutItemMusic;
        @BindView(R.id.image_delete_button)
        ImageView mIageViewBtDelete;
        @BindView(R.id.image_add_button)
        ImageView mIageViewBtAdd;
        @BindView(R.id.iv_add_bt_ch)
        ImageView mImageViewAddBt;
//        MusicAdapter musicAdapter;
//        int c;

        private boolean isClick = false;

        private Animation mEnlargeAnimation;

        public MusicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            androidColors = itemView.getResources().getIntArray(R.array.androidcolors);
//           musicAdapter = new MusicAdapter();

        }


        public void bind(final MusicInfo musicInfo) {
//            c = musicAdapter.getS();
//            Log.e("myLog",c + "");
            mTextViewArtist.setText(musicInfo.getArtist());
            mTextViewTrack.setText(musicInfo.getTrack());
//            if (c > 0&& isClick){
//                mEnlargeAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scale_out_tv);
//                itemView.startAnimation(mEnlargeAnimation);
//                mEnlargeAnimation.setFillAfter(true);
//                mIageViewBtAdd.setVisibility(View.INVISIBLE);
//                mIageViewBtDelete.setVisibility(View.INVISIBLE);
//                isClick = false;
//            }
            linearLayoutItemMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(!isClick) {
                        mEnlargeAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.enlarge);
                        cardView.startAnimation(mEnlargeAnimation);
                        mEnlargeAnimation.setFillAfter(true);

                        mIageViewBtAdd.setVisibility(View.VISIBLE);
                        mIageViewBtDelete.setVisibility(View.VISIBLE);
                        isClick =true;
                    }else {
                        mEnlargeAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.scale_out_tv);
                        cardView.startAnimation(mEnlargeAnimation);
                        mEnlargeAnimation.setFillAfter(true);
                        mIageViewBtAdd.setVisibility(View.INVISIBLE);
                        mIageViewBtDelete.setVisibility(View.INVISIBLE);
                        isClick = false;
                    }
                    mIageViewBtDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEnlargeAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.scale_out_tv);
                            cardView.startAnimation(mEnlargeAnimation);
                            mEnlargeAnimation.setFillAfter(true);
                            mIageViewBtAdd.setVisibility(View.INVISIBLE);
                            mIageViewBtDelete.setVisibility(View.INVISIBLE);
                        }
                    });

                    mIageViewBtAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEnlargeAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.scale_out_tv);
                            cardView.startAnimation(mEnlargeAnimation);
                            mEnlargeAnimation.setFillAfter(true);
                            mOnClickMusicItem.obClickMusic(musicInfo);
                            new CountDownTimer(3000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    mImageViewAddBt.setVisibility(View.VISIBLE);
                                    mTextViewAdd.setVisibility(View.VISIBLE);
                                    linearLayoutItemMusic.setClickable(false);
                                    linearLayoutItemMusic.setEnabled(false);
                                    cardView.setCardBackgroundColor(Color.GRAY);
                                }

                                @Override
                                public void onFinish() {
                                    mImageViewAddBt.setVisibility(View.INVISIBLE);
                                    mTextViewAdd.setVisibility(View.INVISIBLE);
                                    new CountDownTimer(27000, 1000) {

                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            mTextViewTimer.setText(millisUntilFinished / 1000 + "");
                                        }

                                        @Override
                                        public void onFinish() {
                                            linearLayoutItemMusic.setClickable(true);
                                            linearLayoutItemMusic.setEnabled(true);
                                            mTextViewTimer.setText("");

                                            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                                            cardView.setCardBackgroundColor(randomAndroidColor);
                                        }

                                    }.start();
                                }

                            }.start();

                            mIageViewBtAdd.setVisibility(View.INVISIBLE);
                            mIageViewBtDelete.setVisibility(View.INVISIBLE);
                        }

                    });

                }
            });

            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            cardView.setCardBackgroundColor(randomAndroidColor);
        }


    }


    public interface OnClickMusicItem {
        void obClickMusic(MusicInfo musicInfo);
    }
}
