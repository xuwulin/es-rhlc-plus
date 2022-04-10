package com.xwl.esplus.core.register;

import com.xwl.esplus.core.annotation.EsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 注册bean，参照mybatis-spring
 * ImportBeanDefinitionRegistrar注入FactoryBean到SpringIOC中，
 * 而在FactoryBean中定义了类型T的动态代理，通过对InvocationHandler接口的实现来添加自定义行为，这里使用jdk默认的代理，只支持接口类型。
 *
 * ImportBeanDefinitionRegistrar，在Spring中，加载它的实现类，只有一个方法就是配合@Impor使用，是主要负责Bean 的动态注入的。
 *
 * @author xwl
 * @since 2022/3/11 20:29
 */
public class EsMapperRegister implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    /**
     * 根据需要注册 bean 定义。
     *
     * @param importingClassMetadata 导入类的注解元数据
     * @param registry               当前bean定义注册表
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathEsMapperScanner scanner = new ClassPathEsMapperScanner(registry);
        // this check is needed in Spring 3.1
        // java8写法
//        Optional.ofNullable(resourceLoader).ifPresent(scanner::setResourceLoader);
        // 普通写法
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        // 会获取到启动类所在的包，作为扫描包的根路径
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        if (log.isDebugEnabled()) {
            for (String pkg : packages) {
                log.debug("Using auto-configuration base package '{}'", pkg);
            }
        }
        scanner.setAnnotationClass(EsMapper.class);
        // 注册过滤器
        scanner.registerFilters();
        // 开始扫描
        scanner.doScan(StringUtils.toStringArray(packages));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
