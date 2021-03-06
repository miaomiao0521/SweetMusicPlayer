package com.huwei.sweetmusicplayer.fragments;


import com.huwei.sweetmusicplayer.R;
//import com.huwei.sweetmusicplayer.datamanager.MusicManager;
import com.huwei.sweetmusicplayer.SweetApplication;
import com.huwei.sweetmusicplayer.contains.IContain;
import com.huwei.sweetmusicplayer.datamanager.MusicManager;
import com.huwei.sweetmusicplayer.abstracts.AbstractMusic;
import com.nostra13.universalimageloader.core.ImageLoader;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.bottom_action_bar)
public class BottomActionBarFragment extends Fragment implements IContain {

    @ViewById
    TextView tv_title, tv_artist;
    @ViewById
    ToggleButton btn_play;
    @ViewById
    Button btn_next;
    @ViewById
    ImageView img_album;
    @ViewById
    ProgressBar pro_music;

    @AfterViews
    void init() {
        initListener();
        initReciever();
    }


    protected void initListener() {
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MusicManager.getInstance().nextSong();
            }
        });

        btn_play.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked != MusicManager.getInstance().isPlaying()) {
                    //播放意图
                    if (isChecked) {
                        MusicManager.getInstance().play();
                    } else {
                        MusicManager.getInstance().pause();
                    }
                }
            }
        });
    }

    protected void initReciever() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAYBAR_UPDATE);
        intentFilter.addAction(CURRENT_UPDATE);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            switch (action) {
                case PLAYBAR_UPDATE:
                    pro_music.setMax(MusicManager.getInstance().getNowPlayingSong().getDuration());
                    AbstractMusic music = intent.getParcelableExtra("newMusic");
                    updateBottomBarFromService(music);
                    break;
                case CURRENT_UPDATE:
                    pro_music.setProgress(intent.getIntExtra("currentTime", 0));
                    break;
            }
        }

    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    void updateBottomBarFromService(AbstractMusic music) {

        if (music != null) {
            tv_title.setText(music.getTitle());
            tv_artist.setText(music.getArtist());
            btn_play.setChecked(MusicManager.getInstance().isPlaying());

            ImageLoader imageLoader = SweetApplication.getImageLoader();
            imageLoader.displayImage(music.getArtPic(), img_album);
//            img_album.setImageBitmap(MusicUtils.getCachedArtwork(getActivity(), music.getAlbumId(), R.drawable.img_album_background));
        }

    }
}
