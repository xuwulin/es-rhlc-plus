package com.xwl.esplus.core.wrapper;

import com.xwl.esplus.core.wrapper.index.Index;
import com.xwl.esplus.core.wrapper.condition.SFunction;
import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.toolkit.FieldUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    protected String aliasName;
    /**
     * 分片数
     */
    protected Integer shardsNum;
    /**
     * 副本数
     */
    protected Integer replicasNum;
    /**
     * 用户手动指定的mapping信息,优先级最高
     */
    protected Map<String, Object> mapping;
    /**
     * 索引相关参数列表
     */
    List<EsIndexParam> esIndexParamList;
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
            this.shardsNum = shards;
        }
        if (Objects.nonNull(replicas)) {
            this.replicasNum = replicas;
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
        this.aliasName = aliasName;
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
    public EsLambdaIndexWrapper<T> mapping(Map<String, Object> mapping) {
        this.mapping = mapping;
        return null;
    }
}
