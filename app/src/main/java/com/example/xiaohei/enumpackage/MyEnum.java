package com.example.xiaohei.enumpackage;

/**
 * Created by xhh on 2017/9/12.
 */

public class MyEnum {
    /**
     *  - MESSAGE_SEVERMSG 服务器的发送的消息
     *  - MESSAGE_CMDMSG 指挥端发送的消息
     *  - MESSAGE_PHONEMSG 手机端发送的消息
     */
    public enum MessageType {MESSAGE_SEVERMSG,MESSAGE_CMDMSG, MESSAGE_PHONEMSG}

    /**
     * - COMMAND_CONNECT_SUCESS 连接成功的指令
     * - COMMAND_BEGIN_PUSH 开始推流的指令
     * - COMMAND_END_PUSH 结束推流的指令
     * - COMMAND_BEGIN_PULL 开始拉流的指令
     * - COMMAND_END_PULL 结束拉流的指令
     * - COMMAND_BEGIN_REPLAY,      //开始重播
     * - COMMAND_PLOTTING,  //标绘
     * - COMMAND_CLIENT_ONLINE,  //客户端上线
     * - COMMAND_CLIENT_OFFLINE //客户端下线
     */
    public enum CommandType { COMMAND_CONNECT_SUCESS,//连接成功
        COMMAND_CONNECT_FAIL,//连接失败
        COMMAND_BEGIN_PUSH,//开始推流
        COMMAND_END_PUSH,//结束推流
        COMMAND_BEGIN_PULL,//开始拉流
        COMMAND_END_PULL,//结束拉流
        COMMAND_BEGIN_REPLAY, //开始重播
        COMMAND_END_REPLAY,  //结束重播
        COMMAND_CLIENT_ONLINE,//客户端上线
        COMMAND_CLIENT_OFFLINE,  //客户端下线
        COMMAND_VIDEO_DISCUSS,  //视频会商
        COMMAND_PLOTTING,   //标绘
        COMMAND_FOCUSING_UP,  //调焦放大
        COMMAND_FOCUSING_DOWN,  //调焦放大
        COMMAND_FLASHLIGHT//开启或者关闭闪光灯

    }
}
