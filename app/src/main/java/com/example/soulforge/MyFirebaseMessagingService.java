package com.example.soulforge;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.soulforge.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "my_channel_01";
    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    private CharSequence name = "my_channel_01";// The user-visible name of the channel.
    private int importance = NotificationManager.IMPORTANCE_HIGH;
    private String DEFAULT_BROADCAST = "NOTIFICATION_BROADCAST";

    private String title;
    private String message;
    private String screen;
    private String id;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

      /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_person);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_person)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(notificationSoundUri);

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        notificationManager.notify(notificationID, notificationBuilder.build());
//        if (remoteMessage.getNotification() != null) {
//            title =(remoteMessage.getData().get("title"));
//
//            message =(remoteMessage.getData().get("message"));
//        }
//
//        if (title != null && title.length() > 0) {
//            sendNotification(title, message, screen, id);
//        } else if (remoteMessage.getData().size() > 0) {
//            String data = remoteMessage.getData().toString();
//            try {
//                JSONObject data_OBJ = new JSONObject(data);
//                screen = data_OBJ.optString("screen");
//                id = data_OBJ.optString("id");
//
//                sendNotification(title, message, screen, id);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        } else if (remoteMessage.getNotification() != null) {
//            sendNotification(title, message, screen, id);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    private void sendNotification(String title, String messageBody, String screen, String id) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("screen", screen);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(title)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationBuilder.setContentIntent(pendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID); // Channel ID
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        int notifyID = 0;
        notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());

        showAlertDialog("", "");
    }


    private void showAlertDialog(String title, String message) {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am
                .getRunningTasks(1);
        Log.d("current task :", "CURRENT Activity ::"
                + taskInfo.get(0).topActivity.getClass().getSimpleName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        if (componentInfo.getPackageName().equalsIgnoreCase(
                getPackageName())) {
            Intent broadcast = new Intent(DEFAULT_BROADCAST);
            broadcast.putExtra("title", title);
            broadcast.putExtra("message", message);

            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
        }
    }
}
