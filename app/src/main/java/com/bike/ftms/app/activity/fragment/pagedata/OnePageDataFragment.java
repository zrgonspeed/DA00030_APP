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
    @BindView(R.id.tv_optional_2)
    TextView tv_optional_2;

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

        switch (BleManager.categoryType) {
            case MyConstant.CATEGORY_BIKE: {
                tv_optional_1.setText(String.valueOf(rowerDataBean1.getInstSpeed()));
                tv_optional_1_unit.setText(getResources().getString(R.string.home_km_30min));

                tv_optional_2.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAve_five_hundred()));
            }
            break;
            case MyConstant.CATEGORY_BOAT: {
                tv_optional_1.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getFive_hundred()));
                tv_optional_1_unit.setText(getResources().getString(R.string.home_500));

                tv_optional_2.setText(TimeStringUtil.getSToMinSecValue(rowerDataBean1.getAve_five_hundred()));
            }
            break;
        }
    }
}
