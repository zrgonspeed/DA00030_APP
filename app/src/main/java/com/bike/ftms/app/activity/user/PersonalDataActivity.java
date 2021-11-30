package com.bike.ftms.app.activity.user;

import android.view.View;
import android.widget.ImageView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.bean.user.LoginSuccessBean;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.widget.SettingBar;

import butterknife.BindView;
import butterknife.OnClick;


public class PersonalDataActivity extends BaseActivity {
    private static final String TAG = PersonalDataActivity.class.getSimpleName();

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.sb_person_data_name)
    SettingBar sb_person_data_name;
    @BindView(R.id.sb_person_data_username)
    SettingBar sb_person_data_username;
    @BindView(R.id.sb_person_data_gender)
    SettingBar sb_person_data_gender;
    @BindView(R.id.sb_person_data_address)
    SettingBar sb_person_data_address;
    @BindView(R.id.sb_person_data_email)
    SettingBar sb_person_data_email;
    @BindView(R.id.sb_person_data_birthday)
    SettingBar sb_person_data_birthday;

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.personal_data_activity;
    }

    @Override
    protected void initData() {
        Logger.i("initData()");
    }

    @Override
    protected void initView() {
        Logger.i("initView()");

        LoginSuccessBean user = UserManager.getInstance().getUser();
        if (user == null) {
            return;
        }

        sb_person_data_name.setRightText(user.getFirstname() + user.getLastname());
        sb_person_data_username.setRightText(user.getUsername());
        sb_person_data_address.setRightText(user.getCountry());
        sb_person_data_birthday.setRightText(user.getBirthday());
        sb_person_data_gender.setRightText(user.getGender());
        sb_person_data_email.setRightText(user.getEmail());
    }

    @OnClick({R.id.iv_back, R.id.sb_person_data_signout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.sb_person_data_signout:
                Logger.e("点击了 退出登录 按钮");
                signOut();
                finish();
                break;
            default:
                break;
        }
    }

    private void signOut() {
        UserManager.getInstance().signOut();
    }
}
