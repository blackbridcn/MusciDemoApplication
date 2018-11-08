package com.music.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;

import com.music.AppContant.AppContant;
import com.music.javabean.MusicData;

import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer {
    private static AudioPlayer instance;
    private Context mContext;
    private ArrayList<onMediaPlayerEventChanagerListener> playerEventChanagerListeners;


    private MediaPlayer mediaPlayer;
    private AudioFocusManager audioFocusManager;
    private IntentFilter noisyFilter;
    //来电/耳机拔出时暂停播放
    private BroadcastReceiver audioBecomingNoisy = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public static AudioPlayer getInstance() {
        return instance;
    }

    private AudioPlayer(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        audioFocusManager = new AudioFocusManager(this.mContext);
        playerEventChanagerListeners = new ArrayList<onMediaPlayerEventChanagerListener>();

        mediaPlayer = new MediaPlayer();
        noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mediaPlayer.setOnCompletionListener((mediaPlayer) -> nextAudio());
        mediaPlayer.setOnPreparedListener((mediaPlayer) -> {
            if (isPlaying()) {
                startPlayer();
            }
        });
        //网络流媒体的缓冲监听
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners)
                listener.onBufferingUpdate(percent);
        });
    }

    public static void initAudioManager(Context mContext) {
        if (instance == null) {
            instance = new AudioPlayer(mContext);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isPausing() {
        return mediaPlayer.isPlaying();
    }

    public void addAndPlay(MusicData music) {
        if (music == null) return;
        int position = AppContant.PlayContant.addMusicToPlayList(music);
        playCurrentList(position,music);
    }

    public void startPlayer() {

    }

    public void nextAudio() {

    }

    public void playCurrentList(int index, MusicData music) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getFilePath());
            mediaPlayer.prepareAsync();
            //state = STATE_PREPARING;
            for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners) {
                listener.onPlayChange(music);
            }
            PlayNotifier.get().showPlay(music);
            MediaSessionManager.get().updateMetaData(music);
            MediaSessionManager.get().updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show("当前歌曲无法播放");
        }
    }

    public void addMediaPlayerEventChanagerListener(onMediaPlayerEventChanagerListener listeners) {
        if (listeners != null && !playerEventChanagerListeners.contains(listeners))
            playerEventChanagerListeners.add(listeners);
    }

    public void removeMediaPlayerEventChanagerListener(onMediaPlayerEventChanagerListener listeners) {
        if (listeners != null)
            playerEventChanagerListeners.remove(listeners);
    }
}
