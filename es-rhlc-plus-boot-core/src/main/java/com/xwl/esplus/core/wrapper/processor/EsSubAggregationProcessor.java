package com.xwl.esplus.core.wrapper.processor;

import com.xwl.esplus.core.constant.EsAggregationTypeEnum;
import com.xwl.esplus.core.param.EsAggregationParam;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.wrapper.condition.SFunction;
import com.xwl.esplus.core.wrapper.query.SubAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;

import java.time.ZoneId;
import java.util.Arrays;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/7/1 11:21
 */
public class EsSubAggregationProcessor {


    public static <T, R> EsAggregationParam<T> termsAggregation(boolean condition, Integer size, SFunction<? super T, ? extends R> column, EsAggregationParam<T>[] aggregationParams) {
        return doIt(condition, EsAggregationTypeEnum.TERMS, FieldUtils.getFieldName(column), size, column, aggregationParams);
    }

    public static <T, R> EsAggregationParam<T> dateHistogram(boolean condition, SFunction<? super T, ? extends R> column, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, EsAggregationParam<T>[] aggregationParams) {
        return doIt(condition, FieldUtils.getFieldName(column), interval, format, minDocCount, extendedBounds, timeZone, column, aggregationParams);
    }

    public static <T, R> EsAggregationParam<T> topHist(boolean condition, String returnName, Integer from, Integer size, SFunction<? super T, ? extends R> highLight, String[] includes, String[] excludes) {
        return doIt(condition, returnName, from, size, FieldUtils.getFieldName(highLight), includes, excludes);
    }

    public static <T, R> EsAggregationParam<T> min(boolean condition, SFunction<? super T, ? extends R> column) {
        return doIt(condition, EsAggregationTypeEnum.MIN, "min", null, column, null);
    }

    public static <T, R> EsAggregationParam<T> max(boolean condition, SFunction<? super T, ? extends R> column) {
        return doIt(condition, EsAggregationTypeEnum.MAX, "max", null, column, null);

    }

    public static <T, R> EsAggregationParam<T> avg(boolean condition, SFunction<? super T, ? extends R> column) {
        return doIt(condition, EsAggregationTypeEnum.AVG, "avg", null, column, null);

    }

    public static <T, R> EsAggregationParam<T> sum(boolean condition, SFunction<? super T, ? extends R> column) {
        return doIt(condition, EsAggregationTypeEnum.SUM, "sum", null, column, null);
    }

    public static <T, R> EsAggregationParam<T> cardinality(boolean condition, SFunction<? super T, ? extends R> column) {
        return doIt(condition, EsAggregationTypeEnum.CARDINALITY, "cardinality", null, column, null);
    }

    /**
     * 封装查询参数 聚合类
     *
     * @param condition           条件
     * @param aggregationTypeEnum 聚合类型
     * @param returnName          返回的聚合字段名称
     * @param size                返回的聚合字段大小
     * @param column              列
     * @param aggregationParams   子聚合
     * @return 泛型
     */
    private static <T, R> EsAggregationParam<T> doIt(boolean condition, EsAggregationTypeEnum aggregationTypeEnum, String returnName, Integer size, SFunction<? super T, ? extends R> column, EsAggregationParam<T>[] aggregationParams) {
        if (condition) {
            EsAggregationParam<T> aggregationParam = new EsAggregationParam<T>();
            aggregationParam.setName(returnName);
            aggregationParam.setField(FieldUtils.getFieldName(column));
            aggregationParam.setSize(size);
            aggregationParam.setAggregationType(aggregationTypeEnum);
            aggregationParam.setSubAggregations(Arrays.asList(aggregationParams));
            return aggregationParam;
        }
        return null;
    }

    /**
     * 封装查询参数 聚合类
     *
     * @param condition         条件
     * @param returnName        返回名
     * @param condition         条件
     * @param returnName        返回的聚合字段名称
     * @param interval          按什么时间段聚合
     * @param format            日期格式
     * @param minDocCount       为空的话则填充值
     * @param extendedBounds    强制返回的日期区间；如果不加这个就只返回有数据的区间
     * @param timeZone          设置时区
     * @param column            列
     * @param aggregationParams 子聚合
     * @return
     */
    private static <T, R> EsAggregationParam<T> doIt(boolean condition, String returnName, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, R column, EsAggregationParam<T>... aggregationParams) {
        if (condition) {
            EsAggregationParam aggregationParam = new EsAggregationParam();
            aggregationParam.setName(returnName);
            aggregationParam.setField(FieldUtils.getFieldName(column));
            aggregationParam.setInterval(interval);
            aggregationParam.setFormat(format);
            aggregationParam.setMinDocCount(minDocCount);
            aggregationParam.setExtendedBounds(extendedBounds);
            aggregationParam.setTimeZone(timeZone);
            aggregationParam.setAggregationType(EsAggregationTypeEnum.DATE_HISTOGRAM);
            aggregationParam.setSubAggregations(Arrays.asList(aggregationParams));
            return aggregationParam;
        }
        return null;
    }

    /**
     * @param condition  条件
     * @param returnName 返回名
     * @param from       偏移值
     * @param size       条数
     * @param highLight  高亮字段
     * @param includes   查询字段
     * @param excludes   排除字段
     * @return
     */
    private static <T> EsAggregationParam<T> doIt(boolean condition, String returnName, Integer from, Integer size, String highLight, String[] includes, String[] excludes) {
        if (condition) {
            EsAggregationParam aggregationParam = new EsAggregationParam();
            aggregationParam.setName(returnName);
            aggregationParam.setIncludes(includes);
            aggregationParam.setExcludes(excludes);
            aggregationParam.setSize(size);
            aggregationParam.setFrom(from);
            aggregationParam.setHighLight(highLight);
            aggregationParam.setAggregationType(EsAggregationTypeEnum.TOP_HITS);
            return aggregationParam;
        }
        return null;
    }


}
