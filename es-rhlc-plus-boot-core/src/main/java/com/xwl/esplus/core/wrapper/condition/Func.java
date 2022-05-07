package com.xwl.esplus.core.wrapper.condition;

import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.param.EsOrderByParam;
import com.xwl.esplus.core.toolkit.FieldUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * 查询条件封装
 * 高阶语法
 *
 * @author xwl
 * @since 2022/3/14 20:50
 */
public interface Func<Children, R> extends Serializable {
    default Children highLight(R column) {
        return highLight(true, EsConstants.HIGH_LIGHT_PRE_TAG, EsConstants.HIGH_LIGHT_POST_TAG, column);
    }

    default Children highLight(boolean condition, R column) {
        return highLight(condition, EsConstants.HIGH_LIGHT_PRE_TAG, EsConstants.HIGH_LIGHT_POST_TAG, column);
    }

    default Children highLight(String preTag, String postTag, R column) {
        return highLight(true, preTag, postTag, column);
    }

    /**
     * 高亮
     *
     * @param condition 是否执行条件
     * @param preTag    高亮的开始标签
     * @param postTag   高亮的结束标签
     * @param column    列
     * @return Children
     */
    Children highLight(boolean condition, String preTag, String postTag, R column);

    default Children highLight(R... columns) {
        return highLight(true, EsConstants.HIGH_LIGHT_PRE_TAG, EsConstants.HIGH_LIGHT_POST_TAG, columns);
    }

    default Children highLight(boolean condition, R... columns) {
        return highLight(condition, EsConstants.HIGH_LIGHT_PRE_TAG, EsConstants.HIGH_LIGHT_POST_TAG, columns);
    }

    /**
     * 高亮
     *
     * @param condition 是否执行条件
     * @param preTag    高亮的开始标签
     * @param postTag   高亮的结束标签
     * @param columns   列,支持多列
     * @return Children
     */
    Children highLight(boolean condition, String preTag, String postTag, R... columns);


    default Children orderByAsc(R column) {
        return orderByAsc(true, column);
    }

    default Children orderByAsc(R... columns) {
        return orderByAsc(true, columns);
    }

    default Children orderByAsc(boolean condition, R... columns) {
        return orderBy(condition, true, columns);
    }

    default Children orderByDesc(R column) {
        return orderByDesc(true, column);
    }

    default Children orderByDesc(R... columns) {
        return orderByDesc(true, columns);
    }

    default Children orderByDesc(boolean condition, R... columns) {
        return orderBy(condition, false, columns);
    }

    /**
     * 排序：ORDER BY 字段, ...
     *
     * @param condition 条件
     * @param isAsc     是否升序 是:按照升序排列,否:安卓降序排列
     * @param columns   列,支持多列
     * @return Children
     */
    Children orderBy(boolean condition, boolean isAsc, R... columns);

    /**
     * 排序 适用于排序字段和规则从前端通过字符串传入的场景
     *
     * @param condition     条件
     * @param orderByParams 排序字段及规则参数列表
     * @return Children
     */
    Children orderBy(boolean condition, List<EsOrderByParam> orderByParams);

    default Children orderBy(EsOrderByParam orderByParam) {
        return orderBy(true, orderByParam);
    }

    default Children orderBy(List<EsOrderByParam> orderByParams) {
        return orderBy(true, orderByParams);
    }

    default Children orderBy(boolean condition, EsOrderByParam orderByParam) {
        return orderBy(condition, Collections.singletonList(orderByParam));
    }

    default Children in(R column, Collection<?> coll) {
        return in(true, column, coll);
    }

    default Children in(boolean condition, R column, Collection<?> coll) {
        return in(condition, column, coll, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 字段 IN
     *
     * @param condition 条件
     * @param column    列
     * @param coll      集合
     * @param boost     权重
     * @return Children
     */
    Children in(boolean condition, R column, Collection<?> coll, Float boost);

    default Children in(R column, Object... values) {
        return in(true, column, values);
    }

    default Children in(boolean condition, R column, Object... values) {
        return in(condition, column, Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
                .collect(toList()));
    }

    default Children notIn(R column, Collection<?> coll) {
        return notIn(true, column, coll);
    }

    default Children notIn(boolean condition, R column, Collection<?> coll) {
        return notIn(condition, column, coll, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 字段 NOT IN
     *
     * @param condition 条件
     * @param column    列
     * @param coll      集合
     * @param boost     权重
     * @return Children
     */
    Children notIn(boolean condition, R column, Collection<?> coll, Float boost);

    default Children notIn(R column, Object... value) {
        return notIn(true, column, value);
    }

    default Children notIn(boolean condition, R column, Object... values) {
        return notIn(condition, column, Arrays.stream(Optional.ofNullable(values).orElseGet(() -> new Object[]{}))
                .collect(toList()));
    }

    default Children isNull(R column) {
        return isNull(true, column);
    }

    default Children isNull(boolean condition, R column) {
        return isNull(condition, column, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 字段 IS NULL
     *
     * @param condition 条件
     * @param column    列
     * @param boost     权重
     * @return Children
     */
    Children isNull(boolean condition, R column, Float boost);

    default Children isNotNull(R column) {
        return isNotNull(true, column);
    }

    default Children isNotNull(boolean condition, R column) {
        return isNotNull(condition, column, EsConstants.DEFAULT_BOOST);
    }

    /***
     * 字段 IS NOT NULL
     * @param condition 条件
     * @param column 列
     * @param boost 权重
     * @return Children
     */
    Children isNotNull(boolean condition, R column, Float boost);

    default Children groupBy(R... columns) {
        return groupBy(true, null, columns);
    }

    default Children groupBy(Integer size, R... columns) {
        return groupBy(true, size, columns);
    }

    default Children groupBy(boolean condition, R... columns) {
        return groupBy(condition, null, columns);
    }

    /**
     * 分组（Bucket聚合）：参加聚合的字段必须是keyword、日期、数值、布尔类型
     *
     * @param condition 条件
     * @param size      聚合返回的数量，默认10
     * @param columns   列,支持多列
     * @return
     */
    Children groupBy(boolean condition, Integer size, R... columns);

    default Children termsAggregation(R column) {
        return termsAggregation(true, FieldUtils.getFieldName(column), null, column);
    }

    default Children termsAggregation(String returnName, R column) {
        return termsAggregation(true, returnName, null, column);
    }

    /**
     * Bucket聚合：参加聚合的字段必须是keyword、日期、数值、布尔类型
     *
     * @param condition  条件
     * @param returnName 返回的聚合字段名称
     * @param size       聚合返回的数量，默认10
     * @param column     列
     * @return Children
     */
    Children termsAggregation(boolean condition, String returnName, Integer size, R column);

    default Children dateHistogram(DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, R column) {
        return dateHistogram(true, FieldUtils.getFieldName(column), interval, format, minDocCount, extendedBounds, timeZone, column);
    }

    default Children dateHistogram(String returnName, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, R column) {
        return dateHistogram(true, returnName, interval, format, minDocCount, extendedBounds, timeZone, column);
    }

    /**
     * dateHistogram聚合：按照日期阶梯分组，例如一周为一组，或者一月为一组
     *
     * @param condition      条件
     * @param returnName     返回的聚合字段名称
     * @param interval       按什么时间段聚合
     * @param format         日期格式
     * @param minDocCount    为空的话则填充值
     * @param extendedBounds 强制返回的日期区间；如果不加这个就只返回有数据的区间
     * @param timeZone       设置时区
     * @param column         列
     * @return
     */
    Children dateHistogram(boolean condition, String returnName, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, R column);

    default Children avg(R column) {
        return avg(true, FieldUtils.getFieldName(column), column);
    }

    default Children avg(String returnName, R column) {
        return avg(true, returnName, column);
    }

    /**
     * 求平均值
     *
     * @param condition  条件
     * @param returnName 返回的聚合字段名称
     * @param column     列
     * @return Children
     */
    Children avg(boolean condition, String returnName, R column);

    default Children min(R column) {
        return min(true, FieldUtils.getFieldName(column), column);
    }

    default Children min(String returnName, R column) {
        return min(true, returnName, column);
    }

    /**
     * 求最小值
     *
     * @param condition  条件
     * @param returnName 返回的聚合字段名称
     * @param column     列
     * @return Children
     */
    Children min(boolean condition, String returnName, R column);

    default Children max(R column) {
        return max(true, FieldUtils.getFieldName(column), column);
    }

    default Children max(String returnName, R column) {
        return max(true, returnName, column);
    }

    /**
     * 求最大值
     *
     * @param condition  条件
     * @param returnName 返回的聚合字段名称
     * @param column     列
     * @return Children
     */
    Children max(boolean condition, String returnName, R column);

    default Children sum(R column) {
        return sum(true, FieldUtils.getFieldName(column), column);
    }

    default Children sum(String returnName, R column) {
        return sum(true, returnName, column);
    }

    /**
     * 求和
     *
     * @param condition  条件
     * @param returnName 返回的聚合字段名称
     * @param column     列
     * @return Children
     */
    Children sum(boolean condition, String returnName, R column);

    default Children stats(R column) {
        return stats(true, FieldUtils.getFieldName(column), column);
    }

    default Children stats(String returnName, R column) {
        return stats(true, returnName, column);
    }

    /**
     * 同时求count、min、max、avg、sum
     *
     * @param condition  条件
     * @param returnName 返回的聚合字段名称
     * @param column     列
     * @return
     */
    Children stats(boolean condition, String returnName, R column);

    /**
     * 用户自定义排序
     *
     * @param condition    条件
     * @param sortBuilders 排序规则
     * @return Children
     */
    Children sort(boolean condition, List<SortBuilder<?>> sortBuilders);

    default Children sort(SortBuilder<?> sortBuilder) {
        return sort(true, Collections.singletonList(sortBuilder));
    }

    /**
     * 根据得分_score排序 默认为降序 得分高得在前
     *
     * @return Children
     */
    default Children sortByScore() {
        return sortByScore(true, SortOrder.DESC);
    }

    /**
     * 根据得分_score排序 默认为降序 得分高得在前
     *
     * @param condition 条件
     * @return Children
     */
    default Children sortByScore(boolean condition) {
        return sortByScore(condition, SortOrder.DESC);
    }

    default Children sortByScore(SortOrder sortOrder) {
        return sortByScore(true, sortOrder);
    }

    /**
     * 根据得分_score排序
     *
     * @param condition 条件
     * @param sortOrder 升序/降序
     * @return Children
     */
    Children sortByScore(boolean condition, SortOrder sortOrder);
}
