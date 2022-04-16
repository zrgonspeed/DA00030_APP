package com.bike.ftms.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.Debug;
import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.bluetooth.BluetoothActivity;
import com.bike.ftms.app.activity.fragment.HomeFragment;
import com.bike.ftms.app.activity.fragment.workout.WorkoutsFragment;
import com.bike.ftms.app.activity.fragment.workout.WorkoutsLocalFragment;
import com.bike.ftms.app.activity.setting.SettingActivity;
import com.bike.ftms.app.activity.user.LoginActivity;
import com.bike.ftms.app.activity.user.PersonalDataActivity;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.ble.heart.BleHeartData;
import com.bike.ftms.app.ble.heart.BleHeartDeviceManager;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.ble.BleManager;
import com.bike.ftms.app.ble.base.OnRunDataListener;
import com.bike.ftms.app.manager.storage.SpManager;
import com.bike.ftms.app.utils.BasisTimesUtils;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.view.HorizontalViewPager;
import com.bike.ftms.app.view.dialog.ConnectHintDialog;
import com.bike.ftms.app.view.dialog.SomeHintDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;


public class MainActivity extends BaseActivity implements OnRunDataListener, OnOrientationChanged, BleHeartData {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.vp)
    HorizontalViewPager vp;
    @BindView(R.id.tv_device)
    TextView tv_device;
    @BindView(R.id.iv_device)
    ImageView iv_device;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;
    @BindView(R.id.iv_heart_connected)
    ImageView iv_heart_connected;
    @BindView(R.id.btn_workout_login)
    ImageView btn_workout_login;
    @BindView(R.id.btn_workout_user_info)
    ImageView btn_workout_user_info;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.btn_bluetooth)
    ImageView btnBluetooth;
    @BindView(R.id.btn_setting)
    ImageView btnSetting;
    @BindView(R.id.iv_page)
    ImageView ivPage;

    @BindView(R.id.ll_bottom)
    RelativeLayout ll_bottom;

    private PowerManager.WakeLock m_wklk;//屏幕锁屏
    private HomeFragment homeFragment;
    private WorkoutsFragment workoutsFragment;
    private long exitTime = 0;

    private ConnectHintDialog connectHintDialog;
    private SomeHintDialog someHintDialog;

    private boolean isOnPause = false;
    private boolean isLogin = false;
    private int ori = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SpManager.getSkipHint()) {
            // 安装后启动要提示事项，谷歌商店需要
            someHintDialog = SomeHintDialog.showSomeHintDialog(this, someHintDialog);
        }


        Logger.i(BasisTimesUtils.getCurDate());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Logger.w("this == " + this);

        isOnPause = false;
        m_wklk.acquire(); //设置保持唤醒
        BleManager.getInstance().setOnRunDataListener(this);
        BleHeartDeviceManager.getInstance().setBleHeartData(this);
        if (!BleManager.getInstance().isConnected()) {
            if (vp.getCurrentItem() == 0 && (someHintDialog == null || !someHintDialog.isShowing())) {
                connectHintDialog = ConnectHintDialog.showConnectHintDialog(this, connectHintDialog, ori);
            }
            // 刷新界面数据为0
            homeFragment.onRunData(new RowerDataBean1());
        } else {
            if (connectHintDialog != null && connectHintDialog.isShowing()) {
                connectHintDialog.dismiss();
            }
        }

        Configuration cf = this.getResources().getConfiguration(); //获取设置的配置信息
        //获取屏幕方向
        ori = cf.orientation;
        Logger.i("横竖屏: " + ori);
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            setLandLayout();
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            setPortLayout();
        }

        // 判断是否登录，去显示下方用户名头像
        if (UserManager.getInstance().getUser() != null) {
            // 已登录
            btn_workout_login.setVisibility(View.GONE);
            btn_workout_user_info.setVisibility(View.VISIBLE);
            btn_workout_user_info.setImageResource(R.drawable.user_header_def_2);
            tv_username.setVisibility(View.VISIBLE);
            tv_username.setText(UserManager.getInstance().getUser().getUsername());
        } else {
            // 未登录
            btn_workout_login.setVisibility(View.VISIBLE);
            btn_workout_user_info.setVisibility(View.GONE);
            tv_username.setVisibility(View.GONE);
        }

        setConnectStatus(BleManager.getInstance().isConnected(), BleManager.deviceType);
        setHRConnectStatus(BleHeartDeviceManager.getInstance().isConnected());
    }

    // 显示连接上的机型和图片
    private synchronized void setConnectStatus(boolean connected, int deviceType) {
        if (connected && deviceType != -1) {
            tv_device.setText(MyConstant.deviceNames[deviceType]);
            iv_device.setImageDrawable(MyConstant.getCategoryImg(MyConstant.getCategory(deviceType)));
            tv_device.setVisibility(View.VISIBLE);
            iv_device.setVisibility(View.VISIBLE);
        } else {
            tv_device.setVisibility(View.GONE);
            iv_device.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
        m_wklk.release();//解除保持唤醒

        if (connectHintDialog != null) {
            connectHintDialog.cancel();
            connectHintDialog = null;
        }

        if (someHintDialog != null) {
            someHintDialog.cancel();
            someHintDialog = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_wklk.isHeld()) {
            m_wklk.release(); //解除保持唤醒
        }
        m_wklk = null;

        if (someHintDialog != null) {
            someHintDialog.cancel();
            someHintDialog = null;
        }

        release();
    }

    @Override
    protected void initView() {
        List<Fragment> homeFragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        homeFragments.add(homeFragment);

        workoutsFragment = new WorkoutsLocalFragment();
        homeFragments.add(workoutsFragment);

        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), homeFragments);
        vp.setAdapter(adapter);    // 此时开始回调fragment生命周期
        vp.setOffscreenPageLimit(2);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    vp.setSetIntercept(true);
                    ivPage.setImageResource(R.drawable.page1);
                    m_wklk.acquire(); //设置保持唤醒
                } else {
                    vp.setSetIntercept(false);
                    ivPage.setImageResource(R.drawable.page2);
                    m_wklk.release();//解除保持唤醒
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void initData() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "cn");
        m_wklk.acquire(); //设置保持唤醒
    }

    @OnClick({R.id.btn_workout_user_info, R.id.btn_bluetooth, R.id.btn_setting, R.id.btn_workout_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_workout_login:
                if (Debug.canLogin) {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.btn_workout_user_info:
                startActivity(new Intent(this, PersonalDataActivity.class));
                break;
            case R.id.btn_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    @Override
    public void onRunData(RowerDataBean1 rowerDataBean1) {
        Logger.d("" + rowerDataBean1.toString());

        if (isOnPause) {
            return;
        }
        runOnUiThread(() -> {
            if (homeFragment != null) {
                homeFragment.onRunData(rowerDataBean1);
            }
        });
    }

    @Override
    public void disConnect() {
        Logger.d("isFinishing() == " + isFinishing());
        if (isFinishing()) {
            return;
        }

        if (isDestroyed()) {
            return;
        }
        runOnUiThread(() -> {
            setConnectStatus(BleManager.getInstance().isConnected(), BleManager.deviceType);
            if (connectHintDialog == null) {
                connectHintDialog = ConnectHintDialog.showConnectHintDialog(this, connectHintDialog, ori);
            }
        });
    }

    @Override
    public void connected() {
        runOnUiThread(() -> {
            setConnectStatus(BleManager.getInstance().isConnected(), BleManager.deviceType);
            homeFragment.connected();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Logger.i("onBackPressed()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d("onKeyDown");
        if (workoutsFragment.onKeyDown(keyCode, event)) {
            exitTime = 0;
            return false;
        }
        Logger.d("onKeyDown1");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        Logger.d("onKeyDown2");
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        Logger.i("exit()");

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.show(getString(R.string.home_exit), false);
            exitTime = System.currentTimeMillis();
        } else {
            release();
            finish();
        }
    }

    public void release() {
        BleManager.getInstance().destroy();
        BleHeartDeviceManager.getInstance().destroy();

        homeFragment = null;
        workoutsFragment = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @BindView(R.id.rl_uploading)
    RelativeLayout rl_uploading;

    @BindView(R.id.rl_upload_success)
    RelativeLayout rl_upload_success;

    @BindView(R.id.rl_upload_fail)
    RelativeLayout rl_upload_fail;

    public void showUploading() {
        rl_uploading.setVisibility(View.VISIBLE);

        rl_upload_success.setVisibility(View.GONE);
        rl_upload_fail.setVisibility(View.GONE);
    }

    public void showUploadSuccess() {
        rl_upload_success.setVisibility(View.VISIBLE);

        rl_uploading.setVisibility(View.GONE);
        rl_upload_fail.setVisibility(View.GONE);
    }

    public void showUploadFailed() {
        rl_upload_fail.setVisibility(View.VISIBLE);

        rl_uploading.setVisibility(View.GONE);
        rl_upload_success.setVisibility(View.GONE);
    }

    public void hideUpload() {
        rl_uploading.setVisibility(View.GONE);
        rl_upload_success.setVisibility(View.GONE);
        rl_upload_fail.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.i("newConfig 横竖屏 " + newConfig.orientation);
        ori = newConfig.orientation;
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandLayout();
        }
    }


    @Override
    public void setLandLayout() {
        ViewGroup.LayoutParams layoutParams = ll_bottom.getLayoutParams();
        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        ll_bottom.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) btn_workout_login.getLayoutParams();
        layoutParams1.setMarginEnd(getIntDimen(R.dimen.dp_10));
        btn_workout_login.setLayoutParams(layoutParams1);

        ViewGroup.LayoutParams layoutParams2 = btnBluetooth.getLayoutParams();
        layoutParams2.height = getIntDimen(R.dimen.dp_40);
        layoutParams2.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        btnBluetooth.setLayoutParams(layoutParams2);

        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) btnSetting.getLayoutParams();
        layoutParams3.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams3.height = getIntDimen(R.dimen.dp_40);
        layoutParams3.setMarginEnd(getIntDimen(R.dimen.dp_10));
        btnSetting.setLayoutParams(layoutParams3);

        ivPage.setVisibility(View.VISIBLE);

        // 对话框
        if (connectHintDialog != null) {
            connectHintDialog.setLandLayout();
        }

        // 已登录头像
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) tv_username.getLayoutParams();
        layoutParams4.setMarginEnd(-getIntDimen(R.dimen.dp_90));
        tv_username.setLayoutParams(layoutParams4);
        RelativeLayout.LayoutParams layoutParams5 = (RelativeLayout.LayoutParams) btn_workout_user_info.getLayoutParams();
        layoutParams5.height = getIntDimen(R.dimen.dp_25);
        btn_workout_user_info.setLayoutParams(layoutParams5);
        btn_workout_user_info.setPadding(getIntDimen(R.dimen.dp_90), 0, 0, 0);
    }

    @Override
    public void setPortLayout() {
        ViewGroup.LayoutParams layoutParams = ll_bottom.getLayoutParams();
        layoutParams.height = getIntDimen(R.dimen.dp_50);
        ll_bottom.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) btn_workout_login.getLayoutParams();
        layoutParams1.setMarginEnd(getIntDimen(R.dimen.dp_3));
        btn_workout_login.setLayoutParams(layoutParams1);

        ViewGroup.LayoutParams layoutParams2 = btnBluetooth.getLayoutParams();
        layoutParams2.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams2.width = getIntDimen(R.dimen.dp_40);
        btnBluetooth.setLayoutParams(layoutParams2);

        RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) btnSetting.getLayoutParams();
        layoutParams3.width = getIntDimen(R.dimen.dp_40);
        layoutParams3.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams3.setMarginEnd(0);
        btnSetting.setLayoutParams(layoutParams3);

        ivPage.setVisibility(View.GONE);

        // 对话框
        if (connectHintDialog != null) {
            connectHintDialog.setPortLayout();
        }

        // 已登录头像
        RelativeLayout.LayoutParams layoutParams4 = (RelativeLayout.LayoutParams) tv_username.getLayoutParams();
        layoutParams4.setMarginEnd(-getIntDimen(R.dimen.dp_90));
        tv_username.setLayoutParams(layoutParams4);
        RelativeLayout.LayoutParams layoutParams5 = (RelativeLayout.LayoutParams) btn_workout_user_info.getLayoutParams();
        layoutParams5.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        btn_workout_user_info.setLayoutParams(layoutParams5);
        btn_workout_user_info.setPadding(getIntDimen(R.dimen.dp_95), 0, 0, 0);

    }

    private int getIntDimen(int id) {
        return (int) getResources().getDimension(id);
    }

    @Override
    public void onHeartData(int heart) {
        if (isOnPause) {
            return;
        }
        runOnUiThread(() -> homeFragment.onHeartListener(heart));
    }

    @Override
    public void setHRConnectStatus(boolean connected) {
        if (connected) {
            iv_heart_connected.setVisibility(View.VISIBLE);
            iv_logo.setVisibility(View.GONE);
        } else {
            iv_heart_connected.setVisibility(View.GONE);
            iv_logo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHRConnected() {
        runOnUiThread(() -> {
            setHRConnectStatus(BleHeartDeviceManager.getInstance().isConnected());
        });
    }

    @Override
    public void disHRConnect() {
        Logger.d("isFinishing() == " + isFinishing());
        if (isFinishing()) {
            return;
        }

        if (isDestroyed()) {
            return;
        }
        runOnUiThread(() -> {
            setHRConnectStatus(BleHeartDeviceManager.getInstance().isConnected());
        });
    }
}