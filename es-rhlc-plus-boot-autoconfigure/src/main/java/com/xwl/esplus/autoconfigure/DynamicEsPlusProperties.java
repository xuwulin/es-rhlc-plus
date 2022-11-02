package com.xwl.esplus.autoconfigure;

import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.constant.EsGlobalConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 多数据源配置
 * @Author: hl
 * @Date: 2022/10/27 10:07
 */
@ConfigurationProperties(prefix = EsGlobalConstants.PROPERTIES_DYNAMIC_PREFIX)
public class DynamicEsPlusProperties {

    /**
     * 必须设置默认的库,默认master
     */
    private String primary = "master";
    /**
     * 是否启用严格模式,默认不启动. 严格模式下未匹配到数据源直接报错, 非严格模式下则使用默认数据源primary所设置的数据源
     */
    private Boolean strict = false;
    /**
     * 每一个数据源
     */
    private Map<String, EsPlusProperties> client = new LinkedHashMap<>();

    /**
     * 全局配置（嵌套配置）
     */
    @NestedConfigurationProperty
    private GlobalConfig globalConfig = GlobalConfigCache.defaults();

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public Map<String, EsPlusProperties> getClient() {
        return client;
    }

    public void setClient(Map<String, EsPlusProperties> client) {
        this.client = client;
    }
}
