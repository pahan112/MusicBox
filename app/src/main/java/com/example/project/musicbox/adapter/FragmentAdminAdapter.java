package com.example.project.musicbox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.model.MusicInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 17.07.2017.
 */

public class FragmentAdminAdapter extends RecyclerView.Adapter<FragmentAdminAdapter.FragmentAdminViewHolder> {

    private List<MusicInfo> mMusicInfos;

    public FragmentAdminAdapter(List<MusicInfo> mMusicInfos) {
        this.mMusicInfos = mMusicInfos;
    }

    @Override
    public FragmentAdminAdapter.FragmentAdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FragmentAdminViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin,parent,false));
    }

    @Override
    public void onBindViewHolder(FragmentAdminAdapter.FragmentAdminViewHolder holder, int position) {
        holder.bind(mMusicInfos.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicInfos.size();
    }

    public class FragmentAdminViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_admin)
        TextView textViewAdmin;
        @BindView(R.id.bt_admin_delete)
        Button buttonAdminDelete;

        public FragmentAdminViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind (MusicInfo musicInfo){
            textViewAdmin.setText(musicInfo.getArtist() + " - " + musicInfo.getTrack());
        }
    }
}
