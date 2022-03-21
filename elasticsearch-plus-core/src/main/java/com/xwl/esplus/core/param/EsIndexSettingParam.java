package com.xwl.esplus.core.param;

import java.util.Map;

/**
 * elasticsearch索引setting参数
 *
 * @author xwl
 * @since 2022/3/20 20:38
 */
public class EsIndexSettingParam {
    /**
     * 分片数
     */
    private Integer numberOfShards;
    /**
     * 副本数
     */
    private Integer numberOfReplicas;
    /**
     * 分词器设置，分词器最外层，包含analyzer、filter
     */
    private Map<String, Object> analysis;

    public EsIndexSettingParam() {
    }

    public EsIndexSettingParam(Integer numberOfShards, Integer numberOfReplicas, Map<String, Object> analysis) {
        this.numberOfShards = numberOfShards;
        this.numberOfReplicas = numberOfReplicas;
        this.analysis = analysis;
    }

    public Integer getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(Integer numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public Integer getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(Integer numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public Map<String, Object> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Map<String, Object> analysis) {
        this.analysis = analysis;
    }
}
