package com.bike.ftms.app.ble.base;

import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/2
 */
public interface OnRunDataListener {
    void onRunData(RowerDataBean1 rowerDataBean1);

    void disConnect();

    void connected();
}
