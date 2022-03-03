package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public abstract class BasePageDataFragment extends BaseFragment {
    private static final String TAG = BasePageDataFragment.class.getSimpleName();
    Unbinder unbinder;

    @BindView(R.id.tv_drag)
    TextView tvDrag;
    @BindView(R.id.tv_interval)
    TextView tvInterval;

    @BindView(R.id.tv_heart_rate)
    TextView tvHeartRate;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.rl3)
    RelativeLayout rl3;
    @BindView(R.id.rl6)
    RelativeLayout rl6;

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        Logger.i(getTAG() + " initView() " + this);
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

    public void onRunData(RowerDataBean1 rowerDataBean1) {

    }
}
