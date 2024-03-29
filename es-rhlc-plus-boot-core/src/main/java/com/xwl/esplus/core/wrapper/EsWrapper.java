package com.xwl.esplus.core.wrapper;

import org.elasticsearch.action.search.SearchRequest;

import java.io.Serializable;

/**
 * 条件构造抽象类，条件构造器祖宗
 * @author xwl
 * @since 2022/3/11 17:09
 */
public abstract class EsWrapper<T> implements Serializable {
    /**
     * 获取elasticsearch的查询条件（子类实现）
     * @return 查询条件
     */
    protected abstract SearchRequest getSearchRequest();
}
