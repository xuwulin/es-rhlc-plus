package com.xwl.esplus.core.common;

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
     * 字段名
     */
    private String column;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表映射结果集
     */
    private String resultMap;
    /**
     * 表主键ID 属性名
     */
    private String keyProperty;
    /**
     * 表主键ID 字段名
     */
    private String keyColumn;
    /**
     * 字段策略 默认，自判断 null
     */
    private final EsFieldStrategyEnum fieldStrategy;
    /**
     * 表字段信息列表
     */
    private List<DocumentFieldInfo> fieldList;
    /**
     * 标记该字段属于哪个类
     */
    private Class<?> clazz;

    /**
     * 缓存包含主键及字段的 sql select
     */
    private String allSqlSelect;

    /**
     * 缓存主键字段的 sql select
     */
    private String sqlSelect;

    /**
     * 存在 TableField 注解时, 使用的构造函数
     *
     * @param dbConfig      索引配置
     * @param field         字段
     * @param column        字段名
     * @param esDocumentField 文档字段注解
     */
    public DocumentFieldInfo(GlobalConfig.DocumentConfig dbConfig, Field field,
                             String column, EsDocumentField esDocumentField) {
        this.clazz = field.getDeclaringClass();
        this.column = column;
        // 优先使用单个字段注解，否则使用全局配置
        if (esDocumentField.strategy() == EsFieldStrategyEnum.DEFAULT) {
            this.fieldStrategy = dbConfig.getFieldStrategy();
        } else {
            this.fieldStrategy = esDocumentField.strategy();
        }
    }


    /**
     * 不存在 TableField 注解时, 使用的构造函数
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public String getAllSqlSelect() {
        return allSqlSelect;
    }

    public void setAllSqlSelect(String allSqlSelect) {
        this.allSqlSelect = allSqlSelect;
    }

    public String getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(String sqlSelect) {
        this.sqlSelect = sqlSelect;
    }
}
