package com.xwl.esplus.autoconfigure;

import com.xwl.esplus.core.aop.DynamicClientAnnotationAdvisor;
import com.xwl.esplus.core.aop.DynamicClientAnnotationInterceptor;
import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.constant.EsGlobalConstants;
import com.xwl.esplus.core.toolkit.DynamicRoutingClient;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/10/27 10:57
 */
@Configuration
@EnableConfigurationProperties(DynamicEsPlusProperties.class)
@ConditionalOnProperty(prefix = EsGlobalConstants.PROPERTIES_DYNAMIC_PREFIX, name = "primary")
@AutoConfigureBefore(EsPlusAutoConfiguration.class)
public class DynamicEsPlusAutoConfiguration implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String CLIENT_PREFIX = "restHighLevelClient_";

    private DynamicEsPlusProperties dynamicEsPlusProperties;

    private ApplicationContext applicationContext;


    public DynamicEsPlusAutoConfiguration(DynamicEsPlusProperties dynamicEsPlusProperties, ApplicationContext applicationContext) {
        this.dynamicEsPlusProperties = dynamicEsPlusProperties;
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("enable dynamic client");
        // 全局配置缓存至本地
        GlobalConfigCache.setGlobalConfig(dynamicEsPlusProperties.getGlobalConfig());
        // 动态注册es客户端
        restHighLevelClient();
    }

    /**
     * ElasticsearchProperties RestHighLevelClient配置
     *
     * @return org.elasticsearch.client.RestHighLevelClient
     */
    @Bean
    @ConditionalOnMissingBean(RestHighLevelClient.class)
    public RestHighLevelClient restHighLevelClient() {
        AtomicReference<RestHighLevelClient> atomicReference = new AtomicReference<>();
        // 注入每个客户端
        Map<String, EsPlusProperties> datasource = dynamicEsPlusProperties.getClient();
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        datasource.entrySet().forEach(entry -> {
            EsPlusAutoConfiguration esPlusAutoConfiguration = new EsPlusAutoConfiguration(entry.getValue());
            RestHighLevelClient restHighLevelClient = esPlusAutoConfiguration.restHighLevelClient();
            if (entry.getKey().equals(dynamicEsPlusProperties.getPrimary())) {
                atomicReference.set(restHighLevelClient);
            }
            beanFactory.registerSingleton(CLIENT_PREFIX.concat("_").concat(entry.getKey()), restHighLevelClient);
            DynamicRoutingClient.addClient(entry.getKey(), restHighLevelClient);
        });
        if (!DynamicRoutingClient.containsClient(dynamicEsPlusProperties.getPrimary())) {
            throw ExceptionUtils.epe("Please check the configuration : es-plus.dynamic.primary, it does not match the main client");
        }
        return atomicReference.get();
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicClientAnnotationAdvisor dynamicClientAnnotationAdvisor() {
        DynamicClientAnnotationInterceptor interceptor = new DynamicClientAnnotationInterceptor();
        DynamicClientAnnotationAdvisor advisor = new DynamicClientAnnotationAdvisor(interceptor);
        advisor.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return advisor;
    }
}
