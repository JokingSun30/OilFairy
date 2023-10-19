package com.jokingsun.oilfairy.common.constant;

public enum FileSizeUnitEnum {
    /**
     * 定義檔案容量大小及公式
     */
    KB("KB", 1024), MB("MB", (long) Math.pow(1024, 2)),
    GB("GB", (long) Math.pow(1024, 3)), TB("TB", (long) Math.pow(1024, 4));

    private final String unit;
    private final long formula;

    FileSizeUnitEnum(String unit, long formula) {
        this.unit = unit;
        this.formula = formula;
    }

    public String getUnit() {
        return unit;
    }

    public long getFormula() {
        return formula;
    }
}
