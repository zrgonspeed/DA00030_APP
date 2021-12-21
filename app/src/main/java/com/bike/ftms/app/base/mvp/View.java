package com.bike.ftms.app.base.mvp;


import android.content.Context;

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
public interface View {
    Context getContext();
}
