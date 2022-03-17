package com.xwl.esplus.core.enums;

/**
 * @author xwl
 * @since 2022/3/11 17:58
 */
public enum EsFieldTypeEnum {
    /**
     * core
     */
    BYTE("byte"),
    SHORT("short"),
    INTEGER("integer"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    HALF_FLOAT("half_float"),
    SCALED_FLOAT("scaled_float"),
    BOOLEAN("boolean"),
    DATE("date"),
    RANGE("range"),
    BINARY("binary"),
    KEYWORD("keyword"),
    TEXT("text"),
    /**
     * mix
     */
    ARRAY("array"),
    OBJECT("object"),
    NESTED("nested"),
    /**
     * geo
     */
    GEO_POINT("geo_point"),
    GEO_SHAPE("geo_shape"),
    /**
     * special
     */
    IP("ip"),
    COMPLETION("completion"),
    TOKEN("token"),
    ATTACHMENT("attachment"),
    PERCOLATOR("percolator");

    private String type;

    EsFieldTypeEnum() {
    }

    EsFieldTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
