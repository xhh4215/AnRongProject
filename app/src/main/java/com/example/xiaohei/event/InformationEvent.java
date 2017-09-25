package com.example.xiaohei.event;

/**
 * Created by xhh on 2017/9/10.
 */

public class InformationEvent extends BaseEvent  {
    private Object object;
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public InformationEvent(Object object,int what){
        super(what);
        this.object = object;
    }

}
