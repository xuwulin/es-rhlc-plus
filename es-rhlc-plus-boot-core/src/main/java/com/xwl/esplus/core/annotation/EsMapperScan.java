package com.xwl.esplus.core.annotation;

import com.xwl.esplus.core.register.EsMapperScannerRegister;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * elasticsearch 全局Mapper扫描注解
 * 为何在springboot的启动类或者配置类上添加该注解就会起作用呢？
 * springboot或者spring启动时会解析启动类或配置类，以及配置类上的注解，先通过反射拿到类似的注解，然后把注解信息放到Metadata中（类的注解的元数据）
 * 然后再解析注解，同时会解析注解上的注解，最终会执行EsMapperScannerRegister中的registerBeanDefinitions方法，这些工作都是由springboot的IOC容器完成的
 *
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
     * 排斥过滤器
     *
     * @return
     */
    Filter[] excludeFilters() default {};
}
