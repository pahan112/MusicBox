package com.example.project.musicbox.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.musicbox.R;
import com.example.project.musicbox.adapter.FragmentAdapter;
import com.example.project.musicbox.model.MusicIdModel;
import com.example.project.musicbox.model.MusicInfo;
import com.example.project.musicbox.model.MusicInfo_Table;
import com.example.project.musicbox.model.PlayListModel;
import com.example.project.musicbox.model.PlayListModel_Table;
import com.example.project.musicbox.preferense.PreferencesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.lang.reflect.Type;
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


    private List<MusicInfo> mMusicInfos2 = new ArrayList<>();
    private List<MusicInfo> mMusicInfos1 = new ArrayList<>();
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<MusicIdModel> mMusicIdModel = new ArrayList<>();
    private List<PlayListModel> lm = new ArrayList<>();
    private List<PlayListModel> lm1 = new ArrayList<>();
    private List<PlayListModel> lm2 = new ArrayList<>();
    private FragmentAdapter mFragmentAdapter;
    private String strtext;
    public static final String LOG_TAG = "myLog";
//    private int c = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, null);
        ButterKnife.bind(this, view);

        strtext = getArguments().getString("edttext");


        assert strtext != null;
        if (!strtext.equals("admin")) {

            ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                //and in your imlpementaion of
                public boolean onMove(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    // get the viewHolder's and target's positions in your adapter data, swap them
                    Collections.swap(mMusicInfos, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    // and notify the adapter that its dataset has changed
                    mFragmentAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                    PreferencesManager.getInstance().setPrefsListPlayListModel(lm);
                    PreferencesManager.getInstance().setPrefsListMusicInfo(mMusicInfos);


                    return true;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    viewHolder.itemView.setBackgroundColor(Color.WHITE);
                }

                //defines the enabled move directions in each state (idle, swiping, dragging).
                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                }
                @Override
                public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                    super.onSelectedChanged(viewHolder, actionState);
                    if (viewHolder != null) {
                        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                    }
                }

                @Override
                public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    super.clearView(recyclerView, viewHolder);
                    viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
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

//        if(strtext.equals("day")){
        if (!PreferencesManager.getInstance().getPrefsListMusicInfo().isEmpty()) {
            PreferencesManager.getInstance().setPrefsListPlayListModel(lm);
            PreferencesManager.getInstance().setPrefsListMusicInfo(mMusicInfos);
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<PlayListModel>>() {
            }.getType();
            lm = gson1.fromJson(PreferencesManager.getInstance().getPrefsListPlayListModel(), type1);
            if (lm == null) {
                lm = new ArrayList<>();
            }


            Gson gson = new Gson();
            Type type = new TypeToken<List<MusicInfo>>() {
            }.getType();
            mMusicInfos = gson.fromJson(PreferencesManager.getInstance().getPrefsListMusicInfo(), type);
            if (mMusicInfos == null) {
                mMusicInfos = new ArrayList<>();
            }



//        }
//        }else if(strtext.equals("morning")){
//            PreferencesManager.getInstance().setPrefsListPlayListModel(lm1);
//            PreferencesManager.getInstance().setPrefsListMusicInfo(mMusicInfos1);
//
//            Gson gson1 = new Gson();
//            Type type1 = new TypeToken<List<PlayListModel>>(){}.getType();
//            lm1 = gson1.fromJson(PreferencesManager.getInstance().getPrefsListPlayListModel(), type1);
//            if (lm1 == null) {
//                lm1 = new ArrayList<>();
//            }
//Log.e(LOG_TAG,"2");
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<MusicInfo>>() {
//            }.getType();
//            mMusicInfos1 = gson.fromJson(PreferencesManager.getInstance().getPrefsListMusicInfo(), type);
//            if (mMusicInfos1 == null) {
//                mMusicInfos1 = new ArrayList<>();
//            }
//        }else if(strtext.equals("evening")){
//            PreferencesManager.getInstance().setPrefsListPlayListModel(lm2);
//            PreferencesManager.getInstance().setPrefsListMusicInfo(mMusicInfos2);
//            Log.e(LOG_TAG,"3");
//            Gson gson1 = new Gson();
//            Type type1 = new TypeToken<List<PlayListModel>>(){}.getType();
//            lm2 = gson1.fromJson(PreferencesManager.getInstance().getPrefsListPlayListModel(), type1);
//            if (lm2 == null) {
//                lm2 = new ArrayList<>();
//            }
//
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<MusicInfo>>() {
//            }.getType();
//            mMusicInfos2 = gson.fromJson(PreferencesManager.getInstance().getPrefsListMusicInfo(), type);
//            if (mMusicInfos2 == null) {
//                mMusicInfos2 = new ArrayList<>();
//            }
//        }
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
