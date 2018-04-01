package com.sy.notificationlistener;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.sy.notificationlistener.notification.WeChatListener;
import com.sy.notificationlistener.tts.TtsManager;

public class MyApplication extends Application {
	private final static String TAG = MyApplication.class.getSimpleName();
	private static MyApplication sApp = null;
    private Handler mUiHandler;
    private Handler mWorkHandler;
    private HandlerThread mWorkerThread;
	@Override
	public void onCreate() {
		super.onCreate();
		sApp = this;
		init();
	}

	public static MyApplication getApp() {
		return sApp;
	}
    
	public void showMsg(final String msg){
		Runnable oRun = new Runnable(){

			@Override
			public void run() {
				Toast.makeText(getApp(), msg, Toast.LENGTH_SHORT).show();
			}
		};
		if (mUiHandler == null){
			return;
		}
		mUiHandler.postDelayed(oRun, 0);
	}
	

	
	private void init() {
		Log.d("init", "abi: " + android.os.Build.CPU_ABI);
		mUiHandler = new Handler(getMainLooper());
		mWorkerThread = new HandlerThread("worker");
		mWorkerThread.start();
		mWorkHandler = new Handler(mWorkerThread.getLooper());

		WeChatListener.init();
		new Thread(new Runnable() {
			@Override
			public void run() {
				TtsManager.getIntance().init();
			}
		}, "Tts_Init").start();

	}
	
	public void runUiThread(Runnable oRun, long delay){
		if (mUiHandler != null){
			mUiHandler.postDelayed(oRun, delay);
		}
	}
	
	public void runWorkerThread(Runnable oRun, long delay){
		if (mWorkHandler != null){
			mWorkHandler.postDelayed(oRun, delay);
		}
	}
	
	public void removeUiThread(Runnable oRun){
		if (mUiHandler != null){
			mUiHandler.removeCallbacks(oRun);
		}
	}
	
	public void removeWorkerThread(Runnable oRun){ 
		if (mWorkHandler != null){
			mWorkHandler.removeCallbacks(oRun);
		}
	}

	
	public boolean isMainProcess(){
		return this.getApplicationInfo().packageName.equals(getCurrProcessName());
	}
	
	private String getCurrProcessName(){
		String strProcessName = null;
		int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
       //5.0以及5.0以上的系统只能获取到本应用的进程列表
        for (ActivityManager.RunningAppProcessInfo process: manager.getRunningAppProcesses()) {
            if(process.pid == pid)
            {
                strProcessName = process.processName;
                break;
            }
        }
        Log.d(TAG,  "currProcessName : " + strProcessName);
		return strProcessName;
	}
}
