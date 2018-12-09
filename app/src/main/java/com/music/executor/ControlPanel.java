package com.music.executor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.music.Main2Activity;
import com.music.MainActivity;
import com.music.R;
import com.music.javabean.MusicData;
import com.music.manager.AudioPlayer;
import com.music.manager.CoverLoader;
import com.music.manager.onMediaPlayerEventChanagerListener;

public class ControlPanel implements View.OnClickListener,onMediaPlayerEventChanagerListener {


    private ImageView ivPlayBarCover;
    private TextView tvPlayBarTitle;
    private TextView tvPlayBarArtist;
    private ImageView ivPlayBarPlay;
    private ImageView ivPlayBarNext;
    private ImageView vPlayBarPlaylist;
    private ProgressBar mProgressBar;
    private View view;
    MainActivity mainActivity;
    public ControlPanel(MainActivity mainActivity,View controlLayout) {
        this.view = controlLayout;
        this.mainActivity=mainActivity;
         ivPlayBarCover = view.findViewById(R.id.iv_play_bar_cover);
        tvPlayBarTitle = view.findViewById(R.id.tv_play_bar_title);
        tvPlayBarArtist = view.findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlay = view.findViewById(R.id.iv_play_bar_play);
        ivPlayBarNext = view.findViewById(R.id.iv_play_bar_next);
        vPlayBarPlaylist = view.findViewById(R.id.v_play_bar_playlist);
        mProgressBar = view.findViewById(R.id.pb_play_bar);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        vPlayBarPlaylist.setOnClickListener(this);
        ivPlayBarCover.setOnClickListener(this);
        onPlayChange(AudioPlayer.getInstance().getPlayMusic());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_bar_play:
                AudioPlayer.getInstance().playPause();
                break;
            case R.id.iv_play_bar_next:
                AudioPlayer.getInstance().next();
                break;
            case R.id.v_play_bar_playlist:
                Context context = vPlayBarPlaylist.getContext();
              //  Intent intent = new Intent(context, PlaylistActivity.class);
               // context.startActivity(intent);
                break;
            case R.id.iv_play_bar_cover:
                Intent intent = new Intent(mainActivity, Main2Activity.class);
                mainActivity.startActivity(intent);
            break;
        }
    }

    @Override
    public void onPlayChange(MusicData music) {
        if (music == null) {
            return;
        }
        Bitmap cover = CoverLoader.getInstance().loadThumb(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getMusicName());
        tvPlayBarArtist.setText(music.getArtist());
        ivPlayBarPlay.setSelected(AudioPlayer.getInstance().isPlaying() || AudioPlayer.getInstance().isPreparing());
        mProgressBar.setMax((int) music.getDuration());
        mProgressBar.setProgress((int) AudioPlayer.getInstance().getAudioPosition());
    }

    @Override
    public void onPlayStart() {
        ivPlayBarPlay.setSelected(true);
    }

    @Override
    public void onPlayPause() {
        ivPlayBarPlay.setSelected(false);
    }

    @Override
    public void updatePlayProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }
}
