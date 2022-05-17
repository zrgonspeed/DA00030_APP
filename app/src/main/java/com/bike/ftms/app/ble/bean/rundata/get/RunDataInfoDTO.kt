package com.bike.ftms.app.ble.bean.rundata.get

import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean2
import com.bike.ftms.app.utils.TimeStringUtil

class RunDataInfoDTO {
    var time: String? = null
    var meters: String? = null
    var cals: String? = null
    var efm // /500M
            : String? = null
    var sm: String? = null
    var calhr: String? = null
    var watts: String? = null

    constructor() {}
    constructor(bean2: RowerDataBean2) {
        toSelf(bean2)
    }

    override fun toString(): String {
        return "RunDataInfoDTO{" +
                "time='" + time + '\'' +
                ", meters='" + meters + '\'' +
                ", cals='" + cals + '\'' +
                ", efm='" + efm + '\'' +
                ", sm='" + sm + '\'' +
                ", calhr='" + calhr + '\'' +
                ", watts='" + watts + '\'' +
                '}'
    }

    private fun toSelf(bean2: RowerDataBean2) {
        time = TimeStringUtil.getSToHourMinSecValue(bean2.time.toFloat())
        meters = bean2.distance.toString() + "M"
        cals = bean2.calorie.toString() + ""
        efm = TimeStringUtil.getSToMinSecValue(bean2.ave_five_hundred.toFloat())
        sm = bean2.sm.toString() + ""
        calhr = bean2.calories_hr.toString() + ""
        watts = bean2.watts.toString() + ""
    }
}