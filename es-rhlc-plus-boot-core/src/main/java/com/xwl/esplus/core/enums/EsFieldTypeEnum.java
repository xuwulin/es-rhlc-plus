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
    /**
     * 二进制
     */
    BINARY("binary"),

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
    DATE_NANOS("date_nanos"),
    RANGE("range"),

    /**
     * 字符串，不分词，精确值
     */
    KEYWORD("keyword"),
    /**
     * 字符串，可分词
     */
    TEXT("text"),
    /**
     * 数组类型
     */
    ARRAY("array"),
    /**
     * 对象
     */
    OBJECT("object"),
    /**
     * 嵌套类型
     */
    NESTED("nested"),
    /**
     * 地理坐标类型：用于经纬度坐标
     */
    GEO_POINT("geo_point"),
    /**
     * 地理形状类型：用于类似于多边形的复杂形状
     */
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
    /**
     * 用于统计做了标记的字段的index数目，该值会一直增加，不会因为过滤条件而减少。
     */
    TOKEN_COUNT("token_count "),
    /**
     * 附加类型
     */
    ATTACHMENT("attachment"),
    PERCOLATOR("percolator"),

    /**
     * 对象
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
