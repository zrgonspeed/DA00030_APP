package com.bike.ftms.app.common;

public class HttpParam {
    // String mailCodeUrl = "http://192.168.50.21:8080" + "/restapi/verify/email";

    // http://rowerdata-test.anplus-tech.com
    private final static String DOMAIN = "http://192.168.50.21:8080";

    // RESTful
    private final static String MAIL_CODE_REST = "/restapi/verify/email";
    private final static String USER_REGISTER_REST = "/restapi/users/register";
    private final static String USER_LOGIN_REST = "/restapi/users/login";

    // URL
    public static String MAIL_CODE_URL = DOMAIN + MAIL_CODE_REST;
    public static String USER_REGISTER_URL = DOMAIN + USER_REGISTER_REST;
    public static String USER_LOGIN_URL = DOMAIN + USER_LOGIN_REST;

    // 运动数据相关URL
    // 运动数据列表  ?offset=0&limit=10
    private final static String RUN_DATA_LIST_REST = "/restapi/workouts";
    public static String RUN_DATA_LIST_URL = DOMAIN + RUN_DATA_LIST_REST;
}
