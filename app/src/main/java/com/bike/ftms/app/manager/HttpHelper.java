package com.bike.ftms.app.manager;

import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.MainActivity;
import com.bike.ftms.app.activity.user.UserManager;
import com.bike.ftms.app.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.bean.rundata.put.RemarksBO;
import com.bike.ftms.app.bean.rundata.put.RunDataBO;
import com.bike.ftms.app.bean.rundata.put.UploadResult;
import com.bike.ftms.app.bean.user.ResultBean;
import com.bike.ftms.app.common.HttpParam;
import com.bike.ftms.app.http.OkHttpCallBack;
import com.bike.ftms.app.http.OkHttpHelper;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import tech.gujin.toast.ToastUtil;

public class HttpHelper {
    private static final String TAG = HttpHelper.class.getSimpleName();
    private static HttpHelper instance;

    private HttpHelper() {
    }

    public static HttpHelper getInstance() {
        if (instance == null) {
            synchronized (HttpHelper.class) {
                if (instance == null) {
                    instance = new HttpHelper();
                }
            }
        }
        return instance;
    }



   /* public void uploadOneData(RowerDataBean1 bean1) {
        Logger.d("准备上传 bean1 == " + bean1);

        RunDataBO runDataBO = new RunDataBO(bean1);
        String jsonStr = GsonUtil.GsonString(runDataBO);

        // 设置header，加入token
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", UserManager.getInstance().getUser().getToken());

        OkHttpHelper.post(HttpParam.RUN_DATA_UPLOAD_URL, jsonStr, null, map, new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());

                // 网络没打开
                // 请求超时
                ToastUtil.show(R.string.timeout, true, ToastUtil.Mode.REPLACEABLE);

                ((MainActivity) (getActivity())).showUploadFailed();
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
                        HttpHelper.getInstance().uploadNote(bean1, note);
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
                        new Thread(() -> {
                            SystemClock.sleep(5000);
                            ((MainActivity) (getActivity())).runOnUiThread(() -> {
                                ((MainActivity) (getActivity())).showUploadSuccess();

                                // 3秒后消失
                                new Handler(((MainActivity) (getActivity())).getMainLooper()).postDelayed(() -> {
                                    ((MainActivity) (getActivity())).hideUpload();

                                    refreshList1();
//                                    workoutsLocalAdapter.notifyDataSetChanged();
                                }, 3000);
                            });
                        }).start();
                    }

                } else if (httpCode == 422 || httpCode == 404 || httpCode == 401) {
                    ResultBean resultBean = GsonUtil.GsonToBean(response, ResultBean.class);
                    Logger.e("上传失败:" + resultBean.toString());
                    ToastUtil.show(getString(R.string.upload_fail) + resultBean.getMessage());

                    ((MainActivity) (getActivity())).showUploadFailed();
                } else {
                    Logger.e("httpCode == " + httpCode + " 其它处理");
                    Logger.e("上传失败---");
                    ToastUtil.show(getString(R.string.upload_fail_httpcode) + httpCode);

                    ((MainActivity) (getActivity())).showUploadFailed();
                }
            }
        });
    }*/
}
