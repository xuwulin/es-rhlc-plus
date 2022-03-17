package com.xwl.esplus.core.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * elasticsearch Mapper注解
 *
 * @author xwl
 * @since 2022/3/11 20:22
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface EsMapper {
    // Interface Mapper
}
