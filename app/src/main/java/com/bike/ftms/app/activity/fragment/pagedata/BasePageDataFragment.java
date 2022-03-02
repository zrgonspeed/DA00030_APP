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
    @BindView(R.id.tv_optional_4)
    TextView tv_optional_4;
    @BindView(R.id.tv_optional_4_unit)
    TextView tv_optional_4_unit;
    @BindView(R.id.tv_drag)
    TextView tvDrag;
    @BindView(R.id.tv_interval)
    TextView tvInterval;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_optional_3)
    TextView tv_optional_3;
    @BindView(R.id.tv_optional_3_unit)
    TextView tv_optional_3_unit;
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

        {
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
            if (rowerDataBean1.getRunMode() == MyConstant.NORMAL && rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_YES) {
                tvInterval.setText("1");
            }

            if (BleManager.status == BleManager.STATUS_POST) {
                tvInterval.setText("0");
            }
        }

        {
            if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
                long showDistance = rowerDataBean1.getSetIntervalDistance() - rowerDataBean1.getDistance();
                if (showDistance <= 0) {
                    showDistance = rowerDataBean1.getSetIntervalDistance();
                }
                tvDistance.setText(String.valueOf(showDistance));
            } else {
                tvDistance.setText(String.valueOf(rowerDataBean1.getDistance()));
            }

            if (BleManager.status == BleManager.STATUS_POST) {
                tvDistance.setText("0");
            }
        }

        {
            // Logger.i("1111-rowerDataBean1.getTime() == " + rowerDataBean1.getTime());
            tvTime.setText(TimeStringUtil.getSToHourMinSecValue(rowerDataBean1.getTime()));
            if (BleManager.status == BleManager.STATUS_POST) {
                tvTime.setText(TimeStringUtil.getSToHourMinSecValue(0));
            }
        }

        tvDrag.setText(String.valueOf(rowerDataBean1.getDrag()));
        tvHeartRate.setText(String.valueOf(rowerDataBean1.getHeart_rate()));

        switch (BleManager.categoryType) {
            case MyConstant.CATEGORY_BIKE: {
                tv_optional_3.setText(String.valueOf(rowerDataBean1.getInstRpm()));
                tv_optional_3_unit.setText(getResources().getString(R.string.home_rpm));

                tv_optional_4.setText(String.valueOf(rowerDataBean1.getLevel()));
                tv_optional_4_unit.setText(getResources().getString(R.string.home_level));
            }
            break;
            case MyConstant.CATEGORY_BOAT: {
                tv_optional_3.setText(String.valueOf(rowerDataBean1.getSm()));
                tv_optional_3_unit.setText(getResources().getString(R.string.home_sm));

                tv_optional_4.setText(String.valueOf(rowerDataBean1.getStrokes()));
                tv_optional_4_unit.setText(getResources().getString(R.string.home_strokes));
            }
            break;
        }
    }
}
