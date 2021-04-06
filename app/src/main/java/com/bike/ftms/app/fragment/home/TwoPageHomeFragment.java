package com.bike.ftms.app.fragment.home;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean;

import butterknife.BindView;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class TwoPageHomeFragment extends BaseHomeFragment {

    @BindView(R.id.tv_watts)
    TextView tvWatts;
    @BindView(R.id.tv_ave_watts)
    TextView tvAveWatts;

    public TwoPageHomeFragment() {
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pager_home2;
    }

    public void onRunData(RowerDataBean rowerDataBean) {
        super.onRunData(rowerDataBean);
        tvWatts.setText(String.valueOf(rowerDataBean.getWatts()));
        tvAveWatts.setText(String.valueOf(rowerDataBean.getAve_watts()));
    }
}
