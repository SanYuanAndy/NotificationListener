package com.sy.notificationlistener.notification;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import com.sy.notificationlistener.MyApplication;
import com.sy.notificationlistener.tts.TtsManager;
import com.sy.notificationlistener.utils.ChineseCharUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by ASUS User on 2018/2/25.
 */
public class WeChatListener {
    private final static String TAG = WeChatListener.class.getSimpleName();

    public static class WeChatNotificationMsg {
        public String mFriend;
        public String mContent;
    }

    public static enum ConfigStatus {
        STATUS_NONE,
        STATUS_ADD,
        STATUS_EDIT
    }

    public static class WeChatNotificationConfig {
        public String mName;
        public boolean mNeedSound = false;
        public boolean mNeedVibrate = false;
        public boolean mNeedTTS = false;
        public ConfigStatus mEditStatus = ConfigStatus.STATUS_NONE;//编辑状态，默认NONE
    }

    public static class WeChatConfigs {
        public int addTaskCnt = 0;
        public int addEditCnt = 0;
        public static WeChatNotificationConfig globalConfig = new WeChatNotificationConfig();
        public static Map<String, WeChatNotificationConfig> configMap = new TreeMap<String, WeChatNotificationConfig>();
        public static List<String> configNames = new LinkedList<String>();
    }

    public static WeChatConfigs sGlobalConfigs = new WeChatConfigs();
    public final static String CONFIG_FILE_NAME = "wechatlistener.conf";
    public final static String GLOBAL_CONFIG_NAME = "global";
    public final static String DEFAULT_TIP_NAME = "填入联系人名称";

    public static void init() {
        loadConfig();

        if (!sGlobalConfigs.configNames.contains(GLOBAL_CONFIG_NAME)) {
            WeChatNotificationConfig config = new WeChatNotificationConfig();
            config.mName = GLOBAL_CONFIG_NAME;
            sGlobalConfigs.configMap.put(config.mName, config);
            sGlobalConfigs.configNames.add(config.mName);
        }
    }

    /*
    返回false,上个编辑项尚未完成
     */
    public static boolean addItem() {
        boolean bRet = false;
        if (sGlobalConfigs.addTaskCnt > 0) {
            return false;
        }

        WeChatNotificationConfig config = new WeChatNotificationConfig();
        config.mName = DEFAULT_TIP_NAME;
        sGlobalConfigs.configMap.put(config.mName, config);
        sGlobalConfigs.configNames.add(1, config.mName);
        config.mEditStatus = ConfigStatus.STATUS_ADD;
        bRet = true;
        sGlobalConfigs.addTaskCnt++;
        return bRet;
    }

    public static void decreaseAddTaskCnt() {
        if (sGlobalConfigs.addTaskCnt > 0) {
            sGlobalConfigs.addTaskCnt--;
        }
    }

    public static boolean delConfig(int index) {
        if (index <= 0 || index >= sGlobalConfigs.configNames.size()) {
            return false;
        }

        try {
            WeChatListener.sGlobalConfigs.configMap.remove(WeChatListener.sGlobalConfigs.configNames.get(index));
            WeChatListener.sGlobalConfigs.configNames.remove(index);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static WeChatNotificationConfig getConfig(int index) {
        return sGlobalConfigs.configMap.get(WeChatListener.sGlobalConfigs.configNames.get(index));
    }


    private static void loadConfig() {
        File f = new File(MyApplication.getApp().getFilesDir() + File.separator + CONFIG_FILE_NAME);
        if (!f.exists()) {
            return;
        }
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(f));
            while (true) {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                sb.append(s);
            }
        } catch (Exception e) {

        }

        JSONArray json = null;
        try {
            json = new JSONArray(sb.toString());
            for (int i = 0; i < json.length(); ++i) {

                JSONObject obj = json.getJSONObject(i);
                if (obj == null) {
                    continue;
                }

                WeChatNotificationConfig config = new WeChatNotificationConfig();
                try {
                    config.mName = obj.getString("name");
                } catch (Exception e) {

                }

                if (TextUtils.isEmpty(config.mName)) {
                    continue;
                }

                try {
                    config.mNeedSound = obj.getBoolean("needSound");
                } catch (Exception e) {

                }

                try {
                    config.mNeedVibrate = obj.getBoolean("needVibrate");
                } catch (Exception e) {

                }

                try {
                    config.mNeedTTS = obj.getBoolean("needTTS");
                } catch (Exception e) {

                }
                sGlobalConfigs.configMap.put(config.mName, config);
                sGlobalConfigs.configNames.add(config.mName);
            }
        } catch (Exception e) {

        }

    }

    public static void saveConfig() {
        JSONArray json = new JSONArray();
        for (String key : sGlobalConfigs.configNames) {
            WeChatNotificationConfig config = sGlobalConfigs.configMap.get(key);
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", config.mName);
                obj.put("needSound", config.mNeedSound);
                obj.put("needVibrate", config.mNeedVibrate);
                obj.put("needTTS", config.mNeedTTS);
                json.put(obj);
            } catch (Exception e) {

            }
        }
        Log.d("Listener", json.toString());
        String strJson = json.toString();
        if (TextUtils.isEmpty(strJson)) {
            return;
        }

        File f = new File(MyApplication.getApp().getFilesDir() + File.separator + CONFIG_FILE_NAME);
        if (!f.exists()) {
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            writer.write(strJson);
            writer.close();
        } catch (Exception e) {

        }


    }

    private boolean isNeedMsg() {
        boolean ret = false;
        PowerManager pm = (PowerManager) MyApplication.getApp().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        return  ret;
    }

    public static void delMsg(WeChatNotificationMsg msg){
        TtsManager.getIntance().stop();
    }

    public static void notifyMsg(WeChatNotificationMsg msg) {
        WeChatNotificationConfig config = sGlobalConfigs.configMap.get(msg.mFriend);
        //没有特殊配置，则使用全局配置
        if (config == null){
            config = sGlobalConfigs.configMap.get(GLOBAL_CONFIG_NAME);
        }

        if (config != null) {
            if (config.mNeedVibrate) {
                Vibrate();
            }

            //有TTS播报就不需要提示音了
            do {
                if (config.mNeedTTS) {
                    String strNotify = ChineseCharUtils.getTtsReadedText(msg.mFriend, false) + ",来微信了.";
                    Log.d(TAG, strNotify);
                    playTTS(strNotify);
                    break;
                }

                if (config.mNeedSound) {
                    playSound(MyApplication.getApp(), RingtoneManager.TYPE_NOTIFICATION);
                    break;
                }
            } while (false);

        }
    }

    //RingtoneManager.TYPE_NOTIFICATION
    public static void playSound(Context context,int type){
        Log.d(TAG, "playSound");
        MediaPlayer mp = new MediaPlayer();
        mp.reset();
        try {
            mp.setDataSource(context, RingtoneManager.getDefaultUri(type));
            mp.prepare();
        }catch (Exception e){

        }
        mp.start();
    }

    public static void Vibrate(){
        Vibrator vibrator = (Vibrator)MyApplication.getApp().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    public static void playTTS(String strText){
        TtsManager.getIntance().start(strText);
    }
}
