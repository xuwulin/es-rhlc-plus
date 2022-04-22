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
    /**
     * globalConfig缓存
     */
    private static final Map<Class<?>, GlobalConfig> GLOBAL_CONFIG = new ConcurrentHashMap<>(1);

    /**
     * 从缓存中获取全局配置
     *
     * @return
     */
    public static GlobalConfig getGlobalConfig() {
        return GLOBAL_CONFIG.get(GlobalConfig.class);
    }

    /**
     * 将全局配置放入缓存中
     *
     * @param globalConfig 全局配置
     */
    public static void setGlobalConfig(GlobalConfig globalConfig) {
        GLOBAL_CONFIG.putIfAbsent(GlobalConfig.class, globalConfig);
    }
}
