package com.xwl.esplus.core.register;

import com.xwl.esplus.core.cache.BaseCache;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * 代理类工厂bean，参照mybatis-spring
 * 注意这个工厂只能注入接口，不能注入具体类。
 * <p>
 * 所有被@EsMapperScan注解扫描到的Mapper接口的bean都会经由MapperFactoryBean类来创建,
 * 而不是简简单单的直接实例化Mapper接口,当然那也没有任何意义，因为Mapper接口只定义了抽象方法。
 * <p>
 * mapper接口如何被spring管理？
 * 其实spring并不能直接管理接口，最终管理的是对象，接口是如何变成对象从而被spring管理的呢？
 * 如mybatis，就有一个MapperFactoryBean接口，通过这个接口生产出mapper对象，我们使用的就是这个工厂生产的mapper对象，而不是mapper接口
 * <p>
 * 注意：
 * BeanFactory是一个Bean工厂。
 * FactoryBean 则是一个特殊的bean，可以额外的创建出另一个bean，并替代原生的bean,原生bean的名称为 &+名称（实现getObject() 和getObjectType()）
 *
 * @author xwl
 * @since 2022/3/11 20:30
 */
public class EsMapperFactoryBean<T> implements FactoryBean<T> {
    /**
     * 被代理的接口
     */
    private Class<T> mapperInterface;

    @Autowired
    private RestHighLevelClient client;

    public EsMapperFactoryBean() {
    }

    /**
     * 构造函数，给mapperInterface赋值
     * Spring是如何知道Class<T> mapperInterface;具体的类型呢，不可能每个class都去扫描一遍吧？
     * 其实是在ClassPathEsMapperScanner#processBeanDefinitions()方法中，
     * 由definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);决定的
     * beanClassName的类型其实是：EsMapperFactoryBean.class
     *
     * @param mapperInterface 被代理的接口类型，该参数，由注解解析器自动赋值（自动装配），即使用@Autowired或其他方式注入的Mapper（继承BaseEsMapper）对象
     */
    public EsMapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 当 ioc 容器提取对象时，调用此方法获取一个代理对象
     * Mapper代理对象的创建就是在EsMapperFactoryBean的getObject方法中返回的
     *
     * @return 返回的对象最终会成为ioc容器中的一个bean
     * @throws Exception
     */
    @Override
    public T getObject() throws Exception {
        // 代理对象
        EsMapperProxy<T> esMapperProxy = new EsMapperProxy<>(mapperInterface);
        // 缓存至本地
        BaseCache.initCache(mapperInterface, client);
        // 获取代理对象（com.xwl.esplus.core.condition.BaseEsMapperImpl@78e68401）
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, esMapperProxy);
    }

    /**
     * 当 ioc 容器获取类型时，从此方法获取类型
     *
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    /**
     * 该对象是否是单例
     *
     * @return true：单例，容器中只会有一个该Bean；false：多实例，每次获取都会创建一个新的Bean
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
