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
     *
     * @return
     */
    String value() default "";

    /**
     * 是否为文档字段，默认true-存在，false-不存在
     *
     * @return es是否存在该字段
     */
    boolean exist() default true;

    /**
     * 是否高亮，默认false-不高亮，true-高亮
     *
     * @return 该字段是否高亮
     */
    boolean isHighLight() default false;

    /**
     * 是否是对象，默认默认false-不是对象，true-是对象
     *
     * @return 该字段类型是否是自定义对象
     */
    boolean isObj() default false;

    /**
     * 是否是嵌套对象，默认false-不是嵌套对象，true-是嵌套对象
     * 此属性与isObj属性的作用都是用于判断是否是对象，当nested属性为true时，isObj属性其实就没意义了（可以理解为nested包含了isObj）
     *
     * @return 该字段类型是否是自定义对象，并且在es中属于嵌套对象
     */
    boolean isNested() default false;

    /**
     * 字段验证策略，默认DEFAULT（追随全局配置）
     *
     * @return 默认策略
     */
    EsFieldStrategyEnum strategy() default EsFieldStrategyEnum.DEFAULT;
}
