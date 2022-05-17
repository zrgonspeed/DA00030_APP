package com.bike.ftms.app.activity.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
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
import com.bike.ftms.app.adapter.BleAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.base.OnScanConnectListener;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.utils.ButtonUtils;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.view.MyHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;

public abstract class BaseBluetoothActivity extends BaseActivity implements OnScanConnectListener, BleAdapter.OnItemClickListener,
        BaseBleManager.BleOpenCallBack, BaseBleManager.BleClosedCallBack {
    private static final String TAG = BaseBluetoothActivity.class.getSimpleName();
    private static final int PERMISSION_STATE_CODE = 1000;
    private static final int LOCALTION_STATE_CODE = 1050;
    @Nullable
    @BindView(R.id.tv_switch)
    TextView tv_switch;
    @BindView(R.id.tv_search_time)
    TextView tv_search_time;
    @BindView(R.id.cb_switch)
    CheckBox cb_switch;
    @BindView(R.id.ll_loading)
    LinearLayout ll_loading;
    @BindView(R.id.rv_ble)
    RecyclerView rv_ble;
    private BleAdapter bleAdapter;

    private BLEBroadcastReceiver bleBroadcastReceiver;

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e("thread == " + Thread.currentThread());
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

    protected abstract boolean isOpenBle();

    private void setRefreshLayout() {
        if (refreshing) {
            rl_status_refresh.setEnableRefresh(false);
            // Logger.e("setEnableRefresh(false);");
        } else {
            // Logger.e("setEnableRefresh(true);");
            // Logger.e("setEnableRefresh(false);");
            rl_status_refresh.setEnableRefresh(isOpenBle());
        }
        rl_status_refresh.setRefreshHeader(new MyHeader(getApplicationContext()).setSpinnerStyle(SpinnerStyle.FixedBehind).setPrimaryColorId(R.color.colorPrimary).setAccentColorId(android.R.color.white).setEnableLastTime(false));

        rl_status_refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                super.onStateChanged(refreshLayout, oldState, newState);
                // Logger.i("oldState == " + oldState + "   newState == " + newState);
                // oldState == RefreshFinish   newState == None  这时才刷新动画完成
                if (oldState == RefreshState.RefreshFinish && newState == RefreshState.None) {
                    rl_status_refresh.setEnableRefresh(false);

                    rv_ble.setVisibility(View.VISIBLE);
                    ll_loading.setVisibility(View.VISIBLE);
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
                rv_ble.setVisibility(View.GONE);
                ll_loading.setVisibility(View.GONE);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (rl_status_refresh != null) {
                        rl_status_refresh.finishRefresh(true);
                    }
                }, 1500);
            }
        });
    }

    protected abstract BaseBleManager getBleManager();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        Logger.i("initView()");
        getBleManager().getBluetoothAdapter();
        getBleManager().setCountDownTime(new BaseBleManager.CountDownTime() {
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
        ll_loading.setVisibility(View.GONE);
        if (Debug.canShowSearchTime) {
            tv_search_time.setVisibility(View.VISIBLE);
        }

        rv_ble.setNestedScrollingEnabled(true);
        rl_status_refresh.setNestedScrollingEnabled(true);
        rl_status_refresh.setEnableLoadMore(false);
        rl_status_refresh.setEnableRefresh(false);

        // 每点一次就拦住check事件等待蓝牙打开或关闭之后返回的状态
        // cb_switch.setOnClickListener((e) -> {
        //     isCalled = false;
        // });

        // cb_switch.setOnClickListener((e) -> {
        //     if (ButtonUtils.isFastClick()) {
        //         // 进行点击事件后的逻辑操作
        //         Logger.i("点击了APP蓝牙开关");
        //     }
        // });

        // 1秒内不能点多次
        cb_switch.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    boolean canResponse = ButtonUtils.canResponse();
                    // Logger.d("canResponse == " + canResponse);
                    if (canResponse) {
                        v.performClick();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }

            return true;
        });

        cb_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Logger.d("OnCheckedChange--------------cbSwitch.isChecked() == " + cb_switch.isChecked() + " isChecked == " + isChecked);
            Logger.d("buttonView.isPressed() == " + buttonView.isPressed());

            tv_switch.setText(isChecked ? getResources().getString(R.string.bluetooth_switch_enable) : getResources().getString(R.string.bluetooth_switch_disable));

            // 没点击app的蓝牙开关就是false，比如从通知栏关闭蓝牙
            // if (!buttonView.isPressed()) {
            //     // isPre = isChecked;
            //     return;
            // }

            // 代码设置的不会执行下去
            if (isChecked) {
                Logger.i("打开蓝牙?");
                scanDevice();
            } else {
                Logger.e("断开蓝牙?");
                getBleManager().closeBLE(this);
            }
        });


        boolean enabled = getBleManager().getBluetoothAdapter().isEnabled();
        cb_switch.setChecked(enabled);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBleManager().setOnScanConnectListener(this);
        //注册广播
        initBLEBroadcastReceiver();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void scanDevice() {
        boolean locationOpen = checkLocation();

        if (locationOpen) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissionAndOpenBLE();
            } else {
                startRefresh();
                getBleManager().scanDevice();
            }
        }
    }

    public void requestPermissionAndOpenBLE() {
        Logger.i("requestPermissionAndOpenBLE() api==" + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                Logger.e("没有权限，请求权限");
                ActivityCompat.requestPermissions(BaseBluetoothActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                }, PERMISSION_STATE_CODE);

            } else {
                Logger.i("有权限");
                // 还需要开启位置功能

                getBleManager().openBLE(this);
                // 另一种打开蓝牙的方式
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableBtIntent);
            }
        } else {
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
                ActivityCompat.requestPermissions(BaseBluetoothActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH,
                }, PERMISSION_STATE_CODE);

            } else {
                Logger.i("有权限");
                getBleManager().openBLE(this);

                // 另一种打开蓝牙的方式
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableBtIntent);
            }
        }


    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e("-----------------------------------------------------");
        // requestCode即所声明的权限获取码，在checkSelfPermission时传入
        if (requestCode == PERMISSION_STATE_CODE) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                startRefresh();
                getBleManager().openBLE(this);
            } else {
                // 没有获取到权限，做特殊处理
                Logger.e("" + "没有获取到权限");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_open = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Logger.i("GPS是否打开 " + gps_open);
        Logger.i("网络定位是否打开 " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        if (gps_open) {
            scanDevice();
        } else {
            ToastUtil.show(getString(R.string.please_open_local));
            cb_switch.setChecked(false);
        }

    }

    private boolean checkLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //如果用户已经打开定位服务逻辑
            Logger.i("已经打开定位服务");
            return true;
        } else {
            Logger.i("没有打开定位服务， 跳转系统界面");
            //如果用户没有打开定位服务引导用户打开定位
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, LOCALTION_STATE_CODE);
            } else {
                Logger.i(getString(R.string.not_support_location));
                ToastUtil.show(getString(R.string.not_support_location), true);
            }

            return false;
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
            bleAdapter = new BleAdapter(getBleManager().getScanResults());
            bleAdapter.setBleManager(getBleManager());
            bleAdapter.addItemClickListener(this);
            rv_ble.setAdapter(bleAdapter);
            rv_ble.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void onStopScan() {
        ll_loading.setVisibility(View.GONE);
        refreshing = false;
        setRefreshLayout();

        bleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectEvent(boolean isconnect, String name) {
        // Logger.i("isconnect==" + isconnect + "  bt name==" + name);
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
        getBleManager().setOnScanConnectListener(null);
        getBleManager().stopScan();
        getBleManager().setBleOpenCallBack(null);
        getBleManager().setBleClosedCallBack(null);
        getBleManager().setCountDownTime(null);

        //注销广播接收
        unregisterReceiver(bleBroadcastReceiver);
    }

    // 每点击一次要2秒后才能再次点击，防止狂按
    protected boolean isClicked = false;

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
            cb_switch.setChecked(false);
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

        if (disable) {
            if (bleAdapter != null) {
                bleAdapter.notifyDataSetChanged();
            }
            stopRefresh();

            cb_switch.setChecked(false);
            if (tv_switch != null) {
                tv_switch.setText(getResources().getString(R.string.bluetooth_switch_disable));
            }
        } else {
            rv_ble.setVisibility(View.VISIBLE);
            cb_switch.setChecked(true);
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
                Logger.e("蓝牙广播接收：开启搜索");
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {//完成搜素
                Logger.e("蓝牙广播接收：完成搜素");
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {   //系统蓝牙状态监听
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Logger.e("蓝牙广播接收：state == " + state);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Logger.e("蓝牙广播接收：蓝牙关闭");
                    getBleManager().whenBTClosed();
                    isClosed(true);
                    // isPre = false;
                } else if (state == BluetoothAdapter.STATE_ON) {
                    Logger.e("蓝牙广播接收：蓝牙开启");
                    cb_switch.setChecked(true);
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
            ll_loading.setVisibility(View.VISIBLE);
            rv_ble.setVisibility(View.VISIBLE);
        }

    }

    private void stopRefresh() {
        refreshing = false;
        ll_loading.setVisibility(View.GONE);
        rv_ble.setVisibility(View.GONE);
        setRefreshLayout();
    }

    @Override
    public void onItemClickListener(MyScanResult clickScanResult) {
        if (!isClicked) {
            getBleManager().stopScan();
            getBleManager().connectDevice(clickScanResult);

            // 把其他result状态变为0
            List<MyScanResult> scanResults = getBleManager().getScanResults();
            for (int i = 0; i < scanResults.size(); i++) {
                if (clickScanResult != scanResults.get(i)) {
                    scanResults.get(i).setConnectState(0);
                }
            }

            new Handler().postDelayed(() -> {
                isClicked = false;
            }, 2000);
        }
        isClicked = true;
    }
}