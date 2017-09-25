package com.example.xiaohei.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.xiaohei.R;
import com.example.xiaohei.ui.adapter.MyFragmentAdapter;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    public ViewPager viewpager;
    public RadioGroup radioGroup;
    MyFragmentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        viewpager = (ViewPager) findViewById(R.id.viewpagerid);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroupid);
        radioGroup.setOnCheckedChangeListener(this);
        adapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(position);
        radioButton.setTextColor(Color.RED);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        RadioButton button0 = (RadioButton) group.getChildAt(0);
        RadioButton button2 = (RadioButton) group.getChildAt(2);
        RadioButton button1 = (RadioButton) group.getChildAt(1);
        switch (checkedId) {
            case R.id.appid:
                viewpager.setCurrentItem(1);
                button0.setTextColor(Color.WHITE);
                button2.setTextColor(Color.WHITE);
                button1.setTextColor(Color.RED);
                break;
            case R.id.settingid:
                viewpager.setCurrentItem(2);
                button0.setTextColor(Color.WHITE);
                button2.setTextColor(Color.RED);
                button1.setTextColor(Color.WHITE);
                break;
            case R.id.videoid:
                viewpager.setCurrentItem(0);
                button0.setTextColor(Color.RED);
                button2.setTextColor(Color.WHITE);
                button1.setTextColor(Color.WHITE);
                break;
        }
    }
}
