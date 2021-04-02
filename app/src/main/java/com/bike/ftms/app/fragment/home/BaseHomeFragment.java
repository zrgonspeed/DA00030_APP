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
    Unbinder unbinder;
    protected View page1, page2;
    private TextView tvUpload, tvEdit;
    private RecyclerView rvWorkouts;

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
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
