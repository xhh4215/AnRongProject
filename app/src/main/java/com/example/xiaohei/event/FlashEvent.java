package com.example.xiaohei.event;

/**
 * Created by xhh on 2017/9/12.
 */

public class FlashEvent extends BaseEvent {
     public int what;
     public String openFlash;

    public FlashEvent(String openFlash,int what) {
        super(what);
        this.openFlash = openFlash;
    }

    public String getOpenFlash() {
        return openFlash;
    }

    public void setOpenFlash(String openFlash) {
        this.openFlash = openFlash;
    }


}
