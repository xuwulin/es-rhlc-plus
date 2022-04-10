package com.xwl.esplus.core.cache;

import com.xwl.esplus.core.config.GlobalConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置缓存
 *
 * @author xwl
 * @since 2022/3/11 20:32
 */
public class GlobalConfigCache {
    private static final Map<Class<?>, GlobalConfig> globalConfigMap = new ConcurrentHashMap<>(1);

    public static GlobalConfig getGlobalConfig() {
        return globalConfigMap.get(GlobalConfig.class);
    }

    public static void setGlobalConfig(GlobalConfig globalConfig) {
        globalConfigMap.putIfAbsent(GlobalConfig.class, globalConfig);
    }
}
