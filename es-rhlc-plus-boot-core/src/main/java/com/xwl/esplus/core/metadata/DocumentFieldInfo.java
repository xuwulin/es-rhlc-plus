package com.xwl.esplus.core.metadata;

import com.alibaba.fastjson.serializer.NameFilter;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;

import java.lang.reflect.Field;
import java.util.List;

/**
 * elasticsearch文档字段信息
 *
 * @author xwl
 * @since 2022/3/11 18:59
 */
public class DocumentFieldInfo {
    /**
     * 忽略的字段
     */
    private String ignoreColumn;
    /**
     * 实体字段名
     */
    private String column;
    /**
     * es中的字段名
     */
    private String mappingColumn;
    /**
     * 字段名过滤器
     */
    private NameFilter nameFilter;
    /**
     * 索引名称
     */
    private String indexName;
    /**
     * 文档映射结果集
     */
    private String resultMap;
    /**
     * 文档主键ID 属性名
     */
    private String keyProperty;
    /**
     * 文档主键ID 字段名
     */
    private String keyColumn;
    /**
     * 字段策略 默认，自判断 null
     */
    private final EsFieldStrategyEnum fieldStrategy;
    /**
     * 文档字段信息列表
     */
    private List<DocumentFieldInfo> fieldList;
    /**
     * 标记该字段属于哪个类
     */
    private Class<?> clazz;

    /**
     * 存在 EsDocumentField 注解时, 使用的构造函数
     *
     * @param documentConfig        索引配置
     * @param field           字段
     * @param esDocumentField 文档字段注解
     */
    public DocumentFieldInfo(GlobalConfig.DocumentConfig documentConfig, Field field, EsDocumentField esDocumentField) {
        this.clazz = field.getDeclaringClass();
        this.column = field.getName();
        // 优先使用单个字段注解，否则使用全局配置
        if (esDocumentField.strategy() == EsFieldStrategyEnum.DEFAULT) {
            this.fieldStrategy = documentConfig.getFieldStrategy();
        } else {
            this.fieldStrategy = esDocumentField.strategy();
        }
    }

    /**
     * 不存在 EsDocumentField 注解时, 使用的构造函数
     *
     * @param dbConfig 索引配置
     * @param field    字段
     */
    public DocumentFieldInfo(GlobalConfig.DocumentConfig dbConfig, Field field) {
        this.fieldStrategy = dbConfig.getFieldStrategy();
        this.clazz = field.getDeclaringClass();
        this.column = field.getName();
    }

    public String getIgnoreColumn() {
        return ignoreColumn;
    }

    public void setIgnoreColumn(String ignoreColumn) {
        this.ignoreColumn = ignoreColumn;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getMappingColumn() {
        return mappingColumn;
    }

    public void setMappingColumn(String mappingColumn) {
        this.mappingColumn = mappingColumn;
    }

    public NameFilter getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(NameFilter nameFilter) {
        this.nameFilter = nameFilter;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public EsFieldStrategyEnum getFieldStrategy() {
        return fieldStrategy;
    }

    public List<DocumentFieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<DocumentFieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
