package com.xwl.esplus.core.wrapper.condition;

import com.xwl.esplus.core.constant.EsConstants;

import java.io.Serializable;

/**
 * 查询条件封装
 * 比较值
 *
 * @author xwl
 * @since 2022/3/14 19:55
 */
public interface Compare<Children, R> extends Serializable {
    default Children eq(R column, Object val) {
        return eq(true, column, val);
    }

    default Children eq(R column, Object val, Float boost) {
        return eq(true, column, val, boost);
    }

    default Children eq(boolean condition, R column, Object val) {
        return eq(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 等于，term
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children eq(boolean condition, R column, Object val, Float boost);

    default Children ne(R column, Object val) {
        return ne(true, column, val);
    }

    default Children ne(R column, Object val, Float boost) {
        return ne(true, column, val, boost);
    }

    default Children ne(boolean condition, R column, Object val) {
        return ne(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 不等于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重值
     * @return Children
     */
    Children ne(boolean condition, R column, Object val, Float boost);

    default Children reg(R column, Object val) {
        return reg(true, column, val);
    }

    default Children reg(R column, Object val, Float boost) {
        return reg(true, column, val, boost);
    }

    default Children reg(boolean condition, R column, Object val) {
        return reg(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 正则匹配
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重值
     * @return Children
     */
    Children reg(boolean condition, R column, Object val, Float boost);

    default Children match(R column, Object val) {
        return match(true, column, val);
    }

    default Children match(R column, Object val, Float boost) {
        return match(true, column, val, boost);
    }

    default Children match(boolean condition, R column, Object val) {
        return match(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * match 分词匹配
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重值
     * @return Children
     */
    Children match(boolean condition, R column, Object val, Float boost);

    default Children matchPhrase(R column, Object val) {
        return matchPhrase(true, column, val);
    }

    default Children matchPhrase(R column, Object val, Integer slop) {
        return matchPhrase(true, column, val, slop, EsConstants.DEFAULT_BOOST);
    }

    default Children matchPhrase(boolean condition, R column, Object val) {
        return matchPhrase(condition, column, val, EsConstants.ZERO, EsConstants.DEFAULT_BOOST);
    }

    /**
     * match 短语匹配
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param slop      位置距离
     * @param boost     权重值
     * @return Children
     */
    Children matchPhrase(boolean condition, R column, Object val, Integer slop, Float boost);

    default Children notMatch(R column, Object val) {
        return notMatch(true, column, val);
    }

    default Children notMatch(R column, Object val, Float boost) {
        return notMatch(true, column, val, boost);
    }

    default Children notMatch(boolean condition, R column, Object val) {
        return notMatch(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * NOT MATCH 分词不匹配
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children notMatch(boolean condition, R column, Object val, Float boost);

    default Children gt(R column, Object val) {
        return gt(true, column, val);
    }

    default Children gt(R column, Object val, Float boost) {
        return gt(true, column, val, boost);
    }

    default Children gt(boolean condition, R column, Object val) {
        return gt(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 大于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children gt(boolean condition, R column, Object val, Float boost);

    default Children ge(R column, Object val) {
        return ge(true, column, val);
    }

    default Children ge(R column, Object val, Float boost) {
        return ge(true, column, val, boost);
    }

    default Children ge(boolean condition, R column, Object val) {
        return ge(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 大于等于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children ge(boolean condition, R column, Object val, Float boost);

    default Children lt(R column, Object val) {
        return lt(true, column, val);
    }

    default Children lt(R column, Object val, Float boost) {
        return lt(true, column, val, boost);
    }

    default Children lt(boolean condition, R column, Object val) {
        return lt(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 小于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children lt(boolean condition, R column, Object val, Float boost);

    default Children le(R column, Object val) {
        return le(true, column, val);
    }

    default Children le(R column, Object val, Float boost) {
        return le(true, column, val, boost);
    }

    default Children le(boolean condition, R column, Object val) {
        return le(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * 小于等于
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children le(boolean condition, R column, Object val, Float boost);

    default Children between(R column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    default Children between(R column, Object val1, Object val2, Float boost) {
        return between(true, column, val1, val2, boost);
    }

    default Children between(boolean condition, R column, Object val1, Object val2) {
        return between(condition, column, val1, val2, EsConstants.DEFAULT_BOOST);
    }

    /**
     * BETWEEN 值1 AND 值2
     *
     * @param condition 条件
     * @param column    列
     * @param val1      左区间值
     * @param val2      右区间值
     * @param boost     权重
     * @return Children
     */
    Children between(boolean condition, R column, Object val1, Object val2, Float boost);

    default Children notBetween(R column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    default Children notBetween(R column, Object val1, Object val2, Float boost) {
        return notBetween(true, column, val1, val2, boost);
    }

    default Children notBetween(boolean condition, R column, Object val1, Object val2) {
        return notBetween(condition, column, val1, val2, EsConstants.DEFAULT_BOOST);
    }

    /**
     * NOT BETWEEN 值1 AND 值2
     *
     * @param condition 条件
     * @param column    列
     * @param val1      左区间值
     * @param val2      右区间值
     * @param boost     权重
     * @return Children
     */
    Children notBetween(boolean condition, R column, Object val1, Object val2, Float boost);

    default Children like(R column, Object val) {
        return like(true, column, val);
    }

    default Children like(R column, Object val, Float boost) {
        return like(true, column, val, boost);
    }

    default Children like(boolean condition, R column, Object val) {
        return like(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * like '*值*'
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children like(boolean condition, R column, Object val, Float boost);

    default Children notLike(R column, Object val) {
        return notLike(true, column, val);
    }

    default Children notLike(R column, Object val, Float boost) {
        return notLike(true, column, val, boost);
    }

    default Children notLike(boolean condition, R column, Object val) {
        return notLike(condition, column, val, EsConstants.DEFAULT_BOOST);
    }

    /**
     * NOT LIKE
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children notLike(boolean condition, R column, Object val, Float boost);

    default Children likeLeft(R column, Object val) {
        return likeLeft(true, column, val, EsConstants.DEFAULT_BOOST);
    }

    default Children likeLeft(R column, Object val, Float boost) {
        return likeLeft(true, column, val, boost);
    }

    /**
     * LIKE '*值'
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children likeLeft(boolean condition, R column, Object val, Float boost);

    default Children likeRight(R column, Object val) {
        return likeRight(true, column, val, EsConstants.DEFAULT_BOOST);
    }

    default Children likeRight(R column, Object val, Float boost) {
        return likeRight(true, column, val, boost);
    }

    /**
     * LIKE '值*'
     *
     * @param condition 条件
     * @param column    列
     * @param val       值
     * @param boost     权重
     * @return Children
     */
    Children likeRight(boolean condition, R column, Object val, Float boost);
}
