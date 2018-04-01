package com.sy.notificationlistener.tts;

/**
 * Created by ASUS User on 2018/3/31.
 */
public interface ITts {
    public static interface ITtsCallBack{
        /*retCode为负数表示出错*/
        public void onEnd(int retCode);
    }

    public static interface IInitCallBack{
        /*retCode为负数表示出错*/
        public void onInit(int retCode);
    }

    public void initTts(IInitCallBack callBack);

    public void start(String sText, ITtsCallBack cb);

    public void stop();

}
