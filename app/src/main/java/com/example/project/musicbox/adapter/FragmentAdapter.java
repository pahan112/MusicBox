package com.example.project.musicbox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.PlayListModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 17.07.2017.
 */

public class FragmentAdapter extends RecyclerView.Adapter<FragmentAdapter.FragmentAdminViewHolder> {

    private List<MusicInfo> mMusicInfos;
    private List<PlayListModel> mPlayListModels;
    private OnClickDelete mOnClickDelete;

    public FragmentAdapter(List<MusicInfo> mMusicInfos , OnClickDelete mOnClickDelete, List<PlayListModel> mPlayListModels) {
        this.mMusicInfos = mMusicInfos;
        this.mOnClickDelete = mOnClickDelete;
        this.mPlayListModels =mPlayListModels;
    }

    @Override
    public FragmentAdapter.FragmentAdminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FragmentAdminViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin,parent,false));
    }

    @Override
    public void onBindViewHolder(FragmentAdapter.FragmentAdminViewHolder holder, int position) {
        holder.bind(mMusicInfos.get(position),mPlayListModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicInfos.size();
    }

    public void setPlayList(List<MusicInfo> mMusicInfos , List<PlayListModel> mPlayListModels) {
        this.mMusicInfos = mMusicInfos;
        this.mPlayListModels =mPlayListModels;
        notifyDataSetChanged();
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
        public void bind (final MusicInfo musicInfo, final PlayListModel playListModel){
            textViewAdmin.setText(musicInfo.getArtist() + " - " + musicInfo.getTrack());
            buttonAdminDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickDelete.onClickDelete(playListModel);
                }
            });
        }
    }
    public interface OnClickDelete{
        void onClickDelete(PlayListModel playListModel);
    }
}
