package com.xwl.esplus.core.wrapper.query;

import com.xwl.esplus.core.param.EsAggregationParam;
import com.xwl.esplus.core.wrapper.condition.SFunction;
import com.xwl.esplus.core.wrapper.processor.EsSubAggregationProcessor;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;

import java.io.Serializable;
import java.time.ZoneId;

/**
 * @author hl
 */
public interface SubAggregation extends Serializable {

    static <T> EsAggregationParam<T> termsAggregation(SFunction<T, ?> column, EsAggregationParam<T>... esAggregationParams) {
        return termsAggregation(true, 10, column, esAggregationParams);
    }

    static <T> EsAggregationParam<T> termsAggregation(Integer size, SFunction<T, ?> column, EsAggregationParam<T>... esAggregationParams) {
        return termsAggregation(true, size, column, esAggregationParams);
    }

    static <T> EsAggregationParam<T> termsAggregation(boolean condition, SFunction<T, ?> column, EsAggregationParam<T>... esAggregationParams) {
        return termsAggregation(condition, 10, column, esAggregationParams);
    }

    /**
     * terms子聚合
     *
     * @param condition
     * @param size
     * @param column
     * @param esAggregationParams
     * @return EsAggregationParam
     */
    static <T> EsAggregationParam<T> termsAggregation(boolean condition, Integer size, SFunction<T, ?> column, EsAggregationParam<T>... esAggregationParams) {
        return EsSubAggregationProcessor.termsAggregation(condition, size, column, esAggregationParams);
    }

    static <T> EsAggregationParam<T> min(SFunction<T, ?> column) {
        return EsSubAggregationProcessor.min(true, column);
    }

    /**
     * 最小值
     *
     * @param condition
     * @param column
     * @return
     */
    static <T> EsAggregationParam<T> min(boolean condition, SFunction<T, ?> column) {
        return EsSubAggregationProcessor.min(condition, column);
    }

    static <T> EsAggregationParam<T> max(SFunction<T, ?> column) {
        return EsSubAggregationProcessor.max(true, column);
    }

    /**
     * 最大值
     *
     * @param condition
     * @param column
     * @return
     */
    static <T> EsAggregationParam<T> max(boolean condition, SFunction<T, ?> column) {
        return EsSubAggregationProcessor.max(condition, column);
    }

    static <T> EsAggregationParam<T> avg(SFunction<T, ?> column) {
        return EsSubAggregationProcessor.avg(true, column);
    }

    /**
     * 平均值
     *
     * @param condition
     * @param column
     * @return
     */
    static <T> EsAggregationParam<T> avg(boolean condition, SFunction<T, ?> column) {
        return EsSubAggregationProcessor.avg(condition, column);
    }

    static <T> EsAggregationParam<T> sum(SFunction<T, ?> column) {
        return EsSubAggregationProcessor.sum(true, column);
    }

    /**
     * 合计
     *
     * @param condition
     * @param column
     * @return
     */
    static <T> EsAggregationParam<T> sum(boolean condition, SFunction<T, ?> column) {
        return EsSubAggregationProcessor.sum(condition, column);
    }

    static <T> EsAggregationParam<T> cardinality(SFunction<T, ?> column) {
        return EsSubAggregationProcessor.cardinality(true, column);
    }

    /**
     * 基数统计
     *
     * @param condition
     * @param column
     * @return
     */
    static <T> EsAggregationParam<T> cardinality(boolean condition, SFunction<T, ?> column) {
        return EsSubAggregationProcessor.cardinality(condition, column);
    }

    static <T> EsAggregationParam<T> dateHistogram(SFunction<T, ?> column, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, EsAggregationParam<T>... esAggregationParams) {
        return EsSubAggregationProcessor.dateHistogram(true, column, interval, format, minDocCount, extendedBounds, timeZone, esAggregationParams);
    }

    /**
     * 时间聚合
     *
     * @param condition           条件
     * @param interval            按什么时间段聚合
     * @param format              日期格式
     * @param minDocCount         为空的话则填充值
     * @param extendedBounds      强制返回的日期区间；如果不加这个就只返回有数据的区间
     * @param timeZone            设置时区
     * @param column              列
     * @param esAggregationParams 子聚合
     * @return
     */
    static <T> EsAggregationParam<T> dateHistogram(boolean condition, SFunction<T, ?> column, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, EsAggregationParam<T>... esAggregationParams) {
        return EsSubAggregationProcessor.dateHistogram(condition, column, interval, format, minDocCount, extendedBounds, timeZone, esAggregationParams);
    }

    static <T> EsAggregationParam<T> topHist(String returnName) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, 10, null, null);
    }

    static <T> EsAggregationParam<T> topHist(String returnName,Integer size) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, size, null, null);
    }

    static <T> EsAggregationParam<T> topHist(String returnName, SFunction<T, ?>... column) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, 10, null, column);
    }

    static <T> EsAggregationParam<T> topHist(String returnName, Integer size, SFunction<T, ?>... column) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, size, null, column);
    }

    static <T> EsAggregationParam<T> topHist(String returnName, Integer from, Integer size, SFunction<T, ?>... column) {
        return EsSubAggregationProcessor.topHist(true, returnName, from, size, null, column);
    }

    static <T> EsAggregationParam<T> topHist(String returnName, Integer from, Integer size, SFunction<T, ?> highLight, SFunction<T, ?>... column) {
        return EsSubAggregationProcessor.topHist(true, returnName, from, size, highLight, column);
    }

    /**
     * 热值匹配
     *
     * @param condition  条件
     * @param returnName 返回名
     * @param from       偏移值
     * @param size       条数
     * @param highLight  高亮字段
     * @param column     查询字段
     * @return
     */
    static <T> EsAggregationParam<T> topHist(boolean condition, String returnName, Integer from, Integer size, SFunction<T, ?> highLight, SFunction<T, ?>... column) {
        return EsSubAggregationProcessor.topHist(condition, returnName, from, size, highLight, column);
    }
}
