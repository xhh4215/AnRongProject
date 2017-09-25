package com.example.xiaohei.event;

/**
 * Created by xhh on 2017/9/6.
 * 设置登陆返回的数据信息的类
 */

public class LoginEvent extends BaseEvent {
    public String phoneid;
    public int what;

    public String getPhoneid() {
        return phoneid;
    }

    public void setPhoneid(String phoneid) {
        this.phoneid = phoneid;
    }
    public LoginEvent(String phoneid,int what) {
        super(what);
        this.phoneid = phoneid;
    }
}
