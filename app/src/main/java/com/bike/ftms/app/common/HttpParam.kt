package com.bike.ftms.app.common

object HttpParam {
    // String mailCodeUrl = "http://192.168.50.21:8080" + "/restapi/verify/email";
    // http://rowerdata-test.anplus-tech.com
    private const val DOMAIN = "http://rowerdata-test.anplus-tech.com"

    // RESTful
    private const val MAIL_CODE_REST = "/restapi/verify/email"
    private const val USER_REGISTER_REST = "/restapi/users/register"
    private const val USER_LOGIN_REST = "/restapi/users/login"

    // 运动数据相关URL
    // 运动数据列表  ?offset=0&limit=10
    private const val RUN_DATA_LIST_REST = "/restapi/workouts"
    private const val RUN_DATA_UPLOAD = "/restapi/workouts"

    // workouts/1422
    private const val RUN_DATA_DELETE = "/restapi/workouts"

    // workouts/1494/info
    private const val RUN_DATA_GET_INFO = "/restapi/workouts"
    private const val RUN_DATA_UPLOAD_REMARKS = "/restapi/workouts"

    // URL
    @JvmField
    var MAIL_CODE_URL = DOMAIN + MAIL_CODE_REST

    @JvmField
    var USER_REGISTER_URL = DOMAIN + USER_REGISTER_REST

    @JvmField
    var USER_LOGIN_URL = DOMAIN + USER_LOGIN_REST

    @JvmField
    var RUN_DATA_LIST_URL = DOMAIN + RUN_DATA_LIST_REST

    @JvmField
    var RUN_DATA_UPLOAD_URL = DOMAIN + RUN_DATA_UPLOAD

    @JvmField
    var RUN_DATA_DELETE_URL = DOMAIN + RUN_DATA_DELETE

    @JvmField
    var RUN_DATA_GET_INFO_URL = DOMAIN + RUN_DATA_GET_INFO

    @JvmField
    var RUN_DATA_UPLOAD_REMARKS_URL = DOMAIN + RUN_DATA_UPLOAD_REMARKS
}