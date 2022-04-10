package com.xwl.esplus.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页参数 来源:https://github.com/pagehelper/Mybatis-PageHelper
 *
 * @author xwl
 * @since 2022/3/11 19:21
 */
public class PageSerializable<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 总记录数
     */
    protected long total;
    /**
     * 结果集
     */
    protected List<T> list;

    public PageSerializable(List<T> list) {
        this.list = list;
        this.total = list.size();
    }

    public static <T> PageSerializable<T> of(List<T> list) {
        return new PageSerializable<T>(list);
    }

    public PageSerializable() {

    }

    public PageSerializable(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
