package com.bike.ftms.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bike.ftms.app.R;
import com.bike.ftms.app.base.BaseActivity;
import com.bike.ftms.app.utils.BasisTimesUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateNewAccountActivity extends BaseActivity {

    @BindView(R.id.edt_birth)
    TextView edtBirth;

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
        edtBirth.setText(BasisTimesUtils.getDeviceTimeOfYMD());
    }

    @Override
    protected void initView() {
    }


    @OnClick({R.id.iv_back, R.id.edt_birth, R.id.tv_create_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.edt_birth:
                showSelectBirthdayPicker();
                break;
            case R.id.tv_create_account:
                break;
        }
    }

    /**
     * 年月日选择
     */
    private void showSelectBirthdayPicker() {
        String[] strings =edtBirth.getText().toString().split("/");
        BasisTimesUtils.showDatePickerDialog(this, true, "Please select birthday",Integer.valueOf(strings[0])
                , Integer.valueOf(strings[1]), Integer.valueOf(strings[2]), new BasisTimesUtils.OnDatePickerListener() {

            @Override
            public void onConfirm(int year, int month, int dayOfMonth) {
                edtBirth.setText(year + "/" + month + "/" + dayOfMonth);
            }

            @Override
            public void onCancel() {
            }
        });
    }
}