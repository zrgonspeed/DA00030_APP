package com.bike.ftms.app.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
    public static final String TAG = BluetoothActivity.class.getSimpleName();
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

//        Logger.e(TAG,"onCreate " + this.toString());
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
        cbSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Logger.e(TAG, "OnCheckedChange---------------------------------------------------");
            if (isChecked) {
                Logger.e(TAG, "打开蓝牙?");
                if (tv_switch != null) {
                    tv_switch.setText(getResources().getString(R.string.bluetooth_switch_enable));
                }
                scanDevice();
            } else {
                Logger.e(TAG, "断开蓝牙?");
                if (tv_switch != null) {
                    tv_switch.setText(getResources().getString(R.string.bluetooth_switch_disable));
                }

                boolean closed = BleManager.getInstance().closeBLE();
                if (closed) {
                    Logger.e(TAG, "断开蓝牙");
                    BleManager.getInstance().stopScan();
                    BleManager.getInstance().disableCharacterNotifiy();
//                    BleManager.getInstance().disConnectAllDevice();
                    BleManager.getInstance().disConnectDevice();

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
        });
        rvBle.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume()");

        BleManager.getInstance().setOnScanConnectListener(this);
        BleManager.getInstance().setIsScanHrDevice(getIntent().getBooleanExtra("isScanHrDevice", false));

    }

    boolean first = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (first && hasFocus) {
            boolean enabled = BleManager.getInstance().getBluetoothAdapter().isEnabled();
            if (!enabled || !cbSwitch.isChecked()) {
//                cbSwitch.setChecked(true);
            }
            // cb为false时，再check false，不会触发回调

            if (tv_switch != null) {
                tv_switch.setText(cbSwitch.isChecked() ? getResources().getString(R.string.bluetooth_switch_enable) : getResources().getString(R.string.bluetooth_switch_disable));
            }
            if (BleManager.getInstance().getBluetoothAdapter().isEnabled() && !BleManager.isCanning) {
//                scanDevice();
            }

            if (BleManager.getInstance().getBluetoothAdapter().isEnabled()) {
                cbSwitch.setChecked(true);
            }

            first = false;
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
            Logger.e(TAG, "没有权限，请求权限");
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(BluetoothActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, BAIDU_READ_PHONE_STATE);
        } else {
            Logger.e(TAG, "有权限了");
            BleManager.getInstance().openBLE();

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);

            if (BleManager.isOpen) {
                llLoading.setVisibility(View.VISIBLE);

                BleManager.getInstance().getScanResults().clear();
                if (bleAdapter != null) {
                    bleAdapter.notifyDataSetChanged();
                }
                rvBle.setVisibility(View.VISIBLE);

                new Handler().postDelayed(() -> {
                    BleManager.getInstance().scanDevice();
                }, 2000);
            } else {
                llLoading.setVisibility(View.GONE);
                cbSwitch.setChecked(false);
            }
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e(TAG, "-----------------------------------------------------");
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
//        Logger.i("onScanSuccess");
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
        runOnUiThread(() -> {
            if (bleAdapter != null) {
                bleAdapter.notifyDataSetChanged();
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