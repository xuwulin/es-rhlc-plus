package com.xwl.esplus.core.constant;

/**
 * 聚合枚举
 *
 * @author xwl
 * @since 2022/3/16 11:06
 */
public enum EsAggregationTypeEnum {
    /**
     * 求均值
     */
    AVG,
    /**
     * 求最小值
     */
    MIN,
    /**
     * 求最大值
     */
    MAX,
    /**
     * 求和
     */
    SUM,
    /**
     * 按字段分组,相当于mysql group by
     */
    TERMS;
}
