package com.bike.ftms.app.widget;

/**
 * @Description https://blog.csdn.net/divaid/article/details/81259510
 * @Author YeYueHong
 * @Date 2021/3/30
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class VerticalViewPager extends ViewPager {
    private final String TAG = "VerticalViewPager";
    int lastX = -1;
    int lastY = -1;

    private boolean isVertical = true;//是否可以纵向滑动

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new VerticalPageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_NEVER);

        requestDisallowInterceptTouchEvent(true);
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);

                //set Y position to swipe in from top
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = onInterceptTouchEvent2(ev);
//        Logger.d("result1 == " + result);
        return result;
    }

    public boolean onInterceptTouchEvent2(MotionEvent ev) {
        if (!isVertical) {
            return false;
        }
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // return touch coordinates to original reference frame for any child views
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
//                Logger.d("1 dealtX >= dealtY == " + (dealtX >= dealtY));
                if (dealtX >= dealtY) {
                    return false;
                } else {
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

}
