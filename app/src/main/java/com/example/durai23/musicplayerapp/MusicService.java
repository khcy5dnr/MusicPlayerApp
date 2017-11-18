package com.example.durai23.musicplayerapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Durai23 on 15/11/2017.
 */

public class MusicService extends Service {

    NotificationCompat.Builder musicNotification;
    NotificationManager notificationManager;
    static final int NOTIFICATION_ID = 2;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //notification starts when the main activity is stopped.
        musicNotification = new NotificationCompat.Builder(this);
        musicNotification.setAutoCancel(true);

        musicNotification.setSmallIcon(R.drawable.notification_icon);
        musicNotification.setContentTitle("MUSIC PLAYER");
        musicNotification.setContentText(intent.getExtras().getString("songName"));
        musicNotification.setOngoing(true);

        Intent intentNotification = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentNotification,PendingIntent.FLAG_UPDATE_CURRENT);
        musicNotification.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,musicNotification.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
