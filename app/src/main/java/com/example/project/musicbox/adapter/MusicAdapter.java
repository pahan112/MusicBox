package com.example.project.musicbox.adapter;


import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.activity.MainActivity;
import com.example.project.musicbox.model.MusicInfo;

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

    public MusicAdapter(List<MusicInfo> mMusicInfos, OnClickMusicItem mOnClickMusicItem) {
        this.mMusicInfos = mMusicInfos;
        this.mOnClickMusicItem = mOnClickMusicItem;
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

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        int[] androidColors;
        @BindView(R.id.tv_artist)
        TextView mTextViewArtist;
        @BindView(R.id.tv_track)
        TextView mTextViewTrack;
        @BindView(R.id.tv_timer)
        TextView mTextViewTimer;
        @BindView(R.id.cv_item_music)
        CardView cardView;
        @BindView(R.id.ll_item_music)
        RelativeLayout linearLayoutItemMusic;

        public MusicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            androidColors = itemView.getResources().getIntArray(R.array.androidcolors);
        }

        public void bind(final MusicInfo musicInfo) {
            mTextViewArtist.setText(musicInfo.getArtist());
            mTextViewTrack.setText(musicInfo.getTrack());

            linearLayoutItemMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickMusicItem.obClickMusic(musicInfo);

//                    linearLayoutItemMusic.setClickable(false);
//                    linearLayoutItemMusic.setEnabled(false);


                    new CountDownTimer(30000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            linearLayoutItemMusic.setClickable(false);
                            linearLayoutItemMusic.setEnabled(false);
                            mTextViewTimer.setText(millisUntilFinished / 1000 + "");
                            cardView.setCardBackgroundColor(Color.GRAY);
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
            });

            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            cardView.setCardBackgroundColor(randomAndroidColor);
        }
    }

    public interface OnClickMusicItem {
        void obClickMusic(MusicInfo musicInfo);
    }
}
