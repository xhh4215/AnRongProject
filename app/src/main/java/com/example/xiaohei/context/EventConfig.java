package com.example.xiaohei.context;
/**
 * Created by 栾桂明 on 2016/6/16.
 * 定义的一些常量来标识服务端返回的数据的类型
 */
public class EventConfig {
    public static final int LGOIN_SUCCESS =0; //连接成功
    public static final int LOGIN_FAILE = 1;//连接失败
    public static final int BEGIN_PUSH =2;//开始推流
    public static final int END_PUSH =3;//结束推流
    public static final int BEGIN_PULL=4;//开始拉流
    public static final int END_PULL = 5;//结束拉流
    public static final int BEGIN_REPLAY=6;//开始重播
    public static final int END_REPLAY = 7;//结束重播
    public static final int CLIENT_ONLINE = 8;//客户端上线
    public static final int CLIENT_OFFLINE = 9;//客户端下线
    public static final int VIDEO_DISCUSS = 10;//视频会商
    public static final int POINT = 11;//标绘
    public static final int FOCUSING_UP = 12;//调焦放大
    public static final int FOCUSING_DOWN =13; //调焦放大
    public static final int FlASH = 14;//开启或者关闭闪光灯
}
