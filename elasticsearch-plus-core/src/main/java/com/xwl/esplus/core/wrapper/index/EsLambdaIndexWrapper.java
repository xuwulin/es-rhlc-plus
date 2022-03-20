package com.xwl.esplus.core.wrapper.index;

import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.toolkit.DynamicEnumUtil;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.toolkit.StringUtils;
import com.xwl.esplus.core.wrapper.EsWrapper;
import com.xwl.esplus.core.wrapper.condition.SFunction;
import org.elasticsearch.action.search.SearchRequest;

import java.io.Serializable;
import java.util.*;

/**
 * elasticsearch索引Lambda表达式封装类
 *
 * @author xwl
 * @since 2022/3/11 17:41
 */
@SuppressWarnings("serial")
public class EsLambdaIndexWrapper<T> extends EsWrapper<T> implements Index<EsLambdaIndexWrapper<T>, SFunction<T, ?>>, Serializable {
    /**
     * 索引名称
     */
    protected String indexName;
    /**
     * 别名
     */
    protected String alias;
    /**
     * 分片数
     */
    protected Integer numberOfShards;
    /**
     * 副本数
     */
    protected Integer numberOfReplicas;
    /**
     * 用户手动指定的mapping信息,优先级最高
     */
    protected Map<String, Object> mapping;
    /**
     * 索引相关参数列表
     */
    private List<EsIndexParam> esIndexParamList;
    /**
     * 对应实体
     */
    private final T entity;
    /**
     * 此包装类本身
     */
    protected final EsLambdaIndexWrapper<T> typedThis = this;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public EsLambdaIndexWrapper() {
        this(null);
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     *
     * @param entity 实体
     */
    public EsLambdaIndexWrapper(T entity) {
        this.entity = entity;
        esIndexParamList = new ArrayList<>();
    }

    public String getIndexName() {
        return indexName;
    }

    public String getAlias() {
        return alias;
    }

    public Integer getNumberOfShards() {
        return numberOfShards;
    }

    public Integer getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public Map<String, Object> getMapping() {
        return mapping;
    }

    public List<EsIndexParam> getEsIndexParamList() {
        return esIndexParamList;
    }

    public T getEntity() {
        return entity;
    }

    public EsLambdaIndexWrapper<T> getTypedThis() {
        return typedThis;
    }

    @Override
    protected SearchRequest getSearchRequest() {
        return null;
    }

    @Override
    public EsLambdaIndexWrapper<T> indexName(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            throw new RuntimeException("indexName can not be empty");
        }
        this.indexName = indexName;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> settings(Integer shards, Integer replicas) {
        if (Objects.nonNull(shards)) {
            this.numberOfShards = shards;
        }
        if (Objects.nonNull(replicas)) {
            this.numberOfReplicas = replicas;
        }
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> createAlias(String aliasName) {
        if (StringUtils.isEmpty(indexName)) {
            throw new RuntimeException("indexName can not be empty");
        }
        if (StringUtils.isEmpty(aliasName)) {
            throw new RuntimeException("aliasName can not be empty");
        }
        this.alias = aliasName;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, EsFieldTypeEnum fieldType, EsAnalyzerEnum analyzer, EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setAnalyzer(analyzer);
        esIndexParam.setSearchAnalyzer(searchAnalyzer);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, EsFieldTypeEnum fieldType, boolean index) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setIndex(index);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, EsFieldTypeEnum fieldType, boolean index, Integer ignoreAbove) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, EsFieldTypeEnum fieldType, SFunction<T, ?> copyTo, EsAnalyzerEnum analyzer, EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setCopyTo(FieldUtils.getFieldName(copyTo));
        esIndexParam.setAnalyzer(analyzer);
        esIndexParam.setSearchAnalyzer(searchAnalyzer);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, EsFieldTypeEnum fieldType, boolean index, Integer ignoreAbove, SFunction<T, ?> copyTo, EsAnalyzerEnum analyzer, EsAnalyzerEnum searchAnalyzer, List<EsIndexParam> fields) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        esIndexParam.setCopyTo(FieldUtils.getFieldName(copyTo));
        esIndexParam.setAnalyzer(analyzer);
        esIndexParam.setSearchAnalyzer(searchAnalyzer);
        esIndexParam.setFields(fields);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, EsFieldTypeEnum fieldType, boolean index, Integer ignoreAbove, SFunction<T, ?> copyTo, String analyzer, EsAnalyzerEnum searchAnalyzer, List<EsIndexParam> fields) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(fieldType.getType());
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        Optional.ofNullable(copyTo).ifPresent(copy -> esIndexParam.setCopyTo(FieldUtils.getFieldName(copyTo)));
        // 动态生成EsAnalyzerEnum枚举项
        Optional.ofNullable(analyzer)
                .ifPresent(a -> esIndexParam.setAnalyzer(DynamicEnumUtil.addEnum(EsAnalyzerEnum.class, analyzer.toUpperCase(), new Class<?>[]{}, new Object[]{})));
        esIndexParam.setSearchAnalyzer(searchAnalyzer);
        esIndexParam.setFields(fields);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, List<EsIndexParam> properties) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setProperties(properties);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(Map<String, Object> mapping) {
        this.mapping = mapping;
        return null;
    }
}
