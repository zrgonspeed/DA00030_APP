package com.bike.ftms.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class HorizontalViewPager extends ViewPager {
    private final String TAG = "HorizontalViewPager";
    int lastX = -1;
    int lastY = -1;
    private boolean isSetIntercept = true;//是否设置拦截

    public HorizontalViewPager(@NonNull Context context) {
        super(context);
    }

    public HorizontalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result2 = onInterceptTouchEvent2(ev);
//        Logger.d("result2 == " + result2);
        return result2;
    }

    public boolean onInterceptTouchEvent2(MotionEvent ev) {
        if (!isSetIntercept) {
            return super.onInterceptTouchEvent(ev);
        }
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int dealtX = 0;
        int dealtY = 0;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                dealtX += Math.abs(x - lastX);
                dealtY += Math.abs(y - lastY);
                lastX = x;
                lastY = y;
                // 拦截的判断
//                Logger.d("2 dealtX > dealtY == " + (dealtX - 10 > dealtY));
                if (dealtX - 10 > dealtY) {
                    return true;
                } else if (dealtY - 30 > dealtX) {
                    return false;
                }
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setSetIntercept(boolean setIntercept) {
        isSetIntercept = setIntercept;
    }
}
