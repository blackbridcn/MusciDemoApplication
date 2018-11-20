package com.music.manager;

import com.music.AppContant.AppContant;

import java.util.Random;

public class PlayUtils {
    /**
     * @param palyMode
     * @param currentIndex
     * @param playListSize
     * @param isNext
     * @return
     */
    public static int getNextPlayIndex(int palyMode, int currentIndex, int playListSize, boolean isNext) {
        if (playListSize <= 0) return -1;
        int next = currentIndex;
        switch (palyMode) {
            case AppContant.MusicPlayMode.PLAY_MODE_ORDER://顺序播放
                if (isNext) {//向后
                    if (currentIndex == playListSize - 1)
                        next = 0;
                    else
                        ++next;
                } else {//向前
                    if (currentIndex == 0)
                        next = playListSize - 1;
                    else
                        --next;
                }
                break;
            case AppContant.MusicPlayMode.PLAY_MODE_RANDOM://随机播放
                if (playListSize != 1) {
                    next = generateRandom(playListSize, currentIndex);
                    if (next == currentIndex)
                        return getNextPlayIndex(palyMode, currentIndex, playListSize, isNext);
                }
                break;
            case AppContant.MusicPlayMode.PLAY_MODE_SINGLE://单曲循环

                break;
        }
        return next;
    }

    public static int generateRandom(int num, int index) {
        Random ran = new Random();
        int ranNum = ran.nextInt(num);
        if (num > 0) {
            if (ranNum == index) {
                ranNum = generateRandom(num, index);
            }
        }
        return ranNum;
    }
}
