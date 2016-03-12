package com.x91tec.appshelf.components.notifycation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Created by oeager on 16-3-3.
 */
public final class NotificationGenerator {


    public static NotificationCompat.Builder generateCompatBuilder(Context context, PendingIntent pendingIntent, int smallIcon, String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //        // 如果当前Activity启动在前台，则不开启新的Activity。
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode
//        // 将Intent封装进PendingIntent中，点击通知的消息后，就会启动对应的程序
//        PendingIntent pIntent = PendingIntent.getActivity(mContext,
//                requestCode, intent, FLAG);

        builder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        builder.setSmallIcon(smallIcon);// 设置顶部状态栏的小图标
        builder.setTicker(ticker);// 在顶部状态栏中的提示信息

        builder.setContentTitle(title);// 设置通知中心的标题
        builder.setContentText(content);// 设置通知中心中的内容
        builder.setWhen(System.currentTimeMillis());

		/*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
		 * 不设置的话点击消息后也不清除，但可以滑动删除
		 */
        builder.setAutoCancel(true);
        // 将Ongoing设为true 那么notification将不能滑动删除
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
		 * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
		 */
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
		 * Notification.DEFAULT_SOUND：系统默认铃声。
		 * Notification.DEFAULT_VIBRATE：系统默认震动。
		 * Notification.DEFAULT_LIGHTS：系统默认闪光。
		 * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
		 */
        int defaults = 0;

        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }

        builder.setDefaults(defaults);
        return builder;
    }

    public static Notification.Builder generateBuilder(Context context, PendingIntent pendingIntent, int smallIcon, String ticker, boolean sound, boolean vibrate, boolean lights) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(smallIcon);


        builder.setTicker(ticker);
        builder.setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_MAX);
        }

        int defaults = 0;

        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }

        builder.setDefaults(defaults);
        return builder;
    }
    public static void notifySimpleNotification(Context context,int notificationId,PendingIntent pendingIntent, int smallIcon,String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights){
        NotificationCompat.Builder builder =generateCompatBuilder(context,pendingIntent,smallIcon,ticker,title,content,sound,vibrate,lights);
        Notification notification = builder.build();
        sendNotification(context, notificationId, notification);
    }

    public static void notifyMailBoxNotification(Context context,int notificationId,PendingIntent pendingIntent, int smallIcon, int largeIcon, ArrayList<String> messageList,
                                                 String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights){
        NotificationCompat.Builder builder = generateCompatBuilder(context,pendingIntent,smallIcon,ticker,title,content,sound,vibrate,lights);
        // 将Ongoing设为true 那么notification将不能滑动删除
        //cBuilder.setOngoing(true);

        /**
         // 删除时
         Intent deleteIntent = new Intent(mContext, DeleteService.class);
         int deleteCode = (int) SystemClock.uptimeMillis();
         // 删除时开启一个服务
         PendingIntent deletePendingIntent = PendingIntent.getService(mContext,
         deleteCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         cBuilder.setDeleteIntent(deletePendingIntent);

         **/
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), largeIcon);
        builder.setLargeIcon(bitmap);

        builder.setDefaults(Notification.DEFAULT_ALL);// 设置使用默认的声音
        //cBuilder.setVibrate(new long[]{0, 100, 200, 300});// 设置自定义的振动
        builder.setAutoCancel(true);
        // builder.setSound(Uri.parse("file:///sdcard/click.mp3"));

        // 设置通知样式为收件箱样式,在通知中心中两指往外拉动，就能出线更多内容，但是很少见
        //cBuilder.setNumber(messageList.size());
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (String msg : messageList) {
            inboxStyle.addLine(msg);
        }
        inboxStyle.setSummaryText("[" + messageList.size() + "条]" + title);
        builder.setStyle(inboxStyle);
        Notification notification = builder.build();
        sendNotification(context,notificationId,notification);

    }

    public static void notifyCustomUINotification(Context context,int notificationId,RemoteViews remoteViews, PendingIntent pendingIntent, int smallIcon, String ticker, boolean sound, boolean vibrate, boolean lights){
        NotificationCompat.Builder builder = generateCompatBuilder(context, pendingIntent, smallIcon, ticker, null, null, sound, vibrate, lights);
        Notification notification = builder.build();
        notification.contentView = remoteViews;
        // 发送该通知
        sendNotification(context,notificationId,notification);
    }
    public static void notifyMultipleLineNotification(Context context,int notificationId,PendingIntent pendingIntent, int smallIcon, String ticker,
                                                  String title, String content, boolean sound, boolean vibrate, boolean lights){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Notification.Builder builder =generateBuilder(context,pendingIntent, smallIcon, ticker, true, true, false);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setPriority(Notification.PRIORITY_HIGH);
            Notification notification = new Notification.BigTextStyle(builder).bigText(content).build();
            sendNotification(context,notificationId,notification);
            return;
        }
        notifySimpleNotification(context, notificationId, pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);

    }

    public static void notifyProgressNotification(Context context,int notificationId,PendingIntent pendingIntent, int smallIcon,
                                                  String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights,int max,int progress){
        NotificationCompat.Builder builder = generateCompatBuilder(context, pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        builder.setProgress(max,progress,false);
        Notification notification = builder.build();
        sendNotification(context, notificationId, notification);
    }

    public static void notifyLargeImageNotification(Context context,int notificationId,PendingIntent pendingIntent, int smallIcon, String ticker,
                                                    String title, String content, int bigPic, boolean sound, boolean vibrate, boolean lights){
        NotificationCompat.Builder builder = generateCompatBuilder(context, pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        NotificationCompat.BigPictureStyle picStyle = new NotificationCompat.BigPictureStyle();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                bigPic, options);
        picStyle.bigPicture(bitmap);
        picStyle.bigLargeIcon(bitmap);
        builder.setContentText(content);
        builder.setStyle(picStyle);
        sendNotification(context,notificationId,builder.build());
    }

    public static void notifyDoubleButtonNotification(Context context,int notificationId,int smallIcon, int leftbtnicon, String lefttext, PendingIntent leftPendIntent, int rightbtnicon, String righttext, PendingIntent rightPendIntent, String ticker,
                                                      String title, String content, boolean sound, boolean vibrate, boolean lights){
        NotificationCompat.Builder builder = generateCompatBuilder(context,rightPendIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        builder.addAction(leftbtnicon,
                lefttext, leftPendIntent);
        builder.addAction(rightbtnicon,
                righttext, rightPendIntent);
        Notification notification = builder.build();
        sendNotification(context,notificationId,notification);

    }
    public static void notifyHeadUpNotification(Context context,int notificationId,PendingIntent pendingIntent, int smallIcon, int largeIcon,
                              String ticker, String title, String content, int leftbtnicon, String lefttext, PendingIntent leftPendingIntent, int rightbtnicon, String righttext, PendingIntent rightPendingIntent, boolean sound, boolean vibrate, boolean lights) {

        NotificationCompat.Builder builder =generateCompatBuilder(context,pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.addAction(leftbtnicon,
                    lefttext, leftPendingIntent);
            builder.addAction(rightbtnicon,
                    righttext, rightPendingIntent);
        }
        sendNotification(context,notificationId,builder.build());
    }


    public static void sendNotification(Context context, int notificationId, Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    public static void clearNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        if (id > 0) {
            notificationManager.cancel(id);
            return;
        }
        notificationManager.cancelAll();
    }
}
