package com.striketec.mobile.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.striketec.mobile.R;
import com.striketec.mobile.StriketecApp;
import com.striketec.mobile.activity.SplashActivity;
import com.striketec.mobile.activity.tabs.MainActivity;
import com.striketec.mobile.util.AppConst;
import com.striketec.mobile.util.DatesUtil;

import java.util.Map;

/**
 * Created by Qiang on 10/14/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Super";//MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        notificationUtils = new NotificationUtils(getApplicationContext());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        }

        String notificationMessage = "You received new notification";

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationMessage = remoteMessage.getNotification().getBody();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())){
            if(StriketecApp.isLoggedin) {
                Intent pushintent = new Intent(AppConst.PUSH_NOTIFICATION);
                pushintent.putExtra(AppConst.PUSH_MESSAGE, notificationMessage);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushintent);
                notificationUtils.playNotificationSound();
            }
        }else {

            String title = "StrikeTec new notifiction";
            Intent resultIntent = null;
            if (StriketecApp.isLoggedin) {
                resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            }else {
                resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
            }

            showNotificationMessage(getApplicationContext(), title, notificationMessage, DatesUtil.getTimestamp(System.currentTimeMillis()), resultIntent);
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void sendNotificationWithActivity(String title, String messageBody, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + StriketecApp.mContext.getPackageName() + "/raw/notification");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

