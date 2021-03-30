package com.bike.ftms.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.TabFragmentPagerAdapter;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.fragment.OnePageHomeFragment;
import com.bike.ftms.app.fragment.ThreePageHomeFragment;
import com.bike.ftms.app.fragment.TwoPageHomeFragment;
import com.bike.ftms.app.widget.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp)
    VerticalViewPager vp;
    @BindView(R.id.btn_bluetooth)
    ImageView btnBluetooth;
    @BindView(R.id.btn_setting)
    ImageView btnSetting;
    @BindView(R.id.iv_page)
    ImageView ivPage;
    private View page1, page2, page3;
    private PowerManager.WakeLock m_wklk;//屏幕锁屏
    private List<Fragment> fragmentList;
    private TabFragmentPagerAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void initData() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        m_wklk = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "cn");
        m_wklk.acquire(); //设置保持唤醒
    }

    @Override
    protected void initView() {
        //把Fragment添加到List集合里面
        fragmentList = new ArrayList<>();
        fragmentList.add(new OnePageHomeFragment(vp));
        fragmentList.add(new TwoPageHomeFragment(vp));
        fragmentList.add(new ThreePageHomeFragment(vp));
        vp.setOffscreenPageLimit(3);
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ivPage.setImageResource(R.mipmap.page1);
                } else if (position == 1) {
                    ivPage.setImageResource(R.mipmap.page2);
                } else if (position == 2) {
                    ivPage.setImageResource(R.mipmap.page3);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        m_wklk.acquire(); //设置保持唤醒
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}