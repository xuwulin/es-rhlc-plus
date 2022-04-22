package com.xwl.esplus.core.annotation;

import com.xwl.esplus.core.enums.EsFieldStrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档字段注解
 *
 * @author xwl
 * @since 2022/3/11 18:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface EsDocumentField {
    /**
     * 指定es字段名称，如果不指定则和实体字段保持一致
     * @return
     */
    String value() default "";

    /**
     * 是否为文档字段 默认 true 存在，false 不存在
     *
     * @return 存在
     */
    boolean exist() default true;

    /**
     * 字段验证策略
     *
     * @return 默认策略
     */
    EsFieldStrategyEnum strategy() default EsFieldStrategyEnum.DEFAULT;
}
