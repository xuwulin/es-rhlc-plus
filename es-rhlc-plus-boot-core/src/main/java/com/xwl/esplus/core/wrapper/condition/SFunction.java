package com.xwl.esplus.core.wrapper.condition;

import java.io.Serializable;

/**
 * 函数式接口：接收一个参数T，返回一个结果R，和JDK自带的函数式接口Function功能一致
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
