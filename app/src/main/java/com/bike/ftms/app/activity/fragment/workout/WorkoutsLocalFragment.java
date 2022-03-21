package com.bike.ftms.app.activity.fragment.workout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.Debug;
import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.MainActivity;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.adapter.WorkoutsLocalAdapter;
import com.bike.ftms.app.adapter.WorkoutsLocalAdapter2;
import com.bike.ftms.app.ble.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.view.RunInfoVO;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.gujin.toast.ToastUtil;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class WorkoutsLocalFragment extends WorkoutsFragment implements WorkoutsLocalPresenter.GetRunDataInfoFromServerCB, WorkoutsLocalPresenter.DeleteLocalRunDataCB, WorkoutsLocalPresenter.UpdateLocalNoteCB, WorkoutsLocalPresenter.DeleteRunDataFromServerCB, WorkoutsLocalPresenter.GetRunDataFromServerCB, WorkoutsLocalView, WorkoutsLocalPresenter.UploadNoteCB, WorkoutsLocalPresenter.UploadRunDataCB, WorkoutsLocalAdapter.OnItemClickListener, WorkoutsLocalAdapter.OnItemDeleteListener {
    private static final String TAG = WorkoutsLocalFragment.class.getSimpleName();
    // @BindView(R.id.ll_workouts_item_type)
    // LinearLayout ll_workouts_item_type;


    @BindView(R.id.tv_upload)
    TextView tv_upload;
    @BindView(R.id.tv_workouts_local_id)
    TextView tv_workouts_local_id;
    @BindView(R.id.tv_back_workouts)
    TextView tv_back_workouts;
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

    @BindView(R.id.tv_info_date)
    TextView tv_info_date;
    @BindView(R.id.tv_info_mode)
    TextView tv_info_mode;
    @BindView(R.id.iv_info_device)
    ImageView iv_info_device;
    @BindView(R.id.edt_info_note)
    EditText edt_info_note;
    // 详细页面标题
    @BindView(R.id.tv_info_title_500)
    TextView tv_info_title_500;
    @BindView(R.id.tv_info_title_sm)
    TextView tv_info_title_sm;
    @BindView(R.id.tv_info_title_ave_one_km)
    TextView tv_info_title_ave_one_km;
    @BindView(R.id.tv_info_title_level)
    TextView tv_info_title_level;

    private WorkoutsLocalAdapter workoutsLocalAdapter;
    private WorkoutsLocalAdapter2 workoutsLocalAdapter2;
    private boolean isEdit = false;
    private List<RowerDataBean1> rowerDataBean1List = new ArrayList<>();
    private final Vector<Boolean> vector = new Vector<>();
    private int clickPosition;
    private int deletePosition;
    private WorkoutsLocalPresenter presenter;

    public WorkoutsLocalFragment() {
        presenter = new WorkoutsLocalPresenter();
        presenter.attachView(this);

        presenter.setGetRunDataFromServerCB(this);
        presenter.setGetRunDataInfoFromServerCB(this);
        presenter.setUploadNoteCB(this);
        presenter.setUploadRunDataCB(this);
        presenter.setDeleteRunDataFromServerCB(this);

        presenter.setDeleteLocalRunDataCB(this);
        presenter.setUpdateLocalNoteCB(this);

        this.rowerDataBean1List = presenter.getRowerDataBean1List();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isResumed() && !isHidden() && ll_workouts.getVisibility() == View.VISIBLE) {
            presenter.findRunDataFromLocalDB();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detachView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workouts;
    }

    @Override
    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setWorkouts1();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected String getTAG() {
        return TAG;
    }

/*    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && ll_workouts.getVisibility() == View.VISIBLE) {
            presenter.findRunDataFromLocalDB();
        }
    }*/

    @OnClick({R.id.tv_upload, R.id.tv_back_workouts, R.id.tv_edit, R.id.iv_back, R.id.tv_workouts, R.id.iv_info_back, R.id.tv_done, R.id.tv_cancel, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_back_workouts:
                if (isEdit) {
                    isEdit = false;
                    setEditView();
                }
                break;
            case R.id.tv_upload:
                if (Debug.canLogin) {
                    presenter.uploadRunData();
                }
                break;
            case R.id.tv_edit:
                isEdit = !isEdit;
                setEditView();
                break;
            case R.id.tv_done:
                String s = edt_info_note.getText().toString();
                if (TextUtils.isEmpty(s) || TextUtils.isEmpty(s.trim())) {
                    return;
                }
                HttpRowerDataBean1 rowerDataBean1 = (HttpRowerDataBean1) rowerDataBean1List.get(clickPosition);
                if (rowerDataBean1.getStatus() == 1) {
                    presenter.uploadNote(rowerDataBean1, s);
                    presenter.updateLocalNote(rowerDataBean1, s);
                } else {
                    presenter.updateLocalNote(rowerDataBean1, s);
                }
                break;
            case R.id.tv_workouts:
            case R.id.iv_info_back:
                presenter.findRunDataFromLocalDB();
                ll_info.setVisibility(View.GONE);
                ll_workouts.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_cancel:
                rl_delete.setVisibility(View.GONE);
                break;
            case R.id.tv_ok:
                tv_edit.performClick();
                HttpRowerDataBean1 bean1 = (HttpRowerDataBean1) rowerDataBean1List.get(deletePosition);
                if (bean1.getStatus() == 1) {
                    presenter.deleteRunDataFromServer(bean1, deletePosition);
                } else {
                    presenter.deleteRunDataFromLocal(bean1, deletePosition);
                    break;
                }
        }
    }

    private void setEditView() {
        workoutsLocalAdapter.setShowDelete(isEdit);
        workoutsLocalAdapter.notifyDataSetChanged();
        if (isEdit) {
//            tv_upload.setText(getString(R.string.workouts));
//            tv_upload.setTextColor(getResources().getColor(R.color.color_black));
            tv_upload.setVisibility(View.GONE);
            tv_back_workouts.setVisibility(View.VISIBLE);
            iv_back.setVisibility(View.VISIBLE);
            tv_title.setText(getString(R.string.workouts_edit));
            tv_edit.setText(getString(R.string.workouts_done));
        } else {
//            tv_upload.setText(getString(R.string.workouts_upload));
//            tv_upload.setTextColor(getResources().getColor(R.color.color_0B4531));
            tv_upload.setVisibility(View.VISIBLE);
            tv_back_workouts.setVisibility(View.GONE);
            iv_back.setVisibility(View.GONE);
            tv_title.setText(getString(R.string.workouts));
            tv_edit.setText(getString(R.string.workouts_edit));
            presenter.findRunDataFromLocalDB();
        }
    }

    @Override
    public void onItemClickListener(int position) {
        clickPosition = position;

        HttpRowerDataBean1 bean = (HttpRowerDataBean1) rowerDataBean1List.get(clickPosition);
        if (bean.getStatus() == 1) {
            // 从服务器获取详细运动数据，根据workout_id
            presenter.getRunDataInfoFromServer(bean);
        } else {
            setWorkouts2();
        }
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


    @Override
    public void uploadRunDataSuccess(boolean nowUploaded) {
        ToastUtil.show("已全部上传完");
        if (nowUploaded) {
            Handler handler = new Handler(Looper.getMainLooper());
            ((MainActivity) (getActivity())).showUploadSuccess();

            // 3秒后消失
            handler.postDelayed(() -> {
                ((MainActivity) (getActivity())).hideUpload();
                presenter.findRunDataFromLocalDB();
            }, 3000);
        }
    }

    @Override
    public void uploadRunDataFail(int status, String message) {
        if (status == 1) {
            // 网络没打开
            // 请求超时
            ToastUtil.show(R.string.timeout, true, ToastUtil.Mode.REPLACEABLE);
            ((MainActivity) (getActivity())).showUploadFailed();
        } else {
            ToastUtil.show(getString(R.string.upload_fail) + message);
        }
    }

    @Override
    public void uploadNoteSuccess() {

    }

    @Override
    public void uploadNoteFail() {

    }

    @Override
    public void getRunDataFromServerSuccess() {
        workoutsLocalAdapter.notifyDataSetChanged();
    }

    @Override
    public void getRunDataFromServerFail() {
        // 网络没打开
        // 请求超时
        ToastUtil.show(R.string.timeout, true, ToastUtil.Mode.REPLACEABLE);
    }

    @Override
    public void showUploading() {
        // 显示上传中
        ((MainActivity) (getActivity())).showUploading();
    }

    @Override
    public void findRunDataFromLocalDBSuccess() {
        workoutsLocalAdapter.notifyDataSetChanged();
        if (UserManager.getInstance().getUser() != null) {
            presenter.getRunDataFromServer();
        }
    }

    @Override
    public void deleteRunDataFromServerSuccess(HttpRowerDataBean1 bean1) {
        workoutsLocalAdapter.notifyDataSetChanged();
        presenter.deleteDBrowbean1(bean1);
        rl_delete.setVisibility(View.GONE);
    }

    @Override
    public void deleteRunDataFromServerFail() {
        // 网络没打开
        // 请求超时
        ToastUtil.show(R.string.timeout, true, ToastUtil.Mode.REPLACEABLE);
        rl_delete.setVisibility(View.GONE);
    }

    @Override
    public void updateLocalNoteSuccess() {
        workoutsLocalAdapter.notifyItemChanged(clickPosition);
        ToastUtil.show(getString(R.string.save_success), true);
    }

    @Override
    public void updateLocalNoteFail() {
        ToastUtil.show(getString(R.string.save_fail), true);
    }

    @Override
    public void deleteLocalRunDataSuccess() {
        workoutsLocalAdapter.notifyDataSetChanged();
        ToastUtil.show(getString(R.string.delete_success), true, ToastUtil.Mode.REPLACEABLE);
        rl_delete.setVisibility(View.GONE);
    }

    @Override
    public void deleteLocalRunDataFail() {
        ToastUtil.show(getString(R.string.delete_fail), true, ToastUtil.Mode.REPLACEABLE);
        rl_delete.setVisibility(View.GONE);
    }

    @Override
    public void getRunDataInfoFromServerSuccess(HttpRowerDataBean1 bean) {
        // workoutsLocalAdapter2 = new WorkoutsLocalAdapter2(bean.getList());
        rv_workouts2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_workouts2.setAdapter(workoutsLocalAdapter2);

        // 页面其他设置
        tv_info_date.setText("Date：" + TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd"));
        tv_info_mode.setText(bean.getType());
        edt_info_note.setText(bean.getNote() == null ? "" : bean.getNote());

        ll_info.setVisibility(View.VISIBLE);
        ll_workouts.setVisibility(View.GONE);
    }

    @Override
    public void getRunDataInfoFromServerFail() {
        ToastUtil.show(R.string.get_rundata_info_fail);
    }


    private void setWorkouts1() {
        Logger.i("rowerDataBean1List == " + rowerDataBean1List);
        workoutsLocalAdapter = new WorkoutsLocalAdapter(rowerDataBean1List, vector);

        rv_workouts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_workouts.setAdapter(workoutsLocalAdapter);
        workoutsLocalAdapter.addItemClickListener(this);
        workoutsLocalAdapter.addItemDeleteClickListener(this);
        rl_delete.setOnTouchListener((v, event) -> true);
        rl_online.setOnTouchListener((v, event) -> true);

        tv_title.setOnClickListener((e) -> {
            // TODO: 2022/3/15  
            // 点击3次，显示机型号
            Debug.canShowItemDeviceName = !Debug.canShowItemDeviceName;
            Debug.canShowItemLocalId = !Debug.canShowItemLocalId;

            if (Debug.canShowItemLocalId) {
                tv_workouts_local_id.setVisibility(View.VISIBLE);
            } else {
                tv_workouts_local_id.setVisibility(View.GONE);
            }

            workoutsLocalAdapter.notifyDataSetChanged();
        });
    }

    private void setWorkouts2() {
        RowerDataBean1 bean = rowerDataBean1List.get(clickPosition);
        // List<RowerDataBean2> bean2List = presenter.setWorkouts2List(bean);
        RunInfoVO runInfoVO = presenter.newSetWorkouts2List(bean);
        // PrintUtils.printJson("RunInfoVO", GsonUtil.GsonString(runInfoVO), "");
        Logger.i(runInfoVO.toString());

        // 详细页面设置
        {
            // workoutsLocalAdapter2 = new WorkoutsLocalAdapter2(bean2List);
            workoutsLocalAdapter2 = new WorkoutsLocalAdapter2(runInfoVO);
            rv_workouts2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            rv_workouts2.setAdapter(workoutsLocalAdapter2);

            tv_info_date.setText("Date：" + runInfoVO.getDate());
            iv_info_device.setImageDrawable(MyConstant.getCategoryImg(runInfoVO.getCategoryType()));
            tv_info_mode.setText(runInfoVO.getRunMode());
            // 不同机型不同显示内容
            switch (runInfoVO.getCategoryType()) {
                case MyConstant.CATEGORY_BOAT: {
                    tv_info_title_500.setVisibility(View.VISIBLE);
                    tv_info_title_sm.setVisibility(View.VISIBLE);
                    tv_info_title_ave_one_km.setVisibility(View.GONE);
                    tv_info_title_level.setVisibility(View.GONE);
                }
                break;
                case MyConstant.CATEGORY_BIKE: {
                    tv_info_title_ave_one_km.setVisibility(View.VISIBLE);
                    tv_info_title_level.setVisibility(View.VISIBLE);
                    tv_info_title_500.setVisibility(View.GONE);
                    tv_info_title_sm.setVisibility(View.GONE);
                }
                break;
                case MyConstant.CATEGORY_SKI: {
                    tv_info_title_500.setVisibility(View.VISIBLE);
                    tv_info_title_sm.setVisibility(View.VISIBLE);
                    tv_info_title_ave_one_km.setVisibility(View.GONE);
                    tv_info_title_level.setVisibility(View.GONE);
                }
                break;
            }
        }

        // 备注设置
        edt_info_note.setText(runInfoVO.getNote() == null ? "" : runInfoVO.getNote());

        ll_info.setVisibility(View.VISIBLE);
        ll_workouts.setVisibility(View.GONE);
    }

    @Override
    public void setPortLayout() {
        // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_workouts_item_type.getLayoutParams();
        // ll_workouts_item_type.setPadding(0, 0, 0, 0);
        // ll_workouts_item_type.setLayoutParams(params);
        workoutsLocalAdapter.setPortLayout();
    }

    @Override
    public void setLandLayout() {
        // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_workouts_item_type.getLayoutParams();
        // ll_workouts_item_type.setPadding(getIntDimen(R.dimen.dp_50), 0, 0, 0);
        // ll_workouts_item_type.setLayoutParams(params);
        workoutsLocalAdapter.setLandLayout();
    }
}
