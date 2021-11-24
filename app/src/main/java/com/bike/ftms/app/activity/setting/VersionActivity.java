package com.bike.ftms.app.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.utils.VersionUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class VersionActivity extends BaseActivity {

    @BindView(R.id.tv_version_apk_value)
    TextView tv_version_apk_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_version;
    }

    @Override
    protected void initData() {
        tv_version_apk_value.setText(VersionUtil.getAppVersionName(this));
    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.iv_back, R.id.btn_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_update:
                break;
        }
    }
}