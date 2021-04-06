package com.bike.ftms.app.fragment.home;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.utils.TimeStringUtil;

import butterknife.BindView;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class OnePageHomeFragment extends BaseHomeFragment {


    @BindView(R.id.tv_five_hundred)
    TextView tvFiveHundred;
    @BindView(R.id.tv_ave_five_hundred)
    TextView tvAveFiveHundred;

    public OnePageHomeFragment() {
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
    public void onRunData(RowerDataBean rowerDataBean) {
        super.onRunData(rowerDataBean);
        tvFiveHundred.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean.getFive_hundred()));
        tvAveFiveHundred.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean.getAve_five_hundred()));
    }
}
