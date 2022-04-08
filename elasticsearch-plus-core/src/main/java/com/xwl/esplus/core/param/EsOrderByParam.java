package com.xwl.esplus.core.param;

/**
 * 排序
 *
 * @author xwl
 * @since 2022/3/30 10:34
 */
public class EsOrderByParam {
    /**
     * 排序字段
     */
    private String order;
    /**
     * 排序规则 ASC:升序 DESC:降序
     */
    private String sort;

    public EsOrderByParam() {
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
