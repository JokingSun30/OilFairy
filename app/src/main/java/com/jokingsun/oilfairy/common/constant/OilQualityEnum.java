package com.jokingsun.oilfairy.common.constant;

public enum OilQualityEnum {
    QUALITY_92("92", "92 無鉛汽油"), QUALITY_95("95", "95 無鉛汽油"),
    QUALITY_98("98", "98 無鉛汽油"), QUALITY_SUPER("99", "超級柴油");

    private final String oilQualitySign;
    private final String oilQualityDes;

    OilQualityEnum(String oilQualitySign, String oilQualityDes) {
        this.oilQualitySign = oilQualitySign;
        this.oilQualityDes = oilQualityDes;
    }

    public String getOilQualitySign() {
        return oilQualitySign;
    }

    public String getOilQualityDes() {
        return oilQualityDes;
    }
}
