package com.xwl.esplus.core.param;

import java.util.List;

/**
 * 高亮参数
 * @author xwl
 * @since 2022/3/16 11:03
 */
public class EsHighLightParam {
    /**
     * 前置标签
     */
    private String preTag;
    /**
     * 后置标签
     */
    private String postTag;
    /**
     * 高亮字段列表
     */
    private List<String> fields;

    public EsHighLightParam() {
    }

    public EsHighLightParam(String preTag, String postTag, List<String> fields) {
        this.preTag = preTag;
        this.postTag = postTag;
        this.fields = fields;
    }

    public String getPreTag() {
        return preTag;
    }

    public void setPreTag(String preTag) {
        this.preTag = preTag;
    }

    public String getPostTag() {
        return postTag;
    }

    public void setPostTag(String postTag) {
        this.postTag = postTag;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
