package com.jokingsun.oilfairy.widget.service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jokingsun.oilfairy.R;
import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.jokingsun.oilfairy.data.local.simple.ApplicationUserSp;
import com.jokingsun.oilfairy.ui.controller.MainActivity;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

/**
 * @author cfd058
 */
public class AppMessageService extends FirebaseMessagingService {

    private String comingCallUserId = "";
    private int notificationId = 0;
    private int requestPendingCode = 0;

    public AppMessageService() {
        super();
    }

    @Override
    public void onNewToken(@NotNull String token) {
        super.onNewToken(token);
        Logger.i("onNewToken:" + token);
        saveToken(token);
    }

    private void saveToken(String token) {
        ApplicationUserSp.putString(AppConstant.SP_KEY_FCM_TOKEN, token);
        // Sending new token to AppsFlyer
        //AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), token);
        // the rest of the code that makes use of the token goes in this method as well
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void sendNotification(RemoteMessage remoteMessage, Bitmap bitmap) {

        String type = remoteMessage.getData().get("type");
        String title = remoteMessage.getData().get("title");
        String content = remoteMessage.getData().get("body");

        Intent intent = new Intent(this, MainActivity.class);

        try {
            Bundle bundle = new Bundle();

            if (type != null) {
                bundle.putString("type", type);
                bundle.putString("link", remoteMessage.getData().get("link"));
            }

            //推播導引路徑
            String routeSign = remoteMessage.getData().get("route_sign");
            if (routeSign != null && !routeSign.isEmpty()) {
                bundle.putString("routeSign", routeSign);
            }

            //用來告訴後端是否要記錄這則推播是否有被點擊
            String msgId = remoteMessage.getData().get("msg_id");
            if (msgId != null && !msgId.isEmpty()) {
                bundle.putString("msgId", msgId);
                ApplicationUserSp.putString("msgId", msgId);
            }

            intent.putExtras(bundle);

        } catch (Exception e) {
            Logger.d("包裝推播資料有誤");
        }

        //將推播參數包傳給 LauncherActivity，LauncherActivity會再傳給 MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                requestPendingCode++, intent, PendingIntent.FLAG_IMMUTABLE);

        //CHANNEL_ID and CHANNEL_NAME for android os用
        final String CHANNEL_ID = "cfd_fcm_channel_id";
        final String CHANNEL_NAME = "淘妹推播通知";

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setSound(getNotificationSoundUri(true))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setVibrate(new long[]{1000, 1000})
                        .setContentIntent(pendingIntent);

        if (bitmap != null) {
            notificationBuilder.setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // id：渠道 id，每個 package 中應該是唯一的，如果太長，該值可能被截斷
                // name：用戶可見的渠道名稱，可重命名，如果太長，該值可能被截斷
                // importance：用於表示渠道的重要程度。這可以控制發佈到此頻道的中斷通知的方式

                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(getNotificationSoundUri(true), attributes);
                notificationManager.createNotificationChannel(channel);
            }

            /* ID of notification */
            notificationManager.notify(notificationId++, notificationBuilder.build());
        }
    }

    /**
     * 取得指定通知音訊，若無指定，則用各機型預設音效
     */
    private Uri getNotificationSoundUri(boolean isCustom) {
        Uri notificationSoundUri;

        if (isCustom) {
            notificationSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + this.getPackageName() + "/raw/messagevoice");
        } else {
            notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        }
        return notificationSoundUri;
    }


}

