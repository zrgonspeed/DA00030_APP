package com.bike.ftms.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bike.ftms.app.R;
import com.bike.ftms.app.widget.VerticalViewPager;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class TwoPageHomeFragment extends BaseHomeFragment {

    public TwoPageHomeFragment(VerticalViewPager verticalViewPager) {
        this.verticalViewPager = verticalViewPager;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getPageOneId() {
        return R.layout.view_pager_home2;
    }

    @Override
    protected int getPageTwoId() {
        return R.layout.fragment_workouts;
    }
}
