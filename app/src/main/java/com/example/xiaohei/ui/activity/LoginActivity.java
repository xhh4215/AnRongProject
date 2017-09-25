package com.example.xiaohei.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xiaohei.R;
import com.example.xiaohei.context.MyApplication;
import com.example.xiaohei.enumpackage.MyEnum;
import com.example.xiaohei.event.PullEvent;
import com.example.xiaohei.socket.ClientAction;
import com.example.xiaohei.socketdata.BaseServiceData;
import com.example.xiaohei.event.LoginEvent;
import com.example.xiaohei.ui.view.ClearEditText;
import com.example.xiaohei.util.ToastUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "LoginActivity";
    //可清除的文本编辑框
    private ClearEditText mLoginUrl;
    //服务器的url
    public String mUrl;
    //phoneid
    public String mLogin;
    private ClearEditText mLoginId;
    //登陆按钮
    private Button mLoginBtn;
    private ClientAction mClientAction;
    private Button mButtonPull;
    int commandType;
    int messageType;
    public Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //界面视图控件的初始化
        initView();
    }

    private void initView() {
        mLoginUrl = (ClearEditText) findViewById(R.id.login_user_edt);
        mLoginUrl.setText("192.168.1.227");
        mLoginId = (ClearEditText) findViewById(R.id.login_userphoneid_edt);
        mLogin = mLoginId.getText().toString();
        mLoginBtn = ((Button) findViewById(R.id.login_btn));
        mLoginBtn.setOnClickListener(this);
        mButtonPull = (Button) findViewById(R.id.pull_btn);
    }

    //activity重新回到栈顶运行的时候回调的方法
    @Override
    protected void onResume() {
        super.onResume();
        //EventBus注册
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    //activity处于暂停的时候的回调的方法
    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 标识是否是登陆状态的boolean变量
     */
    private boolean isLogin = false;
    private boolean isPull = false;
    //处理EventBus推送回来的连接成功时候返回的标识 mLogin 2
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void getStrings(final LoginEvent loginEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isLogin = true;
                Toast.makeText(LoginActivity.this, "连接成功" + loginEvent.getPhoneid(), Toast.LENGTH_SHORT).show();
                if (loginEvent.getPhoneid().equals("startpush")) {

                } else {
                    //登陆成功跳转到主界面
                    Intent intent = new Intent(LoginActivity.this, VideoActivity.class);
                    intent.putExtra("url", mUrl);
                    intent.putExtra("id", mLogin);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
   @Subscribe(threadMode = ThreadMode.MAIN,priority = 98)
   public void getPullurl(final PullEvent event){
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               isPull = true;
               //播放指定的url的视频

           }
       });
   }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                MyApplication.isVisitor = false;
                String ip = mLoginUrl.getText().toString();
                if (TextUtils.isEmpty(ip)) {
                    ToastUtil.makeToastShort("ip地址不能是空");
                    return;
                }
                //初始化socket管理类
//                MyApplication.newClientAction(ip);
                mClientAction = MyApplication.getmClientAction();
                //启动socket管理的线程
                mClientAction.start();
                //封装登陆发送的数据
                BaseServiceData sendLogon = new BaseServiceData();
                commandType = MyEnum.CommandType.COMMAND_CONNECT_SUCESS.ordinal();
                messageType = MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal();
                //sendLogon.setCommandType(SendService.CommandType.COMMAND_CONNECT_SUCESS);
                //sendLogon.setMessageType(SendService.MessageType.MESSAGE_PHONEMSG);
                sendLogon.setMsgCom(commandType);
                sendLogon.setMsgType(messageType);
                sendLogon.setPhoneId(mLoginId.getText().toString());
                //对象转化为json字符串
                gson = new Gson();
                final String jsonLongon = gson.toJson(sendLogon) + "\n";
                Log.d(TAG, jsonLongon);
                mClientAction.sendData(jsonLongon);
                //发送数据到服务器
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isLogin) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            });
                        }
                    }
                }, 3000);
                break;
            case R.id.pull_btn:
                MyApplication.isVisitor = false;
                mClientAction = MyApplication.getmClientAction();
                //启动socket管理的线程
                mClientAction.start();
                //封装登陆发送的数据
                BaseServiceData sendpull = new BaseServiceData();
                commandType = MyEnum.CommandType.COMMAND_BEGIN_PULL.ordinal();
                messageType = MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal();
                sendpull.setMsgCom(commandType);
                sendpull.setMsgType(messageType);
                sendpull.setVideoUrl("pull urll");
                //对象转化为json字符串
                gson = new Gson();
                final String jsonsendpull = gson.toJson(sendpull) + "\n";
                mClientAction.sendData(jsonsendpull);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isPull) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "拉取失败", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            });
                        }
                    }
                }, 3000);

        }
    }
}
