package com.music.AppContant;

import com.music.javabean.MusicData;

import java.util.ArrayList;
import java.util.List;

public interface AppContant {
    class PlayContant {

        public PlayContant() {
        }

        //播放列表
        public static ArrayList<MusicData> currentPlayList = new ArrayList<MusicData>();
        private static int currentPlayIndex = -1;

        public static void setCurrentPlayData(List<MusicData> playist) {
            if (playist != null && !playist.isEmpty()) {
                currentPlayList.clear();
                currentPlayList.addAll(playist);
            }
        }

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

        /*public static int getMesuicDataByIndext(int indext) {

        }*/

    }

    class NotifierContant {
        public static final  String EXTRA_NOTIFICATION = "me.wcy.music.notification";
        public static final String MUSIC_LIST_TYPE = "music_list_type";
        public static final String TING_UID = "ting_uid";
        public static final  String MUSIC = "music";
    }
}
