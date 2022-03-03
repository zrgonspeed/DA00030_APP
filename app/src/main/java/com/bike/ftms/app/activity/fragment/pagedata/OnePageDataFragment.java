package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.text.DecimalFormat;

import butterknife.BindView;


/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class OnePageDataFragment extends BasePageDataFragment {
    private static final String TAG = OnePageDataFragment.class.getSimpleName();

    // strokes
    @BindView(R.id.tv_home_strokes)
    TextView tv_home_strokes;
    @BindView(R.id.tv_home_strokes_unit)
    TextView tv_home_strokes_unit;

    // level
    @BindView(R.id.tv_home_level)
    TextView tv_home_level;
    @BindView(R.id.tv_home_level_unit)
    TextView tv_home_level_unit;

    // watt  cal
    @BindView(R.id.tv_home_watt)
    TextView tv_home_watt;
    @BindView(R.id.tv_home_watt_unit)
    TextView tv_home_watt_unit;
    @BindView(R.id.tv_home_cal)
    TextView tv_home_cal;
    @BindView(R.id.tv_home_cal_unit)
    TextView tv_home_cal_unit;

    // ave watt   cal hr
    @BindView(R.id.tv_home_ave_watt)
    TextView tv_home_ave_watt;
    @BindView(R.id.tv_home_ave_watt_unit)
    TextView tv_home_ave_watt_unit;
    @BindView(R.id.tv_home_cal_hr)
    TextView tv_home_cal_hr;
    @BindView(R.id.tv_home_cal_hr_unit)
    TextView tv_home_cal_hr_unit;

    // one km      ave one km
    @BindView(R.id.tv_home_one_km)
    TextView tv_home_one_km;
    @BindView(R.id.tv_home_one_km_unit)
    TextView tv_home_one_km_unit;
    @BindView(R.id.tv_home_ave_one_km)
    TextView tv_home_ave_one_km;
    @BindView(R.id.tv_home_ave_one_km_unit)
    TextView tv_home_ave_one_km_unit;

    // /500   ave/500
    @BindView(R.id.tv_home_500)
    TextView tv_home_500;
    @BindView(R.id.tv_home_500_unit)
    TextView tv_home_500_unit;
    @BindView(R.id.tv_home_ave_500)
    TextView tv_home_ave_500;
    @BindView(R.id.tv_home_ave_500_unit)
    TextView tv_home_ave_500_unit;

    // distance
    @BindView(R.id.tv_home_distance)
    TextView tvDistance;
    @BindView(R.id.tv_home_distance_unit)
    TextView tv_home_distance_unit;

    // speed
    @BindView(R.id.tv_home_30min_km)
    TextView tv_home_30min_km;
    @BindView(R.id.tv_home_30min_km_unit)
    TextView tv_home_30min_km_unit;

    // rpm
    @BindView(R.id.tv_home_rpm)
    TextView tv_home_rpm;
    @BindView(R.id.tv_home_rpm_unit)
    TextView tv_home_rpm_unit;

    // sm
    @BindView(R.id.tv_home_sm)
    TextView tv_home_sm;
    @BindView(R.id.tv_home_sm_unit)
    TextView tv_home_sm_unit;

    public OnePageDataFragment() {
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pager_home1;
    }

    private int tv_optional_1_index = 0;
    private View[][] arr1;

    private View[][] arr2;
    private View[][] arr3;
    private View[][] boatViewArr;
    private View[][] bikeViewArr;

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);

        arr1 = new View[][]{
                {tv_home_watt, tv_home_watt_unit, tv_home_cal, tv_home_cal_unit},
                {tv_home_ave_watt, tv_home_ave_watt_unit, tv_home_cal_hr, tv_home_cal_hr_unit}
        };

        boatViewArr = new View[][]{
                {tv_home_500, tv_home_500_unit},
                {tv_home_ave_500, tv_home_ave_500_unit},
        };
        bikeViewArr = new View[][]{
                {tv_home_one_km, tv_home_one_km_unit},
                {tv_home_ave_one_km, tv_home_ave_one_km_unit},
        };

        arr2 = boatViewArr;
        toggleOption(rl3);
        toggleOption(rl6);
    }

    private void toggleOption(View view) {
        view.setOnClickListener((v) -> {
            // 切换数据显示，3种
            arr1[tv_optional_1_index][0].setVisibility(View.GONE);
            arr1[tv_optional_1_index][1].setVisibility(View.GONE);
            arr1[tv_optional_1_index][2].setVisibility(View.GONE);
            arr1[tv_optional_1_index][3].setVisibility(View.GONE);

            arr2[tv_optional_1_index][0].setVisibility(View.GONE);
            arr2[tv_optional_1_index][1].setVisibility(View.GONE);

            tv_optional_1_index = (tv_optional_1_index + 1) % 2;

            arr1[tv_optional_1_index][0].setVisibility(View.VISIBLE);
            arr1[tv_optional_1_index][1].setVisibility(View.VISIBLE);
            arr1[tv_optional_1_index][2].setVisibility(View.VISIBLE);
            arr1[tv_optional_1_index][3].setVisibility(View.VISIBLE);

            arr2[tv_optional_1_index][0].setVisibility(View.VISIBLE);
            arr2[tv_optional_1_index][1].setVisibility(View.VISIBLE);
        });
    }

    private void selectBoat() {
    }

    private void selectBike() {
        arr2 = bikeViewArr;

        // boatViewArr 全部隐藏
        for (View[] views : boatViewArr) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }

        // 右下角
        arr2[0][0].setVisibility(View.VISIBLE);
        arr2[0][1].setVisibility(View.VISIBLE);
        arr2[1][0].setVisibility(View.GONE);
        arr2[1][1].setVisibility(View.GONE);

        // 加上速度
        tv_home_30min_km.setVisibility(View.VISIBLE);
        tv_home_30min_km_unit.setVisibility(View.VISIBLE);

        // 显示RPM
        tv_home_rpm.setVisibility(View.VISIBLE);
        tv_home_rpm_unit.setVisibility(View.VISIBLE);
        tv_home_sm.setVisibility(View.GONE);
        tv_home_sm_unit.setVisibility(View.GONE);

        // 显示LEVEL
        tv_home_level.setVisibility(View.VISIBLE);
        tv_home_level_unit.setVisibility(View.VISIBLE);
        tv_home_strokes.setVisibility(View.GONE);
        tv_home_strokes_unit.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onRunData(RowerDataBean1 rowerDataBean1) {
        super.onRunData(rowerDataBean1);

        DecimalFormat df = new DecimalFormat("0.00");
        // 距离
        {
            if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
                long showDistance = rowerDataBean1.getSetIntervalDistance() - rowerDataBean1.getDistance();
                if (showDistance <= 0) {
                    showDistance = rowerDataBean1.getSetIntervalDistance();
                }
                tvDistance.setText(showDistance == 0 ? "0" : String.valueOf(df.format(showDistance / 1000.0f)));
            } else {
                tvDistance.setText(rowerDataBean1.getDistance() == 0 ? "0" : String.valueOf(df.format(rowerDataBean1.getDistance() / 1000.0f)));
            }

            if (BleManager.status == BleManager.STATUS_POST) {
                tvDistance.setText("0");
            }
        }

        // 分段设置
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

        // 时间设置
        {
            // Logger.i("1111-rowerDataBean1.getTime() == " + rowerDataBean1.getTime());
            tvTime.setText(TimeStringUtil.getSToHourMinSecValue(rowerDataBean1.getTime()));
            if (BleManager.status == BleManager.STATUS_POST) {
                tvTime.setText(TimeStringUtil.getSToHourMinSecValue(0));
            }
        }

        // 机型通用参数
        tv_home_watt.setText(String.valueOf(rowerDataBean1.getWatts()));
        tv_home_cal.setText(String.valueOf(rowerDataBean1.getCalorie()));
        tv_home_ave_watt.setText(String.valueOf(rowerDataBean1.getAve_watts()));
        tv_home_cal_hr.setText(String.valueOf(rowerDataBean1.getCalories_hr()));
        tvDrag.setText(String.valueOf(rowerDataBean1.getDrag()));
        tvHeartRate.setText(String.valueOf(rowerDataBean1.getHeart_rate()));

        // boat
        tv_home_500.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getFive_hundred()));
        tv_home_ave_500.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAve_five_hundred()));
        tv_home_sm.setText(String.valueOf(rowerDataBean1.getSm()));
        tv_home_strokes.setText(String.valueOf(rowerDataBean1.getStrokes()));
        tv_home_strokes_unit.setText(getResources().getString(R.string.home_strokes));

        // bike
        tv_home_one_km.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getOneKmTime()));
        tv_home_ave_one_km.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAveOneKmTime()));
        tv_home_30min_km.setText(df.format(rowerDataBean1.getInstSpeed() / 100.0f));
        tv_home_rpm.setText(String.valueOf(rowerDataBean1.getInstRpm()));
        tv_home_level.setText(String.valueOf(rowerDataBean1.getLevel()));
        tv_home_level_unit.setText(getResources().getString(R.string.home_level));
    }

    public void connected() {
        switch (BleManager.categoryType) {
            case MyConstant.CATEGORY_BIKE: {
                selectBike();
            }
            break;
            case MyConstant.CATEGORY_BOAT: {
                selectBoat();
            }
            break;
        }

    }
}
