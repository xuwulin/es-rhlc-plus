package com.xwl.esplus.core.param;

import java.util.List;

/**
 * elasticsearch索引mapping参数
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
     * 是否创建索引，默认为true
     */
    private Boolean index;
    /**
     * ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，默认值256，
     * 超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     */
    private Integer ignoreAbove;
    /**
     * 时间类型格式
     */
    private String format;
    /**
     * 拷贝
     */
    private String copyTo;
    /**
     * 创建索引时使用的分词器
     */
    private String analyzer;
    /**
     * 搜索时使用的分词器
     */
    private String searchAnalyzer;
    /**
     * 对象，如:
     * "name":{
     *         "properties": {
     *           "firstName": {
     *             "type": "keyword"
     *           },
     *           "lastName": {
     *             "type": "text",
     *             "analyzer": "ik_smart"
     *           }
     *         }
     *       }
     */
    private List<EsIndexParam> properties;
    /**
     * 多（子）字段，如：
     * "wfTopic": {
     *         "type": "text",
     *         "analyzer": "text_anlyzer",
     *         "search_analyzer": "ik_smart",
     *         "copy_to": "all",
     *         "fields": {
     *           "keyword": {
     *             "type": "keyword",
     *             "ignore_above": 256
     *           }
     *         }
     *       }
     */
    private List<EsIndexParam> fields;

    public EsIndexParam() {
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

    public Boolean getIndex() {
        return index;
    }

    public void setIndex(Boolean index) {
        this.index = index;
    }

    public Integer getIgnoreAbove() {
        return ignoreAbove;
    }

    public void setIgnoreAbove(Integer ignoreAbove) {
        this.ignoreAbove = ignoreAbove;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(String searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }

    public List<EsIndexParam> getProperties() {
        return properties;
    }

    public void setProperties(List<EsIndexParam> properties) {
        this.properties = properties;
    }

    public List<EsIndexParam> getFields() {
        return fields;
    }

    public void setFields(List<EsIndexParam> fields) {
        this.fields = fields;
    }
}
