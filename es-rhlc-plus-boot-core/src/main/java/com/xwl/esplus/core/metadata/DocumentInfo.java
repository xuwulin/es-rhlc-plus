package com.xwl.esplus.core.metadata;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.xwl.esplus.core.enums.EsIdTypeEnum;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 文档信息
 *
 * @author xwl
 * @since 2022/3/11 18:57
 */
public class DocumentInfo {
    /**
     * 文档主键ID类型
     */
    private EsIdTypeEnum idType = EsIdTypeEnum.NONE;
    /**
     * id数据类型，如Long.class String.class
     */
    private Class<?> idClass;
    /**
     * 索引名称
     */
    private String indexName;
    /**
     * 文档映射结果集
     */
//    private String resultMap;
    /**
     * 主键字段
     */
    private Field keyField;
    /**
     * 文档主键属性名
     */
    private String keyProperty;
    /**
     * 文档主键字段名（es实际字段名称）
     */
    private String keyColumn;
    /**
     * 文档字段信息列表（不包含主键）
     */
    private List<DocumentFieldInfo> fieldList;
    /**
     * es对应的实体类
     */
    private Class<?> clazz;
    /**
     * 是否有@EsDocumentId注解
     */
    private Boolean hasIdAnnotation = false;
    /**
     * fastjson字段名称过滤器
     */
    private SerializeFilter serializeFilter;
    /**
     * fastjson字段命名策略
     */
    private PropertyNamingStrategy propertyNamingStrategy;
    /**
     * fastjson实体中不存在的字段处理器
     */
    private ExtraProcessor extraProcessor;
    /**
     * 高亮返回结果
     */
    private Map<String, String> highlightFieldMap = new HashMap<>();
    /**
     * key: 实体字段 -> value: es实际字段映射
     */
    private final Map<String, String> mappingColumnMap = new HashMap<>();
    /**
     * key: es实际字段映射 -> value: 实体字段 (仅包含被重命名字段)
     */
    private final Map<String, String> columnMappingMap = new HashMap<>();

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

    /**
     * 获取实体字段映射es中的字段名
     *
     * @param column 字段名
     * @return es中的字段名
     */
    public String getMappingColumn(String column) {
        return Optional.ofNullable(mappingColumnMap.get(column)).orElse(column);
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

//    public String getResultMap() {
//        return resultMap;
//    }
//
//    public void setResultMap(String resultMap) {
//        this.resultMap = resultMap;
//    }

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

    public SerializeFilter getSerializeFilter() {
        return serializeFilter;
    }

    public void setSerializeFilter(SerializeFilter serializeFilter) {
        this.serializeFilter = serializeFilter;
    }

    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return propertyNamingStrategy;
    }

    public void setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
        this.propertyNamingStrategy = propertyNamingStrategy;
    }

    public ExtraProcessor getExtraProcessor() {
        return extraProcessor;
    }

    public void setExtraProcessor(ExtraProcessor extraProcessor) {
        this.extraProcessor = extraProcessor;
    }

    public Map<String, String> getHighlightFieldMap() {
        return highlightFieldMap;
    }

    public void setHighlightFieldMap(Map<String, String> highlightFieldMap) {
        this.highlightFieldMap = highlightFieldMap;
    }

    public Map<String, String> getMappingColumnMap() {
        return mappingColumnMap;
    }

    public Map<String, String> getColumnMappingMap() {
        return columnMappingMap;
    }
}
