package com.xwl.esplus.core.metadata;

import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;

import java.lang.reflect.Field;

/**
 * es文档字段信息
 *
 * @author xwl
 * @since 2022/3/11 18:59
 */
public class DocumentFieldInfo {
    /**
     * es索引名称
     */
//    private String indexName;
    /**
     * es索引对应的实体类
     */
    private Class<?> entityClass;
    /**
     * es索引对应的实体类主键字段名称
     */
//    private String keyFieldName;
    /**
     * es索引主键字段名称
     */
//    private String keyColumnName;
    /**
     * es索引对应的实体类字段名
     */
    private String fieldName;
    /**
     * es索引字段名
     */
    private String columnName;
    /**
     * 忽略的字段
     */
//    private String ignoreColumn;
    /**
     * 字段名过滤器
     */
//    private NameFilter nameFilter;
    /**
     * 文档映射结果集
     */
//    private String resultMap;
    /**
     * 字段策略 默认，自判断 null
     */
    private final EsFieldStrategyEnum fieldStrategy;
    /**
     * 文档字段信息列表
     */
//    private List<DocumentFieldInfo> fieldList;

    /**
     * 存在 EsDocumentField 注解时, 使用的构造函数
     *
     * @param documentConfig  索引配置
     * @param field           字段
     * @param esDocumentField 文档字段注解
     */
    public DocumentFieldInfo(GlobalConfig.DocumentConfig documentConfig, Field field, EsDocumentField esDocumentField) {
        this.entityClass = field.getDeclaringClass();
        this.fieldName = field.getName();
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
        this.entityClass = field.getDeclaringClass();
        this.fieldName = field.getName();
    }

//    public String getIndexName() {
//        return indexName;
//    }
//
//    public void setIndexName(String indexName) {
//        this.indexName = indexName;
//    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

//    public String getKeyFieldName() {
//        return keyFieldName;
//    }
//
//    public void setKeyFieldName(String keyFieldName) {
//        this.keyFieldName = keyFieldName;
//    }

//    public String getKeyColumnName() {
//        return keyColumnName;
//    }
//
//    public void setKeyColumnName(String keyColumnName) {
//        this.keyColumnName = keyColumnName;
//    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

//    public String getIgnoreColumn() {
//        return ignoreColumn;
//    }
//
//    public void setIgnoreColumn(String ignoreColumn) {
//        this.ignoreColumn = ignoreColumn;
//    }

//    public NameFilter getNameFilter() {
//        return nameFilter;
//    }
//
//    public void setNameFilter(NameFilter nameFilter) {
//        this.nameFilter = nameFilter;
//    }

//    public String getResultMap() {
//        return resultMap;
//    }
//
//    public void setResultMap(String resultMap) {
//        this.resultMap = resultMap;
//    }

    public EsFieldStrategyEnum getFieldStrategy() {
        return fieldStrategy;
    }

//    public List<DocumentFieldInfo> getFieldList() {
//        return fieldList;
//    }
//
//    public void setFieldList(List<DocumentFieldInfo> fieldList) {
//        this.fieldList = fieldList;
//    }
}
