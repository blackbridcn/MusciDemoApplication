package com.music;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.music.service.MediaService;
import com.music.widget.ObservableScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.scrollable)
    ObservableScrollView scrollable;
    @BindView(R.id.headerview)
    LinearLayout headerview;
    private MediaService.MediaPlayerBinder mediaPlayerBinder;
    private static String TAG = Main2Activity.class.getName();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mediaPlayerBinder = (MediaService.MediaPlayerBinder) iBinder;
            Log.e(TAG, "onServiceConnected: --------------------->");
           // String filePath = AppContant.PlayContant.musicData.get(0).getDataFilePath();
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
        ButterKnife.bind(this);
        Log.e(TAG, "onCreate: ----->");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main2Activity.this.finish();
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                //  startActivity(intent);
                //bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        init();
    }

    private void init() {
         ViewGroup.LayoutParams layoutParams = headerview.getLayoutParams();
        int height = headerview.getLayoutParams().height;
        scrollable.setOnObservableScrollChangeListener((crollView, x, y, oldx, oldy) -> {
            Log.e(TAG, "init: ------------> y :"+y );
            headerview.setPadding(0,-(height-y),0,0);

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ----->");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ----->");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ----->");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ----->");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ----->");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ----->");
    }
}
