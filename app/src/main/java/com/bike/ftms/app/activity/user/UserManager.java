package com.bike.ftms.app.activity.user;

import androidx.annotation.NonNull;

import com.bike.ftms.app.bean.user.LoginSuccessBean;

public class UserManager {
    private static UserManager instance;
    //    private int userId = -1;
    private LoginSuccessBean user;

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }
/*
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }*/

    public void signOut() {
        this.user = null;
    }

    public void setUser(@NonNull LoginSuccessBean bean) {
        this.user = bean;
    }

    public LoginSuccessBean getUser() {
        return user;
    }
}
