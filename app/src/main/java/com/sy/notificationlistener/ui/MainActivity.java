package com.sy.notificationlistener.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.sy.notificationlistener.MyApplication;
import com.sy.notificationlistener.R;
import com.sy.notificationlistener.notification.WeChatListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button mAuthBtn = null;
    private Button mNotifyBtn = null;
    private Button mAddBtn = null;
    private ListView mConfigInfoListView = null;
    private ConfigInfoAdapter mConfigAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            List<String> results = CheckPermissionActivity.checkPermission(this);
            if (!results.isEmpty()){
                CheckPermissionActivity.permissions = results;
                CheckPermissionActivity.startCheckPermission(this);
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        mAuthBtn = (Button)findViewById(R.id.auth_btn);
        mAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authority();
            }
        });

        mNotifyBtn = (Button)findViewById(R.id.notify_btn);
        mNotifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyMsg();
            }
        });

        mAddBtn = (Button)findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addConfigItem();
                mConfigAdapter.notifyDataSetChanged();
            }
        });

        mConfigInfoListView = (ListView)findViewById(R.id.config_info_lv);

        mConfigAdapter = new ConfigInfoAdapter(this, WeChatListener.sGlobalConfigs, new ConfigInfoAdapter.EventOperations() {
            @Override
            public boolean save(int index, WeChatListener.WeChatNotificationConfig config) {
                Log.d("Main", "index:" + index);
                WeChatListener.WeChatNotificationConfig currConfig = WeChatListener.getConfig(index);
                if (TextUtils.isEmpty(config.mName)){
                    MyApplication.getApp().showMsg("名称不能为空");
                    return false;
                }

                if (TextUtils.equals(WeChatListener.DEFAULT_TIP_NAME, config.mName)){
                    MyApplication.getApp().showMsg("请输入有效的名字");
                    return false;
                }

                //
                if (WeChatListener.sGlobalConfigs.configMap.containsKey(config.mName)
                        && WeChatListener.sGlobalConfigs.configMap.get(config.mName) != currConfig){
                    MyApplication.getApp().showMsg("名称冲突");
                    return false;
                }

                currConfig.mName = config.mName;
                currConfig.mNeedVibrate = config.mNeedVibrate;
                currConfig.mNeedTTS = config.mNeedTTS;
                currConfig.mNeedSound = config.mNeedSound;
                currConfig.mEditStatus = WeChatListener.ConfigStatus.STATUS_NONE;

                WeChatListener.sGlobalConfigs.configMap.remove(WeChatListener.sGlobalConfigs.configNames.get(index));
                WeChatListener.sGlobalConfigs.configMap.put(currConfig.mName, currConfig);
                WeChatListener.sGlobalConfigs.configNames.set(index, currConfig.mName);//保证位置顺序
                WeChatListener.decreaseAddTaskCnt();
                WeChatListener.saveConfig();
                mConfigAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean cancel(int index) {
                WeChatListener.WeChatNotificationConfig config = WeChatListener.getConfig(index);
                if (config.mEditStatus == WeChatListener.ConfigStatus.STATUS_ADD){
                    WeChatListener.delConfig(index);
                }
                config.mEditStatus = WeChatListener.ConfigStatus.STATUS_NONE;
                WeChatListener.decreaseAddTaskCnt();
                WeChatListener.saveConfig();
                mConfigAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean delect(int index) {
                WeChatListener.delConfig(index);
                WeChatListener.saveConfig();
                mConfigAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean edit(int index) {
                WeChatListener.WeChatNotificationConfig currConfig = WeChatListener.getConfig(index);
                currConfig.mEditStatus = WeChatListener.ConfigStatus.STATUS_EDIT;
                mConfigAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean menu_select(View view, List<MenuItemData> data, PopupMenu.OnMenuItemClickListener itemlistener){
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(MainActivity.this, view);//第二个参数是绑定的那个view
                //获取菜单填充器
                //MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                //inflater.inflate(R.menu.eidt_selector, popup.getMenu());
                for (MenuItemData item : data) {
                    popup.getMenu().add(0, item.mItemId, 0, item.mText);
                }
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(itemlistener);
                //显示(这一行代码不要忘记了)
                popup.show();
                return false;
            }
        });

        mConfigInfoListView.setAdapter(mConfigAdapter);
    }

    private void authority(){
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void notifyMsg(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentText("通知内容")
                .setContentTitle("通知。标题aZ09,")
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .build();

        manager.notify(1, notification);
    }

    private void addConfigItem(){
        if(!WeChatListener.addItem()){
            MyApplication.getApp().showMsg("上一个编辑项尚未完成");
        }
    }
}
