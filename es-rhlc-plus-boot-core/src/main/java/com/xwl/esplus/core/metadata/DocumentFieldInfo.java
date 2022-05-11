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
     * es索引对应的实体类
     */
    private Class<?> entityClass;
    /**
     * es索引对应的实体类字段名
     */
    private String fieldName;
    /**
     * es索引字段名
     */
    private String columnName;
    /**
     * 字段策略 默认，自判断 null
     */
    private final EsFieldStrategyEnum fieldStrategy;

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
        this.entityClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.fieldStrategy = dbConfig.getFieldStrategy();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

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

    public EsFieldStrategyEnum getFieldStrategy() {
        return fieldStrategy;
    }
}
