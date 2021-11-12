package com.bike.ftms.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.fragment.home.OnePageHomeFragment;
import com.bike.ftms.app.fragment.home.ThreePageHomeFragment;
import com.bike.ftms.app.fragment.home.TwoPageHomeFragment;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.widget.VerticalViewPager;

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
    @BindView(R.id.iv_bar)
    ImageView ivBar;
    private OnePageHomeFragment onePageHomeFragment;
    private TwoPageHomeFragment twoPageHomeFragment;
    private ThreePageHomeFragment threePageHomeFragment;

    public HomeFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_home_page;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        Logger.i(TAG, "initView()");
        ButterKnife.bind(this, view);
        onePageHomeFragment = new OnePageHomeFragment();
        twoPageHomeFragment = new TwoPageHomeFragment();
        threePageHomeFragment = new ThreePageHomeFragment();


        Logger.e(TAG, "HomeFragment == " + this);
        Logger.e(TAG, "onePageHomeFragment == " + onePageHomeFragment);
        Logger.e(TAG, "twoPageHomeFragment == " + twoPageHomeFragment);
        Logger.e(TAG, "threePageHomeFragment == " + threePageHomeFragment);


        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(onePageHomeFragment);
        fragmentList.add(twoPageHomeFragment);
        fragmentList.add(threePageHomeFragment);
        verticalViewPager.setOffscreenPageLimit(3);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        verticalViewPager.setAdapter(adapter);
        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ivBar.setImageResource(R.mipmap.bar1);
                } else if (position == 1) {
                    ivBar.setImageResource(R.mipmap.bar2);
                } else if (position == 2) {
                    ivBar.setImageResource(R.mipmap.bar3);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i(TAG, "onActivityCreated()");
    }

    @Override
    protected void initData() {

    }

    public void onRunData(RowerDataBean1 rowerDataBean1) {
        Logger.e(TAG, "onRunData() --------------------------------------------------------------");
        Logger.e(TAG, "onePageHomeFragment == " + onePageHomeFragment);
        Logger.e(TAG, "twoPageHomeFragment == " + twoPageHomeFragment);
        Logger.e(TAG, "threePageHomeFragment == " + threePageHomeFragment);
        if (onePageHomeFragment == null || twoPageHomeFragment == null || threePageHomeFragment == null) {
            return;
        }

        if (rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_NO) {
            RowerDataBean1 tempBean1 = rowerDataBean1;
            rowerDataBean1 = new RowerDataBean1();
            // 停止运动，但心跳还是得显示。
            rowerDataBean1.setHeart_rate(tempBean1.getHeart_rate());
        }

        onePageHomeFragment.onRunData(rowerDataBean1);
        twoPageHomeFragment.onRunData(rowerDataBean1);
        threePageHomeFragment.onRunData(rowerDataBean1);
    }
}
