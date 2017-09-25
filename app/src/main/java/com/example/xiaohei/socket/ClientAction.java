package com.example.xiaohei.socket;

import android.util.Log;


import com.example.xiaohei.context.Config;
import com.example.xiaohei.context.EventConfig;
import com.example.xiaohei.event.InformationEvent;
import com.example.xiaohei.paintview.Point;
import com.example.xiaohei.socketdata.BaseServiceData;
import com.example.xiaohei.event.LoginEvent;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.Socket;


/**
 * Created by 昌宜 on 2017/3/20.
 */

public class ClientAction extends Thread {
    private static final String TAG = ClientAction.class.getSimpleName();
    private static Client client;
    public String jsonreceiveData;

    public static Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        ClientAction.client = client;
    }

    private String ip;
    private int port = 0;
    private int type;
    private int message;

    public ClientAction(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }

    public ClientAction(String ip) {
        this.ip = ip;
    }

    private void initSocket() {
        try {
            Socket socket;
            if (port == 0) {
                socket = new Socket(ip, Config.PORT);
            } else {
                socket = new Socket(ip, port);
            }
            client = new Client(socket);

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    @Override
    public void run() {
        initSocket();
        if (client != null) {
            while (!client.isClosed()) {
                try {
                    //服务端返回的json数据
                    jsonreceiveData = (String) client.receive();
                    Gson gson = new Gson();
                    BaseServiceData receiveData = gson.fromJson(jsonreceiveData, BaseServiceData.class);
                    switch (receiveData.getMsgCom()) {
                        //客户端连接成功接收到的服务器的返回的连接成功的标识
                        case EventConfig.LGOIN_SUCCESS:
                            //获取消息类型判断是服务端消息还是指挥端消息
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            //String phoneId = receiveData.getPhoneId();
                            EventBus.getDefault().post(new LoginEvent(""+message, message));
                            //此处应该添加获取推送地址的代码
                            break;
                        //登陆失败的处理
                        case EventConfig.LOGIN_FAILE:
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            EventBus.getDefault().post(new LoginEvent("" + message, message));
                            break;
                        //视频标绘的处理
                        case EventConfig.POINT:
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            double onePointx = receiveData.getLPoint_x();
                            double onePointy = receiveData.getLPoint_y();
                            double twoPointx = receiveData.getRPoint_x();
                            double twoPointy = receiveData.getRPoint_y();
                            Point point = new Point(onePointx, onePointy, twoPointx, twoPointy);
                            EventBus.getDefault().post(new InformationEvent(point, message));
                            break;
                        //闪光灯的处理
                        case EventConfig.FlASH:
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            EventBus.getDefault().post(new InformationEvent(type, message));
                            break;
                        //视频会商的处理
                        case EventConfig.VIDEO_DISCUSS:
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            String discussurl = receiveData.getDiscussUrl();
                            EventBus.getDefault().post(new InformationEvent(discussurl, message));
                        //增大焦距
                        case EventConfig.FOCUSING_UP:
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            //changdata = receiveData.getdata();
                            EventBus.getDefault().post(new InformationEvent(10, message));
                            break;
                        //减小焦距
                        case EventConfig.FOCUSING_DOWN:
                            type = receiveData.getMsgType();
                            message = receiveData.getMsgCom();
                            EventBus.getDefault().post(new InformationEvent(-10, message));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    close();
                }
            }
        }
    }

    public static void sendData(String sendData) {
        sendDataPackage(sendData);
    }

    public static void sendDataPackage(String dp) {
        if (client != null && !client.isClosed()) {
            try {
                client.send(dp);
            } catch (IOException e) {
                close();
            }
        }
    }

    public static void close() {
        if (client != null) {
            client.close();
            Log.e(TAG, "client close");
        }
    }
}
