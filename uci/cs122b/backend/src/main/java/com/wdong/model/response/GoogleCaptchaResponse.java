package com.wdong.model.response;


public class GoogleCaptchaResponse {
    private String success;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public boolean success() {
        return this.success.equals("true");
    }

}
