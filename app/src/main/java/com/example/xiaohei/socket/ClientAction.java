package com.example.xiaohei.socket;

import android.util.Log;
import android.widget.Toast;


import com.example.xiaohei.context.Config;
import com.example.xiaohei.context.EventConfig;
import com.example.xiaohei.context.MyApplication;
import com.example.xiaohei.enumpackage.MyEnum;
import com.example.xiaohei.event.FlashEvent;
import com.example.xiaohei.event.InformationEvent;
import com.example.xiaohei.event.PullEvent;
import com.example.xiaohei.paintview.Flash;
import com.example.xiaohei.paintview.Point;
import com.example.xiaohei.socketdata.BaseServiceData;
import com.example.xiaohei.socketdata.SendService;
import com.example.xiaohei.event.LoginEvent;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

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
                socket = new Socket(ip,Config.PORT);
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
                        case EventConfig.LGOIN:
                            int type1 = receiveData.getMsgCom();
                            String phoneId = receiveData.getPhoneId();
                            EventBus.getDefault().post(new LoginEvent(phoneId, type1));
                            break;
                        case EventConfig.POINT:
                            int type2 = receiveData.getMsgCom();
                            double onePointx = receiveData.getLPoint_x();
                            double onePointy = receiveData.getLPoint_y();
                            double twoPointx = receiveData.getRPoint_x();
                            double twoPointy = receiveData.getRPoint_y();
                            Point point = new Point(onePointx, onePointy, twoPointx, twoPointy);
                            EventBus.getDefault().post(new InformationEvent(point, type2));
                            break;
                        case EventConfig.FlASH:
                            int type3 = receiveData.getMsgCom();
                            int openFlash = receiveData.getOpenFlash();
                            Flash flash = new Flash(openFlash);
                            String open = flash.getOpen();
                            EventBus.getDefault().post(new FlashEvent(open, type3));
                            break;
                        case EventConfig.BEGIN_PULL:
                            int type4 = receiveData.getMsgCom();
                            String pullurl = receiveData.getPhoneId();
                            EventBus.getDefault().post(new PullEvent(pullurl,type4));
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
