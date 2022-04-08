package com.xwl.esplus.core.metadata;

import com.xwl.esplus.core.enums.EsIdTypeEnum;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 文档信息（相当于实体类信息）
 *
 * @author xwl
 * @since 2022/3/11 18:57
 */
public class DocumentInfo {
    /**
     * 表主键ID 类型
     */
    private EsIdTypeEnum idType = EsIdTypeEnum.NONE;
    /**
     * id数据类型 如Long.class String.class
     */
    private Class<?> idClass;
    /**
     * 索引名称
     */
    private String indexName;
    /**
     * 表映射结果集
     */
    private String resultMap;
    /**
     * 主键字段
     */
    private Field keyField;
    /**
     * 表主键ID 属性名
     */
    private String keyProperty;
    /**
     * 表主键ID 字段名
     */
    private String keyColumn;
    /**
     * 表字段信息列表
     */
    private List<DocumentFieldInfo> fieldList;
    /**
     * 标记该字段属于哪个类
     */
    private Class<?> clazz;
    /**
     * 是否有id注解
     */
    private Boolean hasIdAnnotation;
    /**
     * 高亮
     */
    private Map<String, String> highlightFieldMap = new HashMap<>();

    /**
     * 获取需要进行查询的字段列表
     *
     * @param predicate 预言
     * @return 查询字段列表
     */
    public List<String> chooseSelect(Predicate<DocumentFieldInfo> predicate) {
        return fieldList.stream()
                .filter(predicate)
                .map(DocumentFieldInfo::getColumn)
                .collect(Collectors.toList());
    }

    /**
     * 获取id字段名
     *
     * @return id字段名
     */
    public String getId() {
        return keyColumn;
    }

    public DocumentInfo() {
    }

    public EsIdTypeEnum getIdType() {
        return idType;
    }

    public void setIdType(EsIdTypeEnum idType) {
        this.idType = idType;
    }

    public Class<?> getIdClass() {
        return idClass;
    }

    public void setIdClass(Class<?> idClass) {
        this.idClass = idClass;
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

    public Field getKeyField() {
        return keyField;
    }

    public void setKeyField(Field keyField) {
        this.keyField = keyField;
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

    public Boolean getHasIdAnnotation() {
        return hasIdAnnotation;
    }

    public void setHasIdAnnotation(Boolean hasIdAnnotation) {
        this.hasIdAnnotation = hasIdAnnotation;
    }

    public Map<String, String> getHighlightFieldMap() {
        return highlightFieldMap;
    }

    public void setHighlightFieldMap(Map<String, String> highlightFieldMap) {
        this.highlightFieldMap = highlightFieldMap;
    }
}
