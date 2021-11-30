package com.bike.ftms.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.bluetooth.BluetoothActivity;
import com.bike.ftms.app.activity.setting.SettingActivity;
import com.bike.ftms.app.activity.user.LoginActivity;
import com.bike.ftms.app.activity.user.PersonalDataActivity;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.activity.fragment.HomeFragment;
import com.bike.ftms.app.activity.fragment.workout.WorkoutsFragment;
import com.bike.ftms.app.activity.fragment.workout.WorkoutsLocalFragment;
import com.bike.ftms.app.activity.fragment.workout.WorkoutsNetFragment;
import com.bike.ftms.app.manager.VersionManager;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.manager.ble.OnRunDataListener;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.widget.HorizontalViewPager;
import com.bike.ftms.app.widget.YesOrNoDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;


public class MainActivity extends BaseActivity implements OnRunDataListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.vp)
    HorizontalViewPager vp;
    @BindView(R.id.tv_version_apk_name)
    TextView tv_version_apk_name;
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
    private PowerManager.WakeLock m_wklk;//屏幕锁屏
    private HomeFragment homeFragment;
    private WorkoutsFragment workoutsFragment;
    private long exitTime = 0;
    private YesOrNoDialog yesOrNoDialog;
    private boolean isOnPause = false;

    private boolean isLogin = false;

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
        Logger.e("BleManager == " + BleManager.getInstance());
        BleManager.getInstance().setOnRunDataListener(this);
        if (!BleManager.isConnect && !BleManager.isHrConnect) {
            if (vp.getCurrentItem() == 0) {
                showConnectHintDialog();
            }
            homeFragment.onRunData(new RowerDataBean1());
        } else {
            if (yesOrNoDialog != null && yesOrNoDialog.isShowing()) {
                yesOrNoDialog.dismiss();
            }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
        m_wklk.release();//解除保持唤醒
        //BleManager.getInstance().setonRunDataListener(null);

        if (yesOrNoDialog != null) {
            yesOrNoDialog.cancel();
            yesOrNoDialog = null;
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

        if (yesOrNoDialog != null) {
            yesOrNoDialog.cancel();
            yesOrNoDialog = null;
        }

        BleManager.getInstance().disConnectDevice();
        BleManager.getInstance().destroy();
    }


    @Override
    protected void initView() {
        List<Fragment> homeFragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        homeFragments.add(homeFragment);

        if (isLogin) {
            workoutsFragment = new WorkoutsNetFragment();
        } else {
            workoutsFragment = new WorkoutsLocalFragment();
        }
        homeFragments.add(workoutsFragment);

        TabFragmentPagerAdapter adapter1 = new TabFragmentPagerAdapter(getSupportFragmentManager(), homeFragments);
        vp.setAdapter(adapter1);    // 此时开始回调fragment生命周期
        vp.setOffscreenPageLimit(2);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    vp.setSetIntercept(true);
                    ivPage.setImageResource(R.mipmap.page1);
                    m_wklk.acquire(); //设置保持唤醒
                } else {
                    vp.setSetIntercept(false);
                    ivPage.setImageResource(R.mipmap.page2);
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
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "cn");
        m_wklk.acquire(); //设置保持唤醒

        tv_version_apk_name.setText(VersionManager.getAppVersionName(this));
    }

    @OnClick({R.id.btn_workout_user_info, R.id.btn_bluetooth, R.id.btn_setting, R.id.btn_workout_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_workout_login:
                startActivity(new Intent(this, LoginActivity.class));
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
        runOnUiThread(() -> homeFragment.onRunData(rowerDataBean1));
    }

    @Override
    public void disConnect() {
        if (isOnPause) {
            return;
        }
        runOnUiThread(() -> showConnectHintDialog());
    }


    private void showConnectHintDialog() {
        if (yesOrNoDialog == null) {
            yesOrNoDialog = new YesOrNoDialog(MainActivity.this);
            yesOrNoDialog.setTitle("Warm Tip");
            yesOrNoDialog.setMessage("Connect the device or not?");
            yesOrNoDialog.setYesOnclickListener("Yes", new YesOrNoDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
                    yesOrNoDialog.dismiss();
                }
            });
            yesOrNoDialog.setNoOnclickListener("NO", new YesOrNoDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    yesOrNoDialog.dismiss();
                }
            });
        }

        yesOrNoDialog.show();
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

    @Override
    public void onExit() {
//        finish();
//        System.exit(0);
        BleManager.getInstance().disConnectDevice();
        BleManager.getInstance().destroy();
        finish();
    }

    public void exit() {
        Logger.i("exit()");

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.show(getString(R.string.home_exit), false);
            exitTime = System.currentTimeMillis();
        } else {
            onExit();
//            System.exit(0);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }
}