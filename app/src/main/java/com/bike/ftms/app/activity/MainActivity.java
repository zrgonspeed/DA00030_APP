package com.bike.ftms.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.InformationPagerAdapter;
import com.bike.ftms.app.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.btn_bluetooth)
    ImageView btnBluetooth;
    @BindView(R.id.btn_setting)
    ImageView btnSetting;
    private View page1, page2, page3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView() {
        LayoutInflater mLayoutInflater = getLayoutInflater();
        page1 = mLayoutInflater.inflate(R.layout.view_pager_home1, null);
        page2 = mLayoutInflater.inflate(R.layout.view_pager_home2, null);
        page3 = mLayoutInflater.inflate(R.layout.view_pager_home3, null);
        ArrayList<View> mViews = new ArrayList<>();
        mViews.add(page1);
        mViews.add(page2);
        mViews.add(page3);
        vp.addView(page1);
        vp.addView(page2);
        vp.addView(page3);
        vp.setAdapter(new InformationPagerAdapter(mViews));
        vp.setOffscreenPageLimit(3);
       /* vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    iv_page.setImageResource(R.mipmap.page1);
                } else if (position == 1) {
                    iv_page.setImageResource(R.mipmap.page2);
                } else if (position == 2) {
                    iv_page.setImageResource(R.mipmap.page3);
                } else {
                    iv_page.setImageResource(R.mipmap.page4);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
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
}