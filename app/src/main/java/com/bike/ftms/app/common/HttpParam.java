package com.bike.ftms.app.common;

public class HttpParam {
    // String mailCodeUrl = "http://192.168.50.21:8080" + "/restapi/verify/email";

    // http://rowerdata-test.anplus-tech.com
    private final static String DOMAIN = "http://rowerdata-test.anplus-tech.com"; // "http://192.168.50.21:8080";

    // RESTful
    private final static String MAIL_CODE_REST = "/restapi/verify/email";
    private final static String USER_REGISTER_REST = "/restapi/users/register";
    private final static String USER_LOGIN_REST = "/restapi/users/login";
    // 运动数据相关URL
    // 运动数据列表  ?offset=0&limit=10
    private final static String RUN_DATA_LIST_REST = "/restapi/workouts";
    private final static String RUN_DATA_UPLOAD = "/restapi/workouts";
    // workouts/1422
    private final static String RUN_DATA_DELETE = "/restapi/workouts";
    // workouts/1494/info
    private final static String RUN_DATA_GET_INFO = "/restapi/workouts";
    private final static String RUN_DATA_UPLOAD_REMARKS = "/restapi/workouts";

    // URL
    public static String MAIL_CODE_URL = DOMAIN + MAIL_CODE_REST;
    public static String USER_REGISTER_URL = DOMAIN + USER_REGISTER_REST;
    public static String USER_LOGIN_URL = DOMAIN + USER_LOGIN_REST;

    public static String RUN_DATA_LIST_URL = DOMAIN + RUN_DATA_LIST_REST;
    public static String RUN_DATA_UPLOAD_URL = DOMAIN + RUN_DATA_UPLOAD;
    public static String RUN_DATA_DELETE_URL = DOMAIN + RUN_DATA_DELETE;
    public static String RUN_DATA_GET_INFO_URL = DOMAIN + RUN_DATA_GET_INFO;
    public static String RUN_DATA_UPLOAD_REMARKS_URL = DOMAIN + RUN_DATA_UPLOAD_REMARKS;


}
