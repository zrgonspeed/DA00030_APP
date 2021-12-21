package com.bike.ftms.app.activity.fragment.workout;

import com.bike.ftms.app.base.mvp.View;

public interface WorkoutsLocalView extends View {
    void showUploading();

    void findRunDataFromLocalDBSuccess();
}
