package com.sy.notificationlistener.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ASUS User on 2018/3/31.
 */
public class ResourceUtils {
    private final static String TAG = ResourceUtils.class.getSimpleName();
    public static boolean update(Context context, String strSrcName, String strDestDir){
        boolean ret = false;
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open(strSrcName);
            boolean bUpdated = false;
            int srcFileLenght = in.available();
            File strDestFile = new File(strDestDir, strSrcName);
            do {
                if (!strDestFile.exists()) {
                    Log.d(TAG, "destFile does not exists, need update");
                    bUpdated = true;
                    break;
                }

                if (strDestFile.length() != srcFileLenght){
                    Log.d(TAG, "len changed, need update");
                    bUpdated = true;
                    break;
                }
            } while (false) ;
            byte[] buffer = new byte[1024*32];
            if (bUpdated) {
                out = new FileOutputStream(strDestFile);
                int read = 0;
                while(true){
                    read = in.read(buffer, 0, buffer.length);
                    if (read <= 0){
                        break;
                    }
                    out.write(buffer, 0, read);
                }
            }

            ret = true;

        }catch(Exception e){
            e.printStackTrace();
        }

        if (in != null){
            try {
                in.close();
            }catch (Exception e){

            }
        }

        if (out != null){
            try {
                out.close();
            }catch (Exception e){

            }
        }

        return ret;
    }

}
