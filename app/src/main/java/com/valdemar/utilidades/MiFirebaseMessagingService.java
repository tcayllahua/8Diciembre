package com.valdemar.utilidades;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.valdemar.R;
import com.valdemar.SplashActivity;

public class MiFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "SPOOK";
    public static final String NOTIFICATION_CHANNEL_ID = "News";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Log.d(TAG,"Mensaje recibido de: "+ from);

        if (remoteMessage.getNotification() !=null){
            Log.d(TAG, "Notificación; " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "title; " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "url; " + remoteMessage.getNotification().getImageUrl());
            mostrarNotificacion("SPOOK",
                    remoteMessage.getNotification().getBody());

        }
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG,"Data: "+ remoteMessage.getData());
        }

    }

    private void mostrarNotificacion( String title, String body){


        Intent intent = new Intent (this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SPOOK")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);


        int mNotificationId = (int) System.currentTimeMillis();
       // NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "News", importance);

            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotifyMgr.createNotificationChannel(notificationChannel);
        }

        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());




    }
}
