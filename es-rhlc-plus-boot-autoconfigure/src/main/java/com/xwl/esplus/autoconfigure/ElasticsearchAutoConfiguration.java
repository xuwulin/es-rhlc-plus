package com.xwl.esplus.autoconfigure;

import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.constant.EsPropertiesConstants;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;
import com.xwl.esplus.core.enums.EsIdTypeEnum;
import com.xwl.esplus.core.toolkit.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * elasticsearch自动配置
 *
 * @author xwl
 * @since 2022/3/11 14:43
 */
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
@ConditionalOnClass(RestHighLevelClient.class)
@ConditionalOnProperty(prefix = "es-plus", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class ElasticsearchAutoConfiguration implements InitializingBean, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        Optional.ofNullable(environment.getProperty(EsPropertiesConstants.ENABLE_DSL))
                .ifPresent(e -> globalConfig.setEnableDsl(Boolean.valueOf(e)));
        // 文档相关配置
        GlobalConfig.DocumentConfig documentConfig = new GlobalConfig.DocumentConfig();
        Optional.ofNullable(environment.getProperty(EsPropertiesConstants.INDEX_PREFIX))
                .ifPresent(documentConfig::setIndexPrefix);
        Optional.ofNullable(environment.getProperty(EsPropertiesConstants.ID_TYPE))
                .ifPresent(e -> documentConfig.setIdType(Enum.valueOf(EsIdTypeEnum.class, e.toUpperCase())));
        Optional.ofNullable(environment.getProperty(EsPropertiesConstants.FIELD_STRATEGY))
                .ifPresent(e -> documentConfig.setFieldStrategy(Enum.valueOf(EsFieldStrategyEnum.class, e.toUpperCase())));
        Optional.ofNullable(environment.getProperty(EsPropertiesConstants.DATA_FORMAT))
                .ifPresent(documentConfig::setDateFormat);
        Optional.ofNullable(environment.getProperty(EsPropertiesConstants.MAP_UNDERSCORE_TO_CAMEL_CASE))
                .ifPresent(e -> documentConfig.setMapUnderscoreToCamelCase(Boolean.parseBoolean(e)));
        globalConfig.setDocumentConfig(documentConfig);
        // 缓存至本地
        GlobalConfigCache.setGlobalConfig(globalConfig);
    }

    /**
     * ElasticsearchProperties RestHighLevelClient配置
     *
     * @param elasticsearchProperties ElasticsearchProperties属性
     * @return org.elasticsearch.client.RestHighLevelClient
     */
    @Bean
    @ConditionalOnMissingBean(RestHighLevelClient.class)
    public RestHighLevelClient restHighLevelClient(ElasticsearchProperties elasticsearchProperties) {
        // 拆分地址
        List<HttpHost> hostLists = new ArrayList<>();
        String address = elasticsearchProperties.getAddress();
        if (StringUtils.isBlank(address)) {
            throw new RuntimeException("please config the elasticsearch hosts: es-plus.hosts");
        }
        if (!address.contains(":")) {
            throw new RuntimeException("the hosts must contains port and separate by ':'");
        }
        String schema = elasticsearchProperties.getSchema();
        schema = StringUtils.isBlank(schema) ? "http" : schema;
        String[] addresses = address.split(",");
        for (String addr : addresses) {
            String host = addr.split(":")[0];
            String port = addr.split(":")[1];
            hostLists.add(new HttpHost(host, Integer.parseInt(port), schema));
        }
        // 转换成 HttpHost 数组
        HttpHost[] httpHost = hostLists.toArray(new HttpHost[]{});
        // 构建连接对象
        RestClientBuilder builder = RestClient.builder(httpHost);

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // 用户名，密码配置
        String username = elasticsearchProperties.getUsername();
        String password = elasticsearchProperties.getPassword();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }

        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setSocketTimeout(elasticsearchProperties.getSocketTimeout());
            requestConfigBuilder.setConnectTimeout(elasticsearchProperties.getConnectTimeout());
            requestConfigBuilder.setConnectionRequestTimeout(elasticsearchProperties.getConnectionRequestTimeout());
            return requestConfigBuilder;
        });
        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            // 设置账号密码
            httpClientBuilder.setMaxConnTotal(elasticsearchProperties.getMaxConnTotal());
            httpClientBuilder.setMaxConnPerRoute(elasticsearchProperties.getMaxConnPerRoute());
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(builder);
    }
}
