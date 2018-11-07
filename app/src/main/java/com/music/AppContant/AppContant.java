package com.music.AppContant;

import com.music.javabean.MusicData;

import java.util.List;

public class AppContant {
    //播放列表
    private static List<MusicData> currentPlayist;
    public void  setCurrentPlayData(List<MusicData> currentPlayist){
        this.currentPlayist=currentPlayist;
    }



}
