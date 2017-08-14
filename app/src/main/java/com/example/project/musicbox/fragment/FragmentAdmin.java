package com.example.project.musicbox.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.FragmentAdapter;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicIdModel_Table;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.model.PlayListModel;
import com.example.project.musicbox.model.PlayListModel_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 17.07.2017.
 */

public class FragmentAdmin extends Fragment implements FragmentAdapter.OnClickDelete {
    @BindView(R.id.rv_admin)
    RecyclerView recyclerView;


    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<PlayListModel> lm = new ArrayList<>();
    private FragmentAdapter mFragmentAdapter;
    private String strtext;
    public static final String LOG_TAG = "myLog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, null);
        ButterKnife.bind(this, view);

        strtext = getArguments().getString("edttext");


        assert strtext != null;
        if(!strtext.equals("admin")) {

            ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                //and in your imlpementaion of
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    // get the viewHolder's and target's positions in your adapter data, swap them
                    Collections.swap(mMusicInfos, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    // and notify the adapter that its dataset has changed
                    mFragmentAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                    Log.d(LOG_TAG, viewHolder.getAdapterPosition() + " viewH");
                    Log.d(LOG_TAG, target.getAdapterPosition() + " Targ");


//                lm.remove(lm.get(viewHolder.getAdapterPosition()));
//                lm.add(lm.get(target.getAdapterPosition()));


                    SQLite.delete().from(PlayListModel.class)
                            .where(PlayListModel_Table.idd.eq(lm.get(viewHolder.getAdapterPosition()).getIdd())).execute();

                    PlayListModel playListModel = new PlayListModel();
                    playListModel.setIdd(lm.get(viewHolder.getAdapterPosition()).getIdd());
                    playListModel.setNameList(lm.get(viewHolder.getAdapterPosition()).getNameList());
                    playListModel.setIdTrack(lm.get(viewHolder.getAdapterPosition()).getIdTrack());
                    playListModel.save();


//                SQLite.delete().from(PlayListModel.class)
//                        .where(PlayListModel_Table.idd.eq(lm.get(target.getAdapterPosition()).getIdd())).execute();
//
//                PlayListModel playListModel2 = new PlayListModel();
//                playListModel2.setIdd(lm.get(target.getAdapterPosition()).getIdd());
//                playListModel2.setNameList(lm.get(target.getAdapterPosition()).getNameList());
//                playListModel2.setIdTrack(lm.get(target.getAdapterPosition()).getIdTrack());
//                playListModel2.save();


                    //  mFragmentAdapter.setNewList();

//                lm.remove(viewHolder.getAdapterPosition());
//                PlayListModel playListModel = new PlayListModel();
////                playListModel.setIdd(lm.get(viewHolder.getAdapterPosition()).getIdd());
////                playListModel.save();
////                mFragmentAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//                playListModel.update();
                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                }

                //defines the enabled move directions in each state (idle, swiping, dragging).
                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                }
            });
            ith.attachToRecyclerView(recyclerView);
        }
        addMusic();

        mFragmentAdapter = new FragmentAdapter(mMusicInfos, this, lm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mFragmentAdapter);

        return view;
    }

    private void addMusic() {
        Delete.table(MusicIdModel.class);
        lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is(strtext)).queryList();
        for (int i = 0; i < lm.size(); i++) {
            MusicIdModel musicIdModel = new MusicIdModel();
            musicIdModel.setIdMusic(lm.get(i).getIdTrack());
            musicIdModel.save();
        }

        mMusicIdModel.clear();
        mMusicIdModel = new Select().from(MusicIdModel.class).queryList();

        mMusicInfos.clear();
        for (int i = 0; i < mMusicIdModel.size(); i++) {

            mMusicInfos.addAll(SQLite.select().from(MusicInfo.class)
                    .where(MusicInfo_Table.id.is(mMusicIdModel.get(i).getIdMusic())).queryList());
        }
    }

    @Override
    public void onClickDelete(PlayListModel playListModel) {
        SQLite.delete().from(PlayListModel.class)
                .where(PlayListModel_Table.idd.eq(playListModel.getIdd())).execute();
        addMusic();
        mFragmentAdapter.setPlayList(mMusicInfos, lm);
    }

    @Override
    public void onResume() {
        super.onResume();
        addMusic();
        mFragmentAdapter.setPlayList(mMusicInfos, lm);
    }
}
