package com.bike.ftms.app.base.mvp;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the View type that wants to be attached with.
 */
public interface Presenter<V extends View> {

    void attachView(V mvpView);

    void detachView();
}
