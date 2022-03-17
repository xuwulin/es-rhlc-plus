package com.xwl.esplus.core.condition.interfaces;

import java.io.Serializable;

/**
 * 查询条件封装
 * 拼接
 *
 * @author xwl
 * @since 2022/3/15 18:40
 */
public interface Join<Children> extends Serializable {
    default Children or() {
        return or(true);
    }

    /**
     * 拼接 OR
     *
     * @param condition 条件
     * @return 泛型
     */
    Children or(boolean condition);
}
