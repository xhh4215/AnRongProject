package com.example.xiaohei.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xiaohei.R;
import com.example.xiaohei.context.EventConfig;
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
    //日志打印标识
    private String TAG = "LoginActivity";
    //输入服务端的url的文本框
    private ClearEditText mLoginUrl;
    //服务器的url
    public String mUrl;
    //phoneid
    public String mLogin;
    //登陆用户的id
    private ClearEditText mLoginId;
    //登陆按钮
    private Button mLoginBtn;
    //与服务端进行socket连接的对象
    private ClientAction mClientAction;
    //终端接收的指令的来源 服务端/指挥端
    int commandType;
    //具体的做什么的指令
    int messageType;
    //用来解析json字符串的工具类
    public Gson gson;
    //服务器返回的推送的视频的地址
    public String PushUrl;
    //拉取媒体流数据
    public Button btnPull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //界面视图控件的初始化
        initView();
    }

    private void initView() {
        mLoginUrl = (ClearEditText) findViewById(R.id.login_user_edt);
        mLoginUrl.setText("192.168.1.143");
        mLoginId = (ClearEditText) findViewById(R.id.login_userphoneid_edt);
        mLogin = mLoginId.getText().toString();
        mLoginBtn = ((Button) findViewById(R.id.login_btn));
        btnPull = (Button) findViewById(R.id.pull_btn);
        btnPull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.context,SpdyActivity.class);
                startActivity(intent);
            }
        });
        mLoginBtn.setOnClickListener(this);
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
    //处理EventBus推送回来的连接成功时候返回的标识 mLogin 2
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void logSuccess(final LoginEvent loginEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(loginEvent.getWhat()){
                    case EventConfig.LGOIN_SUCCESS:
                        Toast.makeText(LoginActivity.this, "客户端连接成功" , Toast.LENGTH_SHORT).show();
                        PushUrl = "rtmp://192.168.1.22:1935/live/stream2";
                        //登陆成功跳转到主界面
                        Intent intent = new Intent(LoginActivity.this, VideoActivity.class);
                        intent.putExtra("pushurl", PushUrl);
                        startActivity(intent);
                        finish();
                        break;
                    case EventConfig.LOGIN_FAILE:
                        Toast.makeText(LoginActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    @Override
    public void onClick(View view) {
        MyApplication.isVisitor = false;
        String ip = mLoginUrl.getText().toString();
        if (TextUtils.isEmpty(ip)) {
            ToastUtil.makeToastShort("ip地址不能是空");
            return;
        }
        //初始化socket管理类
        mClientAction = MyApplication.getmClientAction();
        //启动socket管理的线程
        mClientAction.start();
        //封装登陆发送的数据
        BaseServiceData sendLogon = new BaseServiceData();
        commandType = MyEnum.CommandType.COMMAND_CONNECT_SUCESS.ordinal();
        messageType = MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal();
        sendLogon.setMsgCom(commandType);
        sendLogon.setMsgType(messageType);
        //对象转化为json字符串
        gson = new Gson();
        final String jsonLongon = gson.toJson(sendLogon) + "\n";
        Log.d(TAG, jsonLongon);
        mClientAction.sendData(jsonLongon); //发送数据到服务器

    }
}
