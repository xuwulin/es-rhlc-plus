package com.xwl.esplus.core.param;

import java.util.List;

/**
 * 排序参数
 * @author xwl
 * @since 2022/3/16 11:04
 */
public class EsSortParam {
    /**
     * 是否升序排列
     */
    private Boolean isAsc;
    /**
     * 排序字段
     */
    private List<String> fields;

    public EsSortParam() {
    }

    public EsSortParam(Boolean isAsc, List<String> fields) {
        this.isAsc = isAsc;
        this.fields = fields;
    }

    public Boolean getAsc() {
        return isAsc;
    }

    public void setAsc(Boolean asc) {
        isAsc = asc;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
