package com.xwl.esplus.core.register;

import com.xwl.esplus.core.annotation.EsMapperScan;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注册bean，参照mybatis-plus
 * ImportBeanDefinitionRegistrar注入EsMapperFactoryBean到SpringIOC中，
 * 而在EsMapperFactoryBean中定义了类型T的动态代理，通过对InvocationHandler接口的实现来添加自定义行为，这里使用jdk默认的代理，只支持接口类型。
 * <p>
 * ImportBeanDefinitionRegistrar，在Spring中，加载它的实现类，只有一个方法就是配合@Impor使用，是主要负责Bean 的动态注入的。
 *
 * EsMapperScannerRegister如何注入到Spring中的？
 * 在springboot的启动类上使用 @EsMapperScan 指定扫描路径，
 * 因为在 @EsMapperScan 这个注解类上使用 @Import 导入 EsMapperScannerRegister类， 注册到spring中
 *
 * @author xwl
 * @since 2022/3/11 20:29
 */
public class EsMapperScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ResourceLoader resourceLoader;

    /**
     * 根据需要注册 bean 定义。
     *
     * @param importingClassMetadata 导入类的注解元数据
     * @param registry               当前bean定义注册表，就是applicationContext，AnnotationConfigApplicationContext的实现接口
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 对注解（自定义注解EsMapperScan）进行扫描
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EsMapperScan.class.getName());
        // 获取自定义注解@EsMapperScan中的属性值（value/basePackages，字符串数组或字符串）
        AnnotationAttributes annAttrs = AnnotationAttributes.fromMap(annotationAttributes);

        // 对类进行扫描
        EsMapperScanner scanner = new EsMapperScanner(registry);
        // this check is needed in Spring 3.1
        Optional.ofNullable(resourceLoader).ifPresent(scanner::setResourceLoader);

        // @EsMapperScan注解扫描的包
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(Arrays.stream(annAttrs.getStringArray("value"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toList()));
        basePackages.addAll(Arrays.stream(annAttrs.getStringArray("basePackages"))
                .filter(StringUtils::hasText)
                .collect(Collectors.toList()));
        // 标注@EsMapperScan注解所在类的包
        basePackages.addAll(Arrays.stream(annAttrs.getClassArray("basePackageClasses"))
                        .map(ClassUtils::getPackageName)
                        .collect(Collectors.toList()));

        // 排斥过滤器
        Arrays.stream(annAttrs.getAnnotationArray("excludeFilters"))
                .forEach(filter -> typeFiltersFor(filter).forEach(typeFilter -> scanner.addExcludeFilter(typeFilter)));
        if (CollectionUtils.isEmpty(basePackages)) {
            throw ExceptionUtils.epe("Annotation @EsMapperScan must be value(basePackages) or basePackageClasses");
        }
        // 注册过滤器，自定义扫描规则，与Spring的默认机制不同，Spring的扫描器是用于扫描bean的，spring扫描到接口是会忽略掉的，但mybatis需要扫描接口
        scanner.registerFilters();
        // 开始扫描，对包路径进行扫描
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    /**
     * 设置运行此对象的资源加载器
     *
     * @param resourceLoader 资源加载器
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 自定义过滤器
     *
     * @param filterAttributes
     * @return
     */
    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = filterAttributes.getEnum("type");

        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                            "@EsMapperScan ANNOTATION type filter requires an annotation type");
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass,
                            "@EsMapperScan CUSTOM type filter requires a TypeFilter implementation");
                    TypeFilter filter = BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("Filter type not supported with Class value: " + filterType);
            }
        }
        return typeFilters;
    }
}
