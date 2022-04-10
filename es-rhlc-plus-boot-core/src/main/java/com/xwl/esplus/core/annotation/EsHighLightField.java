package com.xwl.esplus.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档字段高亮注解
 * @author xwl
 * @since 2022/3/11 18:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@EsDocumentField(exist = false)
public @interface EsHighLightField {
    String value() default "";
}
