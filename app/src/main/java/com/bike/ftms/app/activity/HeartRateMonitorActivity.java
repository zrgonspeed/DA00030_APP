package com.bike.ftms.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.common.ParamData;

import butterknife.BindView;
import butterknife.OnClick;

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