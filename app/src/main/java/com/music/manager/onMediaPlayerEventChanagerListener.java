package com.music.manager;

import com.music.javabean.MusicData;

public interface onMediaPlayerEventChanagerListener {

    /**
     * 切换歌曲
     */
    void onPlayChange(MusicData music);

    /**
     * 继续播放
     */
    void onPlayStart();

    /**
     * 暂停播放
     */
    void onPlayPause();

    /**
     * 更新进度
     */
    void updatePlayProgress(int progress);

    /**
     * 缓冲百分比
     */
    void onBufferingUpdate(int percent);
}
