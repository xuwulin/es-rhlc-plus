package com.xwl.esplus.core.proxy;

import com.xwl.esplus.core.cache.BaseCache;
import com.xwl.esplus.core.condition.EsBaseMapperImpl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理类，使用JDK自动动态代理实例化mapper接口，实现了InvocationHandler接口的调用处理器对象
 * 当需要为某个接口动态添加一种行为时，不需要建立它的实现类，可以通过动态代理去建立它的实现类，在代理中添加自定义的逻辑。
 * 为接口添加动态代理，不需要添加接口实现，通过定义FactoryBean的方式实现，将自定义业务在InvocationHandler接口实现即可
 * InvocationHandler是JdkDynamicAopProxy类
 * <p>
 * JDK代理的基本步骤：
 * >通过实现InvocationHandler接口来自定义自己的InvocationHandler;
 * >通过Proxy.getProxyClass获得动态代理类
 * >通过反射机制获得代理类的构造方法，方法签名为getConstructor(InvocationHandler.class)
 * >通过构造函数获得代理对象并将自定义的InvocationHandler实例对象传为参数传入
 * >通过代理对象调用目标方法
 * <p>
 * JDK动态代理的应用场景：
 * >利用JDK动态代理获取到的动态代理实例的类型默认是Object类型，
 * 如果需要进行类型转化必须转化成目标类的接口类型，因为JDK动态代理是利用目标类的接口实现的
 *
 * @author xwl
 * @since 2022/3/11 20:31
 */
public class EsMapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 1L;

    private Class<T> mapperInterface;

    /**
     * 构造方法，在使用代理对象时，如：使用@Autowired或其他方式注入的Mapper（继承BaseEsMapper）对象时调用该构造函数
     * 在MapperFactoryBean的构造方法之后执行
     *
     * @param mapperInterface 被代理的接口类型，该参数，由注解解析器自动赋值，即使用@Autowired或其他方式注入的Mapper（继承BaseEsMapper）对象
     */
    public EsMapperProxy(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 处理代理实例上的方法调用并返回结果。当在与其关联的代理实例上调用方法时，将在调用处理程序上调用此方法。
     * 只有执行Mapper接口中的方法时，才会调用此方法
     * <p>
     * 比如：public interface TestDocumentMapper extends BaseEsMapper<TestDocument> {}
     * 当我们调用 TestDocumentMapper.createIndex() 方法的时候，
     * 这个方法会被EsMapperProxy.invoke()方法被拦截，
     * 就会直接使用EsMapperProxy.invoke方法的返回值，而不会去走真实的TestDocumentMapper.createIndex()方法；
     * 当然也不能直接调用TestDocumentMapper中的方法，因为TestDocumentMapper并没有被实例化
     *
     * @param proxy  被代理的对象，如：com.xwl.esplus.core.condition.BaseEsMapperImpl@78e68401
     * @param method 被代理的方法，如：public abstract java.lang.Boolean com.xwl.esplus.core.mapper.BaseEsMapper.createIndex()
     * @param args   被代理的方法参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从缓存中获取
        EsBaseMapperImpl<?> baseEsMapperInstance = BaseCache.getBaseEsMapperInstance(mapperInterface);
        // 这里如果后续需要像MP那样 从xml生成代理的其它方法,则可增强method,此处并不需要
        return method.invoke(baseEsMapperInstance, args);
    }
}
