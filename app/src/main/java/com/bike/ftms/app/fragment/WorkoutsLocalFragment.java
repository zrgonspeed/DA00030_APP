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
import com.bike.ftms.app.activity.login.LoginActivity;
import com.bike.ftms.app.adapter.WorkoutsLocalAdapter;
import com.bike.ftms.app.adapter.WorkoutsLocalAdapter2;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.bean.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
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
public class WorkoutsLocalFragment extends WorkoutsFragment implements WorkoutsLocalAdapter.OnItemClickListener, WorkoutsLocalAdapter.OnItemDeleteListener {
    private static final String TAG = WorkoutsLocalFragment.class.getSimpleName();

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
    private WorkoutsLocalAdapter workoutsLocalAdapter;
    private WorkoutsLocalAdapter2 workoutsLocalAdapter2;
    private boolean isEdit = false;
    private final List<RowerDataBean1> rowerDataBean1List = new ArrayList<>();
    private int clickPosition;
    private int deletePosition;

    public WorkoutsLocalFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workouts;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        workoutsLocalAdapter = new WorkoutsLocalAdapter(rowerDataBean1List);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWorkouts.setAdapter(workoutsLocalAdapter);
        workoutsLocalAdapter.addItemClickListener(this);
        workoutsLocalAdapter.addItemDeleteClickListener(this);
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
            refreshList1();
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
                int i = LitePal.updateAll(RowerDataBean1.class, contentValues, "date=?", String.valueOf(rowerDataBean1List.get(clickPosition).getDate()));
                if (i == 1) {
                    rowerDataBean1List.get(clickPosition).setNote(edtInfoNote.getText().toString());
                    workoutsLocalAdapter.notifyItemChanged(clickPosition);
                    Toast.makeText(getContext(), "Save successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Save fail", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_workouts:
            case R.id.iv_info_back:
                refreshList1();
                llInfo.setVisibility(View.GONE);
                llWorkouts.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel:
                rlDelete.setVisibility(View.GONE);
                break;
            case R.id.tv_ok:
                tvEdit.performClick();
                int j = LitePal.deleteAll(RowerDataBean1.class, "date=?", String.valueOf(rowerDataBean1List.get(deletePosition).getDate()));
                if (j > 0) {
                    rowerDataBean1List.remove(deletePosition);
                    workoutsLocalAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Delete successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Delete fail", Toast.LENGTH_LONG).show();
                }

                rlDelete.setVisibility(View.GONE);
                break;
        }
    }

    private void refreshList1() {
        rowerDataBean1List.clear();
        rowerDataBean1List.addAll(LitePal.order("date desc").find(RowerDataBean1.class, true));
        workoutsLocalAdapter.notifyDataSetChanged();
    }

    private void setEditView() {
        workoutsLocalAdapter.setShowDelete(isEdit);
        refreshList1();
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
        RowerDataBean1 bean = rowerDataBean1List.get(clickPosition);

        ArrayList<RowerDataBean2> list = new ArrayList<>();
        for (RowerDataBean2 bean2 : bean.getList()
        ) {
            list.add(bean2.copy());
        }

        for (RowerDataBean2 oo : list) {
            Logger.e(TAG,"oo == " + oo);
        }
//        ToastUtil.show("list.size == " + list.size());
        Logger.d(TAG,"list.size == " + list.size());
        if (list.size() == 0) {
            RowerDataBean2 rowerDataBean2 = new RowerDataBean2(bean);
            list.add(rowerDataBean2);
        }

        if (bean.getRunMode() == MyConstant.GOAL_TIME) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setRunMode(bean.getRunMode());
            bb.setCalories_hr(bean.getCalories_hr());
            bb.setWatts(bean.getWatts());

            long initDistance = 0;
            long initTime = bean.getSetGoalTime();
            long initCal = 0;
            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 每段的运动时间
                bean2.setTime(initTime - bean2.getTime());
                bb.setTime(bb.getTime() + bean2.getTime());
                initTime = initTime - bean2.getTime();

                // 每段的卡路里
                bean2.setCalorie(bean2.getCalorie() - initCal);
                bb.setCalorie(bb.getCalorie() + bean2.getCalorie());
                initCal = initCal + bean2.getCalorie();

                // 每段的运动距离 倒数
                bean2.setDistance(bean2.getDistance() - initDistance);
                bb.setDistance(bb.getDistance() + bean2.getDistance());
                initDistance = initDistance + bean2.getDistance();
            }
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }

        if (bean.getRunMode() == MyConstant.GOAL_DISTANCE) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setRunMode(bean.getRunMode());
            bb.setCalories_hr(bean.getCalories_hr());
            bb.setWatts(bean.getWatts());

            long initDistance = bean.getSetGoalDistance();
            long initTime = 0;
            long initCal = 0;
            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());


                /**
                 *                if (list.indexOf(bean2) != list.size() - 1 ) {
                 *                     bean2.setDistance(100);
                 *                     bb.setDistance(bb.getDistance() + bean2.getDistance());
                 *                     initDistance = initDistance - bean2.getDistance();
                 *                 }else {
                 *                     bean2.setDistance(initDistance - bean2.getDistance());
                 *                     bb.setDistance(bb.getDistance() + bean2.getDistance());
                 *                     initDistance = initDistance - bean2.getDistance();
                 *                 }
                 */

                // 每段的运动距离 倒数
                bean2.setDistance(initDistance - bean2.getDistance());
                bb.setDistance(bb.getDistance() + bean2.getDistance());
                initDistance = initDistance - bean2.getDistance();

                // 每段的运动时间
                bean2.setTime(bean2.getTime() - initTime);
                bb.setTime(bb.getTime() + bean2.getTime());
                initTime = initTime + bean2.getTime();

                // 每段的卡路里
                bean2.setCalorie(bean2.getCalorie() - initCal);
                bb.setCalorie(bb.getCalorie() + bean2.getCalorie());
                initCal = initCal + bean2.getCalorie();
            }
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }

        if (bean.getRunMode() == MyConstant.GOAL_CALORIES) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setRunMode(bean.getRunMode());
            bb.setCalories_hr(bean.getCalories_hr());
            bb.setWatts(bean.getWatts());

            long initDistance = 0;
            long initTime = 0;
            long initCal = bean.getSetGoalCalorie();
            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 每段的卡路里
                bean2.setCalorie(initCal - bean2.getCalorie());
                bb.setCalorie(bb.getCalorie() + bean2.getCalorie());
                initCal = initCal - bean2.getCalorie();

                // 每段的运动时间
                bean2.setTime(bean2.getTime() - initTime);
                bb.setTime(bb.getTime() + bean2.getTime());
                initTime = initTime + bean2.getTime();

                // 每段的运动距离 倒数
                bean2.setDistance(bean2.getDistance() - initDistance);
                bb.setDistance(bb.getDistance() + bean2.getDistance());
                initDistance = initDistance + bean2.getDistance();
            }
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }


        if (bean.getRunMode() == MyConstant.INTERVAL_TIME) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setRunMode(bean.getRunMode());
            bb.setCalories_hr(bean.getCalories_hr());
            bb.setWatts(bean.getWatts() + bb.getWatts());

            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 总和
                if (list.indexOf(bean2) == list.size() - 1) {
                    if (bean2.getSetIntervalTime() == bean2.getTime()) {
                        bb.setSetIntervalTime(bean2.getSetIntervalTime() + bb.getSetIntervalTime());
                    } else {
                        bb.setSetIntervalTime((bean2.getSetIntervalTime() - bean2.getTime()) + bb.getSetIntervalTime());
                    }
                } else {
                    bb.setSetIntervalTime(bean2.getSetIntervalTime() + bb.getSetIntervalTime());
                }
                bb.setCalorie(bean2.getCalorie() + bb.getCalorie());
                bb.setDistance(bean2.getDistance() + bb.getDistance());

            }
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }

        if (bean.getRunMode() == MyConstant.INTERVAL_DISTANCE) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setRunMode(bean.getRunMode());
            bb.setCalories_hr(bean.getCalories_hr());
            bb.setWatts(bean.getWatts() + bb.getWatts());

            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 总和
                if (list.indexOf(bean2) == list.size() - 1) {
                    if (bean2.getSetIntervalDistance() == bean2.getDistance()) {
                        bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                    } else {
                        bb.setSetIntervalDistance((bean2.getSetIntervalDistance() - bean2.getDistance()) + bb.getSetIntervalDistance());
                    }
                } else {
                    bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                }
                bb.setTime(bean2.getTime() + bb.getTime());
                bb.setCalorie(bean2.getCalorie() + bb.getCalorie());

            }
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }

        if (bean.getRunMode() == MyConstant.INTERVAL_CALORIES) {
            RowerDataBean2 bb = new RowerDataBean2();
            bb.setRunMode(bean.getRunMode());
            bb.setCalories_hr(bean.getCalories_hr());
            bb.setWatts(bean.getWatts() + bb.getWatts());

            for (RowerDataBean2 bean2 : list) {
                // 平均
                bb.setFive_hundred(bean2.getFive_hundred() + bb.getFive_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 总和
                if (list.indexOf(bean2) == list.size() - 1) {
                    if (bean2.getSetIntervalCalorie() == bean2.getCalorie()) {
                        bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
                    } else {
                        bb.setSetIntervalCalorie((bean2.getSetIntervalCalorie() - bean2.getCalorie()) + bb.getSetIntervalCalorie());
                    }
                } else {
                    bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
                }
                bb.setTime(bean2.getTime() + bb.getTime());
                bb.setDistance(bean2.getDistance() + bb.getDistance());

            }
            bb.setFive_hundred(bb.getFive_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }

        for (RowerDataBean2 oo : list) {
            Logger.e(TAG,"oo == " + oo);
        }

        workoutsLocalAdapter2 = new WorkoutsLocalAdapter2(list);
        rvWorkouts2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWorkouts2.setAdapter(workoutsLocalAdapter2);

        tvInfoTitle.setText("Date：" + TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd"));

        switch (bean.getRunMode()) {
            case MyConstant.NORMAL:
                tvTitleTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                tvTitleTime.setText(TimeStringUtil.getSToMinSecValue(bean.getSetGoalTime()));
                break;
            case MyConstant.GOAL_DISTANCE:
                tvTitleTime.setText(bean.getSetGoalDistance() + "M");
                break;
            case MyConstant.GOAL_CALORIES:
                tvTitleTime.setText(bean.getSetGoalCalorie() + "C");
                break;
            case MyConstant.INTERVAL_TIME:
                tvTitleTime.setText((bean.getInterval() + "x:" + bean.getSetIntervalTime() + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_DISTANCE:
                tvTitleTime.setText((bean.getInterval() + "x" + bean.getSetIntervalDistance() + "M" + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_CALORIES:
                tvTitleTime.setText((bean.getInterval() + "x" + bean.getSetIntervalCalorie() + "C" + "/:" + bean.getReset_time() + "R"));
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
        if (llInfo != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && llInfo.getVisibility() == View.VISIBLE) {
                ivInfoBack.performClick();
                return true;
            }
        }
        return false;
    }

}
