package com.xwl.esplus.core.toolkit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型工具类
 *
 * @author xwl
 * @since 2022/3/11 20:33
 */
public class GenericTypeUtils {
    private static IGenericTypeResolver GENERIC_TYPE_RESOLVER;

    /**
     * 获取接口泛型的实际类型（返回此对象表示的类或接口，直接实现的接口的类型上的泛型类。）
     *
     * @param clazz 类
     * @param index 下标
     * @return 实现接口上的泛型类
     */
    public static Class<?> getInterfaceGeneric(Class clazz, int index) {
        // 返回此对象表示的类或接口，直接实现的接口的类型(com.xwl.esplus.core.mapper.EsBaseMapper<com.xwl.esplus.test.document.UserDocument>)
        Type[] types = clazz.getGenericInterfaces();
        // 因为type是顶级接口没有定义任何方法，所以需要强转为子接口ParameterizedType
        ParameterizedType parameterizedType = (ParameterizedType) types[index];
        // 通过子接口定义的getActualTypeArguments方法获取到实际参数类型
        // 返回参数为数组，因为Java中接口可以多实现
        Type type = parameterizedType.getActualTypeArguments()[index];
        // 返回class
        return checkType(type, index);
    }

    private static Class<?> checkType(Type type, int index) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type t = pt.getActualTypeArguments()[index];
            return checkType(t, index);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType"
                    + ", but <" + type + "> is of type " + className);
        }
    }

    /**
     * 获取泛型工具助手
     *
     * @param clazz
     * @param genericIfc
     * @return
     */
    public static Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericIfc) {
        if (null == GENERIC_TYPE_RESOLVER) {
            // 直接使用 spring 静态方法，减少对象创建
            return SpringReflectionHelper.resolveTypeArguments(clazz, genericIfc);
        }
        return GENERIC_TYPE_RESOLVER.resolveTypeArguments(clazz, genericIfc);
    }

    /**
     * 设置泛型工具助手。如果不想使用Spring封装，可以使用前替换掉
     */
    public static void setGenericTypeResolver(IGenericTypeResolver genericTypeResolver) {
        GENERIC_TYPE_RESOLVER = genericTypeResolver;
    }
}
