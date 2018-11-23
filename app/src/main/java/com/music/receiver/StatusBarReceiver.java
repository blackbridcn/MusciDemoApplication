package com.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.music.manager.AudioPlayer;

public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "com.music.STATUS_BAR_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_NEXT = "next";
    public static final String EXTRA_PLAY_PAUSE = "play_pause";
    public static final String EXTRA_PREVIOUS = "previous";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        String extra = intent.getStringExtra(EXTRA);
        if (TextUtils.equals(extra, EXTRA_NEXT)) {
            AudioPlayer.getInstance().next();
        } else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            AudioPlayer.getInstance().playPause();
        }else if(TextUtils.equals(extra, EXTRA_PREVIOUS)){
            AudioPlayer.getInstance().previous();
        }
    }
}
