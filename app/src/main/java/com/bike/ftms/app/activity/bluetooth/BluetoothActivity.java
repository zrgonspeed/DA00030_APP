package com.bike.ftms.app.activity.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.Debug;
import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.fragment.workout.MyHeader;
import com.bike.ftms.app.adapter.BleAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.manager.ble.OnScanConnectListener;
import com.bike.ftms.app.utils.Logger;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import butterknife.BindView;
import butterknife.OnClick;
import dev.xesam.android.toolbox.timer.CountDownTimer;
import tech.gujin.toast.ToastUtil;

public class BluetoothActivity extends BaseActivity implements OnScanConnectListener, BleAdapter.OnItemClickListener,
        BleManager.BleOpenCallBack, BleManager.BleClosedCallBack {
    private static final String TAG = BluetoothActivity.class.getSimpleName();
    private static final int PERMISSION_STATE_CODE = 1000;
    @Nullable
    @BindView(R.id.tv_switch)
    TextView tv_switch;
    @BindView(R.id.tv_search_time)
    TextView tv_search_time;
    @BindView(R.id.cb_switch)
    CheckBox cbSwitch;
    @BindView(R.id.ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.rv_ble)
    RecyclerView rvBle;
    private BleAdapter bleAdapter;

    boolean first = true;
    private boolean isCalled = true;
    private boolean isPre = false;

    private BLEBroadcastReceiver bleBroadcastReceiver;

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.e("thread == " + Thread.currentThread().toString());
    }

    /**
     * 注册广播
     */
    private void initBLEBroadcastReceiver() {
        //注册广播接收
        bleBroadcastReceiver = new BLEBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //开始扫描
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//扫描结束
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//手机蓝牙状态监听
        registerReceiver(bleBroadcastReceiver, intentFilter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initData() {
        Logger.i("initData()");

    }

    private boolean refreshing = false;

    private void setRefreshLayout() {
        if (refreshing) {
            rl_status_refresh.setEnableRefresh(false);
            Logger.e("setEnableRefresh(false);");
        } else {
            if (BleManager.isOpen) {
                rl_status_refresh.setEnableRefresh(true);
                Logger.e("setEnableRefresh(true);");

            } else {
                rl_status_refresh.setEnableRefresh(false);
                Logger.e("setEnableRefresh(false);");
            }
        }
        rl_status_refresh.setRefreshHeader(new MyHeader(getApplicationContext()).setSpinnerStyle(SpinnerStyle.FixedBehind).setPrimaryColorId(R.color.colorPrimary).setAccentColorId(android.R.color.white).setEnableLastTime(false));

        rl_status_refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                super.onStateChanged(refreshLayout, oldState, newState);
                Logger.i("oldState == " + oldState + "   newState == " + newState);
                // oldState == RefreshFinish   newState == None  这时才刷新动画完成
                if (oldState == RefreshState.RefreshFinish && newState == RefreshState.None) {
                    rl_status_refresh.setEnableRefresh(false);

                    rvBle.setVisibility(View.VISIBLE);
                    llLoading.setVisibility(View.VISIBLE);
                }
            }
        });
        rl_status_refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                // 正在刷新中不能再下拉刷新
                if (refreshing) {
                    rl_status_refresh.finishRefresh();
                    return;
                }

                bleAdapter.clear();
                scanDevice();
                rvBle.setVisibility(View.GONE);
                llLoading.setVisibility(View.GONE);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    rl_status_refresh.finishRefresh(true);
                }, 1500);
            }
        });
    }

    @Override
    protected void initView() {
        Logger.i("initView()");
        BleManager.getInstance().getBluetoothAdapter();

        BleManager.getInstance().setCountDownTime(new BleManager.CountDownTime() {
            @Override
            public void onTick(long time) {
                tv_search_time.setText("" + time);
            }

            @Override
            public void onFinish() {
                tv_search_time.setText("?");
            }

            @Override
            public void onStart() {
//                tv_search_time.setText("开始扫描");
            }
        });
        tv_search_time.setText("?");
        tv_search_time.setVisibility(View.GONE);
        if (Debug.canShowSearchTime) {
            tv_search_time.setVisibility(View.VISIBLE);
        }

        rvBle.setNestedScrollingEnabled(true);
        rl_status_refresh.setNestedScrollingEnabled(true);
        rl_status_refresh.setEnableLoadMore(false);
        rl_status_refresh.setEnableRefresh(false);

        // 每点一次就拦住check事件等待蓝牙打开或关闭之后返回的状态
        cbSwitch.setOnClickListener((e) -> {
            isCalled = false;
        });

        cbSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Logger.e("OnCheckedChange--------------cbSwitch.isChecked() == " + cbSwitch.isChecked() + " isChecked == " + isChecked);
            Logger.e("buttonView.isPressed() == " + buttonView.isPressed());

            if (!buttonView.isPressed()) {
                isPre = isChecked;
                return;
            }
            cbSwitch.setChecked(isPre);

            if (!isCalled) {
                return;
            }
            // 代码设置的不会执行下去
            if (isChecked) {
                Logger.e("打开蓝牙?");
                checkLocation();
                scanDevice();
            } else {
                Logger.e("断开蓝牙?");
                BleManager.getInstance().closeBLE(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BleManager.getInstance().setOnScanConnectListener(this);
        BleManager.getInstance().setIsScanHrDevice(getIntent().getBooleanExtra("isScanHrDevice", false));
        //注册广播
        initBLEBroadcastReceiver();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Logger.e("onWindowFocusChanged " + hasFocus + " first " + first);
        Logger.e("cbSwitch.isChecked() " + cbSwitch.isChecked());

        if (hasFocus && !first && BleManager.getInstance().isOpen && !cbSwitch.isChecked()) {
//            if (BleManager.getInstance().getBluetoothAdapter().isEnabled()) {
            cbSwitch.setChecked(true);
//            }
        }

        if (hasFocus && !first && !BleManager.getInstance().isOpen && cbSwitch.isChecked()) {
//            if (!BleManager.getInstance().getBluetoothAdapter().isEnabled()) {
            cbSwitch.setChecked(false);
//            }
        }

        if (first && hasFocus) {
            boolean enabled = BleManager.getInstance().getBluetoothAdapter().isEnabled();
            if (!enabled || !cbSwitch.isChecked()) {
            }
            // cb为false时，再check false，不会触发回调
            if (tv_switch != null) {
                tv_switch.setText(cbSwitch.isChecked() ? getResources().getString(R.string.bluetooth_switch_enable) : getResources().getString(R.string.bluetooth_switch_disable));
            }

            if (BleManager.getInstance().getBluetoothAdapter().isEnabled() && !cbSwitch.isChecked()) {
                isPre = true;
                cbSwitch.setPressed(true);
                cbSwitch.setChecked(true);
            }

            first = false;
        }
    }

    private void scanDevice() {
        //判断是否为android6.0系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissionAndOpenLBE();
        } else {
            startRefresh();
            BleManager.getInstance().scanDevice();
        }
    }

    public void requestPermissionAndOpenLBE() {
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
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, PERMISSION_STATE_CODE);

        } else {
            Logger.e("有权限");
            BleManager.getInstance().openBLE(this);

            // 另一种打开蓝牙的方式
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableBtIntent);
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e("-----------------------------------------------------");
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case PERMISSION_STATE_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    startRefresh();
//                    BleManager.getInstance().scanDevice();
                    BleManager.getInstance().openBLE(this);
                } else {
                    // 没有获取到权限，做特殊处理
                    Logger.e("" + "没有获取到权限");
                }
                break;
            default:
                break;
        }
    }

    private void checkLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //如果用户已经打开定位服务逻辑
//                Toast.makeText(this, "已打开定位服务", Toast.LENGTH_SHORT).show();
//            ToastUtil.show("已打开定位服务", true);
        } else {
            //如果用户没有打开定位服务引导用户打开定位
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            } else {
                ToastUtil.show(getString(R.string.not_support_location), true);
            }
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onScanSuccess() {
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
        refreshing = false;
        setRefreshLayout();
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
        BleManager.getInstance().setOnScanConnectListener(null);
        BleManager.getInstance().stopScan();
        BleManager.getInstance().setBleOpenCallBack(null);
        BleManager.getInstance().setBleClosedCallBack(null);
        BleManager.getInstance().setCountDownTime(null);

        //注销广播接收
        unregisterReceiver(bleBroadcastReceiver);
    }

    // 每点击一次要2秒后才能再次点击，防止狂按
    private boolean isClicked = false;

    @Override
    public void onItemClickListener(int position, View v, int connectState) {
        if (!isClicked) {
            if (position != BleManager.getInstance().mPosition && BleManager.getInstance().mPosition != -1) {
                // 断开旧连接
                BleManager.getInstance().connectDevice(BleManager.getInstance().mPosition);
                BleManager.getInstance().mPosition = -1;
            } else {
                BleManager.getInstance().connectDevice(position);
                BleManager.getInstance().mPosition = position;
            }
            new Handler().postDelayed(() -> {
                isClicked = false;
//                v.setEnabled(true);
            }, 2000);
//            v.setEnabled(false);
        }
        isClicked = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void isOpen(boolean open) {
        if (this.isDestroyed()) {
            Logger.e("" + " is destroyed");
            return;
        }

        isCalled = true;
        if (open) {
            if (bleAdapter != null) {
                bleAdapter.notifyDataSetChanged();
            }
            startRefresh();
            if (tv_switch != null) {
                tv_switch.setText(getResources().getString(R.string.bluetooth_switch_enable));
            }
        } else {
            stopRefresh();
            cbSwitch.setChecked(false);
            if (tv_switch != null) {
                tv_switch.setText(getResources().getString(R.string.bluetooth_switch_disable));
            }
        }
    }

    @Override
    public void isClosed(boolean disable) {
        if (this.isDestroyed()) {
            Logger.e("" + " is destroyed");
            return;
        }

        isCalled = true;
        if (disable) {
            if (bleAdapter != null) {
                bleAdapter.notifyDataSetChanged();
            }
            stopRefresh();

            if (tv_switch != null) {
                tv_switch.setText(getResources().getString(R.string.bluetooth_switch_disable));
            }
        } else {
            rvBle.setVisibility(View.VISIBLE);
            cbSwitch.setChecked(true);
            if (tv_switch != null) {
                tv_switch.setText(getResources().getString(R.string.bluetooth_switch_enable));
            }
        }
    }

    @BindView(R.id.rl_status_refresh)
    public SmartRefreshLayout rl_status_refresh;

    /**
     * 蓝牙广播接收器
     */
    private class BLEBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.e("蓝牙广播接收：action == " + action);

            if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_STARTED)) { //开启搜索
                Message message = new Message();
                Logger.e("蓝牙广播接收：开启搜索");
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {//完成搜素
                Message message = new Message();
//                message.what = STOP_DISCOVERY;
//                mHandler.sendMessage(message);
                Logger.e("蓝牙广播接收：完成搜素");
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {   //系统蓝牙状态监听
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Logger.e("蓝牙广播接收：state == " + state);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Message message = new Message();
//                    message.what = BT_CLOSED;
//                    mHandler.sendMessage(message);
                    Logger.e("蓝牙广播接收：蓝牙关闭");

                    BleManager.getInstance().closed();
                    isClosed(true);
                    isPre = false;
                } else if (state == BluetoothAdapter.STATE_ON) {
                    Message message = new Message();
//                    message.what = BT_OPENED;
//                    mHandler.sendMessage(message);
                    Logger.e("蓝牙广播接收：蓝牙开启");

//                    BleManager.getInstance().isOpen = true;
//                    isOpen(true);
                } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                    Logger.e("蓝牙广播接收：蓝牙关闭中");
                } else if (state == BluetoothAdapter.STATE_TURNING_ON) {
                    Logger.e("蓝牙广播接收：蓝牙开启中");
                }
            }
        }
    }

    private void startRefresh() {
        refreshing = true;
        if (!(rl_status_refresh.getRefreshHeader() instanceof MyHeader) || ((MyHeader) (rl_status_refresh.getRefreshHeader())).isFinish() ||
                !((MyHeader) (rl_status_refresh.getRefreshHeader())).isMoving()
        ) {
            llLoading.setVisibility(View.VISIBLE);
            rvBle.setVisibility(View.VISIBLE);
        }

    }

    private void stopRefresh() {
        refreshing = false;
        llLoading.setVisibility(View.GONE);
        rvBle.setVisibility(View.GONE);
        setRefreshLayout();
    }
}