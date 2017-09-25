package com.example.xiaohei.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.xiaohei.R;

public class SplashActivity extends AppCompatActivity {
    //登陆界面标识
    private static final int GO_LOGIN = 0;
    //推送界面的标识
    private static final int GO_MAIN = 1;
    //跳转的延迟时间
    private static final int DELAY = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.sendEmptyMessageDelayed(GO_LOGIN,DELAY);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case GO_LOGIN:
                    //跳转登陆界面
                    goActivity(LoginActivity.class);
                    break;
                case GO_MAIN:
                    //跳转主界面
                    goActivity(LoginActivity.class);
            }
        }
    };
    //实现跳转的工具方法
    private void goActivity(Class<?> cls){
        Intent intent = new Intent(this,cls);
        this.startActivity(intent);
        this.finish();
    }
}
