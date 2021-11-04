package com.bike.ftms.app.fragment;

import android.view.KeyEvent;

import com.bike.ftms.app.base.BaseFragment;

public abstract class WorkoutsFragment extends BaseFragment {
    public abstract boolean onKeyDown(int keyCode, KeyEvent event);
}
