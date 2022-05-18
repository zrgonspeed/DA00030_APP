package com.bike.ftms.app.view

import android.content.Context
import android.graphics.drawable.Animatable
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.bike.ftms.app.R
import com.scwang.smartrefresh.layout.api.RefreshLayout
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

class MyHeader : ClassicsHeader {
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mProgressView.visibility = GONE
        mTextFinish = resources.getString(R.string.start_scan)
        mTextRefreshing = resources.getString(R.string.start_scan)
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        val progressView: View = mProgressView
        val drawable = mProgressView.drawable
        if (drawable is Animatable) {
//            if (((Animatable) drawable).isRunning()) {
//                ((Animatable) drawable).stop();
//            }
        } else {
            progressView.animate().rotation(0f).duration = 0
        }
        progressView.visibility = VISIBLE
        isFinish = true
        isMoving = false
        return 0 //延迟500毫秒之后再弹回
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        isMoving = true
        isFinish = false
    }

    var isFinish = false
        private set
    var isMoving = false
        private set
}