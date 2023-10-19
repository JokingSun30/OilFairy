package com.jokingsun.oilfairy.base;

public class BaseResponseModel {
    /**
     * status : true
     * message : 驗證碼已寄送！
     */

    private boolean status;
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
