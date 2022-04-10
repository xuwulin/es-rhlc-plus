package com.xwl.esplus.core.enums;

/**
 * 字段策略枚举
 *
 * @author xwl
 * @since 2022/3/11 19:01
 */
public enum EsFieldStrategyEnum {
    /**
     * 忽略判断
     */
    IGNORED,
    /**
     * 非NULL判断
     */
    NOT_NULL,
    /**
     * 非空判断
     */
    NOT_EMPTY,
    /**
     * 默认的,一般只用于注解里
     */
    DEFAULT;
}
