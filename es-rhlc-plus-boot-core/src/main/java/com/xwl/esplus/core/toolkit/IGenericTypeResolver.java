package com.xwl.esplus.core.toolkit;

/**
 * 泛型类助手（用于隔离Spring的代码）
 *
 * @author xwl
 * @since 2022/4/13 12:12
 */
public interface IGenericTypeResolver {
    /**
     * 解析类型
     *
     * @param clazz
     * @param genericIfc
     * @return
     */
    Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericIfc);
}
