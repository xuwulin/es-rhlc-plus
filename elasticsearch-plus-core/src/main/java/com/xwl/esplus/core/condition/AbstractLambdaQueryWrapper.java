package com.xwl.esplus.core.condition;

import com.xwl.esplus.core.condition.interfaces.SFunction;

/**
 * 查询抽象Lambda表达式父类
 *
 * @author xwl
 * @since 2022/3/16 14:36
 */
public abstract class AbstractLambdaQueryWrapper<T, Children extends AbstractLambdaQueryWrapper<T, Children>>
        extends AbstractWrapper<T, SFunction<T, ?>, Children> {
    protected T entity;

    @Override
    public Children setEntity(T entity) {
        this.entity = entity;
        return typedThis;
    }
}
