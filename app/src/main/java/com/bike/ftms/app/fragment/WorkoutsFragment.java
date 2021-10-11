package com.bike.ftms.app.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.WorkoutsAdapter;
import com.bike.ftms.app.adapter.WorkoutsAdapter2;
import com.bike.ftms.app.base.BaseFragment;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.bean.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;

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

    @BindView(R.id.rv_workouts2)
    RecyclerView rvWorkouts2;

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

    @BindView(R.id.tv_info_title)
    TextView tvInfoTitle;
    @BindView(R.id.edt_info_note)
    EditText edtInfoNote;
    @BindView(R.id.tv_title_time)
    TextView tvTitleTime;
    private WorkoutsAdapter workoutsAdapter;
    private WorkoutsAdapter2 workoutsAdapter2;
    private boolean isEdit = false;
    private List<RowerDataBean> rowerDataBeanList = new ArrayList<>();
    private int clickPosition;
    private int deletePosition;

    public WorkoutsFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workouts;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        workoutsAdapter = new WorkoutsAdapter(rowerDataBeanList);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && llWorkouts.getVisibility() == View.VISIBLE) {
            rowerDataBeanList.clear();
            rowerDataBeanList.addAll(LitePal.order("date desc").find(RowerDataBean.class, true));
            workoutsAdapter.notifyDataSetChanged();
        }
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
                ContentValues contentValues = new ContentValues();
                contentValues.put("note", edtInfoNote.getText().toString());
                int i = LitePal.updateAll(RowerDataBean.class, contentValues, "date=?", String.valueOf(rowerDataBeanList.get(clickPosition).getDate()));
                if (i == 1) {
                    rowerDataBeanList.get(clickPosition).setNote(edtInfoNote.getText().toString());
                    workoutsAdapter.notifyItemChanged(clickPosition);
                    Toast.makeText(getContext(), "Save successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Save fail", Toast.LENGTH_LONG).show();
                }
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
                tvEdit.performClick();
                int j = LitePal.deleteAll(RowerDataBean.class, "date=?", String.valueOf(rowerDataBeanList.get(deletePosition).getDate()));
                if (j == 1) {
                    rowerDataBeanList.remove(deletePosition);
                    workoutsAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Delete fail", Toast.LENGTH_LONG).show();
                }

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
        clickPosition = position;
        notifyInfoData();
        llInfo.setVisibility(View.VISIBLE);
        llWorkouts.setVisibility(View.GONE);
    }

    private void notifyInfoData() {
        RowerDataBean bean = rowerDataBeanList.get(clickPosition);

        ArrayList<RowerDataBean2> list = new ArrayList<>(bean.getList());

        // ToastUtil.show("list == " + list);
        if (list.size() == 0) {
            RowerDataBean2 rowerDataBean2 = new RowerDataBean2();
            rowerDataBean2.setSetTargetDistance(bean.getSetTargetDistance());
            rowerDataBean2.setSetTargetTime(bean.getSetTargetTime());
            rowerDataBean2.setSetTargetCalorie(bean.getSetTargetCalorie());

            rowerDataBean2.setReset_time(bean.getReset_time());
            rowerDataBean2.setMode(bean.getMode());

            rowerDataBean2.setAve_five_hundred(bean.getAve_five_hundred());
            rowerDataBean2.setAve_watts(bean.getAve_watts());
            rowerDataBean2.setCalorie(bean.getCalorie());
            rowerDataBean2.setCalories_hr(bean.getCalories_hr());
            rowerDataBean2.setDate(bean.getDate());
            rowerDataBean2.setDistance(bean.getDistance());
            rowerDataBean2.setDrag(bean.getDrag());
            rowerDataBean2.setSm(bean.getSm());
            rowerDataBean2.setFive_hundred(bean.getFive_hundred());
            rowerDataBean2.setStrokes(bean.getStrokes());
            rowerDataBean2.setTime(bean.getTime());
            rowerDataBean2.setWatts(bean.getWatts());
            rowerDataBean2.setHeart_rate(bean.getHeart_rate());
            rowerDataBean2.setInterval(bean.getInterval());
            rowerDataBean2.setNote(bean.getNote());

            rowerDataBean2.setSetCalorie(bean.getSetCalorie());
            rowerDataBean2.setSetDistance(bean.getSetDistance());
            rowerDataBean2.setSetTime(bean.getSetTime());

            rowerDataBean2.setRowerDataBean(bean);
            list.add(rowerDataBean2);
        }

        if (bean.getMode() == MyConstant.INTERVAL_DISTANCE) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setMode(bean.getMode());

            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setTime(bean2.getTime() + bb.getTime());
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 总和
                bb.setCalorie(bean2.getCalorie() + bb.getCalorie());
                bb.setSetDistance(bean2.getSetDistance() + bb.getSetDistance());
                bb.setWatts(bean2.getWatts() + bb.getWatts());
            }
            bb.setTime(bb.getTime() / list.size());
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            list.add(bb);
        }

        workoutsAdapter2 = new WorkoutsAdapter2(list);
        rvWorkouts2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWorkouts2.setAdapter(workoutsAdapter2);

        tvInfoTitle.setText("Date：" + TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd"));

        switch (bean.getMode()) {
            case MyConstant.NORMAL:
                tvTitleTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                tvTitleTime.setText(TimeStringUtil.getSToMinSecValue(bean.getSetTargetTime()));
                break;
            case MyConstant.GOAL_DISTANCE:
                tvTitleTime.setText(bean.getSetTargetDistance() + "M");
                break;
            case MyConstant.GOAL_CALORIES:
                tvTitleTime.setText(bean.getSetTargetCalorie() + "C");
                break;
            case MyConstant.INTERVAL_TIME:
                tvTitleTime.setText((bean.getInterval() + "x:" + bean.getSetTime() + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_DISTANCE:
                tvTitleTime.setText((bean.getInterval() + "x" + bean.getSetDistance() + "M" + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_CALORIES:
                tvTitleTime.setText((bean.getInterval() + "x" + bean.getSetCalorie() + "C" + "/:" + bean.getReset_time() + "R"));
                break;
            default:
                break;
        }

        edtInfoNote.setText(bean.getNote() == null ? "" : bean.getNote());

    }

    @Override
    public void onItemDeleteListener(int position) {
        deletePosition = position;
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
