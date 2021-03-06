package com.example.project.musicbox.adapter;


import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.project.musicbox.R;
import com.example.project.musicbox.model.MusicInfo;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Pahan on 07.07.2017.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<MusicInfo> mMusicInfos;
    private OnClickMusicItem mOnClickMusicItem;
    private boolean click = false;
    private boolean usclick = false;
    private int c = 0;

    public MusicAdapter(List<MusicInfo> mMusicInfos, OnClickMusicItem mOnClickMusicItem) {
        this.mMusicInfos = mMusicInfos;
        this.mOnClickMusicItem = mOnClickMusicItem;
        c = 10000 * mMusicInfos.size();
    }

    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.MusicViewHolder holder, int position) {
            holder.bind(mMusicInfos.get(position % mMusicInfos.size()));
    }

    @Override
    public int getItemCount() {
            return c;
    }

    public void setPlayList(List<MusicInfo> mMusicInfos) {
        this.mMusicInfos = mMusicInfos;
        notifyDataSetChanged();
        c = mMusicInfos.size();
        usclick = true;
    }

    public void setClikItNow() {
        usclick = false;
        c = 10000 * mMusicInfos.size();
        notifyDataSetChanged();
    }

    public void setItem(boolean cklik) {
        click = cklik;
    }

//    public Integer getS() {
//        return s;
//    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        int[] androidColors;
        @BindView(R.id.tv_artist)
        TextView mTextViewArtist;
        @BindView(R.id.tv_track)
        TextView mTextViewTrack;
        @BindView(R.id.tv_timer)
        TextView mTextViewTimer;
        @BindView(R.id.textView)
        TextView mTextViewAdd;
        @BindView(R.id.tv_track_name)
        TextView mTextViewNameTrack;
        @BindView(R.id.tv_artist_name)
        TextView mTextViewName;

        @BindView(R.id.cv_item_music)
        CardView cardView;
        @BindView(R.id.ll_item_music)
        RelativeLayout linearLayoutItemMusic;
        @BindView(R.id.image_delete_button)
        ImageView mIageViewBtDelete;
        @BindView(R.id.image_add_button)
        ImageView mIageViewBtAdd;
        @BindView(R.id.iv_add_bt_ch)
        ImageView mImageViewAddBt;
//        MusicAdapter musicAdapter;
//        int c;

        private boolean isClick = false;

        private Animation mEnlargeAnimation;


        public MusicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            androidColors = itemView.getResources().getIntArray(R.array.androidcolors);
            itemView.bringToFront();
//           musicAdapter = new MusicAdapter();

        }



        public void bind(final MusicInfo musicInfo) {


            mTextViewArtist.setText(musicInfo.getArtist());
            mTextViewTrack.setText(musicInfo.getTrack());



            linearLayoutItemMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mOnClickMusicItem.onClickItem(cardView , getAdapterPosition());

                    if (!click) {
                        /**
                         * здесь идет добавление
                         */
//                        mEnlargeAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.enlarge);
//                        cardView.bringChildToFront(itemView);
//                        ((View)itemView.getParent()).requestLayout();
//                        itemView.startAnimation(mEnlargeAnimation);
//                        mEnlargeAnimation.setFillAfter(true);

                        linearLayoutItemMusic.bringToFront();
                        linearLayoutItemMusic.requestLayout();
                        linearLayoutItemMusic.invalidate();

                        mIageViewBtAdd.setVisibility(View.VISIBLE);
                        mIageViewBtDelete.setVisibility(View.VISIBLE);

                        linearLayoutItemMusic.bringToFront();
                        linearLayoutItemMusic.requestLayout();
                        linearLayoutItemMusic.invalidate();


                    }
//                    } else {
                    if (click) {

                        itemView.bringToFront();
                        /**
                         * здесь идет удаление
                         */
                        mIageViewBtAdd.setVisibility(View.INVISIBLE);
                        mIageViewBtDelete.setVisibility(View.INVISIBLE);
                        isClick = false;
                    }
                    mIageViewBtDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mEnlargeAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scale_out_tv);
                            itemView.bringToFront();
                            itemView.startAnimation(mEnlargeAnimation);
                            mEnlargeAnimation.setFillAfter(true);
                            /**
                             * здесь идет удаление
                             */
                            mIageViewBtAdd.setVisibility(View.INVISIBLE);
                            mIageViewBtDelete.setVisibility(View.INVISIBLE);
                        }
                    });

                    mIageViewBtAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnClickMusicItem.inClickAdd(cardView);
                            mEnlargeAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scale_out_tv);
                            itemView.bringToFront();
                            itemView.startAnimation(mEnlargeAnimation);
                            mEnlargeAnimation.setFillAfter(true);
                            mOnClickMusicItem.obClickMusic(musicInfo);
                            new CountDownTimer(3000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    /**
                                     * здесь идет добавление
                                     */
                                    mTextViewArtist.setVisibility(View.INVISIBLE);
                                    mTextViewTrack.setVisibility(View.INVISIBLE);
                                    mTextViewName.setVisibility(View.INVISIBLE);
                                    mTextViewNameTrack.setVisibility(View.INVISIBLE);

                                    mImageViewAddBt.setVisibility(View.VISIBLE);
                                    mTextViewAdd.setVisibility(View.VISIBLE);
                                    linearLayoutItemMusic.setClickable(false);
                                    linearLayoutItemMusic.setEnabled(false);
                                    cardView.setCardBackgroundColor(Color.GRAY);
                                }

                                @Override
                                public void onFinish() {
                                    /**
                                     * здесь идет удаление
                                     */
                                    mTextViewArtist.setVisibility(View.VISIBLE);
                                    mTextViewTrack.setVisibility(View.VISIBLE);
                                    mTextViewName.setVisibility(View.VISIBLE);
                                    mTextViewNameTrack.setVisibility(View.VISIBLE);

                                    mImageViewAddBt.setVisibility(View.INVISIBLE);
                                    mTextViewAdd.setVisibility(View.INVISIBLE);
                                    itemView.bringToFront();
                                    new CountDownTimer(3000, 1000) {

                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            mTextViewTimer.setText(millisUntilFinished / 1000 + "");
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

                            }.start();
                            /**
                             * здесь идет удаление
                             */
                            mIageViewBtAdd.setVisibility(View.INVISIBLE);
                            mIageViewBtDelete.setVisibility(View.INVISIBLE);
                        }

                    });
                }
            });

            int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
            cardView.setCardBackgroundColor(randomAndroidColor);
        }


    }


    public interface OnClickMusicItem {
        void obClickMusic(MusicInfo musicInfo);

        void inClickAdd(CardView mCardView);

        void onClickItem(CardView cardViewv,int position);
    }
}
