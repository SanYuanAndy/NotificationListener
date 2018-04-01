package com.sy.notificationlistener.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.sy.notificationlistener.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class CheckPermissionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (permissions != null) {
            requestPermission(permissions);
        }
    }

    private static final int PERMISSIONS_REQUEST_MAIN = 1000;
    private static final String[] NEED_CHECKED_PERMISSIONS = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE};

    public static List<String> permissions = null;
    public static List<String> checkPermission(Context context){
        List<String> needCheckedPermissions = new ArrayList<String>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return needCheckedPermissions;
        }

        for (String permission:NEED_CHECKED_PERMISSIONS) {
            if (!TextUtils.isEmpty(permission) && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.e("checkPermission", "checkPermission fail  : " + permission);
                needCheckedPermissions.add(permission);
            }
        }
        return needCheckedPermissions;

    }

    public static void startCheckPermission(Context context){
        Intent intent = new Intent();
        intent.setClass(context, CheckPermissionActivity.class);
        try {
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean requestPermission(List<String> requestedPermissions) {
        if (requestedPermissions.isEmpty()){
            return false;
        }
        String[] tmp = new String[requestedPermissions.size()];
        requestedPermissions.toArray(tmp);
        ActivityCompat.requestPermissions(this,tmp, PERMISSIONS_REQUEST_MAIN);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_MAIN: {
                for (int i =0; i < grantResults.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Log.d("checkPermission", "fail : " + permissions[i]);
                        MyApplication.getApp().showMsg("获取权限失败");
                        this.finish();
                        return;
                    }
                }

                goMainActivity();
                break;
            }
            default:
                break;
        }
    }

    public void goMainActivity(){
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}
