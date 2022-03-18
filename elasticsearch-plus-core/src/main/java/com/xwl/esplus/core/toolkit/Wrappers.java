package com.xwl.esplus.core.toolkit;

import com.xwl.esplus.core.wrapper.EsLambdaIndexWrapper;
import com.xwl.esplus.core.wrapper.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.EsLambdaUpdateWrapper;

/**
 * Wrapper 条件构造
 *
 * @author xwl
 * @since 2022/3/16 17:33
 */
public final class Wrappers {
    private Wrappers() {
    }

    /**
     * 获取 LambdaEsIndexWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaEsIndexWrapper
     */
    public static <T> EsLambdaIndexWrapper<T> lambdaIndex() {
        return new EsLambdaIndexWrapper<>();
    }

    /**
     * 获取 LambdaEsIndexWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsIndexWrapper
     */
    public static <T> EsLambdaIndexWrapper<T> lambdaIndex(T entity) {
        return new EsLambdaIndexWrapper<>(entity);
    }

    /**
     * 获取 LambdaEsQueryWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper
     */
    public static <T> EsLambdaQueryWrapper<T> lambdaQuery() {
        return new EsLambdaQueryWrapper<>();
    }

    /**
     * 获取LambdaEsQueryWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsQueryWrapper
     */
    public static <T> EsLambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new EsLambdaQueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaEsUpdateWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaEsUpdateWrapper
     */
    public static <T> EsLambdaUpdateWrapper<T> lambdaUpdate() {
        return new EsLambdaUpdateWrapper<>();
    }

    /**
     * 获取 LambdaEsUpdateWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsUpdateWrapper
     */
    public static <T> EsLambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return new EsLambdaUpdateWrapper<>(entity);
    }
}
