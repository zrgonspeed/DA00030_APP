package com.bike.ftms.app.fragment.home;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean1;

import butterknife.BindView;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class ThreePageHomeFragment extends BaseHomeFragment {

    @BindView(R.id.tv_calories)
    TextView tvCalories;
    @BindView(R.id.tv_calories_hr)
    TextView tvCaloriesHr;

    public ThreePageHomeFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pager_home3;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    public void onRunData(RowerDataBean1 rowerDataBean1) {
        super.onRunData(rowerDataBean1);
        tvCalories.setText(String.valueOf(rowerDataBean1.getCalorie()));
        tvCaloriesHr.setText(String.valueOf(rowerDataBean1.getCalories_hr()));
    }
}
