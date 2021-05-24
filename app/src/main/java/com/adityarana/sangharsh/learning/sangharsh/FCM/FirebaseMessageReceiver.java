package com.adityarana.sangharsh.learning.sangharsh.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.adityarana.sangharsh.learning.sangharsh.MainActivity;
import com.adityarana.sangharsh.learning.sangharsh.R;
import com.adityarana.sangharsh.learning.sangharsh.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title, String body) {
        Intent intent
                = new Intent(this, SplashActivity.class);
        String channel_id = "notification_channel";

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        builder = builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}
