package com.bike.ftms.app.manager.ble;

import com.bike.ftms.app.bean.RowerDataBean;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public interface OnRunDataListener {
    void onRunData(RowerDataBean rowerDataBean);

    void disConnect();

    void onExit();
}
