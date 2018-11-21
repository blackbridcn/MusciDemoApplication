package com.music.application;

import android.app.Application;
import android.content.Context;

import com.music.manager.AudioPlayer;
import com.music.manager.CoverLoader;

public class AppApplication extends Application {
    private static Context context;
    private static AppApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        CoverLoader.getInstance().init(this);
        AudioPlayer.initAudioManager(this);

        instance = this;
        //获取Context
        context = getApplicationContext();
    }
    public synchronized static AppApplication instance() {
        if (instance != null) return instance;
        throw new RuntimeException("Application not instantiated yet");
    }


    //返回 全局的Context
    public static Context getContextObject() {
        return context;
    }
}
