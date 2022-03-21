package com.bike.ftms.app.ble.base;

import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;

public abstract class CategoryType {
    public abstract void setRunData(byte[] data, RowerDataBean1 rowerDataBean1);
}
