package com.bike.ftms.app.activity.user;

import android.view.View;
import android.widget.ImageView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.utils.Logger;

import butterknife.BindView;
import butterknife.OnClick;


public class PersonalDataActivity extends BaseActivity {
    private static final String TAG = PersonalDataActivity.class.getSimpleName();

    @BindView(R.id.iv_back)
    ImageView iv_back;

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
