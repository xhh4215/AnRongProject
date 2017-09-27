package com.example.xiaohei.event;

/**
 * Created by xhh on 2017/9/26.
 */

public class SeeVideoEvent {
    public SeeVideoEvent(String url, int message) {
        this.url = url;
        this.message = message;
    }

    private  String url;
    private int message;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}
