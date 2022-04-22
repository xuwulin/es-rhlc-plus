package com.xwl.esplus.core.toolkit;

import org.springframework.core.GenericTypeResolver;

/**
 * Spring 反射辅助类
 *
 * @author xwl
 * @since 2022/4/13 12:13
 */
public class SpringReflectionHelper {
    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }
}
