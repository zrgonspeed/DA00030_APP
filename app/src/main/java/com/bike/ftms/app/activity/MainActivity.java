package com.bike.ftms.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.fragment.HomeFragment;
import com.bike.ftms.app.fragment.WorkoutsFragment;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.manager.ble.OnRunDataListener;
import com.bike.ftms.app.widget.HorizontalViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements OnRunDataListener {

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
        homeFragment = new HomeFragment(ivPage);
        workoutsFragment = new WorkoutsFragment();
        homeFragments.add(homeFragment);
        homeFragments.add(workoutsFragment);
        vp.setOffscreenPageLimit(2);
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
                } else {
                    vp.setSetIntercept(false);
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
        m_wklk.release(); //解除保持唤醒
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_wklk.release();//解除保持唤醒
        BleManager.getInstance().setonRunDataListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_wklk.acquire(); //设置保持唤醒
        BleManager.getInstance().setonRunDataListener(this);

    }


    @Override
    public void onRunData(RowerDataBean rowerDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                homeFragment.onRunData(rowerDataBean);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (workoutsFragment.onKeyDown(keyCode, event)) {
            exitTime = 0;
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
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