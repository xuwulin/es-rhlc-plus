package com.xwl.esplus.core.wrapper.query;

import com.xwl.esplus.core.common.DocumentFieldInfo;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * 查询相关
 *
 * @author xwl
 * @since 2022/3/14 19:01
 */
public interface Query<Children, T, R> extends Serializable {
    /**
     * 设置查询字段
     *
     * @param columns 查询列,支持多字段
     * @return 泛型
     */
    Children select(R... columns);

    /**
     * 查询字段
     *
     * @param predicate 预言
     * @return 泛型
     */
    Children select(Predicate<DocumentFieldInfo> predicate);

    /**
     * 过滤查询的字段信息(主键除外!)
     *
     * @param entityClass 实体类
     * @param predicate   预言
     * @return 泛型
     */
    Children select(Class<T> entityClass, Predicate<DocumentFieldInfo> predicate);

    /**
     * 设置不查询字段
     *
     * @param columns 不查询字段,支持多字段
     * @return 泛型
     */
    Children notSelect(R... columns);

    /**
     * 从第几条数据开始查询
     *
     * @param from 起始
     * @return 泛型
     */
    Children from(Integer from);

    /**
     * 总共查询多少条数据
     *
     * @param size 查询多少条
     * @return 泛型
     */
    Children size(Integer size);

    /**
     * 兼容MySQL语法 作用同size
     *
     * @param n  查询条数
     * @return 泛型
     */
    Children limit(Integer n);

    /**
     * 兼容MySQL语法 作用同from+size
     *
     * @param m  offset偏移量,从第几条开始取,作用同from
     * @param n  查询条数,作用同size
     * @return
     */
    Children limit(Integer m, Integer n);
}
