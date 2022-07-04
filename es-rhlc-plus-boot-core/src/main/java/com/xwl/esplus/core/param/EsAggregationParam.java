package com.xwl.esplus.core.param;

import com.xwl.esplus.core.constant.EsAggregationTypeEnum;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;

import java.time.ZoneId;
import java.util.List;

/**
 * 聚合参数
 *
 * @author xwl
 * @since 2022/3/16 11:05
 */
public class EsAggregationParam<T> {
    /**
     * 返回字段名称
     */
    private String name;
    /**
     * 聚合字段
     */
    private String field;
    /**
     * 高亮字段
     */
    private String highLight;
    /**
     * 查询字段
     */
    private String[] includes;
    /**
     * 便宜值 默认0
     */
    private Integer from;
    /**
     * 聚合返回条数，默认10
     */
    private Integer size;
    /**
     * 按什么时间段聚合：
     * year（1y）年
     * quarter（1q）季度
     * month（1M）月份
     * week（1w）星期
     * day（1d）天
     * hour（1h）小时
     * minute（1m）分钟
     * second（1s）秒
     */
    private DateHistogramInterval interval;
    /**
     * 日期格式
     */
    private String format;
    /**
     * 为空的话则填充值，默认0
     */
    private long minDocCount;
    /**
     * extended_bounds：强制返回的日期区间；如果不加这个就只返回有数据的区间
     */
    private ExtendedBounds extendedBounds;
    /**
     * 设置时区
     * "time_zone": "Asia/Shanghai"
     * "time_zone": "UTC"
     * "time_zone":"+08:00" 东八区
     */
    private ZoneId timeZone;
    /**
     * 聚合类型
     */
    private EsAggregationTypeEnum aggregationType;

    /**
     * 子聚合
     */
    private List<EsAggregationParam<T>> subAggregations;

    public EsAggregationParam() {
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public DateHistogramInterval getInterval() {
        return interval;
    }

    public void setInterval(DateHistogramInterval interval) {
        this.interval = interval;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getMinDocCount() {
        return minDocCount;
    }

    public void setMinDocCount(long minDocCount) {
        this.minDocCount = minDocCount;
    }

    public ExtendedBounds getExtendedBounds() {
        return extendedBounds;
    }

    public void setExtendedBounds(ExtendedBounds extendedBounds) {
        this.extendedBounds = extendedBounds;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    public EsAggregationTypeEnum getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(EsAggregationTypeEnum aggregationType) {
        this.aggregationType = aggregationType;
    }

    public List<EsAggregationParam<T>> getSubAggregations() {
        return subAggregations;
    }

    public void setSubAggregations(List<EsAggregationParam<T>> subAggregations) {
        this.subAggregations = subAggregations;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public String getHighLight() {
        return highLight;
    }

    public void setHighLight(String highLight) {
        this.highLight = highLight;
    }
}
