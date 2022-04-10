package com.xwl.esplus.test;

import com.xwl.esplus.core.annotation.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * @author xwl
 * @since 2022/3/11 20:22
 */
@SpringBootApplication
//@EsMapperScan(basePackages = {"com.xwl.esplus.test.mapper2"}, excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestDocument3Mapper.class})
//}, includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestDocumentMapper.class})
//})
@EsMapperScan(basePackages = {"com.xwl.esplus.test.mapper"})
public class EsPlusApplication {
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EsPlusApplication.class, args);

        // 注意：classpath:META-INF/spring.factories 只是到当前类路径下查找，在jar包中是找不到的
        // classpath*:META-INF/spring.factories这样就能查找jar包中的
        /*Resource[] resources = applicationContext.getResources("classpath*:META-INF/spring.factories");
        for (Resource resource : resources) {
            System.out.println(resource);
        }*/
    }
}
