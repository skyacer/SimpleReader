package com.rssreader.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.rssreader.app.commons.util.ThreadUtil;
import com.rssreader.app.ui.R;
import com.rssreader.app.ui.activity.MainActivity;

/**
 * Created by LuoChangAn on 16/5/7.
 */
public class PushService extends Service{
    private static boolean isDestroy = false;
    private int mCount = 0;
    private MyBinder mBinder;

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder{
        public PushService getService(){
            return PushService.this;
        }
    }

    public void cancelPush(){
        isDestroy = true;
    }

    public void startPush(){
        isDestroy = false;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i("push_service", "push_service onCreate");
        mBinder = new MyBinder();
        ThreadUtil.runOnAnsy(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!isDestroy){
                        Thread.sleep(30000);
                        Log.i("push_service", "push_service foreach");

                        //获取到通知管理器
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        //定义内容
                        int notificationIcon = R.drawable.app_logo;
                        CharSequence notificationTitle = "猜你喜欢--易悦阅读器头条文章";
                        long when = System.currentTimeMillis();

                        Notification notification = new Notification(notificationIcon, notificationTitle, when);

                        notification.defaults = Notification.DEFAULT_ALL;

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                        notification.setLatestEventInfo(getApplicationContext(), "沈阳当地新闻", "第七批沈阳市爱国主义教育基地命名挂牌", pendingIntent);

                        if (notification != null) {
                            Log.e("notifacation", "notifacation is ok");
                            mNotificationManager.notify(1000 + mCount, notification);
                        }
                        mCount++;

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        },"push_thread");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent,int startId)
    {
        super.onStart(intent, startId);
        Log.i("push_service", "push_service start");

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i("push_service", "push_service destroy");
    }

}
