package com.bike.ftms.app.activity.user;

public class UserManager {
    private static UserManager instance;
    private int userId = -1;

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

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void signOut() {
        this.userId = -1;
    }
}
