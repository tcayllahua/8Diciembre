package com.valdemar.utilidades.sounds;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.valdemar.R;


public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    private MediaPlayer player;
    private SharedPreferences prefs = null;

    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.backgroundsoundone);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

        prefs = getSharedPreferences("com.valdemar.utilidades.sounds", MODE_PRIVATE);

        prefs.edit().putBoolean("firstrun", true).commit();

    }
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (prefs.getBoolean("firstrun", true)) {

            player.start();

        }

        return START_STICKY;
    }


    public void onStart(Intent intent, int startId) {
        // TO DO
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;

    }

    public void onStop() {
        player.stop();
        player.release();
    }

    public void onPause() {
        player.stop();
        player.release();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }


}