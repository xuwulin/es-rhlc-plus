package com.xwl.esplus.core.enums;

/**
 * 主键生成策略类型枚举
 *
 * @author xwl
 * @since 2022/3/11 18:58
 */
public enum EsKeyTypeEnum {
    /**
     * es自动生成
     */
    AUTO,
    /**
     * 无状态，该类型为未设置主键类型（注解里等于跟随全局，全局里约等于 INPUT）
     */
    NONE,
    /**
     * 32 位 UUID 字符串(please use `ASSIGN_UUID`)
     */
    UUID,
    /**
     * 用户自定义，由用户传入
     */
    CUSTOMIZE;
}
