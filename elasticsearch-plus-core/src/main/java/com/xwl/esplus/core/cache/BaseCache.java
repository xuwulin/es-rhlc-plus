package com.xwl.esplus.core.cache;

import com.xwl.esplus.core.mapper.EsBaseMapperImpl;
import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.toolkit.TypeUtils;
import org.elasticsearch.client.RestHighLevelClient;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基本缓存
 *
 * @author xwl
 * @since 2022/3/11 20:33
 */
public class BaseCache {
    /**
     * 用于存放BaseEsMapper的所有实例
     */
    private static final Map<Class<?>, EsBaseMapperImpl<?>> esBaseMapperInstanceMap = new ConcurrentHashMap<>();

    /**
     * 用于存放Es entity 中的字段的get/is方法
     */
    private static final Map<Class<?>, Map<String, Method>> esBaseEntityMethodMap = new ConcurrentHashMap<>();

    /**
     * 初始化缓存
     *
     * @param mapperInterface mapper接口
     * @param client          es客户端
     */
    public static void initCache(Class<?> mapperInterface, RestHighLevelClient client) {
        // 初始化baseEsMapper的所有实现类实例
        EsBaseMapperImpl esBaseMapper = new EsBaseMapperImpl();
        esBaseMapper.setRestHighLevelClient(client);
        Class<?> entityClass = TypeUtils.getInterfaceT(mapperInterface, 0);
        esBaseMapper.setEntityClass(entityClass);
        esBaseMapperInstanceMap.put(mapperInterface, esBaseMapper);

        // 初始化entity中所有字段(注解策略生效)
        Method[] entityMethods = entityClass.getMethods();
        Map<String, Method> invokeMethodsMap = new ConcurrentHashMap<>(entityMethods.length);
        Arrays.stream(entityMethods)
                .forEach(entityMethod -> {
                    String methodName = entityMethod.getName();
                    if (methodName.startsWith(EsConstants.GET_METHOD_PREFIX) || methodName.startsWith(EsConstants.IS_METHOD_PREFIX)
                            || methodName.startsWith(EsConstants.SET_METHOD_PREFIX)) {
                        invokeMethodsMap.put(FieldUtils.resolveFieldName(methodName), entityMethod);
                    }
                });
        esBaseEntityMethodMap.putIfAbsent(entityClass, invokeMethodsMap);
    }

    /**
     * 获取缓存中对应的BaseEsMapperImpl
     *
     * @param mapperInterface mapper接口
     * @return 实现类
     */
    public static EsBaseMapperImpl<?> getBaseEsMapperInstance(Class<?> mapperInterface) {
        return Optional.ofNullable(esBaseMapperInstanceMap.get(mapperInterface))
                .orElseThrow(() -> ExceptionUtils.epe("no such instance", mapperInterface));
    }

    /**
     * 获取缓存中对应的entity的所有字段(字段注解策略生效)
     *
     * @param entityClass 实体
     * @param methodName  方法名
     * @return 执行方法
     */
    public static Method getEsEntityInvokeMethod(Class<?> entityClass, String methodName) {
        return Optional.ofNullable(esBaseEntityMethodMap.get(entityClass))
                .map(b -> b.get(methodName))
                .orElseThrow(() -> ExceptionUtils.epe("no such method:", entityClass, methodName));
    }
}
