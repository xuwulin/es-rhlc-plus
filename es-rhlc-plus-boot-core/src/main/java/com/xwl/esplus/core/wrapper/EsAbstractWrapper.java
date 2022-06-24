package com.xwl.esplus.core.wrapper;

import com.xwl.esplus.core.constant.EsAggregationTypeEnum;
import com.xwl.esplus.core.enums.EsAttachTypeEnum;
import com.xwl.esplus.core.enums.EsBaseParamTypeEnum;
import com.xwl.esplus.core.enums.EsQueryTypeEnum;
import com.xwl.esplus.core.param.*;
import com.xwl.esplus.core.toolkit.CollectionUtils;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.wrapper.condition.*;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.geometry.Geometry;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.Assert;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xwl.esplus.core.enums.EsAttachTypeEnum.MUST;
import static com.xwl.esplus.core.enums.EsAttachTypeEnum.MUST_NOT;
import static com.xwl.esplus.core.enums.EsBaseParamTypeEnum.*;
import static com.xwl.esplus.core.enums.EsQueryTypeEnum.*;

/**
 * 抽象Lambda表达式父类
 * T, R, Children 都是泛型标识：可以随便写任意标识号，标识指定的泛型的类型
 * Children extends EsAbstractWrapper<T, R, Children> 表示：Children类型只能是EsAbstractWrapper或EsAbstractWrapper的子类
 *
 * @author xwl
 * @since 2022/3/15 18:31
 */
public abstract class EsAbstractWrapper<T, R, Children extends EsAbstractWrapper<T, R, Children>> extends EsWrapper<T>
        implements Compare<Children, R>, Nested<Children, Children>, Join<Children>, Func<Children, R>, Geo<Children, R> {
    protected final Children typedThis = (Children) this;

    /**
     * 基础查询参数列表
     */
    protected List<EsBaseParam> baseParamList;
    /**
     * 高亮查询参数列表
     */
    protected List<EsHighLightParam> highLightParamList;
    /**
     * 排序查询参数列表
     */
    protected List<EsSortParam> sortParamList;
    /**
     * 聚合查询参数列表
     */
    protected List<EsAggregationParam> aggregationParamList;
    /**
     * geo相关参数
     */
    protected EsGeoParam geoParam;
    /**
     * 用户自定义的排序规则
     */
    protected List<SortBuilder<?>> sortBuilders;
    /**
     * 得分排序规则
     */
    protected SortOrder sortOrder;
    /**
     * 排序参数列表
     */
    protected List<EsOrderByParam> orderByParams;
    /**
     * 实体对象
     */
    protected T entity;
    /**
     * 实体类型
     */
    protected Class<T> entityClass;

    public Children setEntity(T entity) {
        this.entity = entity;
        this.initEntityClass();
        return typedThis;
    }

    protected void initEntityClass() {
        if (this.entityClass == null && this.entity != null) {
            this.entityClass = (Class<T>) entity.getClass();
        }
    }

    protected Class<T> getCheckEntityClass() {
        Assert.notNull(entityClass, "entityClass must not null, please set entity before use this method!");
        return entityClass;
    }

    /**
     * 必要的初始化
     */
    protected final void initNeed() {
        baseParamList = new ArrayList<>();
        highLightParamList = new ArrayList<>();
        sortParamList = new ArrayList<>();
        aggregationParamList = new ArrayList<>();
    }

    public Children getTypedThis() {
        return typedThis;
    }

    public List<EsBaseParam> getBaseParamList() {
        return baseParamList;
    }

    public List<EsHighLightParam> getHighLightParamList() {
        return highLightParamList;
    }

    public List<EsSortParam> getSortParamList() {
        return sortParamList;
    }

    public List<EsAggregationParam> getAggregationParamList() {
        return aggregationParamList;
    }

    public EsGeoParam getGeoParam() {
        return geoParam;
    }

    public List<SortBuilder<?>> getSortBuilders() {
        return sortBuilders;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public List<EsOrderByParam> getOrderByParams() {
        return orderByParams;
    }

    public T getEntity() {
        return entity;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public Children eq(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, TERM_QUERY, MUST, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children ne(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, TERM_QUERY, MUST_NOT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children reg(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, REGEXP_QUERY, MUST, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children and(boolean condition, Function<Children, Children> func) {
        return doIt(condition, func, AND_LEFT_BRACKET, AND_RIGHT_BRACKET);
    }

    @Override
    public Children or(boolean condition, Function<Children, Children> func) {
        return doIt(condition, func, OR_LEFT_BRACKET, OR_RIGHT_BRACKET);
    }

    @Override
    public Children match(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, MATCH_QUERY, MUST, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children notMatch(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, MATCH_QUERY, MUST_NOT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children gt(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, RANGE_QUERY, EsAttachTypeEnum.GT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children ge(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, RANGE_QUERY, EsAttachTypeEnum.GE, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children lt(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, RANGE_QUERY, EsAttachTypeEnum.LT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children le(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, RANGE_QUERY, EsAttachTypeEnum.LE, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2, Float boost) {
        return doIt(condition, EsAttachTypeEnum.BETWEEN, FieldUtils.getFieldName(column), val1, val2, boost);
    }

    @Override
    public Children notBetween(boolean condition, R column, Object val1, Object val2, Float boost) {
        return doIt(condition, EsAttachTypeEnum.NOT_BETWEEN, FieldUtils.getFieldName(column), val1, val2, boost);
    }

    @Override
    public Children or(boolean condition) {
        if (condition) {
            EsBaseParam esBaseParam = new EsBaseParam();
            esBaseParam.setType(EsBaseParamTypeEnum.OR_ALL.getType());
            baseParamList.add(esBaseParam);
        }
        return typedThis;
    }

    @Override
    public Children like(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, WILDCARD_QUERY, MUST, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children notLike(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, WILDCARD_QUERY, MUST_NOT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, WILDCARD_QUERY, EsAttachTypeEnum.LIKE_LEFT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val, Float boost) {
        return doIt(condition, WILDCARD_QUERY, EsAttachTypeEnum.LIKE_RIGHT, FieldUtils.getFieldName(column), val, boost);
    }

    @Override
    public Children highLight(boolean condition, String preTag, String postTag, R column) {
        if (condition) {
            String fieldName = FieldUtils.getFieldName(column);
            List<String> fields = new ArrayList<>();
            fields.add(fieldName);
            highLightParamList.add(new EsHighLightParam(preTag, postTag, fields));
        }
        return typedThis;
    }

    @Override
    public Children highLight(boolean condition, String preTag, String postTag, R... columns) {
        if (condition) {
            List<String> fields = Arrays.stream(columns).map(FieldUtils::getFieldName).collect(Collectors.toList());
            highLightParamList.add(new EsHighLightParam(preTag, postTag, fields));
        }
        return typedThis;
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R... columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return typedThis;
        }

        if (condition) {
            List<String> fields = Arrays.stream(columns).map(FieldUtils::getFieldName).collect(Collectors.toList());
            sortParamList.add(new EsSortParam(isAsc, fields));
        }
        return typedThis;
    }

    @Override
    public Children orderBy(boolean condition, List<EsOrderByParam> orderByParams) {
        if (CollectionUtils.isNotEmpty(orderByParams)) {
            this.orderByParams = orderByParams;
        }
        return typedThis;
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll, Float boost) {
        if (CollectionUtils.isEmpty(coll)) {
            return typedThis;
        }
        return doIt(condition, EsAttachTypeEnum.IN, FieldUtils.getFieldName(column), new ArrayList<>(coll), boost);
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll, Float boost) {
        if (CollectionUtils.isEmpty(coll)) {
            return typedThis;
        }
        return doIt(condition, EsAttachTypeEnum.NOT_IN, FieldUtils.getFieldName(column), new ArrayList<>(coll), boost);
    }

    @Override
    public Children isNull(boolean condition, R column, Float boost) {
        return doIt(condition, EsAttachTypeEnum.NOT_EXISTS, FieldUtils.getFieldName(column), boost);
    }

    @Override
    public Children isNotNull(boolean condition, R column, Float boost) {
        return doIt(condition, EsAttachTypeEnum.EXISTS, FieldUtils.getFieldName(column), boost);
    }

    @Override
    public Children groupBy(boolean condition, Integer size, R... columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return typedThis;
        }
        Arrays.stream(columns).forEach(column -> {
            String returnName = FieldUtils.getFieldName(column);
            doIt(condition, EsAggregationTypeEnum.TERMS, returnName, size, column);
        });
        return typedThis;
    }

    @Override
    public Children termsAggregation(boolean condition, String returnName, Integer size, R column) {
        return doIt(condition, EsAggregationTypeEnum.TERMS, returnName, size, column);
    }

    @Override
    public Children dateHistogram(boolean condition, String returnName, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, R column) {
        return doIt(condition, returnName, interval, format, minDocCount, extendedBounds, timeZone, column);
    }

    @Override
    public Children avg(boolean condition, String returnName, R column) {
        return doIt(condition, EsAggregationTypeEnum.AVG, returnName, null, column);
    }

    @Override
    public Children min(boolean condition, String returnName, R column) {
        return doIt(condition, EsAggregationTypeEnum.MIN, returnName, null, column);
    }

    @Override
    public Children max(boolean condition, String returnName, R column) {
        return doIt(condition, EsAggregationTypeEnum.MAX, returnName, null, column);
    }

    @Override
    public Children sum(boolean condition, String returnName, R column) {
        return doIt(condition, EsAggregationTypeEnum.SUM, returnName, null, column);
    }

    @Override
    public Children stats(boolean condition, String returnName, R column) {
        return doIt(condition, EsAggregationTypeEnum.STATS, returnName, null, column);
    }

    @Override
    public Children geoBoundingBox(boolean condition, R column, GeoPoint topLeft, GeoPoint bottomRight, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), topLeft, bottomRight, boost, true);
    }

    @Override
    public Children notInGeoBoundingBox(boolean condition, R column, GeoPoint topLeft, GeoPoint bottomRight, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), topLeft, bottomRight, boost, false);
    }

    @Override
    public Children geoDistance(boolean condition, R column, Double distance, DistanceUnit distanceUnit, GeoPoint centralGeoPoint, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), distance, distanceUnit, centralGeoPoint, boost, true);
    }

    @Override
    public Children notInGeoDistance(boolean condition, R column, Double distance, DistanceUnit distanceUnit, GeoPoint centralGeoPoint, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), distance, distanceUnit, centralGeoPoint, boost, false);
    }

    @Override
    public Children geoDistance(boolean condition, R column, String distance, GeoPoint centralGeoPoint, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), distance, centralGeoPoint, boost, true);
    }

    @Override
    public Children notInGeoDistance(boolean condition, R column, String distance, GeoPoint centralGeoPoint, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), distance, centralGeoPoint, boost, false);
    }

    @Override
    public Children geoPolygon(boolean condition, R column, List<GeoPoint> geoPoints, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), geoPoints, boost, true);
    }

    @Override
    public Children notInGeoPolygon(boolean condition, R column, Collection<GeoPoint> geoPoints, Float boost) {
        List<GeoPoint> geoPointList = new ArrayList<>(geoPoints);
        return doIt(condition, FieldUtils.getFieldName(column), geoPointList, boost, false);
    }

    @Override
    public Children geoShape(boolean condition, R column, String indexedShapeId, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), indexedShapeId, boost, true);
    }

    @Override
    public Children notInGeoShape(boolean condition, R column, String indexedShapeId, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), indexedShapeId, boost, false);
    }

    @Override
    public Children geoShape(boolean condition, R column, Geometry geometry, ShapeRelation shapeRelation, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), geometry, shapeRelation, boost, true);
    }

    @Override
    public Children notInGeoShape(boolean condition, R column, Geometry geometry, ShapeRelation shapeRelation, Float boost) {
        return doIt(condition, FieldUtils.getFieldName(column), geometry, shapeRelation, boost, false);
    }

    @Override
    public Children sort(boolean condition, List<SortBuilder<?>> sortBuilders) {
        if (CollectionUtils.isEmpty(sortBuilders)) {
            return typedThis;
        }
        if (condition) {
            this.sortBuilders = sortBuilders;
        }
        return typedThis;
    }

    @Override
    public Children sortByScore(boolean condition, SortOrder sortOrder) {
        if (condition) {
            this.sortOrder = sortOrder;
        }
        return typedThis;
    }

    /**
     * 子类返回一个自己的新对象
     *
     * @return 泛型
     */
    protected abstract Children instance();

    /**
     * 封装查询参数 聚合类
     *
     * @param condition           条件
     * @param aggregationTypeEnum 聚合类型
     * @param returnName          返回的聚合字段名称
     * @param size                返回的聚合字段大小
     * @param column              列
     * @return 泛型
     */
    private Children doIt(boolean condition, EsAggregationTypeEnum aggregationTypeEnum, String returnName, Integer size, R column) {
        if (condition) {
            EsAggregationParam aggregationParam = new EsAggregationParam();
            aggregationParam.setName(returnName);
            aggregationParam.setField(FieldUtils.getFieldName(column));
            aggregationParam.setSize(size);
            aggregationParam.setAggregationType(aggregationTypeEnum);
            aggregationParamList.add(aggregationParam);
        }
        return typedThis;
    }

    /**
     * 封装查询参数 聚合类
     *
     * @param condition      条件
     * @param returnName     返回的聚合字段名称
     * @param interval       按什么时间段聚合
     * @param format         日期格式
     * @param minDocCount    为空的话则填充值
     * @param extendedBounds 强制返回的日期区间；如果不加这个就只返回有数据的区间
     * @param timeZone       设置时区
     * @param column         列
     * @return 泛型
     */
    private Children doIt(boolean condition, String returnName, DateHistogramInterval interval, String format, long minDocCount, ExtendedBounds extendedBounds, ZoneId timeZone, R column) {
        if (condition) {
            EsAggregationParam aggregationParam = new EsAggregationParam();
            aggregationParam.setName(returnName);
            aggregationParam.setField(FieldUtils.getFieldName(column));
            aggregationParam.setInterval(interval);
            aggregationParam.setFormat(format);
            aggregationParam.setMinDocCount(minDocCount);
            aggregationParam.setExtendedBounds(extendedBounds);
            aggregationParam.setTimeZone(timeZone);
            aggregationParam.setAggregationType(EsAggregationTypeEnum.DATE_HISTOGRAM);
            aggregationParamList.add(aggregationParam);
        }
        return typedThis;
    }

    /**
     * 封装查询参数(含AND,OR这种连接操作)
     *
     * @param condition 条件
     * @param func      函数
     * @param open      左括号
     * @param close     右括号
     * @return 泛型
     */
    private Children doIt(boolean condition, Function<Children, Children> func, EsBaseParamTypeEnum open, EsBaseParamTypeEnum close) {
        if (condition) {
            EsBaseParam left = new EsBaseParam();
            left.setType(open.getType());
            baseParamList.add(left);
            func.apply(instance());
            EsBaseParam right = new EsBaseParam();
            right.setType(close.getType());
            baseParamList.add(right);
        }
        return typedThis;
    }

    /**
     * 封装查询参数(普通情况,不带括号)
     *
     * @param condition      条件
     * @param attachTypeEnum 连接类型
     * @param field          字段
     * @param values         值列表
     * @param boost          权重
     * @return 泛型
     */
    private Children doIt(boolean condition, EsAttachTypeEnum attachTypeEnum, String field, List<Object> values, Float boost) {
        if (condition) {
            EsBaseParam baseEsParam = new EsBaseParam();
            EsBaseParam.FieldValueModel model = new EsBaseParam.FieldValueModel();
            model.setField(field);
            model.setValues(values);
            model.setBoost(boost);
            model.setEsQueryType(TERMS_QUERY.getType());
            model.setOriginalAttachType(attachTypeEnum.getType());

            setModel(baseEsParam, model, attachTypeEnum);
            baseParamList.add(baseEsParam);
        }
        return typedThis;
    }

    /**
     * 封装查询参数(普通情况,不带括号)
     *
     * @param condition      条件
     * @param queryTypeEnum  查询类型
     * @param attachTypeEnum 连接类型
     * @param field          字段
     * @param val            值
     * @param boost          权重
     * @return 泛型
     */
    private Children doIt(boolean condition, EsQueryTypeEnum queryTypeEnum, EsAttachTypeEnum attachTypeEnum, String field, Object val, Float boost) {
        if (condition) {
            EsBaseParam baseEsParam = new EsBaseParam();
            EsBaseParam.FieldValueModel model = new EsBaseParam.FieldValueModel();
            model.setField(field);
            model.setValue(val);
            model.setBoost(boost);
            model.setEsQueryType(queryTypeEnum.getType());
            model.setOriginalAttachType(attachTypeEnum.getType());

            setModel(baseEsParam, model, attachTypeEnum);
            baseParamList.add(baseEsParam);
        }
        return typedThis;
    }

    /**
     * 封装查询参数针对is Null / not null 这类无值操作
     *
     * @param condition      条件
     * @param attachTypeEnum 连接类型
     * @param field          字段
     * @param boost          权重
     * @return 泛型
     */
    private Children doIt(boolean condition, EsAttachTypeEnum attachTypeEnum, String field, Float boost) {
        if (condition) {
            EsBaseParam baseEsParam = new EsBaseParam();
            EsBaseParam.FieldValueModel model = new EsBaseParam.FieldValueModel();
            model.setField(field);
            model.setBoost(boost);
            model.setEsQueryType(EXISTS_QUERY.getType());
            model.setOriginalAttachType(attachTypeEnum.getType());

            setModel(baseEsParam, model, attachTypeEnum);
            baseParamList.add(baseEsParam);
        }
        return typedThis;
    }

    /**
     * 仅针对between的情况
     *
     * @param condition      条件
     * @param attachTypeEnum 连接类型
     * @param field          字段
     * @param left           左区间
     * @param right          右区间
     * @param boost          权重
     * @return 泛型
     */
    private Children doIt(boolean condition, EsAttachTypeEnum attachTypeEnum, String field, Object left, Object right, Float boost) {
        if (condition) {
            EsBaseParam baseEsParam = new EsBaseParam();
            EsBaseParam.FieldValueModel model = new EsBaseParam.FieldValueModel();
            model.setField(field);
            model.setLeftValue(left);
            model.setRightValue(right);
            model.setBoost(boost);
            model.setEsQueryType(INTERVAL_QUERY.getType());
            model.setOriginalAttachType(attachTypeEnum.getType());

            setModel(baseEsParam, model, attachTypeEnum);
            baseParamList.add(baseEsParam);
        }
        return typedThis;
    }

    /**
     * geoBoundingBox
     *
     * @param condition   条件
     * @param field       字段名
     * @param topLeft     左上点坐标
     * @param bottomRight 右下点坐标
     * @param boost       权重值
     * @param isIn        是否在范围内
     * @return 泛型
     */
    private Children doIt(boolean condition, String field, GeoPoint topLeft, GeoPoint bottomRight, Float boost, boolean isIn) {
        if (condition) {
            EsGeoParam esGeoParam = new EsGeoParam();
            esGeoParam.setField(field);
            esGeoParam.setTopLeft(topLeft);
            esGeoParam.setBottomRight(bottomRight);
            esGeoParam.setBoost(boost);
            esGeoParam.setIn(isIn);
            this.geoParam = esGeoParam;
        }
        return typedThis;
    }

    /**
     * geoDistance 双精度距离类型
     *
     * @param condition       条件
     * @param fieldName       字段名
     * @param distance        距离
     * @param distanceUnit    距离单位
     * @param centralGeoPoint 中心点
     * @param boost           权重
     * @param isIn            是否在范围内
     * @return 泛型
     */
    private Children doIt(boolean condition, String fieldName, Double distance, DistanceUnit distanceUnit, GeoPoint centralGeoPoint, Float boost, boolean isIn) {
        if (condition) {
            EsGeoParam esGeoParam = new EsGeoParam();
            esGeoParam.setField(fieldName);
            esGeoParam.setBoost(boost);
            esGeoParam.setDistance(distance);
            esGeoParam.setDistanceUnit(distanceUnit);
            esGeoParam.setCentralGeoPoint(centralGeoPoint);
            esGeoParam.setIn(isIn);
            this.geoParam = esGeoParam;
        }
        return typedThis;
    }

    /**
     * geoDistance 字符串距离类型
     *
     * @param condition       条件
     * @param fieldName       字段名
     * @param distance        距离 字符串
     * @param centralGeoPoint 中心点
     * @param boost           权重值
     * @param isIn            是否在范围内
     * @return 泛型
     */
    private Children doIt(boolean condition, String fieldName, String distance, GeoPoint centralGeoPoint, Float boost, boolean isIn) {
        if (condition) {
            EsGeoParam esGeoParam = new EsGeoParam();
            esGeoParam.setField(fieldName);
            esGeoParam.setBoost(boost);
            esGeoParam.setDistanceStr(distance);
            esGeoParam.setCentralGeoPoint(centralGeoPoint);
            esGeoParam.setIn(isIn);
            this.geoParam = esGeoParam;
        }
        return typedThis;
    }

    /**
     * geoPolygon
     *
     * @param condition 条件
     * @param fieldName 字段名
     * @param geoPoints 多边形点坐标列表
     * @param boost     权重值
     * @param isIn      是否在范围内
     * @return 泛型
     */
    private Children doIt(boolean condition, String fieldName, List<GeoPoint> geoPoints, Float boost, boolean isIn) {
        if (condition) {
            EsGeoParam esGeoParam = new EsGeoParam();
            esGeoParam.setField(fieldName);
            esGeoParam.setBoost(boost);
            esGeoParam.setGeoPoints(geoPoints);
            esGeoParam.setIn(isIn);
            this.geoParam = esGeoParam;
        }
        return typedThis;
    }

    /**
     * 图形 已知图形已被索引的情况
     *
     * @param condition      条件
     * @param fieldName      字段名
     * @param indexedShapeId 已被索引的图形索引id
     * @param boost          权重值
     * @param isIn           是否在范围内
     * @return 泛型
     */
    private Children doIt(boolean condition, String fieldName, String indexedShapeId, Float boost, boolean isIn) {
        if (condition) {
            EsGeoParam esGeoParam = new EsGeoParam();
            esGeoParam.setField(fieldName);
            esGeoParam.setBoost(boost);
            esGeoParam.setIndexedShapeId(indexedShapeId);
            esGeoParam.setIn(isIn);
            this.geoParam = esGeoParam;
        }
        return typedThis;
    }

    /**
     * 图形 GeoShape
     *
     * @param condition 条件
     * @param fieldName 字段名
     * @param geometry  图形
     * @param boost     权重值
     * @param isIn      是否在范围内
     * @return 泛型
     */
    private Children doIt(boolean condition, String fieldName, Geometry geometry, ShapeRelation shapeRelation, Float boost, boolean isIn) {
        if (condition) {
            EsGeoParam esGeoParam = new EsGeoParam();
            esGeoParam.setField(fieldName);
            esGeoParam.setBoost(boost);
            esGeoParam.setGeometry(geometry);
            esGeoParam.setShapeRelation(shapeRelation);
            esGeoParam.setIn(isIn);
            this.geoParam = esGeoParam;
        }
        return typedThis;
    }

    /**
     * 设置查询模型类型
     *
     * @param baseEsParam    基础参数
     * @param model          字段&值模型
     * @param attachTypeEnum 连接类型
     */
    private void setModel(EsBaseParam baseEsParam, EsBaseParam.FieldValueModel model, EsAttachTypeEnum attachTypeEnum) {
        switch (attachTypeEnum) {
            case MUST:
                baseEsParam.getMustList().add(model);
                break;
            case FILTER:
                baseEsParam.getFilterList().add(model);
                break;
            case SHOULD:
                baseEsParam.getShouldList().add(model);
                break;
            case MUST_NOT:
                baseEsParam.getMustNotList().add(model);
                break;
            case GT:
                baseEsParam.getGtList().add(model);
                break;
            case LT:
                baseEsParam.getLtList().add(model);
                break;
            case GE:
                baseEsParam.getGeList().add(model);
                break;
            case LE:
                baseEsParam.getLeList().add(model);
                break;
            case IN:
                baseEsParam.getInList().add(model);
                break;
            case NOT_IN:
                baseEsParam.getNotInList().add(model);
                break;
            case EXISTS:
                baseEsParam.getNotNullList().add(model);
                break;
            case NOT_EXISTS:
                baseEsParam.getIsNullList().add(model);
                break;
            case BETWEEN:
                baseEsParam.getBetweenList().add(model);
                break;
            case NOT_BETWEEN:
                baseEsParam.getNotBetweenList().add(model);
            case LIKE_LEFT:
                baseEsParam.getLikeLeftList().add(model);
                break;
            case LIKE_RIGHT:
                baseEsParam.getLikeRightList().add(model);
                break;
            default:
                throw new UnsupportedOperationException("不支持的连接类型,请参见EsAttachTypeEnum");
        }
    }
}
