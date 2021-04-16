package com.bike.ftms.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edt_user_name)
    EditText edtUserName;
    @BindView(R.id.edt_password)
    EditText edtPassword;

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

    @OnClick({R.id.iv_back, R.id.btn_login, R.id.tv_forget, R.id.tv_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_login:
                break;
            case R.id.tv_forget:
                break;
            case R.id.tv_create:
                startActivity(new Intent(this, CreateNewAccountActivity.class));
                break;
        }
    }
}