package com.xwl.esplus.core.wrapper.processor;

public class OrCount {
    /**
     * or出现的总数
     */
    private int orAllCount = 0;
    /**
     * or在and及or内层出现的次数
     */
    private int orInnerCount = 0;

    public int getOrAllCount() {
        return orAllCount;
    }

    public void setOrAllCount(int orAllCount) {
        this.orAllCount = orAllCount;
    }

    public int getOrInnerCount() {
        return orInnerCount;
    }

    public void setOrInnerCount(int orInnerCount) {
        this.orInnerCount = orInnerCount;
    }
}
