package com.bike.ftms.app.bean;

public class RegisterMailBean {
    private String type;
    private String email;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "RegisterMailBean{" +
                "type='" + type + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
