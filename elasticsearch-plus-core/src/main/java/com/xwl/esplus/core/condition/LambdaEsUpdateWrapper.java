package com.xwl.esplus.core.condition;

import com.xwl.esplus.core.condition.interfaces.SFunction;
import com.xwl.esplus.core.condition.interfaces.Update;
import com.xwl.esplus.core.param.EsBaseParam;
import com.xwl.esplus.core.param.EsUpdateParam;
import com.xwl.esplus.core.toolkit.FieldUtils;
import org.elasticsearch.action.search.SearchRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xwl
 * @since 2022/3/16 15:02
 */
public class LambdaEsUpdateWrapper<T> extends AbstractLambdaUpdateWrapper<T, LambdaEsUpdateWrapper<T>>
        implements Update<LambdaEsUpdateWrapper<T>, SFunction<T, ?>> {
    List<EsUpdateParam> updateParamList;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public LambdaEsUpdateWrapper() {
        this(null);
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     *
     * @param entity 实体
     */
    public LambdaEsUpdateWrapper(T entity) {
        super.initNeed();
        super.setEntity(entity);
        updateParamList = new ArrayList<>();
    }

    LambdaEsUpdateWrapper(T entity, List<EsBaseParam> baseEsParamList, List<EsUpdateParam> updateParamList) {
        super.setEntity(entity);
        this.baseParamList = baseEsParamList;
        this.updateParamList = updateParamList;
    }

    @Override
    public LambdaEsUpdateWrapper<T> set(boolean condition, SFunction<T, ?> column, Object val) {
        if (condition) {
            EsUpdateParam esUpdateParam = new EsUpdateParam();
            esUpdateParam.setField(FieldUtils.getFieldName(column));
            esUpdateParam.setValue(val);
            updateParamList.add(esUpdateParam);
        }
        return typedThis;
    }

    @Override
    protected LambdaEsUpdateWrapper<T> instance() {
        return new LambdaEsUpdateWrapper<>(entity, baseParamList, updateParamList);
    }

    @Override
    protected SearchRequest getSearchRequest() {
        return null;
    }
}
