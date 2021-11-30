package com.bike.ftms.app.bean.user;

/*
HTTP/1.1 200 OK
{
    "user_id": "10005",
    "username": "ZRGdddddd",
    "email": "631686921@qq.com",
    "firstname": "zhangddddddd",
    "lastname": "riguang",
    "gender": "Male",
    "birthday": "2021-11-29",
    "country": "Afghanistan",
    "token": "xxxxxxxxxxxxxxx"
}
 */
public class LoginSuccessBean {
    private String user_id;
    private String username;
    private String token;

    private String email;
    private String firstname;
    private String lastname;
    private String gender;
    private String birthday;
    private String country;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

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
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
