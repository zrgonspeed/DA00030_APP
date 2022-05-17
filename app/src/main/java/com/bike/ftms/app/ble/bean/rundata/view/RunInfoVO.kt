package com.bike.ftms.app.ble.bean.rundata.view

class RunInfoVO {
    var local_id = -1
    var server_id = -1
    var date: String? = null
    var categoryType = 0
    var deviceType = 0
    var runMode: String? = null
    var runModeNum = 0
    var note: String? = null
    var totalItem: RunInfoItem? = null
    var items: List<RunInfoItem>? = null
    override fun toString(): String {
        val s1 = "RunInfoVO{" +
                "local_id=" + local_id +
                ", server_id=" + server_id +
                ", categoryType=" + categoryType +
                ", deviceType=" + deviceType +
                ", runMode='" + runMode + '\'' +
                ", runModeNum=" + runModeNum +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                '}'
        val title = "-------------" + "\n" + "\t" + "time" + "\t\t" + "meters" + "\t" + "cals" + "\t" + "ave_500" + "\t\t" + "sm" + "\t\t" + "ave_one_km" + "\t\t" + "level" + "\t\t" + "cal_hr" + "\t\t" +
                "ave_watts" + "\n";
        var row = ""
        for (i in items!!.indices) {
            val item = items!![i]
            row = """$row${item.interval}	${item.time}	${item.meters} 	${item.cals}		${item.ave_500}			${item.sm}			${item.ave_one_km}			${item.level}			${item.cal_hr}		${item.ave_watts}
"""
        }
        return title + row + s1
        // return PrintUtils.stringToJSON(GsonUtil.GsonString(this));

        // return printJson("tag",GsonUtil.GsonString(this),"head");
        // return "RunInfoVO{" +
        //         "local_id=" + local_id +
        //         ", server_id=" + server_id +
        //         ", totalItem=" + totalItem +
        //         ", items=" + items +
        //         ", categoryType=" + categoryType +
        //         ", deviceType=" + deviceType +
        //         ", runMode='" + runMode + '\'' +
        //         ", runModeNum=" + runModeNum +
        //         ", date='" + date + '\'' +
        //         ", note='" + note + '\'' +
        //         '}';
    }
}