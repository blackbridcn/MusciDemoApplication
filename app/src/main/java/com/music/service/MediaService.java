package com.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MediaService extends Service {
    private static String TAG = MediaService.class.getName();

    //初始化MediaPlayer
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private MediaPlayerBinder mediaPlayerBinder = new MediaPlayerBinder(this);

    public MediaService() {
        Log.e("TAG", "MediaService: ---------------------------->");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ---------------------------->");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: -------------------------->" );
        return mediaPlayerBinder;
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void play(String filepath) {
        try {
            if (isPlaying()) {
                mMediaPlayer.pause();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filepath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "play: --------------------->");
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * 播放音乐
     */
    public void playMusic() {
        if (!isPlaying()) {
            //如果还没开始播放，就开始
            mMediaPlayer.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pauseMusic() {
        if (isPlaying()) {
            //如果还没开始播放，就开始
            mMediaPlayer.pause();
        }
    }
    /**
     * 获取歌曲长度
     **/
    public int getDuration() {

        return mMediaPlayer.getDuration();
    }

    /**
     * 获取播放位置
     */
    public int getCurrentPosition() {

        return mMediaPlayer.getCurrentPosition();
    }
    /**
     * 播放指定位置
     */
    public void seekToPositon(int msec) {
        mMediaPlayer.seekTo(msec);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: -----------------------------> ");
        return super.onStartCommand(intent, flags, startId);
    }

    public class MediaPlayerBinder extends Binder {
        private MediaService mediaService;

        public MediaPlayerBinder(MediaService mediaService) {
            this.mediaService = mediaService;
        }

        public void playMusic(String filepth) {
            Log.e(TAG, "play: ---------------------->filepth ：" + filepth);
            this.mediaService.play(filepth);
        }
        public void pauseMusic(){
            this.mediaService.pauseMusic();
        }
    }
}
