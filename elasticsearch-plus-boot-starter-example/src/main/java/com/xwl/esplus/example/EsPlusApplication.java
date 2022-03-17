package com.xwl.esplus.example;

import com.xwl.esplus.core.annotation.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xwl
 * @since 2022/3/11 20:22
 */
@SpringBootApplication
//@EsMapperScan(basePackages = {"com.xwl.esplus.example.mapper2"}, excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestDocument3Mapper.class})
//}, includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {TestDocumentMapper.class})
//})
//@EsMapperScan(basePackages = {"com.xwl.esplus.example.mapper"})
@EsMapperScan(basePackages = {"com.xwl.esplus.example.mapper"})
public class EsPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsPlusApplication.class, args);
    }
}
