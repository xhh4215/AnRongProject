package com.example.xiaohei.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xiaohei.R;
import com.example.xiaohei.event.SeeVideoEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SpdyActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public ListView listview;
    public ArrayAdapter adapter;
    String[] pushlist = new String[]{"rtmp://192.168.1.22:1935/live/stream1",
            "rtmp://192.168.1.22:1935/live/stream2",
            "rtmp://192.168.1.22:1935/live/stream3",
            "rtmp://192.168.1.22:1935/live/stream4",
            "rtmp://192.168.1.22:1935/live/stream5",
            "rtmp://192.168.1.22:1935/live/stream6",
            "rtmp://192.168.1.22:1935/live/stream7",
            "rtmp://192.168.1.22:1935/live/stream8"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spdy);
        listview = (ListView) findViewById(R.id.listviewId);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pushlist);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             String url = pushlist[position];
        Intent intent = new Intent(this,PLayActivity.class);
        intent.putExtra("playurl",url);
        startActivity(intent);


    }
    //处理得到的url地址集合
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 99)
    public void getVideoUrl(SeeVideoEvent event){

    }
    @Override
    protected void onResume() {
        super.onResume();
        //EventBus注册
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    //activity处于暂停的时候的回调的方法
    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
