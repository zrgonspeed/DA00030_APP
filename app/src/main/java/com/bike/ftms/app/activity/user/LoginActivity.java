package com.bike.ftms.app.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.andreabaccega.widget.FormEditText;
import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.MainActivity;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.bean.user.LoginBean;
import com.bike.ftms.app.bean.user.LoginSuccessBean;
import com.bike.ftms.app.bean.user.ResultBean;
import com.bike.ftms.app.common.HttpParam;
import com.bike.ftms.app.http.OkHttpCallBack;
import com.bike.ftms.app.http.OkHttpHelper;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import tech.gujin.toast.ToastUtil;


public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.edt_user_name)
    FormEditText edt_user_name;
    @BindView(R.id.edt_password)
    FormEditText edt_password;

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.btn_login, R.id.tv_forget, R.id.tv_register, R.id.tv_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                // 游客进入
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.iv_back:
//                finish();
                break;
            case R.id.btn_login:
                // 1.校验
                boolean isOk = checkField();
                if (isOk) {
                    // 2.发送到服务器
                    sendAllToServer();
//                    sendAllToServer2();
                }
                break;
            case R.id.tv_forget:
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    /**
     * 测试
     */
    private void sendAllToServer2() {
        String pass = edt_password.getText().toString().trim();
        String userName = edt_user_name.getText().toString().trim();

        if ("zrg".equals(userName) && "123".equals(pass)) {
            // 登录成功
            loginSuccess(null);
        } else {
            loginFail();
        }
    }

    /**
     * 校验表单
     *
     * @return
     */
    private boolean checkField() {
        boolean allValid = true;

        boolean u = edt_user_name.testValidity();
        boolean p = edt_password.testValidity();

        allValid = u && allValid;
        allValid = p && allValid;

        Logger.e("allValid  == " + allValid);
        return allValid;
    }

    private void sendAllToServer() {
        /*
        {
            "account": "test",
            "password": "123456zzz"
        }
         */
        String pass = edt_password.getText().toString().trim();
        String userName = edt_user_name.getText().toString().trim();

        LoginBean loginBean = new LoginBean();
        loginBean.setAccount(userName);
        loginBean.setPassword(pass);
        String jsonStr = GsonUtil.GsonString(loginBean);

        OkHttpHelper.post(HttpParam.USER_LOGIN_URL, jsonStr, null, new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());

                // 网络没打开
                // 请求超时
                ToastUtil.show("连接超时", true, ToastUtil.Mode.REPLACEABLE);
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("请求成功 ->> response.body().string() == " + response);

                if (httpCode == 200) {
                    /*
                    HTTP/1.1 200 OK
                    {
                      "user_id": "111",
                      "username": "test",
                      "token": "38e203a2****************"
                    }
                     */
                    LoginSuccessBean loginSuccessBean = GsonUtil.GsonToBean(response, LoginSuccessBean.class);
                    loginSuccess(loginSuccessBean);
                } else if (httpCode == 422 || httpCode == 404 || httpCode == 401) {
                    ResultBean resultBean = GsonUtil.GsonToBean(response, ResultBean.class);
                    Logger.e("登录失败:" + resultBean.toString());
                    ToastUtil.show("登录失败: " + resultBean.getMessage());
                } else {
                    Logger.e("httpCode == " + httpCode + " 其它处理");
                    Logger.e("登录失败---");
                    ToastUtil.show("登录失败: httpcode = " + httpCode);
                }
            }
        });
    }

    private void loginSuccess(LoginSuccessBean loginSuccessBean) {
        Logger.e("登录成功: " + loginSuccessBean.toString());
        ToastUtil.show("登录成功");

        UserManager.getInstance().setUser(loginSuccessBean);

        // 页面跳转
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void loginFail() {
        ToastUtil.show("登录失败");
    }

}