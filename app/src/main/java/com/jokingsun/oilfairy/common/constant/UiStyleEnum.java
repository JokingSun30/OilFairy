package com.jokingsun.oilfairy.common.constant;

public enum UiStyleEnum {

    /**
     * 定義 App UI 不同風格
     */
    DEFAULT("1","預設風格") ,CHINESE_NEW_YEAR("2","春節風格");

    private final String uiStyleType;
    private final String uiStyleDes;

    UiStyleEnum(String uiStyleType , String uiStyleDes) {
        this.uiStyleType = uiStyleType;
        this.uiStyleDes = uiStyleDes;
    }

    public String getUiStyleType() {
        return uiStyleType;
    }
    public String getUiStyleDes() {
        return uiStyleDes;
    }
}
