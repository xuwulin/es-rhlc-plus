package com.xwl.esplus.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface EsClient {
    /**
     * 客户端名称
     *
     * @return 默认为空
     */
    String value() default "";
}
