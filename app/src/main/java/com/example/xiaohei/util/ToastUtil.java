package com.example.xiaohei.util;

import android.widget.TextView;
import android.widget.Toast;

import com.example.xiaohei.context.MyApplication;


/**
 * Toast的工具类
 */
public class ToastUtil {
    private static Toast mtoast;
    private static boolean isFirst;
    private static TextView tv_toast;
    public static void makeToastShort(String content){
        if (content != null){
            Toast.makeText(MyApplication.getAppContext(),content, Toast.LENGTH_SHORT).show();
        }
    }

    public static void makeToastLong(String content){
        if (content != null){
            Toast.makeText(MyApplication.getAppContext(),content, Toast.LENGTH_LONG).show();
        }
    }

    public static void makeToastLong(int content){
        Toast.makeText(MyApplication.getAppContext(), content, Toast.LENGTH_LONG).show();
    }

    public static void makeToastShort(int content){
        Toast.makeText(MyApplication.getAppContext(), content, Toast.LENGTH_SHORT).show();
    }

}
