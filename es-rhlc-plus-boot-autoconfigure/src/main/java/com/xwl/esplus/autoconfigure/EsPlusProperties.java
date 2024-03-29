package com.xwl.esplus.autoconfigure;

import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.constant.EsGlobalConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * elasticsearch连接配置
 *
 * @author xwl
 * @since 2022/3/11 14:43
 */
@ConfigurationProperties(prefix = EsGlobalConstants.PROPERTIES_PREFIX)
public class EsPlusProperties {
    /**
     * 连接模式：默认http
     */
    private String schema = "http";

    /**
     * es主机ip:端口，如果为集群使用英文逗号（,）隔开
     */
    private String address;

    /**
     * 用户名：默认elastic
     */
    private String username = "elastic";

    /**
     * 密码：可缺省
     */
    private String password;

    /**
     * 客户端和服务器建立连接的超时时间（单位：ms），默认1000
     */
    private int connectTimeout = 1000;

    /**
     * 从连接池获取连接的超时时间（单位：ms），默认500
     */
    private int connectionRequestTimeout = 500;

    /**
     * 客户端从服务器读取数据（通讯）的超时时间（单位：ms），默认30000
     */
    private int socketTimeout = 30000;

    /**
     * 连接池中最大连接数（单位：个），默认100
     */
    private int maxConnTotal = 100;

    /**
     * 最大路由连接数（单位：个），默认100
     * 某一个服务每次能并行接收的请求数量
     */
    private int maxConnPerRoute = 100;

    /**
     * 全局配置（嵌套配置）
     */
    @NestedConfigurationProperty
    private GlobalConfig globalConfig = GlobalConfigCache.defaults();

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public int getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }
}
