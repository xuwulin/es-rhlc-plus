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
public @interface EsDocument {
    /**
     * 索引名称
     *
     * @return 默认为空
     */
    String value() default "";

    /**
     * 是否保持索引使用全局的 index-prefix 的值
     *
     * @return 默认为true
     */
    boolean keepGlobalIndexPrefix() default true;
}
