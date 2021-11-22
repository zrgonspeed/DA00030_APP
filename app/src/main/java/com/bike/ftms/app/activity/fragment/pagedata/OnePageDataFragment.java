package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import butterknife.BindView;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class OnePageDataFragment extends BasePageDataFragment {
    private static final String TAG = OnePageDataFragment.class.getSimpleName();

    @BindView(R.id.tv_five_hundred)
    TextView tvFiveHundred;
    @BindView(R.id.tv_ave_five_hundred)
    TextView tvAveFiveHundred;

    public OnePageDataFragment() {
        Logger.i(TAG, "构造方法 OnePageDataFragment()");
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pager_home1;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onRunData(RowerDataBean1 rowerDataBean1) {
        super.onRunData(rowerDataBean1);
        tvFiveHundred.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getFive_hundred()));
        tvAveFiveHundred.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAve_five_hundred()));
    }
}
