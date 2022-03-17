package com.xwl.esplus.core.annotation;

import com.xwl.esplus.core.enums.EsFieldStrategyEnum;

/**
 * 文档字段注解
 *
 * @author xwl
 * @since 2022/3/11 18:59
 */
public @interface EsDocumentField {
    /**
     * 是否为数据库表字段 默认 true 存在，false 不存在
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
