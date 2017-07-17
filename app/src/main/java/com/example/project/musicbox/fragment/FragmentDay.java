package com.example.project.musicbox.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.FragmentAdminAdapter;
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

public class FragmentDay extends Fragment {
    @BindView(R.id.rv_day)
    RecyclerView recyclerView;

    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private FragmentAdminAdapter mFragmentAdminAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, null);
        ButterKnife.bind(this, view);

        Delete.table(MusicIdModel.class);
        List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is("day")).queryList();
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
        mFragmentAdminAdapter = new FragmentAdminAdapter(mMusicInfos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mFragmentAdminAdapter);

        return view;
    }
}
