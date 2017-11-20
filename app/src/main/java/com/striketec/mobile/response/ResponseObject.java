package com.striketec.mobile.response;

/**
 * Created by rakeshk2 on 4/25/2017.
 */

public class ResponseObject<T> {
    private String error;
    private String message;
    private T data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
