package com.xwl.esplus.core.enums;

/**
 * 刷新策列
 *
 * @author xwl
 * @since 2022/3/16 11:11
 */
public enum EsRefreshPolicy {
    /**
     * 默认不刷新
     */
    NONE("false"),
    /**
     * 立即刷新,性能损耗高
     */
    IMMEDIATE("true"),
    /**
     * 请求提交数据后，等待数据完成刷新(1s)，再结束请求 性能损耗适中
     */
    WAIT_UNTIL("wait_for");

    /**
     * 刷新策略值
     */
    private String value;

    EsRefreshPolicy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
