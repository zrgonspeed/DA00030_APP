package com.bike.ftms.app.activity.bluetooth;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.BleAdapter;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.base.OnScanConnectListener;

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

}