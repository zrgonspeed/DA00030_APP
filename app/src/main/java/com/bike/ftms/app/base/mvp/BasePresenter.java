package com.bike.ftms.app.base.mvp;

import com.bike.ftms.app.utils.Logger;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class BasePresenter<T extends View> implements Presenter<T> {
    private final String TAG = this.getClass().getSimpleName();
    private T mMvpView;

    @Override
    public void attachView(T mvpView) {
        Logger.e(TAG, "attachView()");
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        Logger.e(TAG, "detachView()");
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public T getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(View) before" +
                    " requesting data to the Presenter");
        }
    }
}

