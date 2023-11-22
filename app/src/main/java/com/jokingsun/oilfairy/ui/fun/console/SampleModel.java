package com.jokingsun.oilfairy.ui.fun.console;

import java.util.List;

public class SampleModel {

    /**
     * ErrorCode : 1
     * Des : 身分有誤
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String ErrorCode;
        private String Des;

        public String getErrorCode() {
            return ErrorCode;
        }

        public void setErrorCode(String ErrorCode) {
            this.ErrorCode = ErrorCode;
        }

        public String getDes() {
            return Des;
        }

        public void setDes(String Des) {
            this.Des = Des;
        }
    }
}
