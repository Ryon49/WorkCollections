package com.wdong.model;

import com.wdong.model.wrapper.ResponseWrapper;

public class Response {
    private boolean success;

    private String errMsg;

    private ResponseWrapper data;

    public Response() { }

    private Response(boolean success, ResponseWrapper wrapper) {
        this.success = success;
        this.errMsg = "";
        this.data = wrapper;
    }

    private Response(boolean success, String errMsg) {
        this.success = success;
        this.errMsg = errMsg;
    }

    private Response(boolean success, String errMsg, ResponseWrapper wrapper) {
        this.success = success;
        this.errMsg = errMsg;
        this.data = wrapper;
    }

    public static Response ok() {
        return new Response(true, "Success");
    }

    public static Response ok(String msg, ResponseWrapper wrapper) {

        return new Response(true, wrapper);
    }
    public static Response ok(ResponseWrapper wrapper) {
        return new Response(true, wrapper);
    }

    public static Response error(String msg) {
        return new Response(false, msg);
    }

    public static Response error(ResponseWrapper wrapper) {
        return new Response(false, wrapper);
    }

    // region getter and setter

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public ResponseWrapper getData() {
        return data;
    }

    public void setData(ResponseWrapper data) {
        this.data = data;
    }

// endregion
}
