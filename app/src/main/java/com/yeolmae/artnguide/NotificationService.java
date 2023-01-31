package com.yeolmae.artnguide;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yeolmae.R;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "ART_GUIDE";
    private static final String CHANNEL_ID_GROUP = "ART_GUIDE_GROUP";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//    Log.e("getData", String.valueOf(remoteMessage.getData()));
        // Check if message contains a data payload.

        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        Log.d(TAG, "getNotification(): " + remoteMessage.getNotification());

//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Uri imgLink = remoteMessage.getNotification().getImageUrl();
            sendNotification(title, body, imgLink);
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
    }


    private void sendNotification(String title, String body, Uri imgLink) {
        int notificationId = new Random().nextInt();
        try {
            RemoteViews collapsedViews = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
            collapsedViews.setTextViewText(R.id.title, title);
            collapsedViews.setTextViewText(R.id.subTitle, body);

            RemoteViews expandedViews = new RemoteViews(getPackageName(), R.layout.notification_expanded);
            expandedViews.setTextViewText(R.id.title, title);
            expandedViews.setTextViewText(R.id.subTitle, body);

            if (imgLink != null) {
                Log.d(TAG, "imgLink: " + imgLink);

                URL url = new URL(imgLink + "");
                InputStream in = url.openStream();
                Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                expandedViews.setImageViewBitmap(R.id.imgBody, imageBitmap);
//                Picasso.get().load(imgLink).into(expandedViews, R.id.imgBody, null);
            }

            Notification notification = new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setCustomContentView(collapsedViews).setCustomBigContentView(expandedViews).setGroup(CHANNEL_ID_GROUP).build();
//                    .setGroup(CHANNEL_ID_GROUP)
//                    .setContentTitle(title)
//                    .setContentText(body)


            NotificationManagerCompat manager = NotificationManagerCompat.from(this.getApplicationContext());
            manager.notify(notificationId, notification);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, importance);
                channel.setDescription(body);
                NotificationManager notificationManagers = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManagers.createNotificationChannel(channel);
            }

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mp = MediaPlayer.create(this.getApplicationContext(), alarmSound);
            mp.start();

        } catch (Exception exception) {
            Log.e("Ex", String.valueOf(exception));
        }
    }
}
