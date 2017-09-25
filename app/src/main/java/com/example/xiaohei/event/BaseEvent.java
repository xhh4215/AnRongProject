package com.example.xiaohei.event;

/**
 * Created by xhh on 2017/9/12.
 */

public class BaseEvent  {
  public int what;

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public BaseEvent(int what) {
        this.what = what;
    }
}
