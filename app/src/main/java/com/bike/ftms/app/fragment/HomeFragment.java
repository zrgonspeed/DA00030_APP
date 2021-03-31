package com.bike.ftms.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.fragment.home.OnePageHomeFragment;
import com.bike.ftms.app.fragment.home.ThreePageHomeFragment;
import com.bike.ftms.app.fragment.home.TwoPageHomeFragment;
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
    @BindView(R.id.vp_home_fragment)
    VerticalViewPager vpHomeFragment;
    private ImageView ivPage;

    public HomeFragment() {
    }

    public HomeFragment(ImageView ivPage) {
        this.ivPage = ivPage;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_home_page;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new OnePageHomeFragment());
        fragmentList.add(new TwoPageHomeFragment());
        fragmentList.add(new ThreePageHomeFragment());
        vpHomeFragment.setOffscreenPageLimit(3);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        vpHomeFragment.setAdapter(adapter);
        vpHomeFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ivPage.setImageResource(R.mipmap.page1);
                } else if (position == 1) {
                    ivPage.setImageResource(R.mipmap.page2);
                } else if (position == 2) {
                    ivPage.setImageResource(R.mipmap.page3);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData() {

    }
}
