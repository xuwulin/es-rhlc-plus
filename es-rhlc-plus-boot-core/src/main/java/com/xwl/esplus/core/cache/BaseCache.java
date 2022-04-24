package com.xwl.esplus.core.cache;

import com.xwl.esplus.core.mapper.EsBaseMapperImpl;
import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.toolkit.GenericTypeUtils;
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
     * 存放EsBaseMapper的所有实例
     * key: mapper接口
     * value: mapper接口对于得实例
     */
    private static final Map<Class<?>, EsBaseMapperImpl<?>> ES_BASE_MAPPER_INSTANCE = new ConcurrentHashMap<>();

    /**
     * 用于存放es对于实体中的字段的get/is方法
     * key: mapper接口
     * value: getter/setter方法map(key: 方法名，value: Method)
     */
    private static final Map<Class<?>, Map<String, Method>> ES_ENTITY_GETTER_AND_SETTER_METHOD = new ConcurrentHashMap<>();

    /**
     * 初始化缓存
     *
     * @param mapperInterface     mapper接口
     * @param restHighLevelClient es客户端
     */
    public static void initCache(Class<?> mapperInterface, RestHighLevelClient restHighLevelClient) {
        // 初始化baseEsMapper的所有实现类实例
        EsBaseMapperImpl esBaseMapper = new EsBaseMapperImpl();
        esBaseMapper.setRestHighLevelClient(restHighLevelClient);
        // 获取接口泛型的实际类型
        Class<?> entityClass = GenericTypeUtils.getInterfaceGeneric(mapperInterface, 0);
        esBaseMapper.setEntityClass(entityClass);
        esBaseMapper.setGlobalConfig(GlobalConfigCache.getGlobalConfig());
        ES_BASE_MAPPER_INSTANCE.put(mapperInterface, esBaseMapper);

        // 初始化entity中所有方法
        Method[] entityMethods = entityClass.getMethods();
        Map<String, Method> invokeMethodsMap = new ConcurrentHashMap<>(entityMethods.length);
        Arrays.stream(entityMethods).forEach(entityMethod -> {
            String methodName = entityMethod.getName();
            if (methodName.startsWith(EsConstants.GET_METHOD_PREFIX)
                    || methodName.startsWith(EsConstants.IS_METHOD_PREFIX)
                    || methodName.startsWith(EsConstants.SET_METHOD_PREFIX)) {
                invokeMethodsMap.put(methodName, entityMethod);
            }
        });
        // 注意：比常规的getter/setter方法多出一个getClass方法
        ES_ENTITY_GETTER_AND_SETTER_METHOD.putIfAbsent(entityClass, invokeMethodsMap);
    }

    /**
     * 从缓存中获取对应的EsBaseMapperImpl
     *
     * @param mapperInterface mapper接口
     * @return 实现类
     */
    public static EsBaseMapperImpl<?> getEsBaseMapperInstance(Class<?> mapperInterface) {
        return Optional.ofNullable(ES_BASE_MAPPER_INSTANCE.get(mapperInterface))
                .orElseThrow(() -> ExceptionUtils.epe("no such instance", mapperInterface));
    }

    /**
     * 从缓存中根据方法名获取entity对应的getter/setter方法
     *
     * @param entityClass 实体
     * @param methodName  方法名
     * @return 执行方法
     */
    public static Method getEsEntityMethod(Class<?> entityClass, String methodName) {
        return Optional.ofNullable(ES_ENTITY_GETTER_AND_SETTER_METHOD.get(entityClass))
                .map(b -> b.get(methodName))
                .orElseThrow(() -> ExceptionUtils.epe("entity %s no such method: %s", entityClass, methodName));
    }

    /**
     * 从缓存中根据方法名获取entity对于的getter方法
     *
     * @param entityClass es对应的实体类
     * @param methodName  方法名
     * @return 执行方法
     */
    public static Method getEsEntityGetterMethod(Class<?> entityClass, String methodName) {
        return Optional.ofNullable(ES_ENTITY_GETTER_AND_SETTER_METHOD.get(entityClass))
                .map(e -> {
                    Method method;
                    method = e.get(EsConstants.GET_METHOD_PREFIX + FieldUtils.firstToUpperCase(methodName));
                    if (method == null) {
                        method = e.get(EsConstants.IS_METHOD_PREFIX + FieldUtils.firstToUpperCase(methodName));
                    }
                    return method;
                })
                .orElseThrow(() -> ExceptionUtils.epe("entity %s no such method: %s", entityClass, methodName));
    }

    /**
     * 从缓存中根据方法名获取entity对于的setter方法
     *
     * @param entityClass 实体
     * @param methodName  方法名
     * @return 执行方法
     */
    public static Method getEsEntitySetterMethod(Class<?> entityClass, String methodName) {
        return Optional.ofNullable(ES_ENTITY_GETTER_AND_SETTER_METHOD.get(entityClass))
                .map(b -> b.get(EsConstants.SET_METHOD_PREFIX + FieldUtils.firstToUpperCase(methodName)))
                .orElseThrow(() -> ExceptionUtils.epe("no such method:", entityClass, methodName));
    }
}
