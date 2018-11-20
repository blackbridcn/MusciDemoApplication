package com.music.AppContant;

import com.music.application.AppApplication;
import com.music.javabean.MusicData;
import com.music.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public interface AppContant {
    class PlayContant {

        public PlayContant() {
        }

        //播放列表
        public static List<MusicData> musicData = new ArrayList<>();
        //播放列表
        public static List<MusicData> currentPlayList = musicData;
        private static int currentPlayIndex = -1;

        public static void setCurrentPlayData(List<MusicData> playist) {
            if (playist != null && !playist.isEmpty()) {
                currentPlayList.clear();
                currentPlayList.addAll(playist);
            }
        }

        /**
         * 添加到播放列表中并返回indext
         *
         * @param data
         * @return
         */
        public static int addMusicToPlayList(MusicData data) {
            if (!currentPlayList.isEmpty()) {
                if (!currentPlayList.contains(data)) {
                    currentPlayList.add(data);
                    return currentPlayList.size() - 1;
                } else
                    return currentPlayList.indexOf(data);
            } else {
                currentPlayList.add(data);
                return 0;
            }
        }

        public static MusicData getCurrentPlayData() {
            if (currentPlayList != null && !currentPlayList.isEmpty()) {
                if (currentPlayIndex <= (currentPlayList.size() - 1)) {
                    return currentPlayList.get(currentPlayIndex);
                } else {
                    return null;
                }
            } else return null;
        }

        public static int getCurrentPlaySize() {
            if (currentPlayList != null) {
                return currentPlayList.size();
            } else return -1;
        }

        public static int getCurrentPlayIndex() {
            return currentPlayIndex;
        }

        public static void setCurrentPlayIndex(int index) {
            currentPlayIndex = index;
            SPUtils.putIntValue(AppApplication.getContextObject(),AppContentKey.INSTANCE.getLAST_PLAY_INDEX(),index);
        }

        /*public static int getMesuicDataByIndext(int indext) {

        }*/

    }

    /*
     * 音乐播放模式
     */
    class MusicPlayMode {
        public static final String PLAY_MODEL_KEY="play_model_key";
        // 顺序播放
        public static final int PLAY_MODE_ORDER = 0;
        // 随机播放
        public static final int PLAY_MODE_RANDOM = 1;
        // 单曲循环
        public static final int PLAY_MODE_SINGLE = 2;
        // 播放模式数组
        public static final int[] PLAY_MODE_ARRAY = {PLAY_MODE_ORDER, PLAY_MODE_RANDOM, PLAY_MODE_SINGLE};
        // 当前播放模式，默认播放模式为顺序播放
        public static int CURRENT_PLAY_MODE = PLAY_MODE_ARRAY[0];


        public static void swichPlayLoopMode(playModeChangeLisener lisener) {
            int max = PLAY_MODE_ARRAY.length - 1;
            if (CURRENT_PLAY_MODE == max) {
                CURRENT_PLAY_MODE = PLAY_MODE_ARRAY[0];
                if (lisener != null)
                    lisener.playModeChange(PLAY_MODE_ARRAY[max], PLAY_MODE_ARRAY[0]);
            } else {
                CURRENT_PLAY_MODE = CURRENT_PLAY_MODE + 1;
                if (lisener != null)
                    lisener.playModeChange(PLAY_MODE_ARRAY[CURRENT_PLAY_MODE - 1], PLAY_MODE_ARRAY[CURRENT_PLAY_MODE]);
            }

        }
    }

    interface playModeChangeLisener {
        void playModeChange(int lastMode, int curerntModel);
    }

    class NotifierContant {
        public static final String EXTRA_NOTIFICATION = "me.wcy.music.notification";
        public static final String MUSIC_LIST_TYPE = "music_list_type";
        public static final String TING_UID = "ting_uid";
        public static final String MUSIC = "music";
    }
}
