package com.sy.notificationlistener.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.sy.notificationlistener.MyApplication;
import com.sy.notificationlistener.notification.WeChatListener;

/**
 * Created by ASUS User on 2018/2/25.
 */
public class NotificationListener extends NotificationListenerService{
    private final String TAG = "NotificationListener";
    private final String WECHAT_PKG_NAME = "com.tencent.mm";
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "onNotifycationPosted");
        String strPgkName = sbn.getPackageName();
        Log.d(TAG, strPgkName);
        boolean bTest = TextUtils.equals(MyApplication.getApp().getPackageName(), strPgkName);
        boolean bWeChat = WECHAT_PKG_NAME.equals(strPgkName);

        if (bWeChat || bTest) {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            String strExtraTitle = extras.getString(Notification.EXTRA_TITLE, "");
            String strExtraText = extras.getString(Notification.EXTRA_TITLE, "");
            Log.d(TAG, strExtraTitle);
            Log.d(TAG, strExtraText);
            String strMsg = "";
            strMsg = strMsg + strPgkName + "\n";
            strMsg = strMsg + strExtraTitle + "\n";
            strMsg = strMsg + strExtraText;
            //if (bTest)
            {
                MyApplication.getApp().showMsg("接收到\n" + strMsg);
            }
            final WeChatListener.WeChatNotificationMsg msg = new WeChatListener.WeChatNotificationMsg();
            msg.mFriend = strExtraTitle;
            msg.mContent = strExtraText;
            MyApplication.getApp().runWorkerThread(new Runnable() {
                @Override
                public void run() {
                    WeChatListener.notifyMsg(msg);
                }
            }, 0);

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "onNotifycationRemoved");
        String strPgkName = sbn.getPackageName();
        Log.d(TAG, strPgkName);
        boolean bTest = TextUtils.equals(MyApplication.getApp().getPackageName(), strPgkName);
        boolean bWeChat = WECHAT_PKG_NAME.equals(strPgkName);

        if (bWeChat || bTest) {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            String strExtraTitle = extras.getString(Notification.EXTRA_TITLE, "");
            String strExtraText = extras.getString(Notification.EXTRA_TITLE, "");
            Log.d(TAG, strExtraTitle);
            Log.d(TAG, strExtraText);
            String strMsg = "";
            strMsg = strMsg + strPgkName + "\n";
            strMsg = strMsg + strExtraTitle + "\n";
            strMsg = strMsg + strExtraText;
            if (bTest) {
                MyApplication.getApp().showMsg("清除:\n" + strMsg);
            }
            final WeChatListener.WeChatNotificationMsg msg = new WeChatListener.WeChatNotificationMsg();
            msg.mFriend = strExtraTitle;
            msg.mContent = strExtraText;
            MyApplication.getApp().runWorkerThread(new Runnable() {
                @Override
                public void run() {
                    WeChatListener.delMsg(msg);
                }
            }, 0);

        }
    }
}
