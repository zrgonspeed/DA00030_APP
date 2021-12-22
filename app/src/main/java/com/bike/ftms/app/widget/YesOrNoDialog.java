package com.bike.ftms.app.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.MainActivity;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.UIUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/26
 */
public class YesOrNoDialog extends Dialog {
    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//消息标题文本
    private TextView messageTv;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    private int w = MATCH_PARENT;
    private int h = MATCH_PARENT;
    private LinearLayout ll_content;
    private LinearLayout ll_sv_tv;
    private LinearLayout ll_bottom_button;
    private ScrollView sv_tv;

    private Activity context;

    public YesOrNoDialog(MainActivity context) { //R.style.MyDialog
        this(context, MATCH_PARENT, MATCH_PARENT);
    }

    public YesOrNoDialog(MainActivity mainActivity, int w, int h) {
        super(mainActivity);
        this.context = mainActivity;
        this.w = w;
        this.h = h;
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_yes_or_no);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.width = w;
//        params.height = h;
//        getWindow().setAttributes(params);
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        titleTv = (TextView) findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_sv_tv = (LinearLayout) findViewById(R.id.ll_sv_tv);
        sv_tv = (ScrollView) findViewById(R.id.sv_tv);
        ll_bottom_button = (LinearLayout) findViewById(R.id.ll_bottom_button);

        boolean debug = false;
        if (debug) {

        } else {
            titleTv.setBackgroundResource(R.color.transparent);
            messageTv.setBackgroundResource(R.color.transparent);
            ll_content.setBackgroundResource(R.color.transparent);
            ll_sv_tv.setBackgroundResource(R.color.transparent);
            ll_bottom_button.setBackgroundResource(R.color.transparent);
            sv_tv.setBackgroundResource(R.color.transparent);
        }
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }

    public void setContentWidthHeight(int w, int h) {
        ViewGroup.LayoutParams layoutParams = ll_content.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        ll_content.setLayoutParams(layoutParams);
        ll_content.invalidate();
    }

    public void setType(int type) {
        ViewGroup.LayoutParams llParams = ll_content.getLayoutParams();
        ViewGroup.LayoutParams llParams_bottom = ll_bottom_button.getLayoutParams();
        ViewGroup.LayoutParams ll_sv_tv_Params = ll_sv_tv.getLayoutParams();

        ScrollView sv_tv = findViewById(R.id.sv_tv);
        ViewGroup.LayoutParams sv_params = sv_tv.getLayoutParams();

        int rootHeight = UIUtils.getHeight(context);
        int rootWidth = UIUtils.getWidth(context);

        Logger.e("rootWidth == " + rootWidth + "     rootHeight == " + rootHeight);

        if (type == 1) {
            titleTv.setMinHeight((int) (llParams.height * 0.30));
            titleTv.setMaxHeight((int) (llParams.height * 0.30));

            llParams_bottom.height = (int) (llParams.height * 0.30);
            ll_bottom_button.setLayoutParams(llParams_bottom);

            ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
            ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

            // 设置文字居中
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            messageTv.setLayoutParams(lp);
            // 设置文字大小
            float v = UIUtils.getDensity(context) * UIUtils.getDPI(context);
            if (v > 1000) {
                messageTv.setTextSize((float) (getContext().getResources().getDimension(R.dimen.f_dp_6) * (v / 1000.0)));
            } else {
                messageTv.setTextSize((float) (getContext().getResources().getDimension(R.dimen.f_dp_6) * (1000.0 / v)));
            }
        } else if (type == 2) {
            titleTv.setMinHeight((int) (llParams.height * 0.15));
            titleTv.setMaxHeight((int) (llParams.height * 0.15));

            llParams_bottom.height = (int) (llParams.height * 0.15);
            ll_bottom_button.setLayoutParams(llParams_bottom);

            ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
            ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

            float v = UIUtils.getDensity(context) * UIUtils.getDPI(context);
            if (v > 1000) {
                messageTv.setTextSize((float) (getContext().getResources().getDimension(R.dimen.f_dp_6) * (v / 1000.0)));
            } else {
                messageTv.setTextSize((float) (getContext().getResources().getDimension(R.dimen.f_dp_6) * (1000.0 / v)));
            }
        }
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
}
