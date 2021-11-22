package com.bike.ftms.app.activity.bluetooth;

import android.os.Bundle;

import com.bike.ftms.app.R;

public class HeartRateMonitorActivity extends BluetoothActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_heart_rate_monitor;
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    public void onConnectEvent(boolean isconnect, String name) {
      /*  if (isconnect) {
            startActivity(new Intent(this,MainActivity.class));
            Intent intent = getIntent();
            setResult(ParamData.REQUEST_IS_FINISH_CODE, intent);
            finish();
        }*/
    }
}