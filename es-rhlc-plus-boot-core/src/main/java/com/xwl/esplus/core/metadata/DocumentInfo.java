package com.xwl.esplus.core.metadata;

import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.xwl.esplus.core.enums.EsKeyTypeEnum;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * es文档信息
 *
 * @author xwl
 * @since 2022/3/11 18:57
 */
public class DocumentInfo {
    /**
     * es索引名称
     */
    private String indexName;
    /**
     * es索引对应的实体类
     */
    private Class<?> entityClass;
    /**
     * 文档主键生成策略类型
     */
    private EsKeyTypeEnum keyType = EsKeyTypeEnum.NONE;
    /**
     * 文档主键数据类型，如String、Integer、Long等
     */
    private Class<?> keyClass;
    /**
     * 文档主键字段
     */
    private Field keyField;
    /**
     * es索引对应的实体类主键字段名称
     */
    private String keyFieldName;
    /**
     * es索引主键字段名称
     */
    private String keyColumnName;
    /**
     * 是否有@EsDocumentId注解
     */
    private Boolean hasIdAnnotation = false;
    /**
     * 文档字段信息列表（不包含主键）
     */
    private List<DocumentFieldInfo> fieldList;
    /**
     * fastjson字段名称过滤器
     */
    private SerializeFilter serializeFilter;
    /**
     * fastjson实体中不存在的字段处理器（处理多余字段，即json中有字段，但是在实体中不存在）
     */
    private ExtraProcessor extraProcessor;
    /**
     * 实体字段->es字段映射
     * key: 实体字段名称 -> value: es字段名称
     */
    private final Map<String, String> fieldColumnMap = new HashMap<>();
    /**
     * es字段->实体字段映射（仅包含使用@EsDocumentField注解指定es字段名称的字段）
     * key: es字段名称 -> value: 实体字段名称
     */
    private final Map<String, String> columnFieldMap = new HashMap<>();
    /**
     * 高亮字段名称->实体字段映射
     * key: es字段名称 -> value: 实体字段名称
     */
    private final Map<String, String> highlightFieldMap = new HashMap<>();
    /**
     * 嵌套对象实体字段->es字段映射
     * key: 嵌套对象字段名称 -> value: es字段名称
     */
    private final Map<Class<?>, Map<String, String>> objectClassMap = new HashMap<>();

    /**
     * 获取id字段名
     *
     * @return id字段名
     */
    public String getId() {
        return keyColumnName;
    }

    /**
     * 获取实体字段映射es中的字段名
     *
     * @param fieldName es索引对应的实体字段名称
     * @return es字段名称
     */
    public String getColumnName(String fieldName) {
        return Optional.ofNullable(fieldColumnMap.get(fieldName)).orElse(fieldName);
    }

    /**
     * 获取需要进行查询的字段列表
     *
     * @param predicate 预言
     * @return 查询字段列表
     */
    public List<String> chooseSelect(Predicate<DocumentFieldInfo> predicate) {
        return fieldList.stream()
                .filter(predicate)
                .map(DocumentFieldInfo::getFieldName)
                .collect(Collectors.toList());
    }

    public DocumentInfo() {
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public EsKeyTypeEnum getKeyType() {
        return keyType;
    }

    public void setKeyType(EsKeyTypeEnum keyType) {
        this.keyType = keyType;
    }

    public Class<?> getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(Class<?> keyClass) {
        this.keyClass = keyClass;
    }

    public Field getKeyField() {
        return keyField;
    }

    public void setKeyField(Field keyField) {
        this.keyField = keyField;
    }

    public String getKeyFieldName() {
        return keyFieldName;
    }

    public void setKeyFieldName(String keyFieldName) {
        this.keyFieldName = keyFieldName;
    }

    public String getKeyColumnName() {
        return keyColumnName;
    }

    public void setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
    }

    public Boolean getHasIdAnnotation() {
        return hasIdAnnotation;
    }

    public void setHasIdAnnotation(Boolean hasIdAnnotation) {
        this.hasIdAnnotation = hasIdAnnotation;
    }

    public List<DocumentFieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<DocumentFieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public SerializeFilter getSerializeFilter() {
        return serializeFilter;
    }

    public void setSerializeFilter(SerializeFilter serializeFilter) {
        this.serializeFilter = serializeFilter;
    }

    public ExtraProcessor getExtraProcessor() {
        return extraProcessor;
    }

    public void setExtraProcessor(ExtraProcessor extraProcessor) {
        this.extraProcessor = extraProcessor;
    }

    public Map<String, String> getFieldColumnMap() {
        return fieldColumnMap;
    }

    public Map<String, String> getColumnFieldMap() {
        return columnFieldMap;
    }

    public Map<String, String> getHighlightFieldMap() {
        return highlightFieldMap;
    }

    public Map<Class<?>, Map<String, String>> getObjectClassMap() {
        return objectClassMap;
    }
}
