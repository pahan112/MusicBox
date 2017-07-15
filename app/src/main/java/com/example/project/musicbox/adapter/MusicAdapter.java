package com.example.project.musicbox.adapter;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.model.PlayList;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 07.07.2017.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<PlayList> mPlayLists;
    private OnClickMusicItem mOnClickMusicItem;

    public MusicAdapter(List<PlayList> mPlayLists, OnClickMusicItem mOnClickMusicItem) {
        this.mPlayLists = mPlayLists;
        this.mOnClickMusicItem = mOnClickMusicItem;
    }

    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.MusicViewHolder holder, int position) {
        holder.bind(mPlayLists.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }

    public void setPlayList(List<PlayList> mPlayLists) {
        this.mPlayLists = mPlayLists;
        notifyDataSetChanged();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        int[] androidColors;
        @BindView(R.id.tv_artist)
        TextView mTextViewArtist;
        @BindView(R.id.tv_track)
        TextView mTextViewTrack;
        @BindView(R.id.cv_item_music)
        CardView cardView;
        @BindView(R.id.ll_item_music)
        LinearLayout linearLayoutItemMusic;

        public MusicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            androidColors = itemView.getResources().getIntArray(R.array.androidcolors);
        }

        public void bind(final PlayList playList) {
            mTextViewArtist.setText(playList.getArtist());
            mTextViewTrack.setText(playList.getTrack());

            linearLayoutItemMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickMusicItem.obClickMusic(playList);
                }
            });

            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            cardView.setCardBackgroundColor(randomAndroidColor);
        }
    }

    public interface OnClickMusicItem {
        void obClickMusic(PlayList playList);
    }
}
