package com.smap.group29.getmoving.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {
    //inspired by https://codinginflow.com/tutorials/android/notifications-notification-channels/part-1-notification-channels
    public static final String CHANNEL_1_ID = "channel1";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotifications();
    }

    private void createNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }

}

