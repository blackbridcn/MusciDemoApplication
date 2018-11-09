package com.music.manager;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.music.service.MediaService;

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
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mediaSession.setCallback(callback);
        mediaSession.setActive(true);
    }




    /**
     * API 21 /5.0之后 耳机物理按键的回调监听  MediaSessionCompat.Callback
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
