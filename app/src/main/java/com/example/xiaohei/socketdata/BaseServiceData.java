package com.example.xiaohei.socketdata;

import java.io.Serializable;

/**
 * Created by xhh on 2017/9/12.
 */

public class BaseServiceData implements Serializable {
    //第一个点的x坐标比例
    public double LPoint_x;
    //第一个点的y坐标的比例
    public double LPoint_y;
    //第二个点的x坐标的比例
    public double RPoint_x;
    //第二个点的y坐标的比例
    public double RPoint_y;

    public double getLPoint_x() {
        return LPoint_x;
    }

    public void setLPoint_x(double LPoint_x) {
        this.LPoint_x = LPoint_x;
    }

    public double getLPoint_y() {
        return LPoint_y;
    }

    public void setLPoint_y(double LPoint_y) {
        this.LPoint_y = LPoint_y;
    }

    public double getRPoint_x() {
        return RPoint_x;
    }

    public void setRPoint_x(double RPoint_x) {
        this.RPoint_x = RPoint_x;
    }

    public double getRPoint_y() {
        return RPoint_y;
    }

    public void setRPoint_y(double RPoint_y) {
        this.RPoint_y = RPoint_y;
    }

    //是否打开闪光的标识符
    public int openFlash;
    //登陆的用户id
    public String PhoneId;
    //发送的指令的类型 登陆 绘图。。。。
    public int MsgCom;
    //指明是谁发送的消息
    public int MsgType;
    //保存拉取视频地址的变量
    public String VideoUrl;

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public int getMsgCom() {
        return MsgCom;
    }

    public void setMsgCom(int msgCom) {
        MsgCom = msgCom;
    }

    public int getMsgType() {
        return MsgType;
    }

    public void setMsgType(int msgType) {
        MsgType = msgType;
    }

    public String getPhoneId() {
        return PhoneId;
    }

    public void setPhoneId(String phoneId) {
        this.PhoneId = phoneId;
    }

    public int getOpenFlash() {
        return openFlash;
    }

    public void setOpenFlash(int openFlash) {
        this.openFlash = openFlash;
    }


    public BaseServiceData() {
    }
}
