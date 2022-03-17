package com.xwl.esplus.core.condition.interfaces;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 查询条件封装
 * 嵌套
 * 泛型 Param 是具体需要运行函数的类(也是 wrapper 的子类)
 *
 * @author xwl
 * @since 2022/3/15 18:36
 */
public interface Nested<Param, Children> extends Serializable {
    default Children and(Function<Param, Param> func) {
        return and(true, func);
    }

    /**
     * AND 嵌套
     *
     * @param condition 条件
     * @param func      条件函数
     * @return 泛型
     */
    Children and(boolean condition, Function<Param, Param> func);

    default Children or(Function<Param, Param> func) {
        return or(true, func);
    }

    /**
     * OR 嵌套
     *
     * @param condition 条件
     * @param func      条件函数
     * @return 泛型
     */
    Children or(boolean condition, Function<Param, Param> func);
}
