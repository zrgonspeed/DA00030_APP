package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.utils.Logger;

import butterknife.BindView;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class ThreePageDataFragment extends BasePageDataFragment {
    private static final String TAG = ThreePageDataFragment.class.getSimpleName();
    @BindView(R.id.tv_calories)
    TextView tvCalories;
    @BindView(R.id.tv_calories_hr)
    TextView tvCaloriesHr;

    public ThreePageDataFragment() {
        Logger.i(TAG, "构造方法 ThreePageDataFragment()");
    }

    @Override
    protected String getTAG() {
        return TAG;
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
