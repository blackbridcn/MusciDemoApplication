package com.music.manager;

import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.music.AppContant.AppContant;
import com.music.javabean.MusicData;
import com.music.service.MediaService;

/**
 * MediaSession管理类
 * MediaSession 框架是 Google  在 level 21 （Android 5.0）推出专门解决媒体播放时界面和服务通讯问题，
 * MediaSessionCompat位于android/support/v4/media/session包下，
 * 主要是用于替代Android L 之后推出的MessionSession
 */
public class AudioMediaSessionManager {
    private static final String TAG = AudioMediaSessionManager.class.getName();
    private static final long MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY
            | PlaybackStateCompat.ACTION_PAUSE
            | PlaybackStateCompat.ACTION_PLAY_PAUSE
            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            | PlaybackStateCompat.ACTION_STOP
            | PlaybackStateCompat.ACTION_SEEK_TO;

    private MediaService playService;
    private MediaSessionCompat mediaSession;

    public static AudioMediaSessionManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioMediaSessionManager instance = new AudioMediaSessionManager();
    }

    private AudioMediaSessionManager() {
    }

    public void init(MediaService playService) {
        this.playService = playService;
        setupMediaSession();
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(playService, TAG);
        //handles_transport_controls  handles_media_buttons 传递control 事件
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setCallback(callback);
        mediaSession.setActive(true);
    }

    /**
     * 更新一下MediaSessionCompat 中的MediaMetadataCompat
     * @param music
     */
    public void updateMetaData(MusicData music) {
        if (music == null) {
            mediaSession.setMetadata(null);
            return;
        }
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getMusicName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, music.getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, music.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, CoverLoader.getInstance().loadThumb(music));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, AppContant.PlayContant.currentPlayList.size());
        }
        mediaSession.setMetadata(builder.build());
    }

    /**
     * 更新播放状态，播放/暂停/拖动进度条时调用
     */
    public void updatePlaybackState() {
        int state = (AudioPlayer.getInstance().isPlaying() ||
                AudioPlayer.getInstance().isPreparing()) ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        mediaSession.setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(MEDIA_SESSION_ACTIONS)
                        .setState(state, AudioPlayer.getInstance().getAudioPosition(), 1)
                        .build());
    }


    /**
     * 释放MediaSession，退出播放器时调用
     */
    public void release() {
        mediaSession.setCallback(null);
        mediaSession.setActive(false);
        mediaSession.release();
    }


    /**
     * API 21 /5.0之后
     * MediaSessionCompat.Callback
     * 包括 耳机物理按键的回调监听
     */
    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            //  AudioPlayer.get().playPause();
        }

        @Override
        public void onPause() {
            // AudioPlayer.get().playPause();
        }

        @Override
        public void onSkipToNext() {
            // AudioPlayer.get().next();
        }

        @Override
        public void onSkipToPrevious() {
            //    AudioPlayer.get().prev();
        }

        @Override
        public void onStop() {
            //   AudioPlayer.get().stopPlayer();
        }

        @Override
        public void onSeekTo(long pos) {
            //   AudioPlayer.get().seekTo((int) pos);
        }
    };
}
