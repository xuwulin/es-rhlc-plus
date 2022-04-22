package com.xwl.esplus.core.wrapper.index;

import com.alibaba.fastjson.JSONObject;
import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.param.EsIndexSettingParam;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.toolkit.StringUtils;
import com.xwl.esplus.core.wrapper.EsWrapper;
import com.xwl.esplus.core.wrapper.condition.SFunction;
import org.elasticsearch.action.search.SearchRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * elasticsearch索引Lambda表达式封装类
 *
 * @author xwl
 * @since 2022/3/11 17:41
 */
@SuppressWarnings("serial")
public class EsLambdaIndexWrapper<T>
        extends EsWrapper<T>
        implements Index<EsLambdaIndexWrapper<T>, SFunction<T, ?>>, Serializable {
    /**
     * 索引名称
     */
    protected String indexName;
    /**
     * 别名
     */
    protected String alias;
    /**
     * setting参数
     */
    protected EsIndexSettingParam setting;
    /**
     * 用户手动指定的mapping信息，优先级最高
     */
    protected Map<String, Object> mapping;
    /**
     * 索引mapping参数列表，通过参数构建mapping
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

    public EsIndexSettingParam getSetting() {
        return setting;
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
            throw ExceptionUtils.epe("indexName can not be empty");
        }
        this.indexName = indexName;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> alias(String alias) {
        this.alias = alias;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> settings(EsIndexSettingParam settingParam) {
        this.setting = settingParam;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> settings(Integer shards, Integer replicas) {
        EsIndexSettingParam esIndexSettingParam = new EsIndexSettingParam();
        esIndexSettingParam.setNumberOfShards(shards);
        esIndexSettingParam.setNumberOfReplicas(replicas);
        this.setting = esIndexSettingParam;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> settings(Integer shards, Integer replicas, String analysis) {
        EsIndexSettingParam esIndexSettingParam = new EsIndexSettingParam();
        esIndexSettingParam.setNumberOfShards(shards);
        esIndexSettingParam.setNumberOfReplicas(replicas);
        Map map = JSONObject.parseObject(analysis, Map.class);
        esIndexSettingParam.setAnalysis(map);
        this.setting = esIndexSettingParam;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> settings(Integer shards, Integer replicas, Map<String, Object> analysis) {
        EsIndexSettingParam esIndexSettingParam = new EsIndexSettingParam();
        esIndexSettingParam.setNumberOfShards(shards);
        esIndexSettingParam.setNumberOfReplicas(replicas);
        esIndexSettingParam.setAnalysis(analysis);
        this.setting = esIndexSettingParam;
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column, String dateFormat) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        esIndexParam.setFieldType(EsFieldTypeEnum.DATE.getType());
        esIndexParam.setFormat(dateFormat);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column,
                                           EsFieldTypeEnum fieldType,
                                           EsAnalyzerEnum analyzer,
                                           EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        Optional.ofNullable(analyzer)
                .ifPresent(analyze -> esIndexParam.setAnalyzer(analyze.getType()));
        Optional.ofNullable(searchAnalyzer)
                .ifPresent(search -> esIndexParam.setSearchAnalyzer(search.getType()));
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column,
                                           EsFieldTypeEnum fieldType,
                                           boolean index) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        esIndexParam.setIndex(index);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column,
                                           EsFieldTypeEnum fieldType,
                                           boolean index,
                                           Integer ignoreAbove) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column,
                                           EsFieldTypeEnum fieldType,
                                           SFunction<T, ?> copyTo,
                                           EsAnalyzerEnum analyzer,
                                           EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        esIndexParam.setCopyTo(FieldUtils.getFieldName(copyTo));
        Optional.ofNullable(analyzer)
                .ifPresent(analyze -> esIndexParam.setAnalyzer(analyze.getType()));
        Optional.ofNullable(searchAnalyzer)
                .ifPresent(search -> esIndexParam.setSearchAnalyzer(search.getType()));
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column,
                                           EsFieldTypeEnum fieldType,
                                           boolean index,
                                           Integer ignoreAbove,
                                           SFunction<T, ?> copyTo,
                                           EsAnalyzerEnum analyzer,
                                           EsAnalyzerEnum searchAnalyzer,
                                           List<EsIndexParam> fields) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        Optional.ofNullable(copyTo)
                .ifPresent(copy -> esIndexParam.setCopyTo(FieldUtils.getFieldName(copy)));
        Optional.ofNullable(analyzer)
                .ifPresent(analyze -> esIndexParam.setAnalyzer(analyze.getType()));
        Optional.ofNullable(searchAnalyzer)
                .ifPresent(search -> esIndexParam.setSearchAnalyzer(search.getType()));
        esIndexParam.setFields(fields);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(SFunction<T, ?> column,
                                           EsFieldTypeEnum fieldType,
                                           boolean index,
                                           Integer ignoreAbove,
                                           SFunction<T, ?> copyTo,
                                           String analyzer,
                                           EsAnalyzerEnum searchAnalyzer,
                                           List<EsIndexParam> fields) {
        String fieldName = FieldUtils.getFieldName(column);
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(fieldName);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        Optional.ofNullable(copyTo)
                .ifPresent(copy -> esIndexParam.setCopyTo(FieldUtils.getFieldName(copy)));
        esIndexParam.setAnalyzer(analyzer);
        Optional.ofNullable(searchAnalyzer)
                .ifPresent(search -> esIndexParam.setSearchAnalyzer(search.getType()));
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
    public EsLambdaIndexWrapper<T> mapping(String column,
                                           EsFieldTypeEnum fieldType,
                                           Boolean index,
                                           Integer ignoreAbove,
                                           String copyTo,
                                           String analyzer,
                                           String searchAnalyzer,
                                           List<EsIndexParam> fields) {
        EsIndexParam esIndexParam = new EsIndexParam();
        esIndexParam.setFieldName(column);
        Optional.ofNullable(fieldType)
                .ifPresent(type -> esIndexParam.setFieldType(type.getType()));
        esIndexParam.setIndex(index);
        esIndexParam.setIgnoreAbove(ignoreAbove);
        esIndexParam.setCopyTo(copyTo);
        esIndexParam.setAnalyzer(analyzer);
        esIndexParam.setSearchAnalyzer(searchAnalyzer);
        esIndexParam.setFields(fields);
        esIndexParamList.add(esIndexParam);
        return typedThis;
    }

    @Override
    public EsLambdaIndexWrapper<T> mapping(Map<String, Object> mapping) {
        this.mapping = mapping;
        return typedThis;
    }
}
