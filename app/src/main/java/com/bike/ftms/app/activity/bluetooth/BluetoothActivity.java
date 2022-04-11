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
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.view.MyHeader;
import com.bike.ftms.app.adapter.BleAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.base.OnScanConnectListener;
import com.bike.ftms.app.utils.ButtonUtils;
import com.bike.ftms.app.utils.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import butterknife.BindView;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;

public class BluetoothActivity extends BaseBluetoothActivity implements OnScanConnectListener, BleAdapter.OnItemClickListener,
        BleManager.BleOpenCallBack, BleManager.BleClosedCallBack {
    private static final String TAG = BluetoothActivity.class.getSimpleName();

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected boolean isOpenBle() {
        return BleManager.getInstance().getIsOpen();
    }

    @Override
    protected BaseBleManager getBleManager() {
        return BleManager.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClickListener(int position, View v, int connectState) {
        if (!isClicked) {
            if (position != getBleManager().getPosition() && getBleManager().getPosition() != -1) {
                // 断开旧连接
                getBleManager().connectDevice(getBleManager().getPosition());
                getBleManager().setPosition(-1);
            } else {
                getBleManager().connectDevice(position);
                getBleManager().setPosition(position);
            }
            new Handler().postDelayed(() -> {
                isClicked = false;
            }, 2000);
        }
        isClicked = true;
    }

}