package com.sy.notificationlistener.utils;

import android.util.Log;

/**
 * Created by ASUS User on 2018/3/31.
 */
public class ChineseCharUtils {
    private final  static String TAG = ChineseCharUtils.class.getSimpleName();

    public static class Results{
        public int chineseCharCnt = 0;//汉字个数
        public int readedAsciiCharCnt = 0;//字母和数字的个数
        public int otherAsciiCharCnt = 0;//不可读的ascii码的个数
        public int otherCharCnt = 0;//其他非可读的字符的个数
    }
    /*
        "[\u4e00-\u9fcc]
     */
    public static String getTtsReadedText(String sOriginText, boolean reservedAscii, Results result){
        if (result == null){
            result = new Results();
        }

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < sOriginText.length(); ++i){
            char c = sOriginText.charAt(i);
            int value = c;
            Log.d(TAG, "" + c + ":" + String.format("0x%x", value));
            boolean bReserved = false;
            if (value >= 0x4e00 && value <= 0x9fcc){//汉字
                bReserved = true;
                ++result.chineseCharCnt;
            }else if (value >= '0' && value <= '9'){//0-9
                bReserved = true;
                ++result.readedAsciiCharCnt;
            }else if(value >= 'a' && value <= 'z'){//a-z
                bReserved = true;
                ++result.readedAsciiCharCnt;
            }else if(value >= 'A' && value <= 'Z'){//A-Z
                bReserved = true;
                ++result.readedAsciiCharCnt;
            }else if(reservedAscii && value >= 0 && value <= 0x7f){//ascii
                bReserved = true;
                ++result.otherAsciiCharCnt;
            }else{//中文符号
                bReserved = false;
                ++result.otherCharCnt;
            }

            if(bReserved){
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
