package com.xwl.esplus.core.param;

import com.xwl.esplus.core.enums.EsAnalyzerEnum;

/**
 * elasticsearch索引相关参数
 *
 * @author xwl
 * @since 2022/3/11 17:48
 */
public class EsIndexParam {
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 分词器
     */
    private EsAnalyzerEnum analyzer;
    /**
     * 查询分词器
     */
    private EsAnalyzerEnum searchAnalyzer;

    public EsIndexParam() {
    }

    public EsIndexParam(String fieldName, String fieldType, EsAnalyzerEnum analyzer, EsAnalyzerEnum searchAnalyzer) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.analyzer = analyzer;
        this.searchAnalyzer = searchAnalyzer;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public EsAnalyzerEnum getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(EsAnalyzerEnum analyzer) {
        this.analyzer = analyzer;
    }

    public EsAnalyzerEnum getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(EsAnalyzerEnum searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }
}
