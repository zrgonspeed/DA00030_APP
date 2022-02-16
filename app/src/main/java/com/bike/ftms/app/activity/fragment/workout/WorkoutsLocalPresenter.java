package com.bike.ftms.app.activity.fragment.workout;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.base.mvp.BasePresenter;
import com.bike.ftms.app.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean2;
import com.bike.ftms.app.bean.rundata.get.RunDataInfoDTO;
import com.bike.ftms.app.bean.rundata.get.RunDataResultBO;
import com.bike.ftms.app.bean.rundata.get.RunDataResultDTO;
import com.bike.ftms.app.bean.rundata.get.RunDataResultListBO;
import com.bike.ftms.app.bean.rundata.put.RemarksBO;
import com.bike.ftms.app.bean.rundata.put.RunDataBO;
import com.bike.ftms.app.bean.rundata.put.UploadResult;
import com.bike.ftms.app.bean.user.ResultBean;
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

                Logger.e(rowerDataBean1List.toString());
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

    public List<RowerDataBean2> setWorkouts2List(RowerDataBean1 bean) {
        // 拷贝新的item list
        ArrayList<RowerDataBean2> list = new ArrayList<>();
        for (RowerDataBean2 bean2 : bean.getList()) {
            RowerDataBean2 copyBean2 = bean2.copy();
            copyBean2.setRowerDataBean1(bean);
            list.add(copyBean2);
        }

        for (RowerDataBean2 oo : list) {
            Logger.e("oo == " + oo);
        }
        Logger.e("list.size == " + list.size());


        if (list.size() == 0) {
            RowerDataBean2 rowerDataBean2 = new RowerDataBean2(bean);
            list.add(rowerDataBean2);
        }

        RowerDataBean2 bb = new RowerDataBean2();

        if (bean.getRunMode() != MyConstant.NORMAL) {
            // 计算分段平均
            for (RowerDataBean2 bean2 : list) {
                bb.setWatts(bean2.getWatts() + bb.getWatts());
                bb.setCalories_hr(bean2.getCalories_hr() + bb.getCalories_hr());
            }
            bb.setWatts(Math.round(bb.getWatts() * 1.0f / list.size()));
            bb.setCalories_hr(Math.round(bb.getCalories_hr() * 1.0f / list.size()));
        }

        // 不同模式的总结item设置
        switch (bean.getRunMode()) {
            case MyConstant.GOAL_TIME: {
                bb.setRunMode(bean.getRunMode());
                // bb.setCalories_hr(bean.getCalories_hr());
                // bb.setWatts(bean.getWatts());

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
            break;
            case MyConstant.GOAL_DISTANCE: {
                bb.setRunMode(bean.getRunMode());
                // bb.setCalories_hr(bean.getCalories_hr());
                // bb.setWatts(bean.getWatts());

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
            break;
            case MyConstant.GOAL_CALORIES: {
                bb.setRunMode(bean.getRunMode());
                // bb.setCalories_hr(bean.getCalories_hr());
                // bb.setWatts(bean.getWatts());

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
            break;
            case MyConstant.INTERVAL_TIME: {
                bb.setRunMode(bean.getRunMode());
                // bb.setCalories_hr(bean.getCalories_hr());
                // bb.setWatts(bean.getWatts() + bb.getWatts());

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
            break;
            case MyConstant.INTERVAL_DISTANCE: {
                bb.setRunMode(bean.getRunMode());
                // bb.setCalories_hr(bean.getCalories_hr());
                // bb.setWatts(bean.getWatts() + bb.getWatts());

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
            break;
            case MyConstant.INTERVAL_CALORIES: {
                bb.setRunMode(bean.getRunMode());
                // bb.setCalories_hr(bean.getCalories_hr());
                // bb.setWatts(bean.getWatts() + bb.getWatts());

                for (RowerDataBean2 bean2 : list) {
                    // 平均
                    bb.setAve_five_hundred(bean2.getAve_five_hundred() + bb.getAve_five_hundred());
                    bb.setSm(bean2.getSm() + bb.getSm());

                    // 总和
                    if (list.indexOf(bean2) == list.size() - 1) {
                        bb.setCalorie((bean2.getSetIntervalCalorie() - bean2.getCalorie()) + bb.getCalorie());
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
            break;
            default:
                // idle模式?
                break;
        }

        list.get(0).setRowerDataBean1(bean);

        for (RowerDataBean2 oo : list) {
            Logger.e("oo == " + oo);
        }

        return list;
    }
}
