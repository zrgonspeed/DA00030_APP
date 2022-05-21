package com.bike.ftms.app.view.dialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
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

    private final Activity context;

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

        titleTv.setBackgroundResource(R.color.transparent);
        messageTv.setBackgroundResource(R.color.transparent);
        ll_content.setBackgroundResource(R.color.transparent);
        ll_sv_tv.setBackgroundResource(R.color.transparent);
        ll_bottom_button.setBackgroundResource(R.color.transparent);
        sv_tv.setBackgroundResource(R.color.transparent);
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

    public interface BTOnclickListener {
        void onNoClick();

        void onYesClick();

        void onNext();

        void onDismiss();
    }

    private int page = 1;

    public static SomeHintDialog showSomeHintDialog(Activity activity, SomeHintDialog someHintDialog, int ori) {
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

        someHintDialog.setOri(ori);
        return someHintDialog;
    }

    private void setOri(int ori) {
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            setLandLayout();
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            setPortLayout();
        }
    }

    @Override
    public void setPortLayout() {
        int rootHeight = UIUtils.getHeight(context);
        int rootWidth = UIUtils.getWidth(context);
        setContentWidthHeight((int) (rootWidth * 0.9), (int) (rootHeight * 0.6));

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = (int) (rootWidth * 0.9);
        getWindow().setAttributes(attributes);

        ViewGroup.LayoutParams llParams = ll_content.getLayoutParams();
        ViewGroup.LayoutParams llParams_bottom = ll_bottom_button.getLayoutParams();
        ViewGroup.LayoutParams ll_sv_tv_Params = ll_sv_tv.getLayoutParams();

        titleTv.setMinHeight((int) (llParams.height * 0.15));
        titleTv.setMaxHeight((int) (llParams.height * 0.15));

        llParams_bottom.height = (int) (llParams.height * 0.15);
        ll_bottom_button.setLayoutParams(llParams_bottom);

        ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
        ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

        messageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (getContext().getResources().getDimension(R.dimen.sp_15)));
        Logger.e("messageTv.size " + messageTv.getTextSize());
    }

    @Override
    public void setLandLayout() {
        int rootHeight = UIUtils.getHeight(context);
        int rootWidth = UIUtils.getWidth(context);
        setContentWidthHeight((int) (rootWidth * 0.9), (int) (rootHeight * 0.8));

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = (int) (rootWidth * 0.9);
        getWindow().setAttributes(attributes);

        ViewGroup.LayoutParams llParams = ll_content.getLayoutParams();
        ViewGroup.LayoutParams llParams_bottom = ll_bottom_button.getLayoutParams();
        ViewGroup.LayoutParams ll_sv_tv_Params = ll_sv_tv.getLayoutParams();

        titleTv.setMinHeight((int) (llParams.height * 0.2));
        titleTv.setMaxHeight((int) (llParams.height * 0.2));

        llParams_bottom.height = (int) (llParams.height * 0.2);
        ll_bottom_button.setLayoutParams(llParams_bottom);

        ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
        ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

        messageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (getContext().getResources().getDimension(R.dimen.sp_15)));
        Logger.e("messageTv.size " + messageTv.getTextSize());
    }
}
