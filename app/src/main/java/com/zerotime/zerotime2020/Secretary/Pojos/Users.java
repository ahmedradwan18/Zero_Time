package com.zerotime.zerotime2020.Secretary.Pojos;

public class Users {
    private String userName;
    private String userPrimaryPhone;
private int random;
    public Users() {
    }

    public Users(String userName, String userPrimaryPhone, int random) {
        this.userName = userName;
        this.userPrimaryPhone = userPrimaryPhone;
        this.random = random;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPrimaryPhone() {
        return userPrimaryPhone;
    }

    public void setUserPrimaryPhone(String userPrimaryPhone) {
        this.userPrimaryPhone = userPrimaryPhone;
    }

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }
}
