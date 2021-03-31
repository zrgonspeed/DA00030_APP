package com.bike.ftms.app.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.InformationPagerAdapter;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.widget.VerticalViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public abstract class BaseHomeFragment extends BaseFragment {
 /*   @BindView(R.id.vp_home_fragment)
    protected ViewPager vpHomeFragment;*/
    Unbinder unbinder;
    protected View page1, page2;
    private TextView tvUpload, tvEdit;
    private RecyclerView rvWorkouts;

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
       /* page1 = LayoutInflater.from(mActivity).inflate(getPageOneId(), container, false);
        page2 = LayoutInflater.from(mActivity).inflate(getPageTwoId(), container, false);
        ArrayList<View> mViews = new ArrayList<>();
        mViews.add(page1);
        mViews.add(page2);
        vpHomeFragment.addView(page1);
        vpHomeFragment.addView(page2);
        vpHomeFragment.setAdapter(new InformationPagerAdapter(mViews));
        vpHomeFragment.setOffscreenPageLimit(2);
        vpHomeFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    verticalViewPager.setVertical(true);
                } else if (position == 1) {
                    verticalViewPager.setVertical(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tvUpload = page1.findViewById(R.id.tv_upload);
        tvEdit = page1.findViewById(R.id.tv_edit);
        rvWorkouts = page1.findViewById(R.id.rv_workouts);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void initData() {

    }
}
