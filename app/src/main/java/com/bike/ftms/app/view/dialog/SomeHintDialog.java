package com.bike.ftms.app.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.OnOrientationChanged;
import com.bike.ftms.app.manager.storage.SpManager;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.UIUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/26
 */
public class SomeHintDialog extends Dialog implements OnOrientationChanged {
    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//消息标题文本
    private TextView messageTv;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private int w = MATCH_PARENT;
    private int h = MATCH_PARENT;
    private LinearLayout ll_content;
    private LinearLayout ll_sv_tv;
    private LinearLayout ll_bottom_button;
    private ScrollView sv_tv;

    private Activity context;

    public SomeHintDialog(Activity context) { //R.style.MyDialog
        this(context, MATCH_PARENT, MATCH_PARENT);
    }

    public SomeHintDialog(Activity mainActivity, int w, int h) {
        super(mainActivity);
        this.context = mainActivity;
        this.w = w;
        this.h = h;
    }


    private BTOnclickListener btOnclickListener;

    public void setBtOnclickListener(BTOnclickListener btOnclickListener) {
        this.btOnclickListener = btOnclickListener;
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
        yes.setOnClickListener(v -> {
            if (btOnclickListener != null) {
                btOnclickListener.onYesClick();
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(v -> {
            if (btOnclickListener != null) {
                btOnclickListener.onNoClick();
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (btOnclickListener != null) {
            btOnclickListener.onDismiss();
        }
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
        } else if (type == 2) {
            titleTv.setMinHeight((int) (llParams.height * 0.15));
            titleTv.setMaxHeight((int) (llParams.height * 0.15));

            llParams_bottom.height = (int) (llParams.height * 0.15);
            ll_bottom_button.setLayoutParams(llParams_bottom);

            ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
            ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

            messageTv.setTextSize(getPage1TextSize());

            Logger.e("messageTv.size " + messageTv.getTextSize());
        }
    }

    private float getPage2TextSize() {
        float textSize = (getContext().getResources().getDimension(R.dimen.sp_6));
        Logger.e("den == " + UIUtils.getDensity(context) + "   dpi == " + UIUtils.getDPI(context));
        Logger.e("set fontSize == " + textSize);
        return textSize;
    }

    private float getPage1TextSize() {
        return getPage2TextSize();
    }


    public interface BTOnclickListener {
        public void onNoClick();

        public void onYesClick();

        public void onNext();

        public void onDismiss();
    }

    private int page = 1;

    public static SomeHintDialog showSomeHintDialog(Activity activity, SomeHintDialog someHintDialog) {
        if (someHintDialog == null) {
            someHintDialog = new SomeHintDialog(activity);
            someHintDialog.setTitle(activity.getString(R.string.warm_tip));
            someHintDialog.setMessage(activity.getString(R.string.some_hint));
            SomeHintDialog finalSomeHintDialog = someHintDialog;

            someHintDialog.setBtOnclickListener(new BTOnclickListener() {
                @Override
                public void onNoClick() {
                    if (finalSomeHintDialog.page == 1) {
                        finalSomeHintDialog.dismiss();
                        activity.finish();
                        return;
                    }
                    if (finalSomeHintDialog.page == 2) {
                        finalSomeHintDialog.messageTv.setText(R.string.some_hint);
                        finalSomeHintDialog.messageTv.setTextSize(finalSomeHintDialog.getPage1TextSize());

                        finalSomeHintDialog.page = 1;
                        finalSomeHintDialog.yes.setText(R.string.accept);
                        finalSomeHintDialog.no.setText(R.string.no_accept);
                    }
                }

                @Override
                public void onYesClick() {
                    if (finalSomeHintDialog.page == 1) {
                        onNext();
                        finalSomeHintDialog.page = 2;
                        return;
                    }

                    if (finalSomeHintDialog.page == 2) {
                        SpManager.setSkipHint(true);
                        finalSomeHintDialog.dismiss();
                        return;
                    }
                }

                @Override
                public void onNext() {
                    finalSomeHintDialog.messageTv.setText(R.string.some_hint_2);
                    finalSomeHintDialog.messageTv.setTextSize(finalSomeHintDialog.getPage2TextSize());
                    finalSomeHintDialog.yes.setText(R.string.accept);
                    finalSomeHintDialog.no.setText(R.string.previous);
                }

                @Override
                public void onDismiss() {
                    Logger.i("someHintDialog dismiss");
                    if (!SpManager.getSkipHint()) {
                        activity.finish();
                    }
                }
            });
        }

        someHintDialog.show();
        someHintDialog.yes.setText(R.string.accept);
        someHintDialog.no.setText(R.string.no_accept);

        int rootHeight = UIUtils.getHeight(activity);
        int rootWidth = UIUtils.getWidth(activity);
        WindowManager.LayoutParams attributes = someHintDialog.getWindow().getAttributes();
        attributes.width = (int) (rootWidth * 0.8);
        someHintDialog.getWindow().setAttributes(attributes);
        Logger.e("attributes.w " + attributes.width);
        Logger.e("attributes.h " + attributes.height);

        someHintDialog.setContentWidthHeight((int) (rootWidth * 0.8), (int) (rootHeight * 0.8));
        someHintDialog.setType(2);

        return someHintDialog;
    }

    @Override
    public void setPortLayout() {

    }

    @Override
    public void setLandLayout() {

    }
}
