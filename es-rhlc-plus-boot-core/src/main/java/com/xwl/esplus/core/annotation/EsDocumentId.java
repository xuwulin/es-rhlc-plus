package com.xwl.esplus.core.annotation;

import com.xwl.esplus.core.enums.EsKeyTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文档主键注解
 * @author xwl
 * @since 2022/3/11 18:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EsDocumentId {
    /**
     * 文档主键字段名称
     *
     * @return es默认_id
     */
    String value() default "_id";

    /**
     * 文档主键策略
     *
     * @return 默认为未设置
     */
    EsKeyTypeEnum type() default EsKeyTypeEnum.NONE;
}
