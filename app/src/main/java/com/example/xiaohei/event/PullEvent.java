package com.example.xiaohei.event;

/**
 * Created by xhh on 2017/9/19.
 */

public class PullEvent extends BaseEvent {
    public String pullurl;
    public int what;
    public PullEvent(String pullurl,int what){
        super(what);
        this.pullurl = pullurl;
    }
}
