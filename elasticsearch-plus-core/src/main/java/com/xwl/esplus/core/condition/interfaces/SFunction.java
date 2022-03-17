package com.xwl.esplus.core.condition.interfaces;

import java.io.Serializable;

/**
 * 函数式接口
 *
 * @author xwl
 * @since 2022/3/11 17:45
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable {
    /**
     * 函数式接口方法
     *
     * @param t 参数
     * @return
     */
    R apply(T t);
}
