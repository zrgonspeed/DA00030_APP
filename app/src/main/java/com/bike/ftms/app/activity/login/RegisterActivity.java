package com.bike.ftms.app.activity.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.andreabaccega.formedittextvalidator.EmptyValidator;
import com.andreabaccega.formedittextvalidator.Validator;
import com.andreabaccega.widget.DefaultEditTextValidator;
import com.andreabaccega.widget.FormEditText;
import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.bean.RegisterBean;
import com.bike.ftms.app.bean.RegisterMailBean;
import com.bike.ftms.app.bean.ResultBean;
import com.bike.ftms.app.common.HttpParam;
import com.bike.ftms.app.http.OkHttpCallBack;
import com.bike.ftms.app.http.OkHttpHelper;
import com.bike.ftms.app.utils.BasisTimesUtils;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import tech.gujin.toast.ToastUtil;

public class RegisterActivity extends BaseActivity {

    // 下面3个字段用选择器
    @BindView(R.id.edt_birth)
    TextView edt_birth;
    @BindView(R.id.edt_gender)
    TextView edt_gender;
    @BindView(R.id.edt_country)
    TextView edt_country;
    private int choiceSexInx = 0;
    private int choiceCountryInx = 0;
    private String[] sexItems;
    private String[] countryItems;

    @BindView(R.id.edt_first_name)
    FormEditText edt_first_name;
    @BindView(R.id.edt_last_name)
    FormEditText edt_last_name;
    @BindView(R.id.edt_email_address)
    FormEditText edt_email_address;
    @BindView(R.id.edt_user_name)
    FormEditText edt_user_name;
    @BindView(R.id.edt_password)
    FormEditText edt_password;
    @BindView(R.id.edt_confirm_password)
    FormEditText edt_confirm_password;


    @BindView(R.id.sv_form)
    ScrollView sv_form;

/*    @BindView(R.id.cl_code)
    ConstraintLayout cl_code;*/

    @BindView(R.id.edt_email_code)
    FormEditText edt_email_code;

    @BindView(R.id.tv_send_code)
    TextView tv_send_code;

    @BindView(R.id.tv_create_account_send_all)
    TextView tv_create_account_send_all;

    @BindView(R.id.tv_register_fail_back)
    TextView tv_register_fail_back;

    @BindView(R.id.cl_register_success)
    ConstraintLayout cl_register_success;

    @BindView(R.id.cl_register_fail)
    ConstraintLayout cl_register_fail;

    @BindView(R.id.tv_register_success_count)
    TextView tv_register_success_count;

    @BindView(R.id.tv_register_fail_cause)
    TextView tv_register_fail_cause;

    FormEditText[] etArr = new FormEditText[7];
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_new_account;
    }

    @Override
    protected void initData() {
        sexItems = new String[]{getString(R.string.create_male), getString(R.string.create_female)};
        countryItems = getResources().getStringArray(R.array.country_code_list_en);
        edt_birth.setText(BasisTimesUtils.getDeviceTimeOfYMD());
        edt_gender.setText(sexItems[choiceSexInx]);
        edt_country.setText(countryItems[choiceCountryInx]);


        etArr[0] = edt_first_name;
        etArr[1] = edt_last_name;
        etArr[2] = edt_email_code;
        etArr[3] = edt_email_address;
        etArr[4] = edt_user_name;
        etArr[5] = edt_password;
        etArr[6] = edt_confirm_password;
        for (FormEditText formEditText : etArr) {
//            validator.setEmptyAllowed(false, getApplicationContext());
//            validator.setEmptyErrorString("不能为空");
//            validator.resetValidators(getApplicationContext());
//            formEditText.addValidator(new EmptyValidator("ss"));
            formEditText.setOnFocusChangeListener((View v, boolean focus) -> {
//                Logger.e("focus: " + focus);
                if (!focus) {
                    formEditText.testValidity();
                }
            });
        }


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

/*    private EditTextFocusListener focusListener = new EditTextFocusListener();

    private class EditTextFocusListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
//            v.testValidity();
        }
    }*/

    @Override
    protected void initView() {

    }

    @OnClick({R.id.iv_back,
            R.id.edt_birth, R.id.edt_gender, R.id.edt_country,
            R.id.tv_send_code, R.id.tv_create_account,
            R.id.tv_register_fail_back,
    })
    public void onViewClicked(View view) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.edt_birth:
                showSelectBirthdayPicker();
                break;
            case R.id.edt_gender:
                showChoiceSex();
                break;
            case R.id.edt_country:
                showChoiceCountry();
                break;

            case R.id.tv_send_code:
                if (edt_email_address.testValidity()) {
                    tv_send_code.setText("发送中...");
                    new Thread(() -> {
                        sendEmailToServer();
                    }).start();
                }
                break;
            case R.id.tv_create_account:
                // 1.校验数据
                boolean isOk = checkField();
                if (isOk) {
                    // 2.发送到服务器
                    sendAllToServer();
                }
                break;
            case R.id.tv_register_fail_back:
                registerFailBack();
                break;
        }
    }

    /**
     * 校验表单
     *
     * @return
     */
    private boolean checkField() {
        boolean allValid = true;
        for (FormEditText formEditText : etArr) {
            allValid = formEditText.testValidity() && allValid;
        }

        String pass = edt_password.getText().toString().trim();
        String pass2 = edt_confirm_password.getText().toString().trim();
        if (!pass.equals("") && !pass2.equals("")) {
            if (!pass.equals(pass2)) {
                Logger.e("密码不一致");
                ToastUtil.show("密码不一致", ToastUtil.Mode.REPLACEABLE);
                return false;
            }
        }

        Logger.e("allValid  == " + allValid);
        return allValid;
    }

    /**
     * 年月日选择
     */
    private void showSelectBirthdayPicker() {
        String[] strings = edt_birth.getText().toString().split("/");
        BasisTimesUtils.showDatePickerDialog(this, true, "Please select birthday", Integer.valueOf(strings[0])
                , Integer.valueOf(strings[1]), Integer.valueOf(strings[2]), new BasisTimesUtils.OnDatePickerListener() {

                    @Override
                    public void onConfirm(int year, int month, int dayOfMonth) {
                        edt_birth.setText(year + "/" + month + "/" + dayOfMonth);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }

    /**
     * 性别选择
     */
    private void showChoiceSex() {
        AlertDialog.Builder choiceSexDialog = new AlertDialog.Builder(this);
        choiceSexDialog.setTitle(getString(R.string.create_gender_title));
        choiceSexDialog.setItems(sexItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choiceSexInx = which;
                edt_gender.setText(sexItems[choiceSexInx]);
            }
        });
        choiceSexDialog.show();
    }

    /**
     * 国家选择
     */
    private void showChoiceCountry() {
        AlertDialog.Builder choiceCountry = new AlertDialog.Builder(this);
        choiceCountry.setTitle(getString(R.string.create_country_title));
        choiceCountry.setItems(countryItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choiceCountryInx = which;
                edt_country.setText(countryItems[choiceCountryInx]);
            }
        });
        choiceCountry.show();
    }

    /**
     * 发送请求,先发送验证码到邮箱
     */
    private void sendEmailToServer() {
        // 邮箱验证，成功则邮箱收到验证码，用户输入验证码
//        String mailCodeUrl = "http://192.168.50.21:8080" + "/restapi/verify/email";
        RegisterMailBean bean = new RegisterMailBean();
        bean.setEmail(edt_email_address.getText().toString().trim());
        bean.setType("register");

        String json = GsonUtil.GsonString(bean);
        OkHttpHelper.getInstance().post(HttpParam.MAIL_CODE_URL, json, null, new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 响应失败
                Logger.e("请求失败！");
                Logger.e(e.toString());
                ToastUtil.show("连接超时", true, ToastUtil.Mode.REPLACEABLE);
                tv_send_code.setText("发送失败");
            }

            @Override
            public void onSuccess(Call call, int httpCode, String response) {
                // 响应成功，响应码不一定是200
                // String resStr = response.body().string();
                Logger.e("请求成功 ->> response.body().string() == " + response);
                // {"code":"EmailError","message":"邮箱格式不正确"}

                Logger.e("response.toString() == " + response.toString());
                // Response{protocol=http/1.1, code=422, message=, url=http://192.168.50.180:8080/restapi/verify/email}

                if (httpCode == 204) {
                    // 正确响应，无响应体
                    // 显示注册成功界面
                    Logger.e("发送验证码成功");
                    tv_send_code.setText("已发送");
//                    cl_register_success.setVisibility(View.VISIBLE);
                } else if (httpCode == 422) {
                    // 错误响应，响应体包含错误信息
                    // 显示注册失败界面
                    Logger.e("发送验证码失败");
                    ToastUtil.show("邮箱格式不对!");
                    tv_send_code.setText("发送失败");

                    // 封装响应体为bean
                    ResultBean resultBean = GsonUtil.GsonToBean(response, ResultBean.class);
                    Logger.e("resultBean == " + resultBean);
                } else {
                    // 可能是5xx系列，服务器错误
                    Logger.e("发送验证码失败---------------------");
                    sv_form.setVisibility(View.GONE);
                }

            }
        });
    }

    private void sendAllToServer() {
        // 假设邮箱验证通过，用户得到并输入了验证码
        // 封装全部参数
//        String userRegisterUrl = "http://192.168.50.21:8080/restapi/users/register";

        RegisterBean registerBean = new RegisterBean();
        registerBean.setEmail(edt_email_address.getText().toString().trim());
        registerBean.setBirthday(edt_birth.getText().toString());
        registerBean.setFirstname(edt_first_name.getText().toString().trim());
        registerBean.setLastname(edt_last_name.getText().toString().trim());
        registerBean.setUsername(edt_user_name.getText().toString().trim());
        registerBean.setGender(edt_gender.getText().toString().trim());
        registerBean.setPassword(edt_password.getText().toString().trim());
        registerBean.setCode(edt_email_code.getText().toString().trim());
//        registerBean.setCountry(edt_country.getText().toString().trim());

        String registerBeanJson = GsonUtil.GsonString(registerBean);
        OkHttpHelper.getInstance().post(HttpParam.USER_REGISTER_URL, registerBeanJson, null, new OkHttpCallBack() {
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
                // {"code":"EmailError","message":"邮箱格式不正确"}

                // Response{protocol=http/1.1, code=422, message=, url=http://192.168.50.180:8080/restapi/verify/email}

                if (httpCode == 204) {
                    // 正确响应，无响应体
                    // 显示注册成功界面
                    Logger.e("注册成功");
                    cl_register_success.setVisibility(View.VISIBLE);

                    // 5 秒后返回登录页面
                    new Thread(() -> {
                        for (int i = 5; i >= 1; i--) {
                            int finalI = i;
                            runOnUiThread(() -> {
                                tv_register_success_count.setText(finalI + "秒后返回登录界面");
                            });
                            SystemClock.sleep(1000);
                        }
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }).start();
                } else if (httpCode == 422 || httpCode == 403 || httpCode == 409) {
                    // 错误响应，响应体包含错误信息
                    // 显示注册失败界面
                    cl_register_fail.setVisibility(View.VISIBLE);

                    // 封装响应体为bean
                    ResultBean resultBean = GsonUtil.GsonToBean(response, ResultBean.class);
                    Logger.e("注册失败: " + resultBean);

                    tv_register_fail_cause.setText(resultBean.getMessage());
                } else {
                    Logger.e("httpCode == " + httpCode + " 其它处理");
                    // 可能是5xx系列，服务器错误
                    Logger.e("注册失败---");
                }
                sv_form.setVisibility(View.GONE);
            }
        });

// 随便测试
//        if ("123456".equals(edt_email_code.getText().toString().trim())) {
//            // 注册成功
//            cl_register_success.setVisibility(View.VISIBLE);
//        } else {
//            // 注册失败
//            cl_register_fail.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 注册失败时点击按钮返回表单
     */
    private void registerFailBack() {
        cl_register_fail.setVisibility(View.GONE);
        sv_form.setVisibility(View.VISIBLE);
    }
}