package com.bike.ftms.app.activity.fragment.workout;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.bike.ftms.app.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class MyHeader extends ClassicsHeader {
    public MyHeader(Context context) {
        super(context);
        init();
    }

    public MyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mProgressView.setVisibility(GONE);
        mTextFinish = getResources().getString(R.string.start_scan);
        mTextRefreshing = getResources().getString(R.string.start_scan);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        final View progressView = mProgressView;
        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
//            if (((Animatable) drawable).isRunning()) {
//                ((Animatable) drawable).stop();
//            }
        } else {
            progressView.animate().rotation(0).setDuration(0);
        }
        progressView.setVisibility(VISIBLE);
        finish = true;
        moving = false;
        return 0;//延迟500毫秒之后再弹回
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight);
        moving = true;
        finish = false;
    }

    private boolean finish = false;
    private boolean moving = false;

    public boolean isFinish() {
        return finish;
    }

    public boolean isMoving() {
        return moving;
    }
}
