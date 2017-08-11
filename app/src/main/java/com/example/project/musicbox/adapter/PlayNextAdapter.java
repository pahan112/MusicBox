package com.example.project.musicbox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicPlayNow;
import com.example.project.musicbox.model.MusicTextPlayNext;
import com.example.project.musicbox.model.PlayListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 08.08.2017.
 */

public class PlayNextAdapter extends RecyclerView.Adapter<PlayNextAdapter.PlayerNextViewHolder> {

    private List<MusicTextPlayNext> mMusicTextPlayNext;


    public PlayNextAdapter( List<MusicTextPlayNext> mMusicTextPlayNext){
        this.mMusicTextPlayNext =mMusicTextPlayNext;

    }

    @Override
    public PlayNextAdapter.PlayerNextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlayerNextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.next_music,parent,false));
    }

    @Override
    public void onBindViewHolder(PlayNextAdapter.PlayerNextViewHolder holder, int position) {
        holder.bind(mMusicTextPlayNext.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicTextPlayNext.size();
    }

    public class PlayerNextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text_next)
        TextView textView;
        public PlayerNextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind (MusicTextPlayNext mMusicTextPlayNext){
          textView.setText(mMusicTextPlayNext.getArtist() + " - " + mMusicTextPlayNext.getTrack());
        }
    }
}
