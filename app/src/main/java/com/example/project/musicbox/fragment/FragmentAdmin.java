package com.example.project.musicbox.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.FragmentAdapter;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.model.PlayListModel;
import com.example.project.musicbox.model.PlayListModel_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 17.07.2017.
 */

public class FragmentAdmin extends Fragment implements FragmentAdapter.OnClickDelete {
    @BindView(R.id.rv_admin)
    RecyclerView recyclerView;
    @BindView(R.id.srl_admin)
    SwipeRefreshLayout mAdminSwipeRefreshLayout;

    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<PlayListModel> lm = new ArrayList<>();
    private FragmentAdapter mFragmentAdapter;
    private String strtext;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, null);
        ButterKnife.bind(this, view);

        strtext = getArguments().getString("edttext");
        initSwipeRefreshLayout();
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

    private void initSwipeRefreshLayout() {
        mAdminSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addMusic();
                mFragmentAdapter.setPlayList(mMusicInfos, lm);
                mAdminSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClickDelete(PlayListModel playListModel) {
        SQLite.delete().from(PlayListModel.class)
                .where(PlayListModel_Table.idd.eq(playListModel.getIdd())).execute();

    }
}
