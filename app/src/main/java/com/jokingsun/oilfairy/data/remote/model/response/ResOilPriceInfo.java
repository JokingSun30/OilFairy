package com.jokingsun.oilfairy.data.remote.model.response;

public class ResOilPriceInfo {

    private String currentPrice;
    private String nextWeekPrice;
    private String oilCommonCode;

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getNextWeekPrice() {
        return nextWeekPrice;
    }

    public void setNextWeekPrice(String nextWeekPrice) {
        this.nextWeekPrice = nextWeekPrice;
    }

    public String getOilCommonCode() {
        return oilCommonCode;
    }

    public void setOilCommonCode(String oilCommonCode) {
        this.oilCommonCode = oilCommonCode;
    }
}
