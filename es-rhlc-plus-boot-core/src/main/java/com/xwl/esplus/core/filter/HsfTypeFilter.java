package com.xwl.esplus.core.filter;

import com.xwl.esplus.core.annotation.EsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

/**
 * 自定义HSF类型过滤，过滤抽象类,接口,注解,枚举,内部类及匿名类
 *
 * @author xwl
 * @since 2022/3/12 12:57
 */
public class HsfTypeFilter extends AbstractClassTestingTypeFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean match(ClassMetadata metadata) {
        Class<?> clazz = transformToClass(metadata.getClassName());
        if (clazz == null || !clazz.isAnnotationPresent(EsMapper.class)) {
            return false;
        }
        EsMapper esMapper = clazz.getAnnotation(EsMapper.class);
        if (isAnnotatedBySpring(clazz)) {
            throw new IllegalStateException("类{" + clazz.getName() + "}已经标识了Spring组件注解,不能再指定[registerBean = true]");
        }
        // 过滤抽象类,接口,注解,枚举,内部类及匿名类
        return !metadata.isAbstract() && !clazz.isInterface() && !clazz.isAnnotation() && !clazz.isEnum()
                && !clazz.isMemberClass() && !clazz.getName().contains("$");
    }

    /**
     * 根据类名使用反射转换为类
     *
     * @param className 类名
     * @return
     */
    private Class<?> transformToClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("未找到此类：{}", className);
        }
        return clazz;
    }

    /**
     * 判断一个类是否被spring的注解所标识
     *
     * @param clazz
     * @return
     */
    private boolean isAnnotatedBySpring(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Configuration.class)
                || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)
                || clazz.isAnnotationPresent(Controller.class);
    }
}
