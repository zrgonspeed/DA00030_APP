package com.bike.ftms.app.activity.fragment.pagedata;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bike.ftms.app.R;
import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
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


    // distance
    @BindView(R.id.tv_home_distance)
    TextView tv_home_distance;
    @BindView(R.id.tv_home_distance_unit)
    TextView tv_home_distance_unit;

    // time
    @BindView(R.id.tv_time)
    TextView tv_time;

    // drag cycle
    @BindView(R.id.tv_drag)
    TextView tv_drag;
    @BindView(R.id.tv_interval)
    TextView tv_interval;
    @BindView(R.id.tv_home_title_drag)
    TextView tv_home_title_drag;
    @BindView(R.id.tv_home_title_cycle)
    TextView tv_home_title_cycle;
    // hr
    @BindView(R.id.tv_heart_rate)
    TextView tv_heart_rate;

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
        return R.layout.view_pager_home_data;
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

            // watt cal  -> ave watt   cal/hr
            arr1[tv_optional_1_index][0].setVisibility(View.GONE);
            arr1[tv_optional_1_index][1].setVisibility(View.GONE);
            arr1[tv_optional_1_index][2].setVisibility(View.GONE);
            arr1[tv_optional_1_index][3].setVisibility(View.GONE);

            // /500 -> ave/500      /1km -> /1km ave
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


    @Override
    protected void initData() {

    }

    private final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onRunData(RowerDataBean1 rowerDataBean1) {
        super.onRunData(rowerDataBean1);

        // 距离
        {
            String showDistance = "0";
            long tempDistance = rowerDataBean1.getDistance();
            if (rowerDataBean1.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
                tempDistance = rowerDataBean1.getSetIntervalDistance() - rowerDataBean1.getDistance();
                if (tempDistance <= 0) {
                    tempDistance = rowerDataBean1.getSetIntervalDistance();
                }
            }

            switch (BleManager.categoryType) {
                case MyConstant.CATEGORY_BOAT: {
                    showDistance = tempDistance + "";
                }
                break;
                case MyConstant.CATEGORY_BIKE: {
                    showDistance = df.format(tempDistance / 1000.0f);
                }
                break;
                case MyConstant.CATEGORY_SKI: {
                    showDistance = tempDistance + "";
                }
                break;
            }
            tv_home_distance.setText(showDistance);

            if (BleManager.status == BleManager.STATUS_POST) {
                tv_home_distance.setText("0");
            }
        }

        // 分段设置
        {
            if (rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_NO) {
                tv_interval.setText(String.valueOf(rowerDataBean1.getRunInterval()));
            } else if (rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_YES) {
                if (MyConstant.isIntervalMode(rowerDataBean1.getRunMode())) {
                    tv_interval.setText(String.valueOf(rowerDataBean1.getInterval()));
                } else {
                    tv_interval.setText(String.valueOf(rowerDataBean1.getRunInterval() + 1));
                }
            }

            // 直接运动模式，段数都是1，没有分段
            if (rowerDataBean1.getRunMode() == MyConstant.NORMAL && rowerDataBean1.getRunStatus() == MyConstant.RUN_STATUS_YES) {
                tv_interval.setText("1");
            }

            if (BleManager.status == BleManager.STATUS_POST) {
                tv_interval.setText("0");
            }
        }

        // 时间设置
        {
            // Logger.i("1111-rowerDataBean1.getTime() == " + rowerDataBean1.getTime());
            tv_time.setText(TimeStringUtil.getSToHourMinSecValue(rowerDataBean1.getTime()));
            if (BleManager.status == BleManager.STATUS_POST) {
                tv_time.setText(TimeStringUtil.getSToHourMinSecValue(0));
            }
        }

        // 机型通用参数
        {
            tv_home_watt.setText(String.valueOf(rowerDataBean1.getWatts()));
            tv_home_cal.setText(String.valueOf(rowerDataBean1.getCalorie()));
            tv_home_ave_watt.setText(String.valueOf(rowerDataBean1.getAve_watts()));
            tv_home_cal_hr.setText(String.valueOf(rowerDataBean1.getCalories_hr()));
            tv_drag.setText(String.valueOf(rowerDataBean1.getDrag()));
            tv_heart_rate.setText(String.valueOf(rowerDataBean1.getHeart_rate()));

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

            // boat部分机型也有
            tv_home_level.setText(String.valueOf(rowerDataBean1.getLevel()));
            tv_home_level_unit.setText(getResources().getString(R.string.home_level));
        }
    }

    // TODO: 2022/3/3
    public void connected() {
        switch (BleManager.categoryType) {
            case MyConstant.CATEGORY_BIKE: {
                initBikeUI();
            }
            break;
            case MyConstant.CATEGORY_BOAT: {
                initBoatUI();
            }
            break;
            case MyConstant.CATEGORY_SKI: {
                initSkiUI();
            }
            break;
            case MyConstant.CATEGORY_STEP: {
                initStepUI();
            }
            break;
        }
    }

    private void initStepUI() {

    }

    private void initSkiUI() {
        arr2 = boatViewArr;
        // 其它隐藏
        for (View[] views : bikeViewArr) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }
        // 其它隐藏
        // TODO: 2022/3/3


        // 距离单位 m
        tv_home_distance_unit.setText(getResources().getString(R.string.home_distance));

        // 右下角 参数先显示
        arr2[0][0].setVisibility(View.VISIBLE);
        arr2[0][1].setVisibility(View.VISIBLE);
        arr2[1][0].setVisibility(View.GONE);
        arr2[1][1].setVisibility(View.GONE);

        // 显示S/M
        tv_home_sm.setVisibility(View.VISIBLE);
        tv_home_sm_unit.setVisibility(View.VISIBLE);
        tv_home_rpm.setVisibility(View.GONE);
        tv_home_rpm_unit.setVisibility(View.GONE);

        // 显示STROKES
        tv_home_strokes.setVisibility(View.VISIBLE);
        tv_home_strokes_unit.setVisibility(View.VISIBLE);
        tv_home_level.setVisibility(View.GONE);
        tv_home_level_unit.setVisibility(View.GONE);

        // 隐藏速度
        tv_home_30min_km.setVisibility(View.GONE);
        tv_home_30min_km_unit.setVisibility(View.GONE);
    }

    private void initBoatUI() {
        arr2 = boatViewArr;
        // 其它隐藏
        for (View[] views : bikeViewArr) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }
        // 其它隐藏
        // TODO: 2022/3/3


        // 距离单位 m
        tv_home_distance_unit.setText(getResources().getString(R.string.home_distance));

        // 右下角 参数先显示
        arr2[0][0].setVisibility(View.VISIBLE);
        arr2[0][1].setVisibility(View.VISIBLE);
        arr2[1][0].setVisibility(View.GONE);
        arr2[1][1].setVisibility(View.GONE);

        // 显示S/M
        tv_home_sm.setVisibility(View.VISIBLE);
        tv_home_sm_unit.setVisibility(View.VISIBLE);
        tv_home_rpm.setVisibility(View.GONE);
        tv_home_rpm_unit.setVisibility(View.GONE);

        // 显示STROKES
        tv_home_strokes.setVisibility(View.VISIBLE);
        tv_home_strokes_unit.setVisibility(View.VISIBLE);

        if (BleManager.deviceType == MyConstant.DEVICE_AA02020_00R_03) {
            tv_home_level.setVisibility(View.VISIBLE);
            tv_home_level_unit.setVisibility(View.VISIBLE);
        } else {
            tv_home_level.setVisibility(View.GONE);
            tv_home_level_unit.setVisibility(View.GONE);
        }

        // 隐藏速度
        tv_home_30min_km.setVisibility(View.GONE);
        tv_home_30min_km_unit.setVisibility(View.GONE);
    }

    private void initBikeUI() {
        arr2 = bikeViewArr;
        // boatViewArr 全部隐藏
        for (View[] views : boatViewArr) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }
        // 其它隐藏
        // TODO: 2022/3/3

        // 右下角 参数先显示
        arr2[0][0].setVisibility(View.VISIBLE);
        arr2[0][1].setVisibility(View.VISIBLE);
        arr2[1][0].setVisibility(View.GONE);
        arr2[1][1].setVisibility(View.GONE);

        // 距离单位 km
        tv_home_distance_unit.setText(getResources().getString(R.string.home_distance_km));

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

    @BindView(R.id.ll1)
    LinearLayout ll1;
    @BindView(R.id.ll2)
    LinearLayout ll2;
    @BindView(R.id.ll3)
    LinearLayout ll3;

    @BindView(R.id.rl1)
    RelativeLayout rl1;
    @BindView(R.id.rl2)
    RelativeLayout rl2;
    @BindView(R.id.rl3)
    RelativeLayout rl3;
    @BindView(R.id.rl4)
    RelativeLayout rl4;
    @BindView(R.id.rl5)
    RelativeLayout rl5;
    @BindView(R.id.rl6)
    RelativeLayout rl6;

    @BindView(R.id.rl_blank)
    RelativeLayout rl_blank;

    @Override
    public void setPortLayout() {
        {
            // 顶部3个
            ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) ll1.getLayoutParams();
            params1.height = getIntDimen(R.dimen.dp_100);
            params1.bottomToTop = -1;
            params1.endToStart = -1;
            params1.matchConstraintPercentWidth = 0.31f;
            ll1.setLayoutParams(params1);

            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) ll2.getLayoutParams();
            params2.height = getIntDimen(R.dimen.dp_100);
            params2.topMargin = 0;
            params2.bottomMargin = 0;
            params2.leftMargin = getIntDimen(R.dimen.dp_10);
            params2.bottomToTop = -1;
            params2.endToEnd = -1;
            params2.startToStart = -1;
            params2.topToBottom = -1;
            params2.bottomToBottom = R.id.ll1;
            params2.startToEnd = R.id.ll1;
            params2.topToTop = R.id.ll1;
            params2.matchConstraintPercentWidth = 0.32f;
            ll2.setLayoutParams(params2);

            ConstraintLayout.LayoutParams params3 = (ConstraintLayout.LayoutParams) ll3.getLayoutParams();
            params3.leftMargin = getIntDimen(R.dimen.dp_10);
            params3.bottomToBottom = R.id.ll2;
            params3.endToEnd = -1;
            params3.startToStart = -1;
            params3.topToBottom = -1;
            params3.startToEnd = R.id.ll2;
            params3.topToTop = PARENT_ID;
            params3.matchConstraintPercentWidth = 0.32f;
            ll3.setLayoutParams(params3);

            ConstraintLayout.LayoutParams paramsRl1 = (ConstraintLayout.LayoutParams) rl1.getLayoutParams();
            paramsRl1.topMargin = getIntDimen(R.dimen.dp_10);
            paramsRl1.bottomToTop = -1;
            paramsRl1.endToStart = -1;
            paramsRl1.matchConstraintPercentHeight = 0.1f;
            paramsRl1.horizontalBias = 0.6f;
            paramsRl1.startToEnd = -1;
            paramsRl1.topToTop = -1;
            paramsRl1.startToStart = PARENT_ID;
            paramsRl1.topToBottom = R.id.ll1;
            paramsRl1.verticalBias = 0.2f;
            paramsRl1.matchConstraintPercentWidth = 0.66f;
            rl1.setLayoutParams(paramsRl1);

            ConstraintLayout.LayoutParams paramsRl2 = (ConstraintLayout.LayoutParams) rl2.getLayoutParams();
            paramsRl2.leftMargin = getIntDimen(R.dimen.dp_10);
            paramsRl2.topMargin = getIntDimen(R.dimen.dp_10);
            paramsRl2.bottomToTop = -1;
            paramsRl2.bottomToBottom = R.id.rl1;
            paramsRl2.topToTop = -1;
            paramsRl2.topToBottom = R.id.ll1;
            rl2.setLayoutParams(paramsRl2);

            ConstraintLayout.LayoutParams paramsRl3 = (ConstraintLayout.LayoutParams) rl3.getLayoutParams();
            paramsRl3.topMargin = getIntDimen(R.dimen.dp_10);
            paramsRl3.bottomToTop = -1;
            paramsRl3.matchConstraintPercentHeight = 0.1f;
            paramsRl3.startToStart = PARENT_ID;
            rl3.setLayoutParams(paramsRl3);

            ConstraintLayout.LayoutParams paramsRl4 = (ConstraintLayout.LayoutParams) rl4.getLayoutParams();
            paramsRl4.topMargin = getIntDimen(R.dimen.dp_10);
            paramsRl4.bottomToTop = -1;
            paramsRl4.matchConstraintPercentHeight = 0.1f;
            paramsRl4.startToStart = PARENT_ID;
            rl4.setLayoutParams(paramsRl4);

            ConstraintLayout.LayoutParams paramsRl5 = (ConstraintLayout.LayoutParams) rl5.getLayoutParams();
            paramsRl5.topMargin = getIntDimen(R.dimen.dp_10);
            paramsRl5.bottomToBottom = -1;
            paramsRl5.matchConstraintPercentHeight = 0.1f;
            paramsRl5.startToStart = PARENT_ID;
            rl5.setLayoutParams(paramsRl5);

            ConstraintLayout.LayoutParams paramsRl6 = (ConstraintLayout.LayoutParams) rl6.getLayoutParams();
            paramsRl6.leftMargin = getIntDimen(R.dimen.dp_10);
            paramsRl6.bottomToBottom = -1;
            paramsRl6.matchConstraintPercentHeight = 0.1f;
            rl6.setLayoutParams(paramsRl6);

            rl_blank.setVisibility(View.VISIBLE);

        }
        // 字体
        setTextSize((int) (getIntDimen(R.dimen.sp_14)), (int) (getIntDimen(R.dimen.sp_20)));
        tv_home_30min_km_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (getIntDimen(R.dimen.sp_14)));
        // 部分文本
        {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_home_30min_km.getLayoutParams();
            layoutParams.leftMargin = getIntDimen(R.dimen.dp_80);
            tv_home_30min_km.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) tv_home_cal.getLayoutParams();
            layoutParams1.leftMargin = getIntDimen(R.dimen.dp_130);
            tv_home_cal.setLayoutParams(layoutParams1);

            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) tv_home_cal_hr.getLayoutParams();
            layoutParams2.leftMargin = getIntDimen(R.dimen.dp_130);
            tv_home_cal_hr.setLayoutParams(layoutParams2);
        }
    }

    @Override
    public void setLandLayout() {
        {
            // 顶部3个
            ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) ll1.getLayoutParams();
            params1.height = getIntDimen(R.dimen.dp_0);
            params1.bottomToTop = R.id.ll2;
            params1.endToStart = R.id.rl1;
            params1.matchConstraintPercentWidth = 1.0f;
            ll1.setLayoutParams(params1);

            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) ll2.getLayoutParams();
            params2.height = getIntDimen(R.dimen.dp_0);
            params2.topMargin = getIntDimen(R.dimen.dp_5);
            params2.bottomMargin = getIntDimen(R.dimen.dp_5);
            params2.leftMargin = 0;
            params2.bottomToTop = R.id.ll3;
            params2.endToEnd = R.id.ll1;
            params2.startToStart = R.id.ll1;
            params2.topToBottom = R.id.ll1;
            params2.bottomToBottom = -1;
            params2.startToEnd = -1;
            params2.topToTop = -1;
            params2.matchConstraintPercentWidth = 1.0f;
            ll2.setLayoutParams(params2);

            ConstraintLayout.LayoutParams params3 = (ConstraintLayout.LayoutParams) ll3.getLayoutParams();
            params3.leftMargin = 0;
            params3.bottomToBottom = PARENT_ID;
            params3.endToEnd = R.id.ll1;
            params3.startToStart = R.id.ll1;
            params3.topToBottom = R.id.ll2;
            params3.startToEnd = -1;
            params3.topToTop = -1;
            params3.matchConstraintPercentWidth = 1.0f;
            ll3.setLayoutParams(params3);

            ConstraintLayout.LayoutParams paramsRl1 = (ConstraintLayout.LayoutParams) rl1.getLayoutParams();
            paramsRl1.topMargin = 0;
            paramsRl1.bottomToTop = R.id.rl3;
            paramsRl1.endToStart = R.id.rl2;
            paramsRl1.matchConstraintPercentHeight = 1.0f;
            paramsRl1.horizontalBias = 0.5f;
            paramsRl1.startToEnd = R.id.ll1;
            paramsRl1.topToTop = PARENT_ID;
            paramsRl1.startToStart = -1;
            paramsRl1.topToBottom = -1;
            paramsRl1.verticalBias = 0.5f;
            paramsRl1.matchConstraintPercentWidth = 1.0f;
            rl1.setLayoutParams(paramsRl1);

            ConstraintLayout.LayoutParams paramsRl2 = (ConstraintLayout.LayoutParams) rl2.getLayoutParams();
            paramsRl2.leftMargin = 0;
            paramsRl2.topMargin = 0;
            paramsRl2.bottomToTop = R.id.rl3;
            paramsRl2.bottomToBottom = -1;
            paramsRl2.topToTop = PARENT_ID;
            paramsRl2.topToBottom = -1;
            rl2.setLayoutParams(paramsRl2);

            ConstraintLayout.LayoutParams paramsRl3 = (ConstraintLayout.LayoutParams) rl3.getLayoutParams();
            paramsRl3.topMargin = getIntDimen(R.dimen.dp_5);
            paramsRl3.bottomToTop = R.id.rl4;
            paramsRl3.matchConstraintPercentHeight = 1.0f;
            paramsRl3.startToStart = R.id.rl1;
            rl3.setLayoutParams(paramsRl3);

            ConstraintLayout.LayoutParams paramsRl4 = (ConstraintLayout.LayoutParams) rl4.getLayoutParams();
            paramsRl4.topMargin = getIntDimen(R.dimen.dp_5);
            paramsRl4.bottomToTop = R.id.rl5;
            paramsRl4.matchConstraintPercentHeight = 1.0f;
            paramsRl4.startToStart = R.id.rl1;
            rl4.setLayoutParams(paramsRl4);

            ConstraintLayout.LayoutParams paramsRl5 = (ConstraintLayout.LayoutParams) rl5.getLayoutParams();
            paramsRl5.topMargin = 0;
            paramsRl5.bottomToBottom = PARENT_ID;
            paramsRl5.matchConstraintPercentHeight = 1.0f;
            paramsRl5.startToStart = R.id.rl1;
            rl5.setLayoutParams(paramsRl5);

            ConstraintLayout.LayoutParams paramsRl6 = (ConstraintLayout.LayoutParams) rl6.getLayoutParams();
            paramsRl6.leftMargin = getIntDimen(R.dimen.dp_5);
            paramsRl6.bottomToBottom = PARENT_ID;
            paramsRl6.matchConstraintPercentHeight = 1.0f;
            rl6.setLayoutParams(paramsRl6);

            rl_blank.setVisibility(View.GONE);
        }
        // 字体
        setTextSize((int) (getIntDimen(R.dimen.sp_14)), (int) (getIntDimen(R.dimen.sp_20)));
        tv_home_30min_km_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (getIntDimen(R.dimen.sp_14)));
        // 部分文本
        {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_home_30min_km.getLayoutParams();
            layoutParams.leftMargin = getIntDimen(R.dimen.dp_150);
            tv_home_30min_km.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) tv_home_cal.getLayoutParams();
            layoutParams1.leftMargin = getIntDimen(R.dimen.dp_200);
            tv_home_cal.setLayoutParams(layoutParams1);

            RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) tv_home_cal_hr.getLayoutParams();
            layoutParams2.leftMargin = getIntDimen(R.dimen.dp_200);
            tv_home_cal_hr.setLayoutParams(layoutParams2);
        }
    }

    private void setTextSize(int unitSize, int valueSize) {
        Logger.i("fontSize == " + unitSize + "  " + valueSize);

        tv_home_strokes_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        Logger.i("newSize == " + tv_home_strokes_unit.getTextSize());
        tv_home_level_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_title_drag.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_title_cycle.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_distance_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_30min_km_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_sm_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_rpm_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_watt_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_cal_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_ave_watt_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_cal_hr_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_500_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_ave_500_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_one_km.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_ave_one_km_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);
        tv_home_one_km_unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, unitSize);

        tv_home_strokes.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_level.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_drag.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_interval.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_distance.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_30min_km.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_sm.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_rpm.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_watt.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_cal.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_ave_watt.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_cal_hr.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_heart_rate.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_500.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_ave_500.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_one_km.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
        tv_home_ave_one_km.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueSize);
    }

    public void onHeartData(int heart) {
        tv_heart_rate.setText(String.valueOf(heart));
    }
}
