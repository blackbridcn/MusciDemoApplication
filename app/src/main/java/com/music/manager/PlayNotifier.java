package com.music.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.music.AppContant.AppContant;
import com.music.MainActivity;
import com.music.R;
import com.music.javabean.MusicData;
import com.music.receiver.StatusBarReceiver;
import com.music.service.MediaService;
import com.music.utils.FileUtils;

public class PlayNotifier {
    private static final int NOTIFICATION_ID = 0x111;
    private MediaService playService;
    private NotificationManager notificationManager;

    public static PlayNotifier get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static PlayNotifier instance = new PlayNotifier();
    }

    private PlayNotifier() {
    }

    public void init(MediaService playService) {
        this.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(MusicData music) {
        if (music == null) {
            return;
        }
      //  playService.startForeground(NOTIFICATION_ID, buildNotification(playService, music, true));
    }

    public void showPause(MusicData music) {
        if (music == null) {
            return;
        }
      //  playService.stopForeground(false);
        //notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, music, false));
    }

    private Notification buildNotification(Context context, MusicData music, boolean isPlaying) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppContant.NotifierContant.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomContentView(getRemoteViews(context, music, isPlaying));
        return builder.build();
    }

    private RemoteViews getRemoteViews(Context context, MusicData music, boolean isPlaying) {
        String title = music.getMusicName();
        String subtitle = FileUtils.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        Bitmap cover = CoverLoader.getInstance().loadThumb(music);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, music.getArtist());

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_play_pause, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, playPendingIntent);

        Intent previousIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        previousIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 2, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_play_bar_pre, getNextIconRes(isLightNotificationTheme));
        remoteViews.setOnClickPendingIntent(R.id.iv_play_bar_pre, previousPendingIntent);

        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.iv_next, getNextIconRes(isLightNotificationTheme));
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent);
        return remoteViews;
    }

    private boolean isLightNotificationTheme(Context context) {
        return true;
       /* int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);*/
    }


    private int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
        if (isPlaying) {
            return isLightNotificationTheme
                    ? R.drawable.ic_status_bar_pause_dark_selector
                    : R.drawable.ic_status_bar_pause_light_selector;
        } else {
            return isLightNotificationTheme
                    ? R.drawable.ic_status_bar_play_dark_selector
                    : R.drawable.ic_status_bar_play_light_selector;
        }
    }

    private int getNextIconRes(boolean isLightNotificationTheme) {
        return isLightNotificationTheme
                ? R.drawable.ic_status_bar_next_dark_selector
                : R.drawable.ic_status_bar_next_light_selector;
    }
}
