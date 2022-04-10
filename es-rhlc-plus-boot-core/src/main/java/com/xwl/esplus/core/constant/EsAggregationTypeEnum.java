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
     * 同时求count、min、max、avg、sum
     */
    STATS,
    /**
     * 按字段分组，相当于mysql group by
     */
    TERMS,
    /**
     * 按照日期阶梯分组，例如一周为一组，或者一月为一组等
     */
    DATE_HISTOGRAM;
}
