package com.bike.ftms.app.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.WorkoutsAdapter;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class WorkoutsFragment extends BaseFragment implements WorkoutsAdapter.OnItemClickListener, WorkoutsAdapter.OnItemDeleteListener {
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.rv_workouts)
    RecyclerView rvWorkouts;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_workouts)
    ConstraintLayout llWorkouts;
    @BindView(R.id.ll_info)
    LinearLayout llInfo;
    @BindView(R.id.iv_info_back)
    ImageView ivInfoBack;
    @BindView(R.id.rl_delete)
    RelativeLayout rlDelete;
    @BindView(R.id.rl_online)
    RelativeLayout rlOnline;
    private WorkoutsAdapter workoutsAdapter;
    private boolean isEdit = false;

    public WorkoutsFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workouts;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        workoutsAdapter = new WorkoutsAdapter();
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWorkouts.setAdapter(workoutsAdapter);
        workoutsAdapter.addItemClickListener(this);
        workoutsAdapter.addItemDeleteClickListener(this);
        rlDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        rlOnline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_upload, R.id.tv_edit, R.id.iv_back, R.id.tv_workouts, R.id.iv_info_back, R.id.tv_done, R.id.tv_cancel, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_upload:
                if (isEdit) {
                    isEdit = false;
                    setEditView();
                } else {

                }
                break;
            case R.id.tv_edit:
                isEdit = !isEdit;
                setEditView();
                break;
            case R.id.tv_done:

                break;
            case R.id.tv_workouts:
            case R.id.iv_info_back:
                llInfo.setVisibility(View.GONE);
                llWorkouts.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel:
                rlDelete.setVisibility(View.GONE);
                break;
            case R.id.tv_ok:
                rlDelete.setVisibility(View.GONE);
                break;
        }
    }

    private void setEditView() {
        workoutsAdapter.setShowDelete(isEdit);
        workoutsAdapter.notifyDataSetChanged();
        if (isEdit) {
            tvUpload.setText(getString(R.string.workouts));
            tvUpload.setTextColor(getResources().getColor(R.color.color_black));
            ivBack.setVisibility(View.VISIBLE);
            tvTitle.setText(getString(R.string.workouts_edit));
            tvEdit.setText(getString(R.string.workouts_done));
        } else {
            tvUpload.setText(getString(R.string.workouts_upload));
            tvUpload.setTextColor(getResources().getColor(R.color.color_0B4531));
            ivBack.setVisibility(View.GONE);
            tvTitle.setText(getString(R.string.workouts));
            tvEdit.setText(getString(R.string.workouts_edit));
        }
    }

    @Override
    public void onItemClickListener(int position) {
        llInfo.setVisibility(View.VISIBLE);
        llWorkouts.setVisibility(View.GONE);
    }

    @Override
    public void onItemDeleteListener(int position) {
        rlDelete.setVisibility(View.VISIBLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && llInfo.getVisibility() == View.VISIBLE) {
            ivInfoBack.performClick();
            return true;
        }
        return false;
    }

}
