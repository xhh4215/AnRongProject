package com.example.xiaohei.socketdata;

import java.io.Serializable;

/**
 * Created by xhh on 2017/9/6.
 * 发送给服务端的数据
 *  - 你需要请求什么样的数据
 *  - 标识自己是手机端的枚举
 *  - 客户端的id
 */

public class SendService implements Serializable {

    /**
     * 标识指端的id的声明
     */
    public String PhoneId;

    public void setPhoneId(String phoneId) {
        PhoneId = phoneId;
    }

    public String getPhoneId() {
        return PhoneId;
    }

    /**
     * 发送的指令的类型
     */
    public int MsgCom;

    public int MsgType;

    public void setMsgCom(int msgCom) {
        MsgCom = msgCom;
    }

    public void setMsgType(int msgType) {
        MsgType = msgType;
    }

    public int getMsgCom() {
        return MsgCom;
    }

    public int getMsgType() {
        return MsgType;
    }
    public SendService(){

    }
}
