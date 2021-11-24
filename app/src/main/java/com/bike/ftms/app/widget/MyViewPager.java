package com.bike.ftms.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bike.ftms.app.utils.Logger;

public class MyViewPager extends ViewPager {
    int lastX = -1;
    int lastY = -1;
    public boolean intercepted;

    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Logger.d("==========================================");

        intercepted = false;
        if (this instanceof VerticalViewPager) {
            swapXY(ev); // return touch coordinates to original reference frame for any child views
        }
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int dealtX = 0;
        int dealtY = 0;
        Logger.d("ev.getAction() == " + ev.getAction());

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                dealtX += Math.abs(x - lastX);
                dealtY += Math.abs(y - lastY);
               *//* Timber.d(TAG + " - dealtX:=" + dealtX);
                Timber.d(TAG + " - dealtY:=" + dealtY);*//*
                lastX = x;
                lastY = y;
                // 拦截的判断
                Logger.d("dealtX >= dealtY == " + (dealtX >= dealtY));
                if (dealtX >= dealtY) {
                    intercepted =  false;
                    if (this instanceof HorizontalViewPager) {
                        intercepted = true;
                    }
                } else {
                    if (this instanceof VerticalViewPager) {
                        intercepted = true;
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        Logger.d("intercepted == " + intercepted);
        return intercepted;
    }*/
    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }
}
