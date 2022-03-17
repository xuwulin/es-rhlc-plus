package com.xwl.esplus.core.param;

import com.xwl.esplus.core.constant.EsAggregationTypeEnum;

/**
 * 聚合参数
 * @author xwl
 * @since 2022/3/16 11:05
 */
public class EsAggregationParam {
    /**
     * 返回字段名称
     */
    private String name;
    /**
     * 聚合字段
     */
    private String field;
    /**
     * 聚合类型
     */
    private EsAggregationTypeEnum aggregationType;

    public EsAggregationParam() {
    }

    public EsAggregationParam(String name, String field, EsAggregationTypeEnum aggregationType) {
        this.name = name;
        this.field = field;
        this.aggregationType = aggregationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public EsAggregationTypeEnum getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(EsAggregationTypeEnum aggregationType) {
        this.aggregationType = aggregationType;
    }
}
