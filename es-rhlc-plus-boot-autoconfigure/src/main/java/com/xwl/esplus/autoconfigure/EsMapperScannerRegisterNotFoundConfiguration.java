package com.xwl.esplus.autoconfigure;

import com.xwl.esplus.core.register.EsMapperRegister;
import com.xwl.esplus.core.register.EsMapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
 * mappers based on the same component-scanning path as Spring Boot itself.
 * @author xwl
 * @since 2022/3/13 21:02
 */
@Configuration
@Import(EsMapperRegister.class)
@ConditionalOnMissingBean({EsMapperFactoryBean.class})
public class EsMapperScannerRegisterNotFoundConfiguration implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(EsMapperScannerRegisterNotFoundConfiguration.class);

    @Override
    public void afterPropertiesSet() {
        logger.debug("Not found configuration for registering mapper bean using @EsMapperScan.");
    }
}
