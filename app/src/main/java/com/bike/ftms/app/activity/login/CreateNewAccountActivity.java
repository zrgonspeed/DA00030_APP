package com.bike.ftms.app.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.bean.RegisterBean;
import com.bike.ftms.app.bean.RegisterMailBean;
import com.bike.ftms.app.http.OkHttpCallBack;
import com.bike.ftms.app.http.OkHttpHelper;
import com.bike.ftms.app.utils.BasisTimesUtils;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class CreateNewAccountActivity extends BaseActivity {

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
    EditText edt_first_name;
    @BindView(R.id.edt_last_name)
    EditText edt_last_name;
    @BindView(R.id.edt_email_address)
    EditText edt_email_address;
    @BindView(R.id.edt_user_name)
    EditText edt_user_name;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.edt_confirm_password)
    EditText edt_confirm_password;

    @BindView(R.id.sv_form)
    ScrollView sv_form;

/*    @BindView(R.id.cl_code)
    ConstraintLayout cl_code;*/

    @BindView(R.id.edt_email_code)
    EditText edt_email_code;

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
    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.iv_back,
            R.id.edt_birth, R.id.edt_gender, R.id.edt_country,
            R.id.tv_send_code, R.id.tv_create_account,
            R.id.tv_register_fail_back,
    })
    public void onViewClicked(View view) {
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
                sendEmailToServer();
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

    private boolean checkField() {
        String email = edt_email_address.getText().toString().trim();
        if ("".equals(email)) {
            // 邮箱不能为空
            return false;
        }

        return true;
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
        tv_send_code.setText("已发送");

        // 邮箱验证，成功则邮箱收到验证码，用户输入验证码
        String mailCodeUrl = "http://rowerdata-test.anplus-tech.com/restapi/verify/email";
        RegisterMailBean bean = new RegisterMailBean();
        bean.setEmail(edt_email_address.getText().toString().trim());
        bean.setType("register");

        String json = GsonUtil.GsonString(bean);
        /*OkHttpHelper.getInstance().post(mailCodeUrl, json, null, new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e("请求失败！");
                Logger.e(e.toString());
                e.printStackTrace();
            }

            @Override
            public void onSuccess(Call call, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("onSuccess ->> response == " + response);
                Logger.e("邮箱验证成功！已发送验证码到邮箱。");

                // 显示输入验证码的界面
                cl_code.setVisibility(View.VISIBLE);
                sv_form.setVisibility(View.GONE);
            }
        });*/
    }

    private void sendAllToServer() {
        // 假设邮箱验证通过，用户得到并输入了验证码
        // 封装全部参数
        String userRegisterUrl = "http://rowerdata-test.anplus-tech.com/restapi/users/register";

        RegisterBean registerBean = new RegisterBean();
        registerBean.setEmail(edt_email_address.getText().toString().trim());
        registerBean.setBirthday(edt_birth.getText().toString());
        registerBean.setFirstname(edt_first_name.getText().toString().trim());
        registerBean.setLastname(edt_last_name.getText().toString().trim());
        registerBean.setUsername(edt_user_name.getText().toString().trim());
        registerBean.setGender(edt_gender.getText().toString().trim());
        registerBean.setPassword(edt_password.getText().toString().trim());
        registerBean.setCode("6666");
//        registerBean.setCountry(edt_country.getText().toString().trim());

        String registerBeanJson = GsonUtil.GsonString(registerBean);
/*        OkHttpHelper.getInstance().post(userRegisterUrl, registerBeanJson, null, new OkHttpCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e("请求失败！");
                Logger.e(e.toString());
                e.printStackTrace();
            }

            @Override
            public void onSuccess(Call call, String response) {
                // 响应成功，响应码不一定是200
                Logger.e("请求成功 ->> response == " + response);
                //if ()
                Logger.e("注册成功" + response);

                // 显示注册成功界面
            }
        });*/


        if ("123456".equals(edt_email_code.getText().toString().trim())) {
            // 注册成功
            cl_register_success.setVisibility(View.VISIBLE);
        } else {
            // 注册失败
            cl_register_fail.setVisibility(View.VISIBLE);
        }
        sv_form.setVisibility(View.GONE);
    }

    /**
     * 注册失败时点击按钮返回表单
     */
    private void registerFailBack() {
        cl_register_fail.setVisibility(View.GONE);
        sv_form.setVisibility(View.VISIBLE);
    }
}