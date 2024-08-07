package com.xwl.esplus.core.register;

import com.xwl.esplus.core.proxy.EsMapperFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * mapper接口扫描器：扫描指定路径下的所有接口，参照mybatis-spring
 *
 * @author xwl
 * @since 2022/3/11 20:30
 */
public class EsMapperScanner extends ClassPathBeanDefinitionScanner {
    private EsMapperFactoryBean<?> esMapperFactoryBean;

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    private static final String KEY = "mapperBeanDefinition";

    public void setMapperFactoryBean(EsMapperFactoryBean<?> esMapperFactoryBean) {
        this.esMapperFactoryBean = esMapperFactoryBean != null ? esMapperFactoryBean : new EsMapperFactoryBean<>();
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    /**
     * 创建一个自定义的路径扫描器
     *
     * @param registry 需要传入 BeanDefinitionRegistry，就是IOC容器，该对象默认实现为 DefaultListableBeanFactory
     */
    public EsMapperScanner(BeanDefinitionRegistry registry) {
        // false表示不使用ClassPathBeanDefinitionScanner默认的TypeFilter
        super(registry, false);
    }

    public void registerFilters() {
        // 是否全接口注入标记
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        // 如果配置了 annotation（@EsMapper），新增一个注解过滤器，并且设置不全注入
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        // override AssignableTypeFilter to ignore matches on the actual marker interface
        // 如果配置了 markerInterface，新增一个父类接口过滤器，并且设置不全注入
        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        // 如果全接口注入
        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            // 新增一个默认的过滤器，比较永远返回true
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        // exclude package-info.java
        // 排除 package-info.java 类
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    /**
     * 在指定的基本包中执行扫描，返回注册的 bean 定义。此方法不注册注释配置处理器，而是将其留给调用者
     *
     * @param basePackages 要扫描的包
     * @return
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        // 扫描包，返回BeanDefinition对象集合，有多少个mapper接口，就有多少个BeanDefinition对象
        // 但是这里拿到的beanDefinitions是不能直接使用的（beanClass属性不对），因为扫描的都是接口，不能被实例化，需要进一步处理
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No es-plus mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            // 修改BeanDefinition
            // 对扫描结果进行处理，如果不处理的话，这个接口就当作了一个普通的Bean注入IOC了，在引入调用，就会出现错误了，因为接口不能被实例化。
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    /**
     * 为扫描到的接口创建代理对象（创建代理对象的入口）
     * BeanDefinitionHolder：是BeanDefinition的持有者，包含了Bean的名字，和Bean的别名，也包含了BeanDefinition。
     *
     * @param beanDefinitions bean的定义信息集合
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        // 此时获取的beanDefinition中的类型是mapper接口，不是我们想要的EsMapperFactoryBean类型，因此需要手动修改
        // 遍历bean的定义信息，一个个修改
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            // bean的全类名：mapper接口的全类名
            String beanClassName = definition.getBeanClassName();
            logger.debug("Creating EsMapperFactoryBean with name '" + holder.getBeanName()
                    + "' and '" + beanClassName + "' mapperInterface");

            // the mapper interface is the original class of the bean
            // but, the actual class of the bean is EsMapperFactoryBean
            // 以下两行代码是关键！！！
            // 编程式配置EsMapperFactoryBean的BeanDefinition，手动创建EsMapperFactoryBean对象，
            // 并且是多个EsMapperFactoryBean对象，每个EsMapperFactoryBean对象，属性值都不一样
            // EsMapperFactoryBean最终创建的代理对象，是Mapper接口的代理对象
            /**
             * 所有的Mapper接口被扫描到，封装成BeanDefinition，还经历了一次改造，
             * 最主要的就是将mapper接口BeanDefinition的beanClass改成了com.xwl.esplus.core.register.EsMapperFactoryBean.class
             * 并且将mapper接口BeanDefinition的名称作为构造函数的入参传入进去
             *
             * beanClass被改成MapperFactoryBean这意味着什么?
             * 我们知道spring ioc容器初始化的时候,是循环BeanDefinition的集合，
             * 然后再根据每一个BeanDefinition的各项属性来实例化bean的。
             * 最主要的一个属性肯定是beanClass,有了beanClass,就可以反射调用构造方法来实例化bean
             *
             * 现在所有的Mapper接口bean的Class都被设置为EsMapperFactoryBean,
             * 这就表示,之后所有Mapper接口的bean都会经由EsMapperFactoryBean类来创建（构造方法）,
             * 而不是简简单单的直接实例化Mapper接口,当然那也没有任何意义，因为Mapper接口只定义了抽象方法。
             */
            // getConstructorArgumentValues()，获取构造函数参数值，addGenericArgumentValue()，添加构造函数参数值
            // 即指定EsMapperFactoryBean构造函数的参数为mapper接口的class
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            /** setBeanClass之前，definition中的beanClass是mapper接口，
             * 将bean的真实类型改变为EsMapperFactoryBean，最终由EsMapperFactoryBean来创建mapper接口的代理对象
             * 以UserDocumentMapper为例，意味着当前的mapper接口在Spring容器中，
             * beanName是userDocumentMapper，beanClass是EsMapperFactoryBean.class。
             * 那么在IOC初始化的时候，实例化的对象就是EsMapperFactoryBean对象。
             */
            definition.setBeanClass(EsMapperFactoryBean.class);
            // 完成以上两步，spring在创建EsMapperFactoryBean对象时，就会调用EsMapperFactoryBean的“有参构造方法”来创建EsMapperFactoryBean对象
            // 不同于在EsMapperFactoryBean上添加@Component注解，标注@Component注解，spring在创建EsMapperFactoryBean对象时，默认会调用EsMapperFactoryBean的无参构造方法来创建EsMapperFactoryBean对象
            // 完成EsMapperFactoryBean的bean定义，spring在创建EsMapperFactoryBean对象时，会判断MyFactoryBean类是否实现FactoryBean接口，如果实现了，则会调用getObject()方法创建指定的对象，并加入ioc容器。

            logger.debug("Enabling autowire by type for EsMapperFactoryBean with name '" + holder.getBeanName() + "'.");
            /**
             * 一般的bean，其定义中，autowireMode默认是AUTOWIRE_NO
             *
             * 设置自动装配：按照类型装配，（对于 “构造方法” 和 “工厂方法” 来说选择AUTOWIRE_CONSTRUCTOR）
             * 将BeanDefinition的autowireMode属性改成 AUTOWIRE_BY_TYPE，
             * 后面实例化该bean的时候spring会自动调用类中的所有set方法，也就是说，不管set方法上有没有@Autowired注解，都会调用set方法。
             * 并且set方法所需要的参数，spring也会从容器中去获取并自动注入。
             *
             * mybatis-spring 创建SqlSessionFactoryBean对象时，会调用set方法注入SqlSessionFactory对象（在SqlSessionFactoryBean的父类SqlSessionDaoSupport中申明的setSqlSessionFactory()方法）
             *
             */
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

    /**
     * 用于判断扫描到的类上是否有@Component注解，有的话，则将其加入到beanDefinition中
     * 但是这里是扫描mapper接口，有没有@Component注解都无所谓，直接返回true，也可以不用重写这个方法
     *
     * @param metadataReader the ASM ClassReader for the class
     * @return
     * @throws IOException
     */
    @Override
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        return true;
    }

    /**
     * 判断beanDefinition是否是一个候选的bean，spring的默认逻辑是过滤掉接口，不符合当前框架要求
     * 因此需要修改判断实现逻辑，仅判断该类型是否是接口类型，排除掉非接口的类，即只需要扫描mapper接口
     *
     * @param beanDefinition 要检查的bean定义信息
     * @return true 是候选组件
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    /**
     * 检查给定候选的bean名称，确定相应的bean定义是否需要注册或与现有定义冲突
     *
     * @param beanName       bean名称
     * @param beanDefinition bean定义信息
     * @return
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping EsMapperFactoryBean with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }
}
