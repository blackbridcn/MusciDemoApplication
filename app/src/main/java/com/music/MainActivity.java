package com.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.executor.ControlPanel;
import com.music.localmusicTask.LocalMusicFragment;
import com.music.lrcmodel.PlayFragment;
import com.music.manager.AudioPlayer;
import com.music.service.MediaService;
import com.music.utils.LocalMusicUitls;

import org.com.comlibs.perminssion.RunnTimePreminssion;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.fl_play_bar)
    FrameLayout playBar;


    private ControlPanel controlPanel;

    private MediaService.MediaPlayerBinder mediaPlayerBinder;
    private static String TAG = MainActivity.class.getName();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaPlayerBinder = (MediaService.MediaPlayerBinder) iBinder;
            Log.e("TAG", "onServiceConnected: --------------------->");
            //  String filePath = AppContant.PlayContant.musicData.get(0).getDataFilePath();
            //mediaPlayerBinder.playMusic(filePath);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "onServiceDisconnected: ----------------------------> ");
            mediaPlayerBinder = null;
        }
    };
    private NavigationView navigationView;
    private ImageView albumart;
    private TextView songtitle, songartist;
    private LocalMusicFragment localMusicFragment;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        this.findViewById(R.id.container_frame);

        albumart = (ImageView) header.findViewById(R.id.album_art);
        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        localMusicFragment = new LocalMusicFragment();

        transaction.add(R.id.container_frame, localMusicFragment, LocalMusicFragment.class.getName()).show(localMusicFragment).commit();
        RunnTimePreminssion.requestEach(this, ((permission, granted, requestAgain) -> {
                    if (granted) {
                        LocalMusicUitls.getInstance().getMusicList(this);
                    }
                })
                , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_PHONE_STATE);
        controlPanel = new ControlPanel(this, playBar);

        AudioPlayer.getInstance().addMediaPlayerEventChanagerListener(controlPanel);
    }

    PlayFragment mPlayFragment;

    public void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    boolean isPlayFragmentShow;

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }
}
