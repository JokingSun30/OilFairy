package com.jokingsun.oilfairy.data.remote.model.response;

public class ResOilDetailInfo {
    private String quality;
    private String cpcNowPrice;
    private String cpcNextPrice;
    private String fpgNowPrice;
    private String fpgNextPrice;
    private String costcoNowPrice;

    public ResOilDetailInfo(String quality) {
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getCpcNowPrice() {
        return cpcNowPrice;
    }

    public void setCpcNowPrice(String cpcNowPrice) {
        this.cpcNowPrice = cpcNowPrice;
    }

    public String getCpcNextPrice() {
        return cpcNextPrice;
    }

    public void setCpcNextPrice(String cpcNextPrice) {
        this.cpcNextPrice = cpcNextPrice;
    }

    public String getFpgNowPrice() {
        return fpgNowPrice;
    }

    public void setFpgNowPrice(String fpgNowPrice) {
        this.fpgNowPrice = fpgNowPrice;
    }

    public String getFpgNextPrice() {
        return fpgNextPrice;
    }

    public void setFpgNextPrice(String fpgNextPrice) {
        this.fpgNextPrice = fpgNextPrice;
    }

    public String getCostcoNowPrice() {
        return costcoNowPrice;
    }

    public void setCostcoNowPrice(String costcoNowPrice) {
        this.costcoNowPrice = costcoNowPrice;
    }
}
