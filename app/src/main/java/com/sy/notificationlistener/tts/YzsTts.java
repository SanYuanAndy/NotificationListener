package com.sy.notificationlistener.tts;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sy.notificationlistener.MyApplication;
import com.sy.notificationlistener.utils.ResourceUtils;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;


public class YzsTts implements ITts{
	private final static String TAG = "YzsTts";
	private SpeechSynthesizer mTTSPlayer;
	private String mFrontendModel= "/sdcard/unisound/tts/frontend_model";
	private String mBackendModel = "/sdcard/unisound/tts/backend_female";
	private boolean bInitSuccessed = false;
	private final static String FRONTEND_MODEL_NAME = "frontend_model";
	private final static String BACKEND_MODEL_NAME = "backend_female";

	private boolean initResource(){
		boolean ret = false;
		File root = new File(MyApplication.getApp().getFilesDir(), "yzs_tts");
		if (!root.exists()){
			root.mkdirs();
		}

		do{
			ret = ResourceUtils.update(MyApplication.getApp(), FRONTEND_MODEL_NAME, root.getPath());
			if (!ret){
				break;
			}

			ret = ResourceUtils.update(MyApplication.getApp(), BACKEND_MODEL_NAME, root.getPath());
			if (!ret){
				break;
			}

			mFrontendModel = new File(root, FRONTEND_MODEL_NAME).getPath();
			mBackendModel = new File(root, BACKEND_MODEL_NAME).getPath();

		}while(false);

		return ret;
	}

	/**
	 * 初始化本地离线TTS
	 */
	@Override
	public void initTts(){
		boolean ret = false;
		ret = initResource();
		if (!ret){
			Log.d(TAG, "initResource fail");
			return;
		}

		// 初始化语音合成对象
		mTTSPlayer = new SpeechSynthesizer(MyApplication.getApp(), null, null);
		// 设置本地合成
		mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
		// 设置前端模型
		mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModel);
		// 设置后端模型
		mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModel);
		// 设置回调监听
		mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

			@Override
			public void onEvent(int type) {
				switch (type) {
					case SpeechConstants.TTS_EVENT_INIT:
						// 初始化成功回调
						log_i("onInitFinish");
						bInitSuccessed = true;
						break;
					case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
						// 开始合成回调
						log_i("beginSynthesizer");
						break;
					case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
						// 合成结束回调
						log_i("endSynthesizer");
						break;
					case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
						// 开始缓存回调
						log_i("beginBuffer");
						break;
					case SpeechConstants.TTS_EVENT_BUFFER_READY:
						// 缓存完毕回调
						log_i("bufferReady");
						break;
					case SpeechConstants.TTS_EVENT_PLAYING_START:
						// 开始播放回调
						log_i("onPlayBegin");
						break;
					case SpeechConstants.TTS_EVENT_PLAYING_END:
						// 播放完成回调
						log_i("onPlayEnd");
						onEnd(0);
						break;
					case SpeechConstants.TTS_EVENT_PAUSE:
						// 暂停回调
						log_i("pause");
						break;
					case SpeechConstants.TTS_EVENT_RESUME:
						// 恢复回调
						log_i("resume");
						break;
					case SpeechConstants.TTS_EVENT_STOP:
						// 停止回调
						log_i("stop");
						break;
					case SpeechConstants.TTS_EVENT_RELEASE:
						// 释放资源回调
						log_i("release");
						break;
					default:
						break;
				}

			}

			@Override
			public void onError(int type, String errorMSG) {
				// 语音合成错误回调
				log_i("onError");
				onEnd(-2);
			}
		});
		// 初始化合成引擎
		mTTSPlayer.init("");
	}

	private ITtsCallBack mTtsCallBack = null;
	@Override
	public void start(String sText, ITtsCallBack cb) {
		if (!bInitSuccessed){
			if (cb != null){
				cb.onEnd(-1);
				return;
			}
		}

		mTtsCallBack = cb;
		mTTSPlayer.playText(sText);
	}

	@Override
	public void stop(){
		if (bInitSuccessed){
			mTTSPlayer.cancel();
		}
	}

	private void onEnd(int code){
		ITtsCallBack cb = mTtsCallBack;
		mTtsCallBack = null;
		if (cb != null){
			cb.onEnd(code);
		}
	}

	private void log_i(String log) {
		Log.i("demo", log);
	}


}
