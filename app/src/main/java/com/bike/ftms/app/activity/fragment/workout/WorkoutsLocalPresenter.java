package com.bike.ftms.app.activity.fragment.workout;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.base.mvp.BasePresenter;
import com.bike.ftms.app.bean.user.ResultBean;
import com.bike.ftms.app.ble.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.get.RunDataInfoDTO;
import com.bike.ftms.app.ble.bean.rundata.get.RunDataResultBO;
import com.bike.ftms.app.ble.bean.rundata.get.RunDataResultDTO;
import com.bike.ftms.app.ble.bean.rundata.get.RunDataResultListBO;
import com.bike.ftms.app.ble.bean.rundata.put.RemarksBO;
import com.bike.ftms.app.ble.bean.rundata.put.RunDataBO;
import com.bike.ftms.app.ble.bean.rundata.put.UploadResult;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean2;
import com.bike.ftms.app.ble.bean.rundata.view.RunInfoItem;
import com.bike.ftms.app.ble.bean.rundata.view.RunInfoVO;
import com.bike.ftms.app.common.HttpParam;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.http.OkHttpCallBack;
import com.bike.ftms.app.http.OkHttpHelper;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import tech.gujin.toast.ToastUtil;

public class WorkoutsLocalPresenter extends BasePresenter<WorkoutsLocalView> {
    private final List<RowerDataBean1> rowerDataBean1List = new ArrayList<>();

    private int noUploadCount = 0;
    private int uploadSuccessCount = 0;

    private UploadRunDataCB uploadRunDataCB;
    private UploadNoteCB uploadNoteCB;
    private GetRunDataFromServerCB getRunDataFromServerCB;
    private DeleteRunDataFromServerCB deleteRunDataFromServerCB;
    private GetRunDataInfoFromServerCB getRunDataInfoFromServerCB;

    private UpdateLocalNoteCB updateLocalNoteCB;
    private DeleteLocalRunDataCB deleteLocalRunDataCB;

    public void setGetRunDataInfoFromServerCB(GetRunDataInfoFromServerCB getRunDataInfoFromServerCB) {
        this.getRunDataInfoFromServerCB = getRunDataInfoFromServerCB;
    }

    public void setDeleteLocalRunDataCB(DeleteLocalRunDataCB deleteLocalRunDataCB) {
        this.deleteLocalRunDataCB = deleteLocalRunDataCB;
    }

    public void setUpdateLocalNoteCB(UpdateLocalNoteCB updateLocalNoteCB) {
        this.updateLocalNoteCB = updateLocalNoteCB;
    }

    public List<RowerDataBean1> getRowerDataBean1List() {
        return rowerDataBean1List;
    }

    public void setGetRunDataFromServerCB(GetRunDataFromServerCB getRunDataFromServerCB) {
        this.getRunDataFromServerCB = getRunDataFromServerCB;
    }

    public void setUploadNoteCB(UploadNoteCB uploadNoteCB) {
        this.uploadNoteCB = uploadNoteCB;
    }

    public void setUploadRunDataCB(UploadRunDataCB uploadRunDataCB) {
        this.uploadRunDataCB = uploadRunDataCB;
    }

    public void setDeleteRunDataFromServerCB(DeleteRunDataFromServerCB deleteRunDataFromServerCB) {
        this.deleteRunDataFromServerCB = deleteRunDataFromServerCB;
    }

    interface UploadRunDataCB {
        /**
         * @param nowUploaded 是之前已经上传完了就为false，是这次才上传完为true
         */
        void uploadRunDataSuccess(boolean nowUploaded);

        void uploadRunDataFail(int status, String message);
    }

    interface UploadNoteCB {

        void uploadNoteSuccess();

        void uploadNoteFail();
    }

    interface GetRunDataFromServerCB {

        void getRunDataFromServerSuccess();

        void getRunDataFromServerFail();
    }

    interface GetRunDataInfoFromServerCB {

        void getRunDataInfoFromServerSuccess(HttpRowerDataBean1 bean);

        void getRunDataInfoFromServerFail();
    }

    interface DeleteRunDataFromServerCB {

        void deleteRunDataFromServerSuccess(HttpRowerDataBean1 bean1);

        void deleteRunDataFromServerFail();
    }


    interface UpdateLocalNoteCB {

        void updateLocalNoteSuccess();

        void updateLocalNoteFail();
    }

    interface DeleteLocalRunDataCB {

        void deleteLocalRunDataSuccess();

        void deleteLocalRunDataFail();
    }

    /**
     * 上传所有运动数据
     */
    public void uploadRunData() {
        // 没登录不能上传
        if (UserManager.getInstance().getUser() == null) {
            ToastUtil.show(R.string.please_logged);
            return;
        }

        if (uploadRunDataCB == null) {
            return;
        }

        // DO -> DTO
//        for (RowerDataBean1 bean1 : rowerDataBean1List) {
//            RunDataResultDTO resultDTO = new RunDataResultDTO(bean1);
//        }


        // 计算没有上传的数量
        for (int i = 0; i < rowerDataBean1List.size(); i++) {
            HttpRowerDataBean1 bean1 = (HttpRowerDataBean1) rowerDataBean1List.get(i);
            if (bean1.getStatus() == 0) {
                noUploadCount++;
            }
        }

        if (noUploadCount != 0) {
            getMvpView().showUploading();
            new Thread(() -> {
                for (int i = 0; i < rowerDataBean1List.size(); i++) {
                    HttpRowerDataBean1 bean1 = (HttpRowerDataBean1) rowerDataBean1List.get(i);
                    if (bean1.getStatus() == 0) {
                        uploadOneData(bean1);
                    }
                }
            }).start();
        } else {
            uploadRunDataCB.uploadRunDataSuccess(false);
        }
    }

    /*
    上传一条运动数据
     */
    public void uploadOneData(RowerDataBean1 bean1) {
        if (UserManager.getInstance().getUser() == null) {
            return;
        }

        Logger.d("准备上传 bean1 == " + bean1);

        RunDataBO runDataBO = new RunDataBO(bean1);
        String jsonStr = GsonUtil.GsonString(runDataBO);

        OkHttpHelper.post(HttpParam.RUN_DATA_UPLOAD_URL, jsonStr, null, buildHeaderMap(), new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());
                uploadRunDataCB.uploadRunDataFail(1, "");
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("请求成功 ->> response.body().string() == " + response);

                if (httpCode == 200) {
                    UploadResult resultBean = GsonUtil.GsonToBean(response, UploadResult.class);
                    Logger.e("上传成功: workout_id == " + resultBean.getWorkout_id());

                    ((HttpRowerDataBean1) bean1).setWorkout_id(resultBean.getWorkout_id());
                    String note = bean1.getNote();
                    if (!TextUtils.isEmpty(note) && !TextUtils.isEmpty(note.trim())) {
                        uploadNote(bean1, note);
                    }

                    int index = rowerDataBean1List.indexOf(bean1);
                    Logger.d("upload success index == " + index);
                    uploadSuccessCount++;

                    // 0 没上传， 1 已上传
                    ((HttpRowerDataBean1) bean1).setStatus(1);

                    // 列表上传完
                    if (uploadSuccessCount == noUploadCount) {
                        // 故意延迟2秒再显示上传成功
                        uploadSuccessCount = 0;

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            uploadRunDataCB.uploadRunDataSuccess(true);
                        }, 5000);
                    }
                } else if (httpCode == 422 || httpCode == 404 || httpCode == 401) {
                    ResultBean resultBean = GsonUtil.GsonToBean(response, ResultBean.class);
                    Logger.e("上传失败:" + resultBean.toString());
                    uploadRunDataCB.uploadRunDataFail(2, resultBean.getMessage());
                } else {
                    Logger.e("httpCode == " + httpCode + " 其它处理");
                    Logger.e("上传失败---");
                    uploadRunDataCB.uploadRunDataFail(3, "");
                }
            }
        });
    }

    /**
     * 上传备注
     *
     * @param bean1
     * @param note
     */
    public void uploadNote(RowerDataBean1 bean1, String note) {
        if (UserManager.getInstance().getUser() == null) {
            return;
        }

        if (uploadNoteCB == null) {
            return;
        }
        HttpRowerDataBean1 httpBean1 = (HttpRowerDataBean1) bean1;
        String workout_id = httpBean1.getWorkout_id();

        RemarksBO remarksBO = new RemarksBO();
        remarksBO.setRemarks(note);
        String jsonStr = GsonUtil.GsonString(remarksBO);

        OkHttpHelper.put(HttpParam.RUN_DATA_UPLOAD_REMARKS_URL + "/" + workout_id + "/remarks", jsonStr, null, buildHeaderMap(), new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());
                ToastUtil.show(R.string.timeout, true, ToastUtil.Mode.REPLACEABLE);
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                Logger.e("请求成功 ->> response.body().string() == " + response);
                if (httpCode == 204) {
                    Logger.e("上传成功: workout_id == " + workout_id);
                    httpBean1.setNote(note);
                    uploadNoteCB.uploadNoteSuccess();
                }
            }
        });
    }

    /**
     * 从服务器获取运动数据
     */
    public void getRunDataFromServer() {
        if (UserManager.getInstance().getUser() == null) {
            return;
        }

        if (getRunDataFromServerCB == null) {
            return;
        }

        // 从服务器获取数据
        OkHttpHelper.get(HttpParam.RUN_DATA_LIST_URL + "?offset=0&limit=500", null, buildHeaderMap(), new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());

                getRunDataFromServerCB.getRunDataFromServerFail();
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("请求成功 ->> response.body().string() == " + response);

                if (httpCode == 200) {

                    RunDataResultListBO runDataResultListBO = GsonUtil.GsonToBean(response, RunDataResultListBO.class);
                    Logger.e("运动数据列表: " + runDataResultListBO);

                    // DTO 转 DO httprowbean1
                    List<RunDataResultDTO> items = runDataResultListBO.getItems();
                    List<HttpRowerDataBean1> httpRowerDataBean1List = new ArrayList<>();
                    for (RunDataResultDTO dto : items) {
                        HttpRowerDataBean1 httpRowerDataBean1 = new HttpRowerDataBean1();
                        httpRowerDataBean1.setDate(TimeStringUtil.getLongTime(dto.getDate()));
                        httpRowerDataBean1.setWorkout_id(dto.getWorkout_id());
                        httpRowerDataBean1.setType(dto.getType());
                        httpRowerDataBean1.setResult(dto.getResult());
                        httpRowerDataBean1.setNote(dto.getRemarks());

                        httpRowerDataBean1.setStatus(1);

                        httpRowerDataBean1List.add(httpRowerDataBean1);
                    }
                    // 日期降序
                    Collections.sort(httpRowerDataBean1List, (o1, o2) -> Long.compare(o2.getDate(), o1.getDate()));

                    // 加在前面
//                    rowerDataBean1List.addAll(0, httpRowerDataBean1List);

                    List<HttpRowerDataBean1> newList = new ArrayList<>();
                    // 本地已经上传的就不显示
                    for (int i = 0; i < rowerDataBean1List.size(); i++) {
                        RowerDataBean1 bean1 = rowerDataBean1List.get(i);
                        boolean isSameDate = false;
                        for (int j = 0; j < httpRowerDataBean1List.size(); j++) {
                            HttpRowerDataBean1 httpbean1 = httpRowerDataBean1List.get(j);
//                            Logger.e("bean1.getDate()  == " + bean1.getDate() + "    httpbean1.getDate() " + httpbean1.getDate());
                            if (bean1.getDate() == httpbean1.getDate()) {
                                isSameDate = true;
                            }
                        }

                        if (!isSameDate) {
                            newList.add((HttpRowerDataBean1) bean1);
                        }
                    }

                    Logger.e("newList == " + newList);
                    newList.addAll(0, httpRowerDataBean1List);

                    rowerDataBean1List.clear();
                    rowerDataBean1List.addAll(newList);

                    getRunDataFromServerCB.getRunDataFromServerSuccess();

                } else {
                    getRunDataFromServerCB.getRunDataFromServerFail();
                }

            }
        });
    }

    public void deleteRunDataFromServer(HttpRowerDataBean1 bean1, int deletePosition) {
        if (UserManager.getInstance().getUser() == null) {
            return;
        }

        // 删除服务器的
        String workout_id = ((HttpRowerDataBean1) bean1).getWorkout_id();
        OkHttpHelper.delete(HttpParam.RUN_DATA_DELETE_URL + "/" + workout_id, null, buildHeaderMap(), new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());
                deleteRunDataFromServerCB.deleteRunDataFromServerFail();
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                Logger.e("请求成功 ->> response.body().string() == " + response);
                if (httpCode == 204) {
                    Logger.e("删除 " + workout_id + " 成功");
                    rowerDataBean1List.remove(deletePosition);
                    deleteRunDataFromServerCB.deleteRunDataFromServerSuccess(bean1);
                }
            }
        });
    }

    /**
     * 设置header，加入token
     *
     * @return
     */
    private Map<String, String> buildHeaderMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", UserManager.getInstance().getUser().getToken());
        return map;
    }


    public void findRunDataFromLocalDB() {
        LitePal.order("date desc").findAsync(RowerDataBean1.class, true).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> list) {
                Logger.i("数据库查找成功");
                rowerDataBean1List.clear();
                for (T t : list) {
                    rowerDataBean1List.add(new HttpRowerDataBean1((RowerDataBean1) t));
                }

                Logger.d(rowerDataBean1List.toString());
                getMvpView().findRunDataFromLocalDBSuccess();
            }
        });
    }

    /**
     * 删除本地数据库中的对应数据
     *
     * @param bean1
     * @return
     */
    public boolean deleteDBrowbean1(RowerDataBean1 bean1) {
        int j = LitePal.deleteAll(RowerDataBean1.class, "date=?", String.valueOf(bean1.getDate()));
        return j > 0;
    }

    public void updateLocalNote(RowerDataBean1 rowerDataBean1, String s) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("note", s);
        // 日期long还不一样，服务器的后三位是000,即不算毫秒
        int i = LitePal.updateAll(RowerDataBean1.class, contentValues, "date=?", String.valueOf(rowerDataBean1.getDate()));
        if (i == 1) {
            rowerDataBean1.setNote(s);
            updateLocalNoteCB.updateLocalNoteSuccess();
        } else {
            updateLocalNoteCB.updateLocalNoteFail();
        }
    }

    public void deleteRunDataFromLocal(HttpRowerDataBean1 bean1, int deletePosition) {
        boolean deleteResultOk = deleteDBrowbean1(bean1);
        if (deleteResultOk) {
            rowerDataBean1List.remove(deletePosition);
            deleteLocalRunDataCB.deleteLocalRunDataSuccess();
        } else {
            deleteLocalRunDataCB.deleteLocalRunDataFail();
        }
    }

    public void getRunDataInfoFromServer(HttpRowerDataBean1 bean) {
        if (UserManager.getInstance().getUser() == null) {
            return;
        }

        List<RowerDataBean2> rowerDataBean2s = new ArrayList<>();
        String workout_id = bean.getWorkout_id();
        OkHttpHelper.get(HttpParam.RUN_DATA_GET_INFO_URL + "/" + workout_id + "/info", null, buildHeaderMap(), new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                getRunDataInfoFromServerCB.getRunDataInfoFromServerFail();
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("请求成功 ->> response.body().string() == " + response);

                if (httpCode == 200) {
                    RunDataResultBO bo = GsonUtil.GsonToBean(response, RunDataResultBO.class);

                    List<RunDataInfoDTO> items = bo.getItems();
                    RunDataInfoDTO totals = bo.getTotals();

                    // 转成bean2
                    // 设置总结item
                    RowerDataBean2 bean2 = new RowerDataBean2();
                    bean2.setTime(TimeStringUtil.getLongTimeHHMMSS(totals.getTime()));
                    bean2.setDistance(Long.parseLong(totals.getMeters().replace("M", "")));
                    bean2.setCalorie(Long.parseLong(totals.getCals()));
                    bean2.setAve_five_hundred(TimeStringUtil.getLongTimeMMSS(totals.getEfm()));
                    bean2.setSm(Integer.parseInt(totals.getSm()));
                    bean2.setCalories_hr(Integer.parseInt(totals.getCalhr()));
                    bean2.setWatts(Integer.parseInt(totals.getWatts()));
                    bean2.setRowerDataBean1(bean);
                    rowerDataBean2s.add(0, bean2);

                    // 设置其他item
                    for (int i = 0; i < items.size(); i++) {
                        RunDataInfoDTO infoDTO = items.get(i);
                        RowerDataBean2 bean22 = new RowerDataBean2();
                        bean22.setTime(TimeStringUtil.getLongTimeHHMMSS(infoDTO.getTime()));
                        bean22.setDistance(Long.parseLong(infoDTO.getMeters().replace("M", "")));
                        bean22.setCalorie(Long.parseLong(infoDTO.getCals()));
                        bean22.setAve_five_hundred(TimeStringUtil.getLongTimeMMSS(infoDTO.getEfm()));
                        bean22.setSm(Integer.parseInt(infoDTO.getSm()));
                        bean22.setCalories_hr(Integer.parseInt(infoDTO.getCalhr()));
                        bean22.setWatts(Integer.parseInt(infoDTO.getWatts()));
                        bean22.setRowerDataBean1(bean);
                        rowerDataBean2s.add(bean22);
                    }

                    bean.setList(rowerDataBean2s);
                    getRunDataInfoFromServerCB.getRunDataInfoFromServerSuccess(bean);
                }
            }
        });
    }

    /**
     * 设置详细界面的VO，包括vo中的items
     */
    public RunInfoVO newSetWorkouts2List(RowerDataBean1 bean) {
        RunInfoVO runInfoVO = new RunInfoVO();
        runInfoVO.setLocal_id(bean.getId());
        // runInfoVO.setServer_id();

        runInfoVO.setNote(bean.getNote());
        runInfoVO.setRunModeNum(bean.getRunMode());
        runInfoVO.setDate(TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd"));
        runInfoVO.setCategoryType(bean.getCategoryType());
        runInfoVO.setDeviceType(bean.getDeviceType());

        // 运动模式显示
        switch (bean.getRunMode()) {
            case MyConstant.NORMAL:
                runInfoVO.setRunMode(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                runInfoVO.setRunMode(TimeStringUtil.getSToMinSecValue(bean.getSetGoalTime()));
                break;
            case MyConstant.GOAL_DISTANCE:
                runInfoVO.setRunMode(bean.getSetGoalDistance() + "M");
                break;
            case MyConstant.GOAL_CALORIES:
                runInfoVO.setRunMode(bean.getSetGoalCalorie() + "C");
                break;
            case MyConstant.INTERVAL_TIME:
                runInfoVO.setRunMode((bean.getInterval() + "x:" + bean.getSetIntervalTime() + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_DISTANCE:
                runInfoVO.setRunMode((bean.getInterval() + "x" + bean.getSetIntervalDistance() + "M" + "/:" + bean.getReset_time() + "R"));
                break;
            case MyConstant.INTERVAL_CALORIES:
                runInfoVO.setRunMode((bean.getInterval() + "x" + bean.getSetIntervalCalorie() + "C" + "/:" + bean.getReset_time() + "R"));
                break;

            case MyConstant.CUSTOM_INTERVAL_TIME:
                runInfoVO.setRunMode("custom interval");
                break;
            case MyConstant.CUSTOM_INTERVAL_DISTANCE:
                runInfoVO.setRunMode("custom interval");
                break;
            case MyConstant.CUSTOM_INTERVAL_CALORIES:
                runInfoVO.setRunMode("custom interval");
                break;
            default:
                break;
        }

        // 拷贝新的item bean2s
        ArrayList<RowerDataBean2> bean2s = new ArrayList<>();
        {
            for (RowerDataBean2 bean2 : bean.getList()) {
                RowerDataBean2 copyBean2 = bean2.copy();
                copyBean2.setRowerDataBean1(bean);
                bean2s.add(copyBean2);
            }
            for (RowerDataBean2 oo : bean2s) {
                Logger.d("未设置新的items前 == " + oo);
            }
            Logger.d("bean2s.size == " + bean2s.size());
            // 只有1条数据, 可能是 normal运动
            if (bean2s.size() == 0) {
                RowerDataBean2 rowerDataBean2 = new RowerDataBean2(bean);
                bean2s.add(rowerDataBean2);
            }
        }

        // item list 设置
        List<RunInfoItem> items = new ArrayList<>();
        for (RowerDataBean2 bean2 : bean2s) {
            // bean2 -> item
            RunInfoItem item = new RunInfoItem();
            item.setAve_500(TimeStringUtil.getSToMinSecValue(bean2.getAve_five_hundred()));
            item.setAve_one_km(TimeStringUtil.getSToMinSecValue(bean2.getAveOneKmTime()));
            item.setCal_hr(String.valueOf(bean2.getCalories_hr()));
            item.setAve_watts(String.valueOf(bean2.getAve_watts()));
            item.setLevel(String.valueOf(bean2.getLevel()));
            item.setSm(String.valueOf(bean2.getSm()));
            if (MyConstant.isGoalMode(bean2.getRunMode())) {
                item.setInterval(String.valueOf(bean2.getRunInterval() + 1));
            } else if (MyConstant.isIntervalMode(bean2.getRunMode())) {
                item.setInterval(String.valueOf(bean2.getInterval()));
            } else {
                item.setInterval(String.valueOf(bean2.getRunInterval()));
            }

            items.add(item);
        }

        // total item  平均设置
        RunInfoItem totalItem = new RunInfoItem();
        if (bean.getRunMode() != MyConstant.NORMAL) {
            long ave_500 = 0;
            long ave_one_km = 0;
            int sm = 0;
            int level = 0;
            int ave_watts = 0;
            int cal_hr = 0;
            // 计算分段平均
            for (RowerDataBean2 bean2 : bean2s) {
                ave_watts = ave_watts + bean2.getAve_watts();
                cal_hr = cal_hr + bean2.getCalories_hr();
                level = level + bean2.getLevel();
                sm = sm + bean2.getSm();
                ave_500 = ave_500 + bean2.getAve_five_hundred();
                ave_one_km = ave_one_km + bean2.getAveOneKmTime();
            }
            totalItem.setAve_watts(String.valueOf(Math.round(ave_watts * 1.0f / bean2s.size())));
            totalItem.setCal_hr(String.valueOf(Math.round(cal_hr * 1.0f / bean2s.size())));
            // 不同机型不同
            totalItem.setAve_500(TimeStringUtil.getSToMinSecValue(ave_500 / bean2s.size()));
            totalItem.setSm(String.valueOf(sm / bean2s.size()));
            totalItem.setAve_one_km(TimeStringUtil.getSToMinSecValue(ave_one_km / bean2s.size()));
            totalItem.setLevel(String.valueOf(level / bean2s.size()));
        }

        // total item 与 其它item   根据不同模式设置  时间 距离 卡路里
        switch (bean.getRunMode()) {
            case MyConstant.NORMAL: {
                RunInfoItem item = items.get(0);
                item.setTime(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                item.setMeters(String.valueOf(bean.getDistance()));
                item.setCals(String.valueOf(bean.getCalorie()));
                item.setInterval(String.valueOf(-1));
            }
            break;
            case MyConstant.GOAL_TIME: {
                long initTime = bean.getSetGoalTime();
                long distance = 0;
                long temp_dist = 0;
                long temp_time = 0;
                long time = 0;
                long cal = 0;
                long temp_cal = 0;

                for (int i = 0; i < bean2s.size(); i++) {
                    RowerDataBean2 bean2 = bean2s.get(i);
                    RunInfoItem item = items.get(i);

                    // 每段的运动时间
                    temp_time = initTime - bean2.getTime();
                    item.setTime(TimeStringUtil.getSToHourMinSecValue(temp_time));
                    initTime = initTime - temp_time;
                    time = time + temp_time;

                    // 每段的卡路里
                    temp_cal = bean2.getCalorie() - cal;
                    item.setCals(String.valueOf(temp_cal));
                    cal = cal + temp_cal;

                    // 每段的运动距离 倒数
                    temp_dist = bean2.getDistance() - distance;
                    item.setMeters(String.valueOf(temp_dist));
                    distance = distance + temp_dist;
                }

                totalItem.setTime(TimeStringUtil.getSToHourMinSecValue(time));
                totalItem.setMeters(String.valueOf(distance));
                totalItem.setCals(String.valueOf(cal));
                totalItem.setInterval(String.valueOf(-1));
                items.add(0, totalItem);
            }
            break;
            case MyConstant.GOAL_DISTANCE: {
                long initDistance = bean.getSetGoalDistance();
                long distance = 0;
                long temp_dist = 0;
                long temp_time = 0;
                long time = 0;
                long cal = 0;
                long temp_cal = 0;
                for (int i = 0; i < bean2s.size(); i++) {
                    RowerDataBean2 bean2 = bean2s.get(i);
                    RunInfoItem item = items.get(i);

                    // 每段的运动距离 倒数
                    temp_dist = initDistance - bean2.getDistance();
                    item.setMeters(String.valueOf(temp_dist));
                    initDistance = initDistance - temp_dist;
                    distance = distance + temp_dist;

                    // 每段的运动时间
                    temp_time = bean2.getTime() - time;
                    item.setTime(TimeStringUtil.getSToHourMinSecValue(temp_time));
                    time = time + temp_time;

                    // 每段的卡路里
                    // 每段的卡路里
                    temp_cal = bean2.getCalorie() - cal;
                    item.setCals(String.valueOf(temp_cal));
                    cal = cal + temp_cal;
                }

                totalItem.setTime(TimeStringUtil.getSToHourMinSecValue(time));
                totalItem.setMeters(String.valueOf(distance));
                totalItem.setCals(String.valueOf(cal));
                totalItem.setInterval(String.valueOf(-1));
                items.add(0, totalItem);
            }
            break;
            case MyConstant.GOAL_CALORIES: {
                long initCal = bean.getSetGoalCalorie();
                long distance = 0;
                long temp_dist = 0;
                long temp_time = 0;
                long time = 0;
                long cal = 0;
                long temp_cal = 0;

                for (int i = 0; i < bean2s.size(); i++) {
                    RowerDataBean2 bean2 = bean2s.get(i);
                    RunInfoItem item = items.get(i);

                    // 每段的卡路里
                    temp_cal = initCal - bean2.getCalorie();
                    item.setCals(String.valueOf(temp_cal));
                    initCal = initCal - temp_cal;
                    cal = cal + temp_cal;

                    // 每段的运动时间
                    temp_time = bean2.getTime() - time;
                    item.setTime(TimeStringUtil.getSToHourMinSecValue(temp_time));
                    time = time + temp_time;

                    // 每段的运动距离 倒数
                    temp_dist = bean2.getDistance() - distance;
                    item.setMeters(String.valueOf(temp_dist));
                    distance = distance + temp_dist;
                }
                totalItem.setTime(TimeStringUtil.getSToHourMinSecValue(time));
                totalItem.setMeters(String.valueOf(distance));
                totalItem.setCals(String.valueOf(cal));
                totalItem.setInterval(String.valueOf(-1));
                items.add(0, totalItem);
            }
            break;

            case MyConstant.INTERVAL_TIME: {
                long initTime = bean.getSetIntervalTime();
                long distance = 0;
                long temp_dist = 0;
                long temp_time = 0;
                long time = 0;
                long cal = 0;
                long temp_cal = 0;


                for (int i = 0; i < bean2s.size(); i++) {
                    RowerDataBean2 bean2 = bean2s.get(i);
                    RunInfoItem item = items.get(i);
                    // 总和
                    if (bean2s.indexOf(bean2) == bean2s.size() - 1) {
                        if (initTime == bean2.getTime()) {
                            time = time + initTime;
                            item.setTime(TimeStringUtil.getSToHourMinSecValue(initTime));
                        } else {
                            time = time + bean2.getTime();
                            item.setTime(TimeStringUtil.getSToHourMinSecValue(bean2.getTime()));
                        }
                    } else {
                        time = time + initTime;
                        item.setTime(TimeStringUtil.getSToHourMinSecValue(initTime));
                    }

                    // 每段的卡路里
                    temp_cal = bean2.getCalorie();
                    item.setCals(String.valueOf(temp_cal));
                    cal = cal + temp_cal;

                    // 每段的运动距离 倒数
                    temp_dist = bean2.getDistance();
                    item.setMeters(String.valueOf(temp_dist));
                    distance = distance + temp_dist;
                }

                totalItem.setTime(TimeStringUtil.getSToHourMinSecValue(time));
                totalItem.setMeters(String.valueOf(distance));
                totalItem.setCals(String.valueOf(cal));
                totalItem.setInterval(String.valueOf(-1));
                items.add(0, totalItem);
            }
            break;
            case MyConstant.INTERVAL_DISTANCE: {
                long initDistance = bean.getSetIntervalDistance();
                long distance = 0;
                long temp_dist = 0;
                long temp_time = 0;
                long time = 0;
                long cal = 0;
                long temp_cal = 0;

                for (int i = 0; i < bean2s.size(); i++) {
                    RowerDataBean2 bean2 = bean2s.get(i);
                    RunInfoItem item = items.get(i);

                    // 平均
                    // 总和
                    if (bean2s.indexOf(bean2) == bean2s.size() - 1) {
                        distance = distance + bean2.getDistance();
                    } else {
                        distance = distance + bean2.getDistance();
                    }
                    item.setMeters(String.valueOf(bean2.getDistance()));

                    // 每段的卡路里
                    temp_cal = bean2.getCalorie();
                    item.setCals(String.valueOf(temp_cal));
                    cal = cal + temp_cal;

                    // 每段的时间
                    if (runInfoVO.getDeviceType() == MyConstant.DEVICE_AA01990) {
                        item.setTime(TimeStringUtil.getSToHourMinSecValue(bean2.getTime() - temp_time));
                        time = time + (bean2.getTime() - temp_time);
                        temp_time = bean2.getTime();
                    } else {
                        temp_time = bean2.getTime();
                        item.setTime(TimeStringUtil.getSToHourMinSecValue(temp_time));
                        time = time + temp_time;
                    }

                }
                totalItem.setTime(TimeStringUtil.getSToHourMinSecValue(time));
                totalItem.setMeters(String.valueOf(distance));
                totalItem.setCals(String.valueOf(cal));
                totalItem.setInterval(String.valueOf(-1));
                items.add(0, totalItem);
            }
            break;
            case MyConstant.INTERVAL_CALORIES: {
                long initCal = bean.getSetIntervalCalorie();
                long distance = 0;
                long temp_dist = 0;
                long temp_time = 0;
                long time = 0;
                long cal = 0;
                long temp_cal = 0;

                for (int i = 0; i < bean2s.size(); i++) {
                    RowerDataBean2 bean2 = bean2s.get(i);
                    RunInfoItem item = items.get(i);
                    // 总和
                    if (bean2s.indexOf(bean2) == bean2s.size() - 1) {
                        cal = bean2.getCalorie() + cal;
                        temp_cal = bean2.getCalorie();
                    } else {
                        cal = cal + initCal;
                        temp_cal = bean2.getSetIntervalCalorie();
                    }

                    item.setCals(String.valueOf(temp_cal));

                    // 每段的运动距离 倒数
                    temp_dist = bean2.getDistance();
                    item.setMeters(String.valueOf(temp_dist));
                    distance = distance + temp_dist;

                    // 每段的时间
                    if (runInfoVO.getDeviceType() == MyConstant.DEVICE_AA01990) {
                        item.setTime(TimeStringUtil.getSToHourMinSecValue(bean2.getTime() - temp_time));
                        time = time + (bean2.getTime() - temp_time);
                        temp_time = bean2.getTime();
                    } else {
                        temp_time = bean2.getTime();
                        item.setTime(TimeStringUtil.getSToHourMinSecValue(temp_time));
                        time = time + temp_time;
                    }

                }

                totalItem.setTime(TimeStringUtil.getSToHourMinSecValue(time));
                totalItem.setMeters(String.valueOf(distance));
                totalItem.setCals(String.valueOf(cal));
                totalItem.setInterval(String.valueOf(-1));
                items.add(0, totalItem);
            }
            break;
            default:
                // idle模式?
                break;
        }

        // item meters加单位 "M"
        for (RunInfoItem item : items) {
            item.setMeters(item.getMeters() + "M");
        }

        // normal模式，totalItem内部数据为 null，items列表只有1条
        runInfoVO.setItems(items);
        runInfoVO.setTotalItem(totalItem);
        return runInfoVO;
    }
}
