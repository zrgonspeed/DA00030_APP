package com.bike.ftms.app.activity.user;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.utils.Logger;

public class PersonalDataActivity extends BaseActivity {
    private static final String TAG = PersonalDataActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.personal_data_activity;
    }

    @Override
    protected void initData() {
        Logger.i(TAG, "initData()");
    }

    @Override
    protected void initView() {
        Logger.i(TAG, "initView()");

    }
}
