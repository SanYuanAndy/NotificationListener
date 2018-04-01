package com.sy.notificationlistener.tts;

import android.os.SystemClock;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ASUS User on 2018/3/31.
 */
public class TtsManager {
    private final static String TAG = TtsManager.class.getSimpleName();
    private static TtsManager instance = new TtsManager();
    private ITts mTts = null;
    private long mId = 0;
    private static class TtsTask{
        public long id;
        public String sText;
        public long addedTime;
    }

    private List<TtsTask> mTaskQueues = new LinkedList<TtsTask>();
    private TtsTask mCurrTask = null;

    private TtsManager(){

    }

    public static TtsManager getIntance(){
        return instance;
    }

    private ITts.ITtsCallBack mTtsCallback = new ITts.ITtsCallBack() {
        @Override
        public void onEnd(int retCode) {
            synchronized (TtsManager.this){
                mCurrTask = null;
                processTaskQueues();
            }
        }
    };

    public synchronized  void init(){
        if (mTts == null){
            try {
                Class<?> clazz = Class.forName("com.sy.notificationlistener.tts.YzsTts");
                mTts = (ITts)clazz.newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }

            if (mTts != null){
                mTts.initTts();
            }
        }
    }

    public synchronized  void start(String sText){
        Log.d(TAG, "start");
        TtsTask task = new TtsTask();
        task.id = ++mId;
        task.sText = sText;
        task.addedTime = SystemClock.elapsedRealtime();
        mTaskQueues.add(task);
        processTaskQueues();
    }

    public synchronized void stop(){
        if (mCurrTask != null){
            mTts.stop();
        }
        mTaskQueues.clear();
        mCurrTask = null;
    }

    private void processTaskQueues(){
        if (mCurrTask == null){
            if (!mTaskQueues.isEmpty()){
                mCurrTask = mTaskQueues.get(0);
                Log.d(TAG, "processTask : " + mCurrTask.id);
                mTaskQueues.remove(0);
                mTts.start(mCurrTask.sText, mTtsCallback);
            }
        }
    }



}
