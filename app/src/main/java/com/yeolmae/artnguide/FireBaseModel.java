package com.yeolmae.artnguide;

public class FireBaseModel {
    private String email;
    private String fcmToken;

    public FireBaseModel(String email, String fcmToken) {
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public String getEmail() {
        return email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "{" + "email:" + email +","+ "deviceToken:" + fcmToken + '}';
    }
}
