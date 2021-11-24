package com.bike.ftms.app.activity.fragment.workout;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.bike.ftms.app.adapter.WorkoutsLocalAdapter;
import com.bike.ftms.app.adapter.WorkoutsLocalAdapter2;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.bean.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;
import timber.log.Timber;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class WorkoutsLocalFragment extends WorkoutsFragment implements WorkoutsLocalAdapter.OnItemClickListener, WorkoutsLocalAdapter.OnItemDeleteListener {
    private static final String TAG = WorkoutsLocalFragment.class.getSimpleName();

    @BindView(R.id.tv_upload)
    TextView tv_upload;
    @BindView(R.id.tv_edit)
    TextView tv_edit;
    @BindView(R.id.rv_workouts)
    RecyclerView rv_workouts;

    @BindView(R.id.rv_workouts2)
    RecyclerView rv_workouts2;

    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.ll_workouts)
    ConstraintLayout ll_workouts;
    @BindView(R.id.ll_info)
    LinearLayout ll_info;
    @BindView(R.id.iv_info_back)
    ImageView iv_info_back;
    @BindView(R.id.rl_delete)
    RelativeLayout rl_delete;
    @BindView(R.id.rl_online)
    RelativeLayout rl_online;

    @BindView(R.id.tv_info_title)
    TextView tv_info_title;
    @BindView(R.id.edt_info_note)
    EditText edt_info_note;
    @BindView(R.id.tv_title_time)
    TextView tv_title_time;
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
        rv_workouts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_workouts.setAdapter(workoutsLocalAdapter);
        workoutsLocalAdapter.addItemClickListener(this);
        workoutsLocalAdapter.addItemDeleteClickListener(this);
        rl_delete.setOnTouchListener((v, event) -> true);
        rl_online.setOnTouchListener((v, event) -> true);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && ll_workouts.getVisibility() == View.VISIBLE) {
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
                contentValues.put("note", edt_info_note.getText().toString());
                int i = LitePal.updateAll(RowerDataBean1.class, contentValues, "date=?", String.valueOf(rowerDataBean1List.get(clickPosition).getDate()));
                if (i == 1) {
                    rowerDataBean1List.get(clickPosition).setNote(edt_info_note.getText().toString());
                    workoutsLocalAdapter.notifyItemChanged(clickPosition);
                    Toast.makeText(getContext(), "Save successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Save fail", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_workouts:
            case R.id.iv_info_back:
                refreshList1();
                ll_info.setVisibility(View.GONE);
                ll_workouts.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel:
                rl_delete.setVisibility(View.GONE);
                break;
            case R.id.tv_ok:
                tv_edit.performClick();
                int j = LitePal.deleteAll(RowerDataBean1.class, "date=?", String.valueOf(rowerDataBean1List.get(deletePosition).getDate()));
                if (j > 0) {
                    rowerDataBean1List.remove(deletePosition);
                    workoutsLocalAdapter.notifyDataSetChanged();
                    ToastUtil.show("Delete successfully", true, ToastUtil.Mode.REPLACEABLE);
                } else {
                    ToastUtil.show("Delete fail", true, ToastUtil.Mode.REPLACEABLE);
                }

                rl_delete.setVisibility(View.GONE);
                break;
        }
    }

    private void refreshList1() {
        LitePal.order("date desc").findAsync(RowerDataBean1.class, true).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> list) {
                Timber.i("数据库查找成功");
                rowerDataBean1List.clear();
                rowerDataBean1List.addAll((Collection<? extends RowerDataBean1>) list);
                workoutsLocalAdapter.notifyDataSetChanged();
            }
        });

//        rowerDataBean1List.addAll(LitePal.order("date desc").find(RowerDataBean1.class, true));
    }

    private void setEditView() {
        workoutsLocalAdapter.setShowDelete(isEdit);
        refreshList1();
        if (isEdit) {
            tv_upload.setText(getString(R.string.workouts));
            tv_upload.setTextColor(getResources().getColor(R.color.color_black));
            iv_back.setVisibility(View.VISIBLE);
            tv_title.setText(getString(R.string.workouts_edit));
            tv_edit.setText(getString(R.string.workouts_done));
        } else {
            tv_upload.setText(getString(R.string.workouts_upload));
            tv_upload.setTextColor(getResources().getColor(R.color.color_0B4531));
            iv_back.setVisibility(View.GONE);
            tv_title.setText(getString(R.string.workouts));
            tv_edit.setText(getString(R.string.workouts_edit));
        }
    }

    @Override
    public void onItemClickListener(int position) {
        clickPosition = position;
        notifyInfoData();
        ll_info.setVisibility(View.VISIBLE);
        ll_workouts.setVisibility(View.GONE);
    }

    private void notifyInfoData() {
        RowerDataBean1 bean = rowerDataBean1List.get(clickPosition);

        ArrayList<RowerDataBean2> list = new ArrayList<>();
        for (RowerDataBean2 bean2 : bean.getList()
        ) {
            list.add(bean2.copy());
        }

        for (RowerDataBean2 oo : list) {
            Timber.e("oo == " + oo);
        }
//        ToastUtil.show("list.size == " + list.size());
        Timber.d("list.size == " + list.size());
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
                bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
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
            bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
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
                bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
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
            bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
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
                bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
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
            bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
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
                bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
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
            bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
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
                bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 总和
                if (list.indexOf(bean2) == list.size() - 1) {
/*                    if (bean2.getSetIntervalDistance() == bean2.getDistance()) {
//                        bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                        bb.setDistance(bean2.getDistance() + bb.getDistance());
                    } else {
//                        bb.setSetIntervalDistance((bean2.getSetIntervalDistance() - bean2.getDistance()) + bb.getSetIntervalDistance());
                        bb.setDistance((bean2.getDistance() - bean2.getDistance()) + bb.getDistance());
                    }*/

                    bb.setDistance(bean2.getDistance() + bb.getDistance());

                } else {
//                    bb.setSetIntervalDistance(bean2.getSetIntervalDistance() + bb.getSetIntervalDistance());
                    bb.setDistance(bean2.getDistance() + bb.getDistance());
                }
                bb.setTime(bean2.getTime() + bb.getTime());
                bb.setCalorie(bean2.getCalorie() + bb.getCalorie());

            }
            bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
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
                bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                bb.setSm(bean2.getSm() + bb.getSm());

                // 总和
                if (list.indexOf(bean2) == list.size() - 1) {
//                    if (bean2.getSetIntervalCalorie() == bean2.getCalorie()) {
//                        bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
//                    } else {
//                        bb.setSetIntervalCalorie((bean2.getSetIntervalCalorie() - bean2.getCalorie()) + bb.getSetIntervalCalorie());
//                    }
                    bb.setCalorie(bean2.getCalorie() + bb.getCalorie());

                } else {
//                    bb.setSetIntervalCalorie(bean2.getSetIntervalCalorie() + bb.getSetIntervalCalorie());
                    bb.setCalorie(bean2.getCalorie() + bb.getCalorie());
                }
                bb.setTime(bean2.getTime() + bb.getTime());
                bb.setDistance(bean2.getDistance() + bb.getDistance());

            }
            bb.setAve_five_hundred(bb.getAve_five_hundred() / list.size());
            bb.setSm(bb.getSm() / list.size());

            bb.setInterval(-1);
            list.add(0, bb);
        }

        for (RowerDataBean2 oo : list) {
            Timber.e("oo == " + oo);
        }

        workoutsLocalAdapter2 = new WorkoutsLocalAdapter2(list);
        rv_workouts2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_workouts2.setAdapter(workoutsLocalAdapter2);

        tv_info_title.setText("Date：" + TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd"));

        switch (bean.getRunMode()) {
            case MyConstant.NORMAL:
                tv_title_time.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                tv_title_time.setText(TimeStringUtil.getSToMinSecValue(bean.getSetGoalTime()));
                break;
            case MyConstant.GOAL_DISTANCE:
                tv_title_time.setText(bean.getSetGoalDistance() + "M");
                break;
            case MyConstant.GOAL_CALORIES:
                tv_title_time.setText(bean.getSetGoalCalorie() + "C");
                break;
            case MyConstant.INTERVAL_TIME:
                tv_title_time.setText((bean.getInterval() + "x:" + bean.getSetIntervalTime() + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_DISTANCE:
                tv_title_time.setText((bean.getInterval() + "x" + bean.getSetIntervalDistance() + "M" + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_CALORIES:
                tv_title_time.setText((bean.getInterval() + "x" + bean.getSetIntervalCalorie() + "C" + "/:" + bean.getReset_time() + "R"));
                break;
            default:
                break;
        }

        edt_info_note.setText(bean.getNote() == null ? "" : bean.getNote());
    }

    @Override
    public void onItemDeleteListener(int position) {
        deletePosition = position;
        rl_delete.setVisibility(View.VISIBLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ll_info != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && ll_info.getVisibility() == View.VISIBLE) {
                iv_info_back.performClick();
                return true;
            }
        }
        return false;
    }

}
