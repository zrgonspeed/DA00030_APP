package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;

import butterknife.BindView;


/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class TwoPageDataFragment extends BasePageDataFragment {
    private static final String TAG = TwoPageDataFragment.class.getSimpleName();

    @BindView(R.id.tv_watts)
    TextView tvWatts;
    @BindView(R.id.tv_ave_watts)
    TextView tvAveWatts;

    public TwoPageDataFragment() {
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pager_home2;
    }

    public void onRunData(RowerDataBean1 rowerDataBean1) {
        super.onRunData(rowerDataBean1);
        tvWatts.setText(String.valueOf(rowerDataBean1.getWatts()));
        tvAveWatts.setText(String.valueOf(rowerDataBean1.getAve_watts()));
    }
}
