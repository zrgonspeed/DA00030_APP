package com.bike.ftms.app.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.InformationPagerAdapter;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;
import com.bike.ftms.app.widget.VerticalViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Description
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public abstract class BaseHomeFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.tv_strokes)
    TextView tvStrokes;
    @BindView(R.id.tv_drag)
    TextView tvDrag;
    @BindView(R.id.tv_interval)
    TextView tvInterval;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_sm)
    TextView tvSm;
    @BindView(R.id.tv_heart_rate)
    TextView tvHeartRate;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void initData() {

    }

    public void onRunData(RowerDataBean rowerDataBean) {
        tvStrokes.setText(String.valueOf(rowerDataBean.getStrokes()));
        if (rowerDataBean.getRunMode() == MyConstant.INTERVAL_TIME ||
                rowerDataBean.getRunMode() == MyConstant.INTERVAL_DISTANCE ||
                rowerDataBean.getRunMode() == MyConstant.INTERVAL_CALORIES
        ) {
            tvInterval.setText(String.valueOf(rowerDataBean.getInterval()));
        } else {
            tvInterval.setText(String.valueOf(rowerDataBean.getRunInterval() + 1));
        }
        tvDistance.setText(String.valueOf(rowerDataBean.getDistance()));
        tvDrag.setText(String.valueOf(rowerDataBean.getDrag()));
        tvSm.setText(String.valueOf(rowerDataBean.getSm()));
        tvHeartRate.setText(String.valueOf(rowerDataBean.getHeart_rate()));
        tvTime.setText(TimeStringUtil.getSToHourMinSecValue(rowerDataBean.getTime()));
    }
}
