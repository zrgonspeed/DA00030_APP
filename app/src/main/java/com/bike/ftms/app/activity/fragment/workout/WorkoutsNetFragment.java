package com.bike.ftms.app.activity.fragment.workout;

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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.WorkoutsNetAdapter;
import com.bike.ftms.app.adapter.WorkoutsNetAdapter2;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.bean.rundata.get.RunDataInfoDTO;
import com.bike.ftms.app.bean.rundata.get.RunDataResultDTO;
import com.bike.ftms.app.bean.rundata.get.RunDataResultListBO;
import com.bike.ftms.app.common.HttpParam;
import com.bike.ftms.app.http.OkHttpCallBack;
import com.bike.ftms.app.http.OkHttpHelper;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import tech.gujin.toast.ToastUtil;


/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class WorkoutsNetFragment extends WorkoutsFragment implements WorkoutsNetAdapter.OnItemClickListener, WorkoutsNetAdapter.OnItemDeleteListener {
    private static final String TAG = WorkoutsNetFragment.class.getSimpleName();
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
    private WorkoutsNetAdapter workoutsAdapter;
    private WorkoutsNetAdapter2 workoutsAdapter2;
    private boolean isEdit = false;
    private List<RunDataResultDTO> runDataResultDTOS = new ArrayList<>();
    private int clickPosition;
    private int deletePosition;

    public WorkoutsNetFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workouts;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        workoutsAdapter = new WorkoutsNetAdapter(runDataResultDTOS);
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

    private void getRunDataResultList() {
// 获取列表
        OkHttpHelper.getInstance().get(HttpParam.RUN_DATA_LIST_URL + "?offset=" + 0 + "&limit=" + 10, null, new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());

                // 网络没打开
                // 请求超时
                ToastUtil.show(getString(R.string.timeout), true, ToastUtil.Mode.REPLACEABLE);
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("请求成功 ->> response.body().string() == " + response);

                if (httpCode == 200) {
                    RunDataResultListBO runDataResultListBO = GsonUtil.GsonToBean(response, RunDataResultListBO.class);

                    String next = runDataResultListBO.getNext();
                    List<RunDataResultDTO> runDataResultDTOS = runDataResultListBO.getItems();
//                    Logger.i(runDataResultDTOS.toString());

                    //
                    WorkoutsNetFragment.this.runDataResultDTOS = runDataResultDTOS;

                    // 显示列表数据
                    workoutsAdapter.notifyDataSetChanged();

                } else if (httpCode == 401) {

                }
            }
        });
    }

//    private RunDataDetail getRunDataInfo(String workout_id) {

//
//    }

    @Override
    protected String getTAG() {
        return TAG;
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
                int i = LitePal.updateAll(RowerDataBean1.class, contentValues, "date=?", String.valueOf(runDataResultDTOS.get(clickPosition).getDate()));
                if (i == 1) {
                    runDataResultDTOS.get(clickPosition).setRemarks(edtInfoNote.getText().toString());
                    workoutsAdapter.notifyItemChanged(clickPosition);
                    ToastUtil.show(R.string.save_success, true);
                } else {
                    ToastUtil.show(R.string.save_fail, true);
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
                int j = LitePal.deleteAll(RowerDataBean1.class, "date=?", String.valueOf(runDataResultDTOS.get(deletePosition).getDate()));
                if (j > 0) {
                    runDataResultDTOS.remove(deletePosition);
                    workoutsAdapter.notifyDataSetChanged();
                    ToastUtil.show(R.string.delete_success, true);
                } else {
                    ToastUtil.show(getString(R.string.delete_fail), true);
                }

                rlDelete.setVisibility(View.GONE);
                break;
        }
    }

    private void refreshList1() {
        runDataResultDTOS.clear();
//        runDataResultDTOS.addAll(LitePal.order("date desc").find(RowerDataBean1.class, true));
        getRunDataResultList();
    }

    private void setEditView() {
        workoutsAdapter.setShowDelete(isEdit);
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

    /**
     * 刷新详细列表
     */
    private void notifyInfoData() {
        RunDataResultDTO bean = runDataResultDTOS.get(clickPosition);
        ArrayList<RunDataInfoDTO> list = new ArrayList<>();

        for (RunDataInfoDTO oo : list) {
            Logger.e("oo == " + oo);
        }

        workoutsAdapter2 = new WorkoutsNetAdapter2(list);
        rvWorkouts2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWorkouts2.setAdapter(workoutsAdapter2);

        tvInfoTitle.setText("Date：" + bean.getDate());

        /*switch (bean.getRunMode()) {
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
        }*/

//        edtInfoNote.setText(bean.getNote() == null ? "" : bean.getNote());

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
