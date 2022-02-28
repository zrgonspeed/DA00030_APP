package com.bike.ftms.app.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bike.ftms.app.Debug;
import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.bluetooth.BluetoothActivity;
import com.bike.ftms.app.activity.bluetooth.HeartRateMonitorActivity;
import com.bike.ftms.app.activity.user.LoginActivity;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private final int HR_REQUEST_CODE = 100;

    @BindView(R.id.iv_setting_login)
    ImageView iv_setting_login;

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        if (!Debug.canLogin) {
            iv_setting_login.setEnabled(false);
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_setting_login, R.id.iv_setting_bluetooth, R.id.iv_setting_hr, R.id.iv_setting_version})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_setting_login:
                if (UserManager.getInstance().getUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    ToastUtil.show(getString(R.string.logged), ToastUtil.Mode.REPLACEABLE);
                }
                break;
            case R.id.iv_setting_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.iv_setting_hr:
                Intent intent = new Intent(this, HeartRateMonitorActivity.class);
                intent.putExtra("isScanHrDevice", true);
                startActivityForResult(intent, HR_REQUEST_CODE);
                break;
            case R.id.iv_setting_version:
                startActivity(new Intent(this, VersionActivity.class));
                break;
        }
    }
}