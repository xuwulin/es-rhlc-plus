package com.xwl.esplus.core.wrapper;

import com.xwl.esplus.core.wrapper.condition.SFunction;

/**
 * 查询抽象Lambda表达式父类
 *
 * @author xwl
 * @since 2022/3/16 14:36
 */
public abstract class EsAbstractLambdaQueryWrapper<T, Children extends EsAbstractLambdaQueryWrapper<T, Children>>
        extends EsAbstractWrapper<T, SFunction<T, ?>, Children> {
    protected T entity;

    @Override
    public Children setEntity(T entity) {
        this.entity = entity;
        return typedThis;
    }
}
