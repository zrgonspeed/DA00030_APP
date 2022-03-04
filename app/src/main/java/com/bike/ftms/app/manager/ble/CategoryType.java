package com.bike.ftms.app.manager.ble;

import com.bike.ftms.app.bean.rundata.RowerDataBean1;

public abstract class CategoryType {
    abstract void setRunData(byte[] data, RowerDataBean1 rowerDataBean1);
}
