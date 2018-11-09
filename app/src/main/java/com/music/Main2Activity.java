package com.music;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.music.AppContant.MusicContant;
import com.music.service.MediaService;

public class Main2Activity extends AppCompatActivity {
    private MediaService.MediaPlayerBinder mediaPlayerBinder;
    private static String TAG = Main2Activity.class.getName();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaPlayerBinder = (MediaService.MediaPlayerBinder) iBinder;
            Log.e(TAG, "onServiceConnected: --------------------->");
            String filePath = MusicContant.MusicPlayData.musicData.get(0).getDataFilePath();
           // mediaPlayerBinder.play(filePath);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "onServiceDisconnected: ----------------------------> ");
            mediaPlayerBinder = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.e(TAG, "onCreate: ----------------------》" );
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, MediaService.class);
                bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
