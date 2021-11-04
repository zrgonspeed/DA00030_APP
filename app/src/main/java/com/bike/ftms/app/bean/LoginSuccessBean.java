package com.bike.ftms.app.bean;

/*
HTTP/1.1 200 OK
{
  "user_id": "111",
  "username": "test",
  "token": "38e203a2****************"
}
 */
public class LoginSuccessBean {
    private String user_id;
    private String username;
    private String token;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginSuccessBean{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
