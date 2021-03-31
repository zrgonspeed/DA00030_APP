package com.bike.ftms.app.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class WorkoutsFragment extends BaseFragment {
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.rv_workouts)
    RecyclerView rvWorkouts;

    public WorkoutsFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workouts;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_upload, R.id.tv_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_upload:
                break;
            case R.id.tv_edit:
                break;
        }
    }
}
