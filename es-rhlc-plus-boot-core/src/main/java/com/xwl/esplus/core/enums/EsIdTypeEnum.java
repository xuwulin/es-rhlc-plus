package com.xwl.esplus.core.enums;

/**
 * 主键类型枚举
 *
 * @author xwl
 * @since 2022/3/11 18:58
 */
public enum EsIdTypeEnum {
    /**
     * es自动生成
     */
    AUTO,
    /**
     * 该类型为未设置主键类型
     */
    NONE,
    /**
     * 全局唯一ID (UUID)
     */
    UUID,
    /**
     * 用户自定义,由用户传入
     */
    CUSTOMIZE;
}
