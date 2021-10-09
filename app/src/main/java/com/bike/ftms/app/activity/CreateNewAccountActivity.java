package com.bike.ftms.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    @BindView(R.id.edt_gender)
    TextView edtGender;
    @BindView(R.id.edt_country)
    TextView edtCountry;
    private int choiceSexInx = 0;
    private int choiceCountryInx = 0;
    private String[] sexItems;
    private String[] countryItems;

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
        edtBirth.setText(BasisTimesUtils.getDeviceTimeOfYMD());
        edtGender.setText(sexItems[choiceSexInx]);
        edtCountry.setText(countryItems[choiceCountryInx]);
    }

    @Override
    protected void initView() {

    }


    @OnClick({R.id.iv_back, R.id.edt_birth, R.id.tv_create_account, R.id.edt_gender, R.id.edt_country})
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
            case R.id.tv_create_account:
                break;
            case R.id.edt_country:
                showChoiceCountry();
                break;
        }
    }

    /**
     * 年月日选择
     */
    private void showSelectBirthdayPicker() {
        String[] strings = edtBirth.getText().toString().split("/");
        BasisTimesUtils.showDatePickerDialog(this, true, "Please select birthday", Integer.valueOf(strings[0])
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

    private void showChoiceSex() {
        AlertDialog.Builder choiceSexDialog = new AlertDialog.Builder(this);
        choiceSexDialog.setTitle(getString(R.string.create_gender_title));
        choiceSexDialog.setItems(sexItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choiceSexInx = which;
                edtGender.setText(sexItems[choiceSexInx]);
            }
        });
        choiceSexDialog.show();
    }

    private void showChoiceCountry() {
        AlertDialog.Builder choiceCountry = new AlertDialog.Builder(this);
        choiceCountry.setTitle(getString(R.string.create_country_title));
        choiceCountry.setItems(countryItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choiceCountryInx = which;
                edtCountry.setText(countryItems[choiceCountryInx]);
            }
        });
        choiceCountry.show();
    }
}