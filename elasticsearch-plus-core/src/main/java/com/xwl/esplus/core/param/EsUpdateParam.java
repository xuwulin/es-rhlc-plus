package com.xwl.esplus.core.param;

/**
 * 更新参数
 * @author xwl
 * @since 2022/3/16 15:03
 */
public class EsUpdateParam {
    /**
     * 字段
     */
    private String field;
    /**
     * 值
     */
    private Object value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
