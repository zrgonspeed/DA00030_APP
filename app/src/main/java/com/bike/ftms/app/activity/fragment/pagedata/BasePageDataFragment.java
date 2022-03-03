package com.bike.ftms.app.activity.fragment.pagedata;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.utils.Logger;

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
