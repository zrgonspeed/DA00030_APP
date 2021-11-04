package com.bike.ftms.app.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    @Nullable
    @BindView(R.id.tv_switch)
    TextView tv_switch;
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

        Logger.e("onCreate " + this.toString());
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
                    Logger.e("打开蓝牙啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");

                    if (tv_switch != null) {
                        tv_switch.setText(getResources().getString(R.string.bluetooth_switch_enable));
                    }
                    if (!BleManager.isConnect) {
                        scanDevice();
                    }

//                    if () {
//
//                    } else {
//                        cbSwitch.setChecked(false);
//                    }

                } else {
                    Logger.e("getScanResults(): " + BleManager.getInstance().getScanResults());
                    Logger.e("断开蓝牙啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");
                    if (tv_switch != null) {
                        tv_switch.setText(getResources().getString(R.string.bluetooth_switch_disable));
                    }

                    boolean closed = BleManager.getInstance().closeBLE();
                    if (closed) {
                        BleManager.getInstance().stopScan();
//                    BleManager.getInstance().disableCharacterNotifiy();
//                    BleManager.getInstance().disConnectAllDevice();
//                    BleManager.getInstance().getScanResults().clear();
//                    BleManager.getInstance().close();
                        if (bleAdapter != null) {
                            bleAdapter.notifyDataSetChanged();
                        }
                        llLoading.setVisibility(View.GONE);
                        rvBle.setVisibility(View.GONE);
                    } else {
                        cbSwitch.setChecked(true);
                    }
                }
            }
        });
        rvBle.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Logger.e(getIntent().toString() + "");

        BleManager.getInstance().setOnScanConnectListener(this);
        BleManager.getInstance().setIsScanHrDevice(getIntent().getBooleanExtra("isScanHrDevice", false));
        cbSwitch.setChecked(BleManager.getInstance().getBluetoothAdapter().isEnabled());
        if (tv_switch != null) {
            tv_switch.setText(cbSwitch.isChecked() ? getResources().getString(R.string.bluetooth_switch_enable) : getResources().getString(R.string.bluetooth_switch_disable));
        }
        if (BleManager.getInstance().getBluetoothAdapter().isEnabled() && !BleManager.isCanning) {
            scanDevice();
        }

    }

    private void scanDevice() {
        //判断是否为android6.0系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
        } else {
            llLoading.setVisibility(View.VISIBLE);
            rvBle.setVisibility(View.VISIBLE);
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
            Logger.e("没有权限，请求权限");
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
            Logger.e("有权限了");
            llLoading.setVisibility(View.VISIBLE);
            BleManager.getInstance().openBLE();
            BleManager.getInstance().getScanResults().clear();
            if (bleAdapter != null)
                bleAdapter.notifyDataSetChanged();
            rvBle.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> {
                BleManager.getInstance().scanDevice();
            }, 2000);
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e("-----------------------------------------------------");
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    llLoading.setVisibility(View.VISIBLE);
                    rvBle.setVisibility(View.VISIBLE);
                    BleManager.getInstance().scanDevice();
                } else {
                    // 没有获取到权限，做特殊处理
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
        Logger.i("onScanSuccess");
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
        /*if (isconnect) {
            finish();
        }*/
    }

    @Override
    public void onNotifyData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bleAdapter != null) {
                    bleAdapter.notifyDataSetChanged();
                }
            }
        });

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