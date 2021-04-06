package com.bike.ftms.app.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.BleAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.manager.ble.OnScanConnectListener;
import com.bike.ftms.app.utils.Logger;

import butterknife.BindView;
import butterknife.OnClick;

public class BluetoothActivity extends BaseActivity implements OnScanConnectListener, BleAdapter.OnItemClickListener {
    private static final int BAIDU_READ_PHONE_STATE = 1000;
    @BindView(R.id.cb_switch)
    CheckBox cbSwitch;
    @BindView(R.id.ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.rv_ble)
    RecyclerView rvBle;
    private BleAdapter bleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        BleManager.getInstance().getBluetoothAdapter();
        BleManager.getInstance().getScanResults();
        cbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!BleManager.isConnect) {
                        scanDevice();
                    }
                } else {
                    BleManager.getInstance().closeBLE();
                    BleManager.getInstance().getScanResults().clear();
                    llLoading.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cbSwitch.setChecked(BleManager.getInstance().getBluetoothAdapter().isEnabled());
        if (BleManager.getInstance().getBluetoothAdapter().isEnabled() && !BleManager.isCanning) {
            scanDevice();
        }
        BleManager.getInstance().setOnScanConnectListener(this);

    }

    private void scanDevice() {
        //判断是否为android6.0系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
        } else {
            llLoading.setVisibility(View.VISIBLE);
            BleManager.getInstance().scanDevice();
        }
    }

    //请求权限
    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED
        ) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
            }, BAIDU_READ_PHONE_STATE);
        } else {
            llLoading.setVisibility(View.VISIBLE);
            BleManager.getInstance().openBLE();
            BleManager.getInstance().scanDevice();
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    llLoading.setVisibility(View.VISIBLE);
                    BleManager.getInstance().scanDevice();
                } else {
                    // 没有获取到权限，做特殊处理
                    Toast.makeText(getApplicationContext(), "Failed to get permission. Please open it manually!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onScanSuccess() {
        Logger.d("onScanSuccess");
        if (bleAdapter != null) {
            bleAdapter.notifyDataSetChanged();
        } else {
            bleAdapter = new BleAdapter(BleManager.getInstance().getScanResults());
            bleAdapter.addItemClickListener(this);
            rvBle.setAdapter(bleAdapter);
            rvBle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void onStopScan() {
        llLoading.setVisibility(View.GONE);
    }

    @Override
    public void onConnectEvent(boolean isconnect, String name) {
        if (isconnect) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BleManager.getInstance().stopScan();
        BleManager.getInstance().setOnScanConnectListener(null);
    }

    @Override
    public void onItemClickListener(int position) {
        BleManager.getInstance().connectDevice(position);
    }
}