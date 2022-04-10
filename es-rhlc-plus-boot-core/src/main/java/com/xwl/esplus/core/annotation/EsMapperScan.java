package com.xwl.esplus.core.annotation;

import com.xwl.esplus.core.register.EsMapperScannerRegister;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * elasticsearch 全局Mapper扫描注解
 *
 * @author xwl
 * @since 2022/3/11 20:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EsMapperScannerRegister.class)
public @interface EsMapperScan {
    /**
     * basePackages() 属性的别名
     *
     * @return
     */
    String[] value() default {};

    /**
     * 用于扫描 elasticsearch Mapper接口的基础包
     * value() 属性的别名
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * basePackages() 的类型安全替代方案，用于指定要扫描带注释组件的包。将扫描该包下的所有类
     *
     * @return
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 包含过滤器，待优化
     *
     * @return
     */
    Filter[] includeFilters() default {};

    /**
     * 排斥过滤器
     *
     * @return
     */
    Filter[] excludeFilters() default {};
}
