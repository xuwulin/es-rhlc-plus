package com.xwl.esplus.core.condition;

import com.xwl.esplus.core.common.DocumentFieldInfo;
import com.xwl.esplus.core.condition.interfaces.Query;
import com.xwl.esplus.core.condition.interfaces.SFunction;
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
public class LambdaEsQueryWrapper<T> extends AbstractLambdaQueryWrapper<T, LambdaEsQueryWrapper<T>>
        implements Query<LambdaEsQueryWrapper<T>, T, SFunction<T, ?>> {
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
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public LambdaEsQueryWrapper() {
        this(null);
        include = new String[]{};
        exclude = new String[]{};
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     *
     * @param entity 实体
     */
    public LambdaEsQueryWrapper(T entity) {
        super.initNeed();
        super.setEntity(entity);
        include = new String[]{};
        exclude = new String[]{};
    }

    LambdaEsQueryWrapper(T entity, List<EsBaseParam> baseEsParamList, List<EsHighLightParam> highLightParamList,
                         List<EsSortParam> sortParamList, List<EsAggregationParam> aggregationParamList) {
        super.setEntity(entity);
        include = new String[]{};
        exclude = new String[]{};
        this.baseParamList = baseEsParamList;
        this.highLightParamList = highLightParamList;
        this.sortParamList = sortParamList;
        this.aggregationParamList = aggregationParamList;
    }

    @Override
    protected LambdaEsQueryWrapper<T> instance() {
        return new LambdaEsQueryWrapper<>(entity, baseParamList, highLightParamList, sortParamList, aggregationParamList);
    }

    @Override
    public LambdaEsQueryWrapper<T> select(SFunction<T, ?>... columns) {
        if (CollectionUtils.isNotEmpty(columns)) {
            List<String> list = Arrays.stream(columns)
                    .map(FieldUtils::getFieldName)
                    .collect(Collectors.toList());
            include = list.toArray(include);
        }
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> select(Predicate<DocumentFieldInfo> predicate) {
        return select(entityClass, predicate);
    }

    @Override
    public LambdaEsQueryWrapper<T> select(Class<T> entityClass, Predicate<DocumentFieldInfo> predicate) {
        this.entityClass = entityClass;
        List<String> list = DocumentInfoUtils.getEntityInfo(getCheckEntityClass()).chooseSelect(predicate);
        include = list.toArray(include);
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> notSelect(SFunction<T, ?>... columns) {
        if (CollectionUtils.isNotEmpty(columns)) {
            List<String> list = Arrays.stream(columns)
                    .map(FieldUtils::getFieldName)
                    .collect(Collectors.toList());
            exclude = list.toArray(exclude);
        }
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> from(Integer from) {
        this.from = from;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> size(Integer size) {
        this.size = size;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> limit(Integer m) {
        this.size = m;
        return typedThis;
    }

    @Override
    public LambdaEsQueryWrapper<T> limit(Integer m, Integer n) {
        this.from = m;
        this.size = n;
        return typedThis;
    }

    @Override
    protected SearchRequest getSearchRequest() {
        // TODO 待优化
        return null;
    }
}
