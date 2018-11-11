package com.music.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.music.AppContant.AppContant;
import com.music.javabean.MusicData;
import com.music.utils.StringUtils;
import com.music.utils.ToastUtils;

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

    /**
     * 将歌曲添加到播放列表中并播放
     *
     * @param music
     */
    public void addAndPlay(MusicData music) {
        if (music == null) return;
        int position = AppContant.PlayContant.addMusicToPlayList(music);
        playCurrentList(position, music);
    }

    public void startPlayer() {

    }

    public void nextAudio() {

    }

    public void playCurrentList(int position){
        if(position>=0&&AppContant.PlayContant.getCurrentPlaySize()>position){
            AppContant.PlayContant.setCurrentPlayIndex(position);
            playCurrentList(position,AppContant.PlayContant.getCurrentPlayData());
        }
    }




    public void playCurrentList(int index, MusicData music) {
        if (index == -1 || music == null || StringUtils.isEmpty(music.getDataFilePath())) return;
        AppContant.PlayContant.setCurrentPlayIndex(index);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(music.getDataFilePath());
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show("当前歌曲无法播放");
            return;
        }
        mediaPlayer.prepareAsync();
        //state = STATE_PREPARING;
        for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners) {
            listener.onPlayChange(music);
        }
        PlayNotifier.get().showPlay(music);
       // AudioMediaSessionManager.getInstance().updateMetaData(music);
       // AudioMediaSessionManager.getInstance().updatePlaybackState();

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
