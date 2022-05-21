package com.bike.ftms.app.view.dialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.OnOrientationChanged;
import com.bike.ftms.app.activity.bluetooth.BluetoothActivity;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.UIUtils;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/26
 */
public class ConnectHintDialog extends Dialog implements OnOrientationChanged {
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

    private final Activity context;

    public ConnectHintDialog(Activity context) { //R.style.MyDialog
        this(context, MATCH_PARENT, MATCH_PARENT);
    }

    public ConnectHintDialog(Activity mainActivity, int w, int h) {
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
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(v -> {
            if (yesOnclickListener != null) {
                yesOnclickListener.onYesClick();
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(v -> {
            if (noOnclickListener != null) {
                noOnclickListener.onNoClick();
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


    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        void onYesClick();
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }

    public synchronized static ConnectHintDialog showConnectHintDialog(Activity activity, ConnectHintDialog connectHintDialog, int ori) {
        Logger.i("showConnectHintDialog()-----------------------------connectHintDialog== " + connectHintDialog);
        if (connectHintDialog != null) {
            Logger.i("connectHintDialog.isShowing() == " + connectHintDialog.isShowing());
        }

        if (connectHintDialog != null && connectHintDialog.isShowing()) {
            return connectHintDialog;
        }

        if (connectHintDialog == null) {
            connectHintDialog = new ConnectHintDialog(activity);
            connectHintDialog.setTitle(activity.getString(R.string.warm_tip));
            connectHintDialog.setMessage(activity.getString(R.string.connect_now));
            ConnectHintDialog finalConnectHintDialog = connectHintDialog;
            connectHintDialog.setYesOnclickListener(activity.getString(R.string.ok), () -> {
                activity.startActivity(new Intent(activity, BluetoothActivity.class));
                finalConnectHintDialog.dismiss();
            });
            connectHintDialog.setNoOnclickListener(activity.getString(R.string.cancel), () -> finalConnectHintDialog.dismiss());
        }

        Logger.i("connectHintDialog == " + connectHintDialog);
        if (!activity.isFinishing() && !connectHintDialog.isShowing()) {
            connectHintDialog.show();
        }

        connectHintDialog.setOri(ori);
        return connectHintDialog;
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
        setContentWidthHeight((int) (rootWidth), (int) (rootHeight * 0.3));

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = (int) (rootWidth * 0.8);
        getWindow().setAttributes(attributes);

        ViewGroup.LayoutParams llParams = ll_content.getLayoutParams();
        ViewGroup.LayoutParams llParams_bottom = ll_bottom_button.getLayoutParams();
        ViewGroup.LayoutParams ll_sv_tv_Params = ll_sv_tv.getLayoutParams();

        titleTv.setMinHeight((int) (llParams.height * 0.28));
        titleTv.setMaxHeight((int) (llParams.height * 0.28));

        llParams_bottom.height = (int) (llParams.height * 0.25);
        ll_bottom_button.setLayoutParams(llParams_bottom);

        ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
        ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

        // 设置文字居中
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        messageTv.setLayoutParams(lp);
        // 设置文字大小
        messageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((float) (getContext().getResources().getDimension(R.dimen.sp_18))));
        Logger.d("fontSize == " + messageTv.getTextSize());
    }

    @Override
    public void setLandLayout() {
        int rootHeight = UIUtils.getHeight(context);
        int rootWidth = UIUtils.getWidth(context);
        setContentWidthHeight((int) (rootWidth * 0.4), (int) (rootHeight * 0.5));

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = (int) (rootWidth * 0.4);
        getWindow().setAttributes(attributes);

        ViewGroup.LayoutParams llParams = ll_content.getLayoutParams();
        ViewGroup.LayoutParams llParams_bottom = ll_bottom_button.getLayoutParams();
        ViewGroup.LayoutParams ll_sv_tv_Params = ll_sv_tv.getLayoutParams();

        titleTv.setMinHeight((int) (llParams.height * 0.30));
        titleTv.setMaxHeight((int) (llParams.height * 0.30));

        llParams_bottom.height = (int) (llParams.height * 0.3);
        ll_bottom_button.setLayoutParams(llParams_bottom);

        ll_sv_tv_Params.height = (int) (llParams.height - titleTv.getMinHeight() - llParams_bottom.height);
        ll_sv_tv.setLayoutParams(ll_sv_tv_Params);

        // 设置文字居中
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        messageTv.setLayoutParams(lp);
        // 设置文字大小
        messageTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((float) (getContext().getResources().getDimension(R.dimen.sp_18))));
        Logger.d("fontSize == " + messageTv.getTextSize());
    }
}
