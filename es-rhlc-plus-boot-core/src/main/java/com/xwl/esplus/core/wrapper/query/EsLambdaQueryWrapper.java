package com.xwl.esplus.core.wrapper.query;

import com.xwl.esplus.core.metadata.DocumentFieldInfo;
import com.xwl.esplus.core.wrapper.EsAbstractLambdaWrapper;
import com.xwl.esplus.core.wrapper.condition.SFunction;
import com.xwl.esplus.core.param.EsAggregationParam;
import com.xwl.esplus.core.param.EsBaseParam;
import com.xwl.esplus.core.param.EsHighLightParam;
import com.xwl.esplus.core.param.EsSortParam;
import com.xwl.esplus.core.toolkit.CollectionUtils;
import com.xwl.esplus.core.toolkit.DocumentInfoUtils;
import com.xwl.esplus.core.toolkit.FieldUtils;
import org.elasticsearch.action.search.SearchRequest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author xwl
 * @since 2022/3/16 14:38
 */
public class EsLambdaQueryWrapper<T> extends EsAbstractLambdaWrapper<T, EsLambdaQueryWrapper<T>>
        implements Query<EsLambdaQueryWrapper<T>, T, SFunction<T, ?>> {
    /**
     * 查询字段
     */
    protected String[] include;
    /**
     * 不查字段
     */
    protected String[] exclude;
    /**
     * 从第多少条开始查询
     */
    protected Integer from;
    /**
     * 查询多少条记录
     */
    protected Integer size;

    /**
     * must条件转filter
     */
    protected Boolean enableMust2Filter;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public EsLambdaQueryWrapper() {
        this(null);
        include = new String[]{};
        exclude = new String[]{};
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     *
     * @param entity 实体
     */
    public EsLambdaQueryWrapper(T entity) {
        super.initNeed();
        super.setEntity(entity);
        include = new String[]{};
        exclude = new String[]{};
    }

    EsLambdaQueryWrapper(T entity, List<EsBaseParam> baseEsParamList, List<EsHighLightParam> highLightParamList,
                         List<EsSortParam> sortParamList, List<EsAggregationParam<T>> aggregationParamList) {
        super.setEntity(entity);
        include = new String[]{};
        exclude = new String[]{};
        this.baseParamList = baseEsParamList;
        this.highLightParamList = highLightParamList;
        this.sortParamList = sortParamList;
        this.aggregationParamList = aggregationParamList;
    }

    public String[] getInclude() {
        return include;
    }

    public String[] getExclude() {
        return exclude;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getSize() {
        return size;
    }

    public Boolean getEnableMust2Filter() {
        return enableMust2Filter;
    }

    public void setEnableMust2Filter(Boolean enableMust2Filter) {
        this.enableMust2Filter = enableMust2Filter;
    }

    @Override
    protected EsLambdaQueryWrapper<T> instance() {
        return new EsLambdaQueryWrapper<>(entity, baseParamList, highLightParamList, sortParamList, aggregationParamList);
    }

    @Override
    public EsLambdaQueryWrapper<T> select(SFunction<T, ?>... columns) {
        if (CollectionUtils.isNotEmpty(columns)) {
            List<String> list = Arrays.stream(columns)
                    .map(FieldUtils::getFieldName)
                    .collect(Collectors.toList());
            include = list.toArray(include);
        }
        return typedThis;
    }

    @Override
    public EsLambdaQueryWrapper<T> select(Predicate<DocumentFieldInfo> predicate) {
        return select(entityClass, predicate);
    }

    @Override
    public EsLambdaQueryWrapper<T> select(Class<T> entityClass, Predicate<DocumentFieldInfo> predicate) {
        this.entityClass = entityClass;
        List<String> list = DocumentInfoUtils.getDocumentInfo(getCheckEntityClass()).chooseSelect(predicate);
        include = list.toArray(include);
        return typedThis;
    }

    @Override
    public EsLambdaQueryWrapper<T> notSelect(SFunction<T, ?>... columns) {
        if (CollectionUtils.isNotEmpty(columns)) {
            List<String> list = Arrays.stream(columns)
                    .map(FieldUtils::getFieldName)
                    .collect(Collectors.toList());
            exclude = list.toArray(exclude);
        }
        return typedThis;
    }

    @Override
    public EsLambdaQueryWrapper<T> from(Integer from) {
        this.from = from;
        return typedThis;
    }

    @Override
    public EsLambdaQueryWrapper<T> size(Integer size) {
        this.size = size;
        return typedThis;
    }

    @Override
    public EsLambdaQueryWrapper<T> limit(Integer m) {
        this.size = m;
        return typedThis;
    }

    @Override
    public EsLambdaQueryWrapper<T> limit(Integer m, Integer n) {
        this.from = m;
        this.size = n;
        return typedThis;
    }

    @Override
    protected SearchRequest getSearchRequest() {
        // TODO 待优化
        return null;
    }

    @Override
    public EsLambdaQueryWrapper<T> enableMust2Filter(boolean condition, boolean enable) {
        if (condition) {
            this.enableMust2Filter = enable;
        }
        return typedThis;
    }
}
