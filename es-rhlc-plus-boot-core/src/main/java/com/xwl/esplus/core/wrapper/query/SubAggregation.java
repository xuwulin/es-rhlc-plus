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

    static <T, R> EsAggregationParam<T> termsAggregation(SFunction<? super T, ? extends R> column, EsAggregationParam<T>... esAggregationParams) {
        return termsAggregation(true, 10, column, esAggregationParams);
    }

    static <T, R> EsAggregationParam<T> termsAggregation(Integer size, SFunction<? super T, ? extends R> column, EsAggregationParam<T>... esAggregationParams) {
        return termsAggregation(true, size, column, esAggregationParams);
    }

    static <T, R> EsAggregationParam<T> termsAggregation(boolean condition, SFunction<? super T, ? extends R> column, EsAggregationParam<T>... esAggregationParams) {
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
    static <T, R> EsAggregationParam<T> termsAggregation(boolean condition, Integer size, SFunction<? super T, ? extends R> column, EsAggregationParam<T>... esAggregationParams) {
        return EsSubAggregationProcessor.termsAggregation(condition, size, column, esAggregationParams);
    }

    static <T, R> EsAggregationParam<T> min(SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.min(true, column);
    }

    /**
     * 最小值
     *
     * @param condition
     * @param column
     * @return
     */
    static <T, R> EsAggregationParam<T> min(boolean condition, SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.min(condition, column);
    }

    static <T, R> EsAggregationParam<T> max(SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.max(true, column);
    }

    /**
     * 最大值
     *
     * @param condition
     * @param column
     * @return
     */
    static <T, R> EsAggregationParam<T> max(boolean condition, SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.max(condition, column);
    }

    static <T, R> EsAggregationParam<T> avg(SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.avg(true, column);
    }

    /**
     * 平均值
     *
     * @param condition
     * @param column
     * @return
     */
    static <T, R> EsAggregationParam<T> avg(boolean condition, SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.avg(condition, column);
    }

    static <T, R> EsAggregationParam<T> sum(SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.sum(true, column);
    }

    /**
     * 合计
     *
     * @param condition
     * @param column
     * @return
     */
    static <T, R> EsAggregationParam<T> sum(boolean condition, SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.sum(condition, column);
    }

    static <T, R> EsAggregationParam<T> cardinality(SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.cardinality(true, column);
    }

    /**
     * 基数统计
     *
     * @param condition
     * @param column
     * @return
     */
    static <T, R> EsAggregationParam<T> cardinality(boolean condition, SFunction<? super T, ? extends R> column) {
        return EsSubAggregationProcessor.cardinality(condition, column);
    }

    static <T, R> EsAggregationParam<T> dateHistogram(SFunction<? super T, ? extends R> column, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, EsAggregationParam<T>... esAggregationParams) {
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
    static <T, R> EsAggregationParam<T> dateHistogram(boolean condition, SFunction<? super T, ? extends R> column, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, EsAggregationParam<T>... esAggregationParams) {
        return EsSubAggregationProcessor.dateHistogram(condition, column, interval, format, minDocCount, extendedBounds, timeZone, esAggregationParams);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, 10, null, null, null);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName, String[] includes, String[] excludes) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, 10, null, includes, excludes);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName, Integer size, String[] includes, String[] excludes) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, size, null, null, null);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName, SFunction<? super T, ? extends R> highLight) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, 10, highLight, null, null);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName, Integer size, SFunction<? super T, ? extends R> highLight) {
        return EsSubAggregationProcessor.topHist(true, returnName, 0, size, highLight, null, null);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName, Integer from, Integer size, SFunction<? super T, ? extends R> highLight) {
        return EsSubAggregationProcessor.topHist(true, returnName, from, size, highLight, null, null);
    }

    static <T, R> EsAggregationParam<T> topHist(String returnName, Integer from, Integer size, SFunction<? super T, ? extends R> highLight, String[] includes, String[] excludes) {
        return EsSubAggregationProcessor.topHist(true, returnName, from, size, highLight, includes, excludes);
    }

    /**
     * 热值匹配
     *
     * @param condition  条件
     * @param returnName 返回名
     * @param from       偏移值
     * @param size       条数
     * @param highLight  高亮字段
     * @param includes   查询字段
     * @param excludes   排除字段
     * @return
     */
    static <T, R> EsAggregationParam<T> topHist(boolean condition, String returnName, Integer from, Integer size, SFunction<? super T, ? extends R> highLight, String[] includes, String[] excludes) {
        return EsSubAggregationProcessor.topHist(condition, returnName, from, size, highLight, includes, excludes);
    }
}
