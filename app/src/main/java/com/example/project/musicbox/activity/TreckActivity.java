package com.example.project.musicbox.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.project.musicbox.R;
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
import butterknife.OnClick;

/**
 * Created by Pahan on 21.07.2017.
 */

public class TreckActivity extends AppCompatActivity {

    @BindView(R.id.lv_track)
    ListView mLvTrack;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private List<String> mTracks = new ArrayList<>();
    private List<String> mTracksId = new ArrayList<>();
    public static final String LOG_TAG = "myLog";
    private String msg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treck);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        msg = extras.getString("qwewrwqr");

        mMusicInfos = new Select().from(MusicInfo.class).queryList();

        Delete.table(MusicIdModel.class);
        List<PlayListModel> lm = SQLite.select().from(PlayListModel.class).where(PlayListModel_Table.nameList.is(msg)).queryList();
        for (int i = 0; i < lm.size(); i++) {
            MusicIdModel musicIdModel = new MusicIdModel();
            musicIdModel.setIdMusic(lm.get(i).getIdTrack());
            musicIdModel.save();
        }
        for (int i = 0; i < mMusicInfos.size(); i++) {
            mTracksId.add(mMusicInfos.get(i).getId());
            mTracks.add(mMusicInfos.get(i).getArtist() + " - " + mMusicInfos.get(i).getTrack());
        }
        mLvTrack.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mTracksId));
        mLvTrack.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mTracks));
        mLvTrack.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    @OnClick(R.id.bt_track_add)
    void clickAdd() {
        SparseBooleanArray sbArray = mLvTrack.getCheckedItemPositions();
        for (int i = 0; i < sbArray.size(); i++) {
            int key = sbArray.keyAt(i);
            if (sbArray.get(key)) {
                Log.d(LOG_TAG, mTracksId.get(key));
                final PlayListModel playListModel = new PlayListModel();
                playListModel.setIdd(msg + mTracks.get(key));
                playListModel.setNameList(msg);
                playListModel.setIdTrack(mTracksId.get(key));
                playListModel.save();
            }
        }
        super.onBackPressed();
    }
    @OnClick(R.id.bt_back)
    void clickBack(){
        super.onBackPressed();
    }
}
