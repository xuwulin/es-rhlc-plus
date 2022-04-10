package com.xwl.esplus.core.enums;

/**
 * @author xwl
 * @since 2022/3/11 17:58
 */
public enum EsFieldTypeEnum {
    /**
     * 数值
     */
    BYTE("byte"),
    SHORT("short"),
    INTEGER("integer"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),

    HALF_FLOAT("half_float"),
    SCALED_FLOAT("scaled_float"),
    /**
     * 布尔
     */
    BOOLEAN("boolean"),
    /**
     * 日期
     */
    DATE("date"),
    RANGE("range"),
    BINARY("binary"),
    /**
     * 字符串，不分词，精确值
     */
    KEYWORD("keyword"),
    /**
     * 字符串，可分词
     */
    TEXT("text"),
    /**
     * mix
     */
    ARRAY("array"),
    /**
     * 对象
     */
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
    /**
     * 搜索时作为自动补全使用
     * 参与查询补全的字段必须是completion
     * 字段的内容一般是用来补全的多个词条形成的数组
     */
    COMPLETION("completion"),
    TOKEN("token"),
    ATTACHMENT("attachment"),
    PERCOLATOR("percolator"),

    /**
     * 子对象
     */
    PROPERTIES("properties"),
    /**
     * 多（子）字段
     */
    FIELDS("fields");

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
