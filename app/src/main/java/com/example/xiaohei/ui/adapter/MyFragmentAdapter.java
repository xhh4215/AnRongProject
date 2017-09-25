package com.example.xiaohei.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.FrameLayout;

import com.example.xiaohei.ui.fragment.FragmentAt;
import com.example.xiaohei.ui.fragment.FragmentAuth;
import com.example.xiaohei.ui.fragment.FragmentMore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xhh on 2017/9/25.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentAt());
        fragmentList.add(new FragmentAuth());
        fragmentList.add(new FragmentMore());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
