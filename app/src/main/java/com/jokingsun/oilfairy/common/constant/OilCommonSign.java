package com.jokingsun.oilfairy.common.constant;

/**
 * 石油相關用詞與品牌
 */
public enum OilCommonSign {

    CPC_92("1","中油-92") ,CPC_95("2","中油-95"),
    CPC_98("3","中油-98") ,CPC_SUPER("4","中油-超柴"),
    FPG_92("5","台塑-92") ,FPG_95("6","台塑-95"),
    FPG_98("7","台塑-98") ,FPG_SUPER("8","台塑-超柴");

    private final String oilCommonCode;
    private final String oilCommonDes;

    OilCommonSign(String oilCommonCode , String oilCommonDes) {
        this.oilCommonCode = oilCommonCode;
        this.oilCommonDes = oilCommonDes;
    }

    public String getOilCommonCode() {
        return oilCommonCode;
    }

    public String getOilCommonDes() {
        return oilCommonDes;
    }
}
