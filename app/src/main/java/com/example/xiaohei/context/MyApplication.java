package com.example.xiaohei.context;

import android.app.Application;
import android.content.Context;

import com.example.xiaohei.socket.Client;
import com.example.xiaohei.socket.ClientAction;

/**
 * Created by 昌宜 on 2017/3/24.
 */

public class MyApplication extends Application {
    public static Context context;
    public static ClientAction mClientAction;
    public static boolean isVisitor = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mClientAction = new ClientAction("192.168.1.143");
    }

        /**
         * 全局获取Context
         *
         * @return
         */

    public static Context getAppContext() {
        return context;
    }

    public static ClientAction getmClientAction() {
        return mClientAction;
    }
}
