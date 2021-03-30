package com.bike.ftms.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.widget.VerticalViewPager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class OnePageHomeFragment extends BaseHomeFragment {


    public OnePageHomeFragment(VerticalViewPager verticalViewPager) {
        this.verticalViewPager = verticalViewPager;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_home_page;
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
        return R.layout.view_pager_home1;
    }

    @Override
    protected int getPageTwoId() {
        return R.layout.fragment_workouts;
    }


}
