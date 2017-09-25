package com.example.xiaohei.paintview;

/**
 * Created by xhh on 2017/9/5.
 */

public class Flash {
    private int type;

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    private String open;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Flash(int type) {

        this.type = type;
    }

}
