package com.sy.notificationlistener.service;

/**
 * Created by ASUS User on 2018/4/5.
 */
public class AuthConfig {
    public static final String AUTH_TEST_TITLE = "AuthTest";
    public static final String AUTH_TEST_CONTENT = "AuthTest";

    private static boolean mAuthed = false;

    public static boolean isAuthed(){
        return mAuthed;
    }

    public static void setAuthedFlag(boolean flag){
        mAuthed = flag;
    }
}
