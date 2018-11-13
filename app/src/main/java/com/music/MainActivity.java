package com.music;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.AppContant.AppContant;
import com.music.localmusicTask.LocalMusicFragment;
import com.music.service.MediaService;
import com.music.utils.LocalMusicUitls;

public class MainActivity extends AppCompatActivity {
    private MediaService.MediaPlayerBinder mediaPlayerBinder;
    private static String TAG = MainActivity.class.getName();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaPlayerBinder = (MediaService.MediaPlayerBinder) iBinder;
            Log.e("TAG", "onServiceConnected: --------------------->");
            String filePath = AppContant.PlayContant.musicData.get(0).getDataFilePath();
            //mediaPlayerBinder.playMusic(filePath);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "onServiceDisconnected: ----------------------------> ");
            mediaPlayerBinder = null;
        }
    };
    private NavigationView navigationView;
    private ImageView albumart;
    private TextView songtitle, songartist;
    private LocalMusicFragment localMusicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalMusicUitls.getInstance().getMusicList(this);
        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        this.findViewById(R.id.container_frame);

        albumart = (ImageView) header.findViewById(R.id.album_art);
        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        localMusicFragment = new LocalMusicFragment();
        transaction.add(R.id.container_frame, localMusicFragment, LocalMusicFragment.class.getName()).show(localMusicFragment).commit();
    }
}
