package com.bike.ftms.app.activity.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.fragment.pagedata.OnePageDataFragment;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.view.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class HomeFragment extends BaseFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.vp_home_fragment)
    VerticalViewPager verticalViewPager;

    private OnePageDataFragment onePageHomeFragment;

    public HomeFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_home_page;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("initView() " + this);
        ButterKnife.bind(this, view);
        onePageHomeFragment = new OnePageDataFragment();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(onePageHomeFragment);
        verticalViewPager.setOffscreenPageLimit(1);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        verticalViewPager.setAdapter(adapter);
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    public void onRunData(RowerDataBean1 rowerDataBean1) {
        if (onePageHomeFragment == null) {
            Logger.e("onRunData() --------------------------------------------------------------");
            Logger.e("HomeFragment == " + this);
            Logger.e("onePageHomeFragment == " + onePageHomeFragment);
            return;
        }

        onePageHomeFragment.onRunData(rowerDataBean1);
    }

    public void connected() {
        onePageHomeFragment.connected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onePageHomeFragment = null;
    }


    @Override
    public void setPortLayout() {

    }

    @Override
    public void setLandLayout() {

    }
}
