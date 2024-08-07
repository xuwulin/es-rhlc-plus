package com.xwl.esplus.core.proxy;

import com.xwl.esplus.core.cache.BaseCache;
import com.xwl.esplus.core.mapper.EsBaseMapperImpl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理类handler，使用JDK自动动态代理实例化mapper接口，实现了InvocationHandler接口的调用处理器对象
 * 当需要为某个接口动态添加一种行为时，不需要建立它的实现类，可以通过动态代理去建立它的实现类，在代理中添加自定义的逻辑。
 * 为接口添加动态代理，不需要添加接口实现，通过定义FactoryBean（EsMapperFactoryBean）的方式实现，将自定义业务在InvocationHandler接口实现即可
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
 * >如果需要进行类型转化必须转化成目标类的接口类型，因为JDK动态代理是利用目标类的接口实现的
 *
 * @author xwl
 * @since 2022/3/11 20:31
 */
public class EsMapperProxyHandler<T> implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 被代理的接口，需要使用被代理接口的实现进行赋值
     */
    private Class<T> mapperInterface;

    /**
     * 构造函数赋值，在使用代理对象时，如：使用@Autowired或其他方式注入的Mapper（继承EsBaseMapper）对象时调用该构造函数
     * 在EsMapperFactoryBean的构造方法之后执行
     *
     * @param mapperInterface 被代理接口的实现，该参数，由注解解析器自动赋值（自动装配），即使用@Autowired或其他方式注入的Mapper（继承EsBaseMapper）对象
     */
    public EsMapperProxyHandler(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 处理代理实例上的方法调用并返回结果。当在与其关联的代理实例上调用方法时，将在调用处理程序上调用此方法。
     * 只有执行Mapper接口中的方法时，才会调用此方法
     * <p>
     * 比如：public interface UserDocumentMapper extends EsBaseMapper<UserDocument> {}
     * 当我们调用 UserDocumentMapper.createIndex() 方法的时候，
     * 这个方法会被EsMapperProxy.invoke()方法被拦截，
     * 就会直接使用EsMapperProxy.invoke方法的返回值，而不会去走真实的UserDocumentMapper.createIndex()方法；
     * 当然也不能直接调用UserDocumentMapper中的方法，因为UserDocumentMapper并没有被实例化
     *
     * @param proxy  代理类的对象本身，如：com.xwl.esplus.core.condition.EsBaseMapperImpl@78e68401
     * @param method 正在执行的方法（代理类对象调用的方法），如：public abstract java.lang.Boolean com.xwl.esplus.core.mapper.EsBaseMapper.createIndex()
     * @param args   正在执行的方法的实际参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从缓存中获取目标对象（被代理接口的具体实现类），最终创建的代理对象也是该对象
        EsBaseMapperImpl<?> esBaseMapperInstance = BaseCache.getEsBaseMapperInstance(mapperInterface);
        // 方法反射调用：方法.invoke(目标对象, 参数);
        Object invoke = method.invoke(esBaseMapperInstance, args);
        return invoke;
    }
}
