package com.sy.notificationlistener.utils;

import android.util.Log;

/**
 * Created by ASUS User on 2018/3/31.
 */
public class ChineseCharUtils {
    private final  static String TAG = ChineseCharUtils.class.getSimpleName();
    /*
        "[\u4e00-\u9fcc]
     */
    public static String getTtsReadedText(String sOriginText, boolean reservedAscii){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < sOriginText.length(); ++i){
            char c = sOriginText.charAt(i);
            int value = c;
            Log.d(TAG, "" + c + ":" + String.format("0x%x", value));
            boolean bReserved = false;
            if (value >= 0x4e00 && value <= 0x9fcc){//汉字
                bReserved = true;
            }else if (value >= '0' && value <= '9'){//0-9
                bReserved = true;
            }else if(value >= 'a' && value <= 'z'){//a-z
                bReserved = true;
            }else if(value >= 'A' && value <= 'Z'){//A-Z
                bReserved = true;
            }else if(reservedAscii && value >= 0 && value <= 0x7f){//ascii
                bReserved = true;
            }else{//中文符号
                bReserved = false;
            }

            if(bReserved){
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
