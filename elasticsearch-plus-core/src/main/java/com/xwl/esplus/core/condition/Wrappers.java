package com.xwl.esplus.core.condition;

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
    public static <T> LambdaEsIndexWrapper<T> lambdaIndex() {
        return new LambdaEsIndexWrapper<>();
    }

    /**
     * 获取 LambdaEsIndexWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsIndexWrapper
     */
    public static <T> LambdaEsIndexWrapper<T> lambdaIndex(T entity) {
        return new LambdaEsIndexWrapper<>(entity);
    }

    /**
     * 获取 LambdaEsQueryWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper
     */
    public static <T> LambdaEsQueryWrapper<T> lambdaQuery() {
        return new LambdaEsQueryWrapper<>();
    }

    /**
     * 获取LambdaEsQueryWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsQueryWrapper
     */
    public static <T> LambdaEsQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaEsQueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaEsUpdateWrapper
     *
     * @param <T> 实体类泛型
     * @return LambdaEsUpdateWrapper
     */
    public static <T> LambdaEsUpdateWrapper<T> lambdaUpdate() {
        return new LambdaEsUpdateWrapper<>();
    }

    /**
     * 获取 LambdaEsUpdateWrapper
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaEsUpdateWrapper
     */
    public static <T> LambdaEsUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaEsUpdateWrapper<>(entity);
    }
}
