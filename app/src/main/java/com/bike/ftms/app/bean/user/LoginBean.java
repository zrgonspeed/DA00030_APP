package com.bike.ftms.app.bean.user;

public class LoginBean {
    private String account;
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
