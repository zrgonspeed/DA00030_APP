package com.bike.ftms.app.activity.user;

import android.view.View;
import android.widget.ImageView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.utils.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class PersonalDataActivity extends BaseActivity {
    private static final String TAG = PersonalDataActivity.class.getSimpleName();

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @Override
    protected int getLayoutId() {
        return R.layout.personal_data_activity;
    }

    @Override
    protected void initData() {
        Timber.i("initData()");
    }

    @Override
    protected void initView() {
        Timber.i("initView()");
    }

    @OnClick({R.id.iv_back, R.id.sb_person_data_signout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.sb_person_data_signout:
                Timber.e("点击了 退出登录 按钮");
                finish();
                break;
            default:
                break;
        }
    }
}
