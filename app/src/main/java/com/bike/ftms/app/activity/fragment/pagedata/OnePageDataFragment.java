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

import butterknife.BindView;


/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public class OnePageDataFragment extends BasePageDataFragment {
    private static final String TAG = OnePageDataFragment.class.getSimpleName();

    @BindView(R.id.tv_optional_1)
    TextView tv_optional_1;
    @BindView(R.id.tv_optional_1_unit)
    TextView tv_optional_1_unit;
    @BindView(R.id.tv_optional_11)
    TextView tv_optional_11;
    @BindView(R.id.tv_optional_11_unit)
    TextView tv_optional_11_unit;
    @BindView(R.id.tv_optional_111)
    TextView tv_optional_111;
    @BindView(R.id.tv_optional_111_unit)
    TextView tv_optional_111_unit;


    @BindView(R.id.tv_optional_2)
    TextView tv_optional_2;
    @BindView(R.id.tv_optional_2_unit)
    TextView tv_optional_2_unit;
    @BindView(R.id.tv_optional_22)
    TextView tv_optional_22;
    @BindView(R.id.tv_optional_22_unit)
    TextView tv_optional_22_unit;
    @BindView(R.id.tv_optional_222)
    TextView tv_optional_222;
    @BindView(R.id.tv_optional_222_unit)
    TextView tv_optional_222_unit;

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

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        super.initView(view, container, savedInstanceState);

        arr1 = new View[][]{
                {tv_optional_1, tv_optional_1_unit},
                {tv_optional_11, tv_optional_11_unit},
                {tv_optional_111, tv_optional_111_unit}
        };
        arr2 = new View[][]{
                {tv_optional_2, tv_optional_2_unit},
                {tv_optional_22, tv_optional_22_unit},
                {tv_optional_222, tv_optional_222_unit}
        };
        toggleOption(rl3);
        toggleOption(rl6);
    }

    private void toggleOption(View view) {
        view.setOnClickListener((v) -> {
            // 切换数据显示，3种
            arr1[tv_optional_1_index][0].setVisibility(View.GONE);
            arr1[tv_optional_1_index][1].setVisibility(View.GONE);
            arr2[tv_optional_1_index][0].setVisibility(View.GONE);
            arr2[tv_optional_1_index][1].setVisibility(View.GONE);
            tv_optional_1_index = (tv_optional_1_index + 1) % 3;
            arr1[tv_optional_1_index][0].setVisibility(View.VISIBLE);
            arr1[tv_optional_1_index][1].setVisibility(View.VISIBLE);
            arr2[tv_optional_1_index][0].setVisibility(View.VISIBLE);
            arr2[tv_optional_1_index][1].setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onRunData(RowerDataBean1 rowerDataBean1) {
        super.onRunData(rowerDataBean1);
        tv_optional_11.setText(String.valueOf(rowerDataBean1.getWatts()));
        tv_optional_111.setText(String.valueOf(rowerDataBean1.getCalorie()));

        tv_optional_22.setText(String.valueOf(rowerDataBean1.getAve_watts()));
        tv_optional_222.setText(String.valueOf(rowerDataBean1.getCalories_hr()));

        switch (BleManager.categoryType) {
            case MyConstant.CATEGORY_BIKE: {
                tv_optional_1.setText(String.valueOf(rowerDataBean1.getInstSpeed()));
                tv_optional_1_unit.setText(getResources().getString(R.string.home_km_30min));

                tv_optional_2.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAve_five_hundred()));
                tv_optional_2_unit.setText(getResources().getString(R.string.home_ave_500));
            }
            break;
            case MyConstant.CATEGORY_BOAT: {
                tv_optional_1.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getFive_hundred()));
                tv_optional_1_unit.setText(getResources().getString(R.string.home_500));

                tv_optional_2.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAve_five_hundred()));
                tv_optional_2_unit.setText(getResources().getString(R.string.home_ave_500));
            }
            break;
        }
    }
}
