package com.music.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.music.AppContant.AppContant;
import com.music.AppContant.AppContentKey;
import com.music.javabean.MusicData;
import com.music.utils.SPUtils;
import com.music.utils.StringUtils;
import com.music.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer {
    private static AudioPlayer instance;
    //播放器状态
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;
    private int state = STATE_IDLE;

    //MusicPlayControl
    // 播放命令
    public static final int MUSIC_CONTROL_PLAY = 0;
    // 暂停命令
    public static final int MUSIC_CONTROL_PAUSE = 1;
    // 上一首命令
    public static final int MUSIC_CONTROL_PREVIOUS = 2;
    // 下一首命令
    public static final int MUSIC_CONTROL_NEXT = 3;

    private static final long TIME_UPDATE = 300L;

    private Context mContext;
    private ArrayList<onMediaPlayerEventChanagerListener> playerEventChanagerListeners;
    private Handler handler;

    private MediaPlayer mediaPlayer;
    private AudioFocusManager audioFocusManager;
    private IntentFilter noisyFilter;
    //来电/耳机拔出时暂停播放
    private BroadcastReceiver audioBecomingNoisy = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playPause();
        }
    };

    public static AudioPlayer getInstance() {
        return instance;
    }

    private AudioPlayer(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        audioFocusManager = new AudioFocusManager(this.mContext);
        playerEventChanagerListeners = new ArrayList<onMediaPlayerEventChanagerListener>();
        handler = new Handler(Looper.getMainLooper());
        mediaPlayer = new MediaPlayer();
        noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mediaPlayer.setOnCompletionListener((mediaPlayer) -> next());
        mediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
            mediaPlayer.reset();
            next();
            return false;
        });
        mediaPlayer.setOnPreparedListener((mediaPlayer) -> {
            if (isPreparing()) {
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 将歌曲添加到播放列表中并播放
     *
     * @param music
     */
    public void addAndPlay(MusicData music) {
        if (music == null) return;
        int position = AppContant.PlayContant.addMusicToPlayList(music);
        play(position, music);
    }

    /**
     * 播放当前播放列表中poition中歌曲
     *
     * @param position
     */
    public void play(int position) {
        if (position >= 0 && AppContant.PlayContant.getCurrentPlaySize() > position) {
            Log.e("TAG", "play: ----------------> " + position);
            AppContant.PlayContant.setCurrentPlayIndex(position);
            play(position, AppContant.PlayContant.getCurrentPlayData());
        }
    }

    public void play(int index, MusicData music) {
        if (index == -1 || music == null || StringUtils.isEmpty(music.getDataFilePath())) return;
        Log.e("TAG", "play: -------------->" + music.getMusicName());
        AppContant.PlayContant.setCurrentPlayIndex(index);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(music.getDataFilePath());
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show("当前歌曲无法播放");
            return;
        }
        state=STATE_PREPARING;
        mediaPlayer.prepareAsync();
        performPlayEvent(music);
    }

    private void performPlayEvent(MusicData music) {
        state = STATE_PREPARING;
        for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners) {
            listener.onPlayChange(music);
        }
        PlayNotifier.get().showPlay(music);
        AudioMediaSessionManager.getInstance().updateMetaData(music);
        AudioMediaSessionManager.getInstance().updatePlaybackState();
    }

    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getLastPlayPosition());
        }
    }

    public void next() {
        if (AppContant.PlayContant.currentPlayList.isEmpty()) {
            return;
        }
        int index = getNextPosition();
        play(index);
    }

    public void previous() {
        if (AppContant.PlayContant.currentPlayList.isEmpty()) {
            return;
        }
        int index = getPrePosition();
        play(index);
    }

    public void startPlayer() {
        if (!isPreparing() && !isPausing())
            return;
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mUpdatePlayProRunnable);
            this.mContext.registerReceiver(audioBecomingNoisy, noisyFilter);
            for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners) {
                listener.onPlayStart();
            }
            PlayNotifier.get().showPlay(getPlayMusic());
            AudioMediaSessionManager.getInstance().updatePlaybackState();
        }
    }

    public void pausePlayer() {
        pausePlayer(true);
    }

    public void pausePlayer(boolean abandonAudioFocus) {
        if (!isPlaying()) {
            return;
        }
        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mUpdatePlayProRunnable);
        PlayNotifier.get().showPause(getPlayMusic());
        AudioMediaSessionManager.getInstance().updatePlaybackState();
        this.mContext.unregisterReceiver(audioBecomingNoisy);
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus();
        }
        for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners) {
            listener.onPlayPause();
        }
    }

    private void stopPlayer() {
        if (isIdle()) {
            return;
        }
        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    private int getNextPosition() {
        return PlayUtils.getNextPlayIndex(getPlayMode(), getCurrPlayIndex(), getPlayListSize(), true);
    }

    private int getPrePosition() {
        return PlayUtils.getNextPlayIndex(getPlayMode(), getCurrPlayIndex(), getPlayListSize(), false);
    }

    private Runnable mUpdatePlayProRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (onMediaPlayerEventChanagerListener listener : playerEventChanagerListeners) {
                    listener.updatePlayProgress(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public MusicData getPlayMusic() {
        return AppContant.PlayContant.getCurrentPlayData();
    }

    public void addMediaPlayerEventChanagerListener(onMediaPlayerEventChanagerListener listeners) {
        if (listeners != null && !playerEventChanagerListeners.contains(listeners))
            playerEventChanagerListeners.add(listeners);
    }

    public void removeMediaPlayerEventChanagerListener(onMediaPlayerEventChanagerListener listeners) {
        if (listeners != null)
            playerEventChanagerListeners.remove(listeners);
    }

    public void setPlayMode(int playModel) {
        if (playModel >= 0 && playModel <= 2)
            SPUtils.putIntValue(this.mContext, AppContant.MusicPlayMode.PLAY_MODEL_KEY, playModel);
    }

    public int getPlayMode() {
        return SPUtils.getIntValue(this.mContext, AppContant.MusicPlayMode.PLAY_MODEL_KEY, AppContant.MusicPlayMode.PLAY_MODE_ORDER);
    }

    public int getCurrPlayIndex() {
        return AppContant.PlayContant.getCurrentPlayIndex();
    }

    public int getPlayListSize() {
        return AppContant.PlayContant.getCurrentPlaySize();
    }

    private int getLastPlayPosition() {
        return SPUtils.getIntValue(this.mContext, AppContentKey.INSTANCE.getLAST_PLAY_INDEX(), 0);
    }


}
