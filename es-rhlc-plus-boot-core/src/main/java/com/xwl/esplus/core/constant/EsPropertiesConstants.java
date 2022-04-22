package com.xwl.esplus.core.constant;

/**
 * 配置常量
 * @author xwl
 * @since 2022/3/14 10:09
 */
public class EsPropertiesConstants {
    /**
     * 框架banner是否展示
     */
    public static final String BANNER = "es-plus.global-config.document-config.banner";
    /**
     * 是否输出DSL日志（JSON格式）
     */
    public static final String ENABLE_DSL = "es-plus.global-config.enable-dsl";
    /**
     * es索引前缀
     */
    public static final String INDEX_PREFIX = "es-plus.global-config.document-config.index-prefix";
    /**
     * es id类型
     */
    public static final String ID_TYPE = "es-plus.global-config.document-config.id-type";
    /**
     * es字段策略
     */
    public static final String FIELD_STRATEGY = "es-plus.global-config.document-config.field-strategy";
    /**
     * es全局日期格式（日期字段统一格式）
     */
    public static final String DATA_FORMAT = "es-plus.global-config.document-config.data-format";
    /**
     * es字段下划线转驼峰
     */
    public static final String MAP_UNDERSCORE_TO_CAMEL_CASE = "es-plus.global-config.document-config.map-underscore-to-camel-case";
}
