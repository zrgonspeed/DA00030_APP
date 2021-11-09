package com.bike.ftms.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.fragment.HomeFragment;
import com.bike.ftms.app.fragment.WorkoutsFragment;
import com.bike.ftms.app.fragment.WorkoutsLocalFragment;
import com.bike.ftms.app.fragment.WorkoutsNetFragment;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.manager.ble.OnRunDataListener;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.widget.HorizontalViewPager;
import com.bike.ftms.app.widget.YesOrNoDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements OnRunDataListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.vp)
    HorizontalViewPager vp;
    @BindView(R.id.btn_bluetooth)
    ImageView btnBluetooth;
    @BindView(R.id.btn_setting)
    ImageView btnSetting;
    @BindView(R.id.iv_page)
    ImageView ivPage;
    private View page1, page2, page3;
    private PowerManager.WakeLock m_wklk;//屏幕锁屏
    private HomeFragment homeFragment;
    private WorkoutsFragment workoutsFragment;
    private long exitTime = 0;
    private YesOrNoDialog yesOrNoDialog;
    private boolean isOnPause = false;

    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void initData() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "cn");
        m_wklk.acquire(); //设置保持唤醒
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
        vp.setAdapter(adapter1);
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

    @OnClick({R.id.btn_bluetooth, R.id.btn_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bluetooth:
                startActivity(new Intent(this, BluetoothActivity.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_wklk.isHeld()) {
            m_wklk.release(); //解除保持唤醒
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
        m_wklk.release();//解除保持唤醒
        //BleManager.getInstance().setonRunDataListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
        m_wklk.acquire(); //设置保持唤醒
        BleManager.getInstance().setonRunDataListener(this);
        if (!BleManager.isConnect && !BleManager.isHeartbeatConnect) {
            showConnectHintDialog();
            homeFragment.onRunData(new RowerDataBean1());
        } else {
            if (yesOrNoDialog != null && yesOrNoDialog.isShowing()) {
                yesOrNoDialog.dismiss();
            }
        }

    }


    @Override
    public void onRunData(RowerDataBean1 rowerDataBean1) {
        Logger.d(TAG, rowerDataBean1.toString());

        if (isOnPause) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeFragment.onRunData(rowerDataBean1);
            }
        });
    }

    @Override
    public void disConnect() {
        if (isOnPause) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showConnectHintDialog();
            }
        });
    }

    @Override
    public void onExit() {
        finish();
        System.exit(0);
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
        Logger.d(TAG, "onKeyDown");
        if (workoutsFragment.onKeyDown(keyCode, event)) {
            exitTime = 0;
            return false;
        }
        Logger.d(TAG, "onKeyDown1");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        Logger.d(TAG, "onKeyDown2");
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), getString(R.string.home_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }

    }
}