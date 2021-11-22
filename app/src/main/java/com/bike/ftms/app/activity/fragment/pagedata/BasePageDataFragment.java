package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.manager.ble.BleManager;
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
    @BindView(R.id.tv_strokes)
    TextView tvStrokes;
    @BindView(R.id.tv_drag)
    TextView tvDrag;
    @BindView(R.id.tv_interval)
    TextView tvInterval;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_sm)
    TextView tvSm;
    @BindView(R.id.tv_heart_rate)
    TextView tvHeartRate;
    @BindView(R.id.tv_time)
    TextView tvTime;

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

    public void onRunData(RowerDataBean1 rowerDataBean1) {
        tvStrokes.setText(String.valueOf(rowerDataBean1.getStrokes()));

        if (rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_NO) {
            tvInterval.setText(String.valueOf(rowerDataBean1.getRunInterval()));
        } else if (rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_YES) {
            if (MyConstant.isIntervalMode(rowerDataBean1.getRunMode())) {
                tvInterval.setText(String.valueOf(rowerDataBean1.getInterval()));
            } else {
                tvInterval.setText(String.valueOf(rowerDataBean1.getRunInterval() + 1));
            }
        }

        // 直接运动模式，段数都是1，没有分段
        if (rowerDataBean1.getRunMode() == MyConstant.NORMAL) {
            tvInterval.setText("1");
        }

        if (BleManager.status == BleManager.STATUS_POST) {
            tvInterval.setText("0");
        }

        tvDistance.setText(String.valueOf(rowerDataBean1.getDistance()));
        tvDrag.setText(String.valueOf(rowerDataBean1.getDrag()));
        tvSm.setText(String.valueOf(rowerDataBean1.getSm()));
        tvHeartRate.setText(String.valueOf(rowerDataBean1.getHeart_rate()));
        tvTime.setText(TimeStringUtil.getSToHourMinSecValue(rowerDataBean1.getTime()));
    }
}
