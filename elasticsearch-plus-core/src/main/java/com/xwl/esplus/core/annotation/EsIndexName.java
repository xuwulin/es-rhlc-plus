package com.xwl.esplus.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * elasticsearch索引注解
 *
 * @author xwl
 * @since 2022/3/11 18:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface EsIndexName {
    /**
     * 实体对应的索引名
     *
     * @return 默认为空
     */
    String value() default "";

    /**
     * 是否保持使用全局的 tablePrefix 的值
     * 只生效于 既设置了全局的 tablePrefix 也设置了上面 value 的值
     * 如果是 false , 全局的 tablePrefix 不生效
     *
     * @return 默认为false
     */
    boolean keepGlobalPrefix() default false;
}
