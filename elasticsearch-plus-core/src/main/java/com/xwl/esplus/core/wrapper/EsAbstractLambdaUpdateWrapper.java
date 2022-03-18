package com.xwl.esplus.core.wrapper;

import com.xwl.esplus.core.wrapper.condition.SFunction;

/**
 * 更新抽象Lambda表达式父类
 *
 * @author xwl
 * @since 2022/3/16 14:37
 */
public abstract class EsAbstractLambdaUpdateWrapper<T, Children extends EsAbstractLambdaUpdateWrapper<T, Children>>
        extends EsAbstractWrapper<T, SFunction<T, ?>, Children> {
}
