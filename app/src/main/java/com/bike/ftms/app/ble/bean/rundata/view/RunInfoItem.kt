package com.bike.ftms.app.ble.bean.rundata.view

class RunInfoItem {
    var interval: String? = null
    var time: String? = null
    var meters: String? = null
    var cals: String? = null
    var cal_hr: String? = null
    var ave_watts: String? = null
    var ave_500: String? = null
    var sm: String? = null
    var ave_one_km: String? = null
    var level: String? = null
    override fun toString(): String {
        return "RunInfoItem{" +
                "interval=" + interval +
                ", time='" + time + '\'' +
                ", meters='" + meters + '\'' +
                ", cals='" + cals + '\'' +
                ", cal_hr='" + cal_hr + '\'' +
                ", ave_watts='" + ave_watts + '\'' +
                ", ave_500='" + ave_500 + '\'' +
                ", sm='" + sm + '\'' +
                ", ave_one_km='" + ave_one_km + '\'' +
                ", level='" + level + '\'' +
                '}'
    }
}