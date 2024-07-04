package com.xwl.esplus.core.toolkit;

import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;

/**
 * @Description: 防止和mp Wrappers重名
 * @Author: hl
 * @Date: 2022/7/25 14:54
 */
public final class EsWrappers {
    private EsWrappers() {
    }

    /**
     * 获取 EsLambdaIndexWrapper
     *
     * @param <T> 实体类泛型
     * @return EsLambdaIndexWrapper
     */
    public static <T> EsLambdaIndexWrapper<T> lambdaIndex() {
        return new EsLambdaIndexWrapper<>();
    }

    /**
     * 获取 EsLambdaIndexWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return EsLambdaIndexWrapper
     */
    public static <T> EsLambdaIndexWrapper<T> lambdaIndex(T entity) {
        return new EsLambdaIndexWrapper<>(entity);
    }

    /**
     * 获取 EsLambdaQueryWrapper
     *
     * @param <T> 实体类泛型
     * @return EsLambdaQueryWrapper
     */
    public static <T> EsLambdaQueryWrapper<T> lambdaQuery() {
        return new EsLambdaQueryWrapper<>();
    }

    /**
     * 获取 EsLambdaQueryWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return EsLambdaQueryWrapper
     */
    public static <T> EsLambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new EsLambdaQueryWrapper<>(entity);
    }

    /**
     * 获取 EsLambdaUpdateWrapper
     *
     * @param <T> 实体类泛型
     * @return EsLambdaUpdateWrapper
     */
    public static <T> EsLambdaUpdateWrapper<T> lambdaUpdate() {
        return new EsLambdaUpdateWrapper<>();
    }

    /**
     * 获取 EsLambdaUpdateWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return EsLambdaUpdateWrapper
     */
    public static <T> EsLambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return new EsLambdaUpdateWrapper<>(entity);
    }

}
