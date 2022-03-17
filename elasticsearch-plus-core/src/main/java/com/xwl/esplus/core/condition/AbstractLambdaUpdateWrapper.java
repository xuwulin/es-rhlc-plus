package com.xwl.esplus.core.condition;

import com.xwl.esplus.core.condition.interfaces.SFunction;

/**
 * 更新抽象Lambda表达式父类
 *
 * @author xwl
 * @since 2022/3/16 14:37
 */
public abstract class AbstractLambdaUpdateWrapper<T, Children extends AbstractLambdaUpdateWrapper<T, Children>>
        extends AbstractWrapper<T, SFunction<T, ?>, Children> {
}
