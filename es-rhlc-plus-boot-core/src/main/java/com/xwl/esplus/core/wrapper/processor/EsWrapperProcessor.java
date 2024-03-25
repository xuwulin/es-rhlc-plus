package com.xwl.esplus.core.wrapper.processor;

import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.enums.EsAttachTypeEnum;
import com.xwl.esplus.core.metadata.DocumentInfo;
import com.xwl.esplus.core.param.EsAggregationParam;
import com.xwl.esplus.core.param.EsBaseParam;
import com.xwl.esplus.core.param.EsGeoParam;
import com.xwl.esplus.core.param.EsSortParam;
import com.xwl.esplus.core.toolkit.*;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.xwl.esplus.core.constant.EsConstants.DEFAULT_SIZE;
import static com.xwl.esplus.core.enums.EsAttachTypeEnum.*;
import static com.xwl.esplus.core.enums.EsBaseParamTypeEnum.*;

/**
 * wrapper处理类
 *
 * @author xwl
 * @since 2022/3/16 15:06
 */
public class EsWrapperProcessor {
    private EsWrapperProcessor() {
    }

    /**
     * 构建es查询参数
     *
     * @param wrapper     查询条件
     * @param entityClass es索引对应的实体类
     * @return SearchSourceBuilder
     */
    public static SearchSourceBuilder buildSearchSourceBuilder(EsLambdaQueryWrapper<?> wrapper, Class<?> entityClass) {
        SearchSourceBuilder searchSourceBuilder = initSearchSourceBuilder(wrapper, entityClass);
        // 构建BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = buildBoolQueryBuilder(wrapper.getBaseParamList(), wrapper.getEnableMust2Filter(), entityClass);
        // 初始化geo相关: BoundingBox,geoDistance,geoPolygon,geoShape
        Optional.ofNullable(wrapper.getGeoParam()).ifPresent(esGeoParam -> setGeoQuery(esGeoParam, boolQueryBuilder, entityClass));
        // 设置参数
        searchSourceBuilder.query(boolQueryBuilder);
        return searchSourceBuilder;
    }

    /**
     * 设置Geo相关查询参数 geoBoundingBox, geoDistance, geoPolygon, geoShape
     *
     * @param geoParam         geo参数
     * @param boolQueryBuilder boolQuery参数建造者
     */
    private static void setGeoQuery(EsGeoParam geoParam, BoolQueryBuilder boolQueryBuilder, Class<?> entityClass) {
        // 获取配置信息
        Map<String, String> columnMappingMap = DocumentInfoUtils.getDocumentInfo(entityClass).getColumnFieldMap();
        GlobalConfig.DocumentConfig documentConfig = getGlobalConfig().getDocumentConfig();

        // 使用实际字段名称覆盖实体类字段名称
        String realField = getRealField(geoParam.getField(), columnMappingMap, documentConfig);
        geoParam.setField(realField);

        GeoBoundingBoxQueryBuilder geoBoundingBox = buildGeoBoundingBoxQueryBuilder(geoParam);
        doGeoSet(geoParam.isIn(), geoBoundingBox, boolQueryBuilder);

        GeoDistanceQueryBuilder geoDistance = buildGeoDistanceQueryBuilder(geoParam);
        doGeoSet(geoParam.isIn(), geoDistance, boolQueryBuilder);

        GeoPolygonQueryBuilder geoPolygon = buildGeoPolygonQueryBuilder(geoParam);
        doGeoSet(geoParam.isIn(), geoPolygon, boolQueryBuilder);

        GeoShapeQueryBuilder geoShape = buildGeoShapeQueryBuilder(geoParam);
        doGeoSet(geoParam.isIn(), geoShape, boolQueryBuilder);
    }

    /**
     * 根据查询是否在指定范围内设置geo查询过滤条件
     *
     * @param isIn
     * @param queryBuilder
     * @param boolQueryBuilder
     */
    private static void doGeoSet(Boolean isIn, QueryBuilder queryBuilder, BoolQueryBuilder boolQueryBuilder) {
        Optional.ofNullable(queryBuilder)
                .ifPresent(present -> {
                    if (isIn) {
                        boolQueryBuilder.filter(present);
                    } else {
                        boolQueryBuilder.mustNot(present);
                    }
                });
    }

    /**
     * 初始化SearchSourceBuilder
     *
     * @param wrapper 查询条件
     * @return SearchSourceBuilder
     */
    private static SearchSourceBuilder initSearchSourceBuilder(EsLambdaQueryWrapper<?> wrapper, Class<?> entityClass) {
        // 获取自定义字段map
        Map<String, String> columnMappingMap = DocumentInfoUtils.getDocumentInfo(entityClass).getFieldColumnMap();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 查询字段或排除字段
        setFetchSource(wrapper, columnMappingMap, searchSourceBuilder);

        // from & size（默认10000条）
        Optional.ofNullable(wrapper.getFrom()).ifPresent(searchSourceBuilder::from);
        Optional.ofNullable(wrapper.getSize()).ifPresent(searchSourceBuilder::size);

        // 高亮
        setHighLight(wrapper, columnMappingMap, searchSourceBuilder);

        // 设置用户指定的各种排序规则
        setSort(wrapper, columnMappingMap, searchSourceBuilder);

        // 聚合
        setAggregations(wrapper, columnMappingMap, searchSourceBuilder);

        // 大于一万条, trackTotalHists自动开启
        if (searchSourceBuilder.size() > DEFAULT_SIZE) {
            searchSourceBuilder.trackTotalHits(true);
        } else {
            // 根据全局配置决定是否开启
            searchSourceBuilder.trackTotalHits(GlobalConfigCache.getGlobalConfig().getDocumentConfig().isEnableTrackTotalHits());
        }

        return searchSourceBuilder;
    }

    /**
     * 设置查询/不查询字段列表
     *
     * @param wrapper             参数包装类
     * @param columnMappingMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setFetchSource(EsLambdaQueryWrapper<?> wrapper,
                                       Map<String, String> columnMappingMap,
                                       SearchSourceBuilder searchSourceBuilder) {
        if (CollectionUtils.isEmpty(wrapper.getInclude()) && CollectionUtils.isEmpty(wrapper.getExclude())) {
            return;
        }
        // 获取配置
        GlobalConfig.DocumentConfig documentConfig = getGlobalConfig().getDocumentConfig();
        String[] includes = getRealFields(wrapper.getInclude(), columnMappingMap, documentConfig);
        String[] excludes = getRealFields(wrapper.getExclude(), columnMappingMap, documentConfig);
        searchSourceBuilder.fetchSource(includes, excludes);
    }

    /**
     * 获取实际字段名数组
     *
     * @param fields           原字段名数组
     * @param columnMappingMap 字段映射关系map
     * @param documentConfig   配置
     * @return 实际字段数组
     */
    private static String[] getRealFields(String[] fields,
                                          Map<String, String> columnMappingMap,
                                          GlobalConfig.DocumentConfig documentConfig) {
        return Arrays.stream(fields)
                .map(field -> getRealField(field, columnMappingMap, documentConfig))
                .collect(Collectors.toList())
                .toArray(new String[]{});
    }

    /**
     * 获取实际字段名
     *
     * @param field            原字段名
     * @param columnMappingMap 字段映射关系map
     * @param documentConfig   配置
     * @return 实际字段名
     */
    private static String getRealField(String field,
                                       Map<String, String> columnMappingMap,
                                       GlobalConfig.DocumentConfig documentConfig) {
        String customField = columnMappingMap.get(field);
        if (Objects.nonNull(customField)) {
            return customField;
        } else {
            if (documentConfig.isMapUnderscoreToCamelCase()) {
                return StringUtils.camelToUnderline(field);
            } else {
                return field;
            }
        }
    }

    /**
     * 获取全局配置
     *
     * @return 全局配置
     */
    private static GlobalConfig getGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDocumentConfig(new GlobalConfig.DocumentConfig());
        return Optional.ofNullable(GlobalConfigCache.getGlobalConfig())
                .orElse(globalConfig);
    }

    /**
     * 设置高亮参数
     *
     * @param wrapper             参数包装类
     * @param columnMappingMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setHighLight(EsLambdaQueryWrapper<?> wrapper,
                                     Map<String, String> columnMappingMap,
                                     SearchSourceBuilder searchSourceBuilder) {
        // 获取配置
        GlobalConfig.DocumentConfig documentConfig = getGlobalConfig().getDocumentConfig();

        // 设置高亮字段
        if (CollectionUtils.isNotEmpty(wrapper.getHighLightParamList())) {
            wrapper.getHighLightParamList().forEach(highLightParam -> {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highLightParam.getFields().forEach(field -> {
                    String customField = columnMappingMap.get(field);
                    if (Objects.nonNull(customField)) {
                        highlightBuilder.field(customField);
                    } else {
                        if (documentConfig.isMapUnderscoreToCamelCase()) {
                            highlightBuilder.field(StringUtils.camelToUnderline(field));
                        } else {
                            highlightBuilder.field(field);
                        }
                    }
                });
                highlightBuilder.preTags(highLightParam.getPreTag());
                highlightBuilder.postTags(highLightParam.getPostTag());
                searchSourceBuilder.highlighter(highlightBuilder);
            });
        }
    }

    /**
     * 构建BoolQueryBuilder
     *
     * @param wrapper     参数包装类
     * @param entityClass es索引对应的实体类
     * @return BoolQueryBuilder
     */
    public static BoolQueryBuilder buildBoolQueryBuilder(EsLambdaQueryWrapper<?> wrapper, Class<?> entityClass) {
        List<EsBaseParam> baseParamList = wrapper.getBaseParamList();
        Boolean enableMust2Filter = wrapper.getEnableMust2Filter();
        BoolQueryBuilder boolQueryBuilder = buildBoolQueryBuilder(baseParamList, enableMust2Filter, entityClass);
        Optional.ofNullable(wrapper.getGeoParam()).ifPresent(esGeoParam -> setGeoQuery(esGeoParam, boolQueryBuilder, entityClass));
        return boolQueryBuilder;
    }

    /**
     * 构建BoolQueryBuilder
     *
     * @param baseParamList 基础参数列表
     * @param entityClass   es索引对应的实体类
     * @return BoolQueryBuilder
     */
//    public static BoolQueryBuilder buildBoolQueryBuilder(List<EsBaseParam> baseParamList, Class<?> entityClass) {
//        DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(entityClass);
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        // 用于连接and，or条件内的多个查询条件，包装成boolQuery
//        BoolQueryBuilder inner = null;
//        // 是否有外层or
//        boolean hasOuterOr = false;
//        for (int i = 0; i < baseParamList.size(); i++) {
//            EsBaseParam baseEsParam = baseParamList.get(i);
//            if (Objects.equals(AND_LEFT_BRACKET.getType(), baseEsParam.getType()) || Objects.equals(OR_LEFT_BRACKET.getType(), baseEsParam.getType())) {
//                // 说明有and或者or
//                for (int j = i + 1; j < baseParamList.size(); j++) {
//                    if (Objects.equals(baseParamList.get(j).getType(), OR_ALL.getType())) {
//                        // 说明左括号内出现了内层or查询条件
//                        for (int k = i + 1; k < j; k++) {
//                            // 内层or只会出现在中间，此处将内层or之前的查询条件类型进行处理
//                            EsBaseParam.setUp(baseParamList.get(k));
//                        }
//                    }
//                }
//                inner = QueryBuilders.boolQuery();
//            }
//
//            // 此处处理所有内外层or后面的查询条件类型
//            if (Objects.equals(baseEsParam.getType(), OR_ALL.getType())) {
//                hasOuterOr = true;
//            }
//            if (hasOuterOr) {
//                EsBaseParam.setUp(baseEsParam);
//            }
//
//            // 处理括号中and和or的最终连接类型 and->must，or->should
//            if (Objects.equals(AND_RIGHT_BRACKET.getType(), baseEsParam.getType())) {
//                if (Objects.nonNull(inner)) {
//                    boolQueryBuilder.must(inner);
//                }
//                inner = null;
//            }
//            if (Objects.equals(OR_RIGHT_BRACKET.getType(), baseEsParam.getType())) {
//                if (Objects.nonNull(inner)) {
//                    boolQueryBuilder.should(inner);
//                }
//                inner = null;
//            }
//
//            // 添加字段名称,值,查询类型等
//            if (Objects.isNull(inner)) {
//                addQuery(baseEsParam, boolQueryBuilder, documentInfo);
//            } else {
//                addQuery(baseEsParam, inner, documentInfo);
//            }
//        }
//        return boolQueryBuilder;
//    }

    /**
     * 构建BoolQueryBuilder
     *
     * @param baseParamList     基础参数列表
     * @param enableMust2Filter 是否开启must条件转filter
     * @param entityClass       es索引对应的实体类
     * @return BoolQueryBuilder
     */
    public static BoolQueryBuilder buildBoolQueryBuilder(List<EsBaseParam> baseParamList,
                                                         Boolean enableMust2Filter,
                                                         Class<?> entityClass) {
        DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(entityClass);
        GlobalConfig.DocumentConfig documentConfig = GlobalConfigCache.getGlobalConfig().getDocumentConfig();

        // 获取内层or和内外层or总数,用于处理 是否有外层or:全部重置; 如果仅内层OR,只重置内层.
        OrCount orCount = getOrCount(baseParamList);
        // 根节点
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 用于连接and,or条件内的多个查询条件,包装成boolQuery
        BoolQueryBuilder inner = null;
        // 正式封装参数
        int start = 0;
        int end = 0;
        int remainSetUp = orCount.getOrInnerCount();
        boolean hasSetUp = false;
        for (int i = 0; i < baseParamList.size(); i++) {
            EsBaseParam esBaseParam = baseParamList.get(i);
            if (orCount.getOrAllCount() > orCount.getOrInnerCount()) {
                // 存在外层or 统统重置
                EsBaseParam.setUp(esBaseParam);
            } else {
                if (!hasSetUp) {
                    // 处理or在内层的情况,仅重置括号中的内容
                    for (int j = i; j < baseParamList.size(); j++) {
                        EsBaseParam andOr = baseParamList.get(j);
                        if (AND_LEFT_BRACKET.getType().equals(andOr.getType()) || OR_LEFT_BRACKET.getType().equals(andOr.getType())) {
                            // 找到了and/or的开始标志
                            start = j;
                        }

                        if (AND_RIGHT_BRACKET.getType().equals(andOr.getType()) || OR_RIGHT_BRACKET.getType().equals(andOr.getType())) {
                            // 找到了and/or的结束标志
                            end = j;
                        }
                        if (remainSetUp > 0 && end > start) {
                            // 重置内层or
                            remainSetUp--;
                            for (int k = start; k < end; k++) {
                                EsBaseParam.setUp(baseParamList.get(k));
                                hasSetUp = true;
                            }
                        }
                    }
                }
            }

            boolean hasLogicOperator = AND_LEFT_BRACKET.getType().equals(esBaseParam.getType())
                    || OR_LEFT_BRACKET.getType().equals(esBaseParam.getType());
            if (hasLogicOperator) {
                // 说明有and或者or 需要将括号中的内容置入新的boolQuery
                inner = QueryBuilders.boolQuery();
            }

            // 处理括号中and和or的最终连接类型 and->must, or->should
            if (Objects.equals(AND_RIGHT_BRACKET.getType(), esBaseParam.getType())) {
                boolQueryBuilder.must(inner);
                inner = null;
            }
            if (Objects.equals(OR_RIGHT_BRACKET.getType(), esBaseParam.getType())) {
                boolQueryBuilder.should(inner);
                inner = null;
            }

            // 添加字段名称,值,查询类型等
            Optional.ofNullable(enableMust2Filter).ifPresent(esBaseParam::setEnableMust2Filter);
            if (Objects.isNull(inner)) {
                addQuery(esBaseParam, boolQueryBuilder, documentInfo, documentConfig);
            } else {
                addQuery(esBaseParam, inner, documentInfo, documentConfig);
            }
        }
        return boolQueryBuilder;
    }

    /**
     * 获取内层or和内外层or总数
     *
     * @param esBaseParamList 参数列表
     * @return 内外侧or总数信息
     */
    private static OrCount getOrCount(List<EsBaseParam> esBaseParamList) {
        OrCount orCount = new OrCount();
        int start;
        int end = 0;
        int orAllCount = 0;
        int orInnerCount = 0;
        for (int i = 0; i < esBaseParamList.size(); i++) {
            EsBaseParam esBaseParam = esBaseParamList.get(i);
            if (OR_ALL.getType().equals(esBaseParam.getType())) {
                orAllCount++;
            }
            boolean hasLogicOperator = AND_LEFT_BRACKET.getType().equals(esBaseParam.getType())
                    || OR_LEFT_BRACKET.getType().equals(esBaseParam.getType());
            if (hasLogicOperator) {
                start = i;
                for (int j = i; j < esBaseParamList.size(); j++) {
                    EsBaseParam andOr = esBaseParamList.get(j);
                    if (AND_RIGHT_BRACKET.getType().equals(andOr.getType()) || OR_RIGHT_BRACKET.getType().equals(andOr.getType())) {
                        end = j;
                    }

                    if (start < end) {
                        for (int k = start; k < end; k++) {
                            if (OR_ALL.getType().equals(esBaseParamList.get(k).getType())) {
                                orInnerCount++;
                            }
                        }
                        break;
                    }
                }
            }
        }

        orCount.setOrAllCount(orAllCount);
        orCount.setOrInnerCount(orInnerCount);
        return orCount;
    }

    private static void addQuery(EsBaseParam esBaseParam, BoolQueryBuilder boolQueryBuilder, DocumentInfo documentInfo,
                                 GlobalConfig.DocumentConfig documentConfig) {
        // 获取must是否转filter 默认不转,以wrapper中指定的优先级最高,全局次之
        boolean enableMust2Filter = Objects.isNull(esBaseParam.getEnableMust2Filter()) ? documentConfig.isEnableMust2Filter() :
                esBaseParam.getEnableMust2Filter();

        esBaseParam.getMustList().forEach(fieldValueModel -> EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                MUST.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        // 多字段情形
        esBaseParam.getMustMultiFieldList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, fieldValueModel.getEsQueryType(), MUST.getType(),
                        fieldValueModel.getOriginalAttachType(), enableMust2Filter, FieldUtils.getRealFields(fieldValueModel.getFields(),
                                documentInfo.getFieldColumnMap()), fieldValueModel.getValue(), fieldValueModel.getExt(),
                        fieldValueModel.getMinimumShouldMatch(), fieldValueModel.getBoost()));

        esBaseParam.getFilterList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, FILTER.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getShouldList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, SHOULD.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        // 多字段情形
        esBaseParam.getShouldMultiFieldList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, fieldValueModel.getEsQueryType(),
                        SHOULD.getType(), fieldValueModel.getOriginalAttachType(), enableMust2Filter,
                        FieldUtils.getRealFields(fieldValueModel.getFields(), documentInfo.getFieldColumnMap()), fieldValueModel.getValue(),
                        fieldValueModel.getExt(), fieldValueModel.getMinimumShouldMatch(), fieldValueModel.getBoost()));

        esBaseParam.getMustNotList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, MUST_NOT.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getGtList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, GT.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getLtList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, LT.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getGeList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, GE.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getLeList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, LE.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getBetweenList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, BETWEEN.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getNotBetweenList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, NOT_BETWEEN.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getInList().forEach(fieldValueModel -> EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                IN.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getNotInList().forEach(fieldValueModel -> EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                NOT_IN.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getIsNullList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, NOT_EXISTS.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getNotNullList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, EXISTS.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getLikeLeftList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, LIKE_LEFT.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));

        esBaseParam.getLikeRightList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder, LIKE_RIGHT.getType(), enableMust2Filter, fieldValueModel, documentInfo, documentConfig));
    }

    static AggregationBuilder convertAggregationBuilder(EsAggregationParam aggregationParam,
                                                        Map<String, String> columnMappingMap,
                                                        GlobalConfig.DocumentConfig documentConfig) {
        String realField = getRealField(aggregationParam.getField(), columnMappingMap, documentConfig);
        AggregationBuilder aggregationBuilder;
        switch (aggregationParam.getAggregationType()) {
            case AVG:
                aggregationBuilder = AggregationBuilders
                        .avg(aggregationParam.getName())
                        .field(realField);
                break;
            case MIN:
                aggregationBuilder = AggregationBuilders
                        .min(aggregationParam.getName())
                        .field(realField);
                break;
            case MAX:
                aggregationBuilder = AggregationBuilders
                        .max(aggregationParam.getName())
                        .field(realField);
                break;
            case SUM:
                aggregationBuilder = AggregationBuilders
                        .sum(aggregationParam.getName())
                        .field(realField);
                break;
            case STATS:
                aggregationBuilder = AggregationBuilders
                        .stats(aggregationParam.getName())
                        .field(realField);
                break;
            case TERMS:
                aggregationBuilder = AggregationBuilders
                        .terms(aggregationParam.getName())
                        .field(realField)
                        .size(aggregationParam.getSize() == null ? EsConstants.TEN : aggregationParam.getSize());
                break;
            case CARDINALITY:
                aggregationBuilder = AggregationBuilders.cardinality(aggregationParam.getName()).field(realField).precisionThreshold(aggregationParam.getPrecisionThreshold());
                break;
            case TOP_HITS:
                TopHitsAggregationBuilder topHitsAggregationBuilder = AggregationBuilders.topHits(aggregationParam.getName())
                        .size(aggregationParam.getSize())
                        .fetchSource(aggregationParam.getIncludes(), null);
                if (StringUtils.isNotBlank(aggregationParam.getHighLight())) {
                    String field = aggregationParam.getHighLight();
                    HighlightBuilder highlightBuilder = new HighlightBuilder();
                    String customField = columnMappingMap.get(field);
                    if (Objects.nonNull(customField)) {
                        highlightBuilder.field(customField);
                    } else {
                        if (documentConfig.isMapUnderscoreToCamelCase()) {
                            highlightBuilder.field(StringUtils.camelToUnderline(field));
                        } else {
                            highlightBuilder.field(field);
                        }
                    }
                    highlightBuilder.preTags(EsConstants.HIGH_LIGHT_PRE_TAG);
                    highlightBuilder.postTags(EsConstants.HIGH_LIGHT_POST_TAG);
                    topHitsAggregationBuilder.highlighter(highlightBuilder);
                }
                List<EsSortParam> sortParamList = aggregationParam.getSortParamList();
                if (Objects.nonNull(sortParamList) && sortParamList.size() > 0) {
                    sortParamList.forEach(sortParam -> {
                        SortOrder sortOrder = sortParam.getAsc() ? SortOrder.ASC : SortOrder.DESC;
                        sortParam.getFields().forEach(field -> {
                            FieldSortBuilder fieldSortBuilder;
                            String customField = columnMappingMap.get(field);
                            if (Objects.nonNull(customField)) {
                                fieldSortBuilder = new FieldSortBuilder(customField).order(sortOrder);
                            } else {
                                if (documentConfig.isMapUnderscoreToCamelCase()) {
                                    fieldSortBuilder = new FieldSortBuilder(StringUtils.camelToUnderline(field)).order(sortOrder);
                                } else {
                                    fieldSortBuilder = new FieldSortBuilder(field).order(sortOrder);
                                }
                            }
                            topHitsAggregationBuilder.sort(fieldSortBuilder);
                        });
                    });
                }
                aggregationBuilder = topHitsAggregationBuilder;
                break;
            case DATE_HISTOGRAM:
                aggregationBuilder = AggregationBuilders
                        .dateHistogram(aggregationParam.getName())
                        .field(realField)
                        .calendarInterval(aggregationParam.getInterval())
                        .format(aggregationParam.getFormat())
                        .minDocCount(aggregationParam.getMinDocCount())
                        .extendedBounds(aggregationParam.getExtendedBounds())
                        .timeZone(aggregationParam.getTimeZone());
                break;
            default:
                throw new UnsupportedOperationException("不支持的聚合类型，聚合类型参见EsAggregationTypeEnum");
        }
        List<? extends EsAggregationParam<?>> subAggregations = aggregationParam.getSubAggregations();
        // 添加子聚合
        if (Objects.nonNull(subAggregations) && subAggregations.size() > 0) {
            subAggregations.forEach(x -> {
                if (Objects.nonNull(x)) {
                    aggregationBuilder.subAggregation(convertAggregationBuilder(x, columnMappingMap, documentConfig));
                }
            });
        }
        return aggregationBuilder;
    }

    /**
     * 设置聚合参数
     *
     * @param wrapper             参数包装类
     * @param columnMappingMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setAggregations(EsLambdaQueryWrapper<?> wrapper,
                                        Map<String, String> columnMappingMap,
                                        SearchSourceBuilder searchSourceBuilder) {
        List<? extends EsAggregationParam<?>> aggregationParamList = wrapper.getAggregationParamList();
        if (CollectionUtils.isEmpty(aggregationParamList)) {
            return;
        }

        // 获取配置
        GlobalConfig.DocumentConfig documentConfig = getGlobalConfig().getDocumentConfig();

        // 封装聚合参数
        aggregationParamList.forEach(aggregationParam -> {
            AggregationBuilder aggregationBuilder = convertAggregationBuilder(aggregationParam, columnMappingMap, documentConfig);
            if (Objects.nonNull(aggregationBuilder)) {
                searchSourceBuilder.aggregation(aggregationBuilder);
            }
        });
    }


    /**
     * 构建GeoBoundingBoxQueryBuilder
     *
     * @param geoParam Geo相关参数
     * @return GeoBoundingBoxQueryBuilder
     */
    private static GeoBoundingBoxQueryBuilder buildGeoBoundingBoxQueryBuilder(EsGeoParam geoParam) {
        // 参数校验
        boolean invalidParam = Objects.isNull(geoParam) || (Objects.isNull(geoParam.getTopLeft()) || Objects.isNull(geoParam.getBottomRight()));
        if (invalidParam) {
            return null;
        }
        GeoBoundingBoxQueryBuilder builder = QueryBuilders.geoBoundingBoxQuery(geoParam.getField());
        Optional.ofNullable(geoParam.getBoost()).ifPresent(builder::boost);
        builder.setCorners(geoParam.getTopLeft(), geoParam.getBottomRight());
        return builder;
    }

    /**
     * 构建GeoDistanceQueryBuilder
     *
     * @param geoParam Geo相关参数
     * @return GeoDistanceQueryBuilder
     */
    private static GeoDistanceQueryBuilder buildGeoDistanceQueryBuilder(EsGeoParam geoParam) {
        // 参数校验
        boolean invalidParam = Objects.isNull(geoParam) || (Objects.isNull(geoParam.getDistanceStr()) && Objects.isNull(geoParam.getDistance()));
        if (invalidParam) {
            return null;
        }
        GeoDistanceQueryBuilder builder = QueryBuilders.geoDistanceQuery(geoParam.getField());
        Optional.ofNullable(geoParam.getBoost()).ifPresent(builder::boost);
        // 距离来源: 双精度类型+单位或字符串类型
        Optional.ofNullable(geoParam.getDistanceStr()).ifPresent(builder::distance);
        Optional.ofNullable(geoParam.getDistance()).ifPresent(distance -> builder.distance(distance, geoParam.getDistanceUnit()));
        Optional.ofNullable(geoParam.getCentralGeoPoint()).ifPresent(builder::point);
        return builder;
    }

    /**
     * 构建GeoPolygonQueryBuilder
     *
     * @param geoParam Geo相关参数
     * @return GeoPolygonQueryBuilder
     */
    private static GeoPolygonQueryBuilder buildGeoPolygonQueryBuilder(EsGeoParam geoParam) {
        // 参数校验
        boolean invalidParam = Objects.isNull(geoParam) || CollectionUtils.isEmpty(geoParam.getGeoPoints());
        if (invalidParam) {
            return null;
        }
        GeoPolygonQueryBuilder builder = QueryBuilders.geoPolygonQuery(geoParam.getField(), geoParam.getGeoPoints());
        Optional.ofNullable(geoParam.getBoost()).ifPresent(builder::boost);
        return builder;
    }

    /**
     * 构建GeoShapeQueryBuilder
     *
     * @param geoParam Geo相关参数
     * @return GeoShapeQueryBuilder
     */
    private static GeoShapeQueryBuilder buildGeoShapeQueryBuilder(EsGeoParam geoParam) {
        // 参数校验
        boolean invalidParam = Objects.isNull(geoParam) || (Objects.isNull(geoParam.getIndexedShapeId()) && Objects.isNull(geoParam.getGeometry()));
        if (invalidParam) {
            return null;
        }
        GeoShapeQueryBuilder builder = null;
        try {
            builder = QueryBuilders.geoShapeQuery(geoParam.getField(), geoParam.getGeometry());
        } catch (IOException e) {
            throw ExceptionUtils.epe("buildGeoShapeQueryBuilder exception: {}", e, e.getMessage());
        }
        Optional.ofNullable(geoParam.getShapeRelation()).ifPresent(builder::relation);
        Optional.ofNullable(geoParam.getBoost()).ifPresent(builder::boost);
        return builder;
    }

    /**
     * 添加进参数容器
     *
     * @param baseEsParam      基础参数
     * @param boolQueryBuilder es boolQueryBuilder
     * @param documentInfo     文档信息8
     */
    private static void addQuery(EsBaseParam baseEsParam, BoolQueryBuilder boolQueryBuilder, DocumentInfo documentInfo) {
        baseEsParam.getMustList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        MUST.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getFilterList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        FILTER.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getShouldList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        SHOULD.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getMustNotList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.MUST_NOT.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getGtList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.GT.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getLtList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.LT.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getGeList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.GE.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getLeList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.LE.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getBetweenList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.BETWEEN.getType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getLeftValue(),
                        fieldValueModel.getRightValue(),
                        fieldValueModel.getBoost()));
        baseEsParam.getNotBetweenList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.NOT_BETWEEN.getType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getLeftValue(),
                        fieldValueModel.getRightValue(),
                        fieldValueModel.getBoost()));

        baseEsParam.getInList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.IN.getType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValues(),
                        fieldValueModel.getBoost()));

        baseEsParam.getNotInList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.NOT_IN.getType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValues(),
                        fieldValueModel.getBoost()));

        baseEsParam.getIsNullList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.NOT_EXISTS.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        Optional.empty(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getNotNullList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.EXISTS.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        Optional.empty(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getLikeLeftList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.LIKE_LEFT.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));

        baseEsParam.getLikeRightList().forEach(fieldValueModel ->
                EsQueryTypeUtils.addQueryByType(boolQueryBuilder,
                        fieldValueModel.getEsQueryType(),
                        EsAttachTypeEnum.LIKE_RIGHT.getType(),
                        fieldValueModel.getOriginalAttachType(),
                        documentInfo.getColumnName(fieldValueModel.getField()),
                        fieldValueModel.getValue(),
                        fieldValueModel.getBoost(),
                        fieldValueModel.getSlop()));
    }

    /**
     * 查询字段中是否包含id
     *
     * @param idField 字段
     * @param wrapper 条件
     * @return 是否包含的布尔值
     */
    public static boolean includeId(String idField, EsLambdaQueryWrapper<?> wrapper) {
        if (CollectionUtils.isEmpty(wrapper.getInclude()) && CollectionUtils.isEmpty(wrapper.getExclude())) {
            // 未设置, 默认返回
            return true;
        } else if (CollectionUtils.isNotEmpty(wrapper.getInclude()) && Arrays.asList(wrapper.getInclude()).contains(idField)) {
            return true;
        } else {
            return CollectionUtils.isNotEmpty(wrapper.getExclude()) && !Arrays.asList(wrapper.getExclude()).contains(idField);
        }
    }

    /**
     * 设置排序参数
     *
     * @param wrapper             参数包装类
     * @param columnMappingMap    字段映射map
     * @param searchSourceBuilder 查询参数建造者
     */
    private static void setSort(EsLambdaQueryWrapper<?> wrapper,
                                Map<String, String> columnMappingMap,
                                SearchSourceBuilder searchSourceBuilder) {
        // 获取配置
        GlobalConfig.DocumentConfig documentConfig = getGlobalConfig().getDocumentConfig();
        // 设置排序字段
        if (CollectionUtils.isNotEmpty(wrapper.getSortParamList())) {
            wrapper.getSortParamList().forEach(sortParam -> {
                SortOrder sortOrder = sortParam.getAsc() ? SortOrder.ASC : SortOrder.DESC;
                sortParam.getFields().forEach(field -> {
                    FieldSortBuilder fieldSortBuilder;
                    String customField = columnMappingMap.get(field);
                    if (Objects.nonNull(customField)) {
                        fieldSortBuilder = new FieldSortBuilder(customField).order(sortOrder);
                    } else {
                        if (documentConfig.isMapUnderscoreToCamelCase()) {
                            fieldSortBuilder = new FieldSortBuilder(StringUtils.camelToUnderline(field)).order(sortOrder);
                        } else {
                            fieldSortBuilder = new FieldSortBuilder(field).order(sortOrder);
                        }
                    }
                    searchSourceBuilder.sort(fieldSortBuilder);
                });
            });
        }

        // 设置以String形式指定的排序字段及规则
        if (CollectionUtils.isNotEmpty(wrapper.getOrderByParams())) {
            wrapper.getOrderByParams().forEach(orderByParam -> {
                // 设置排序字段
                FieldSortBuilder fieldSortBuilder;
                String customField = columnMappingMap.get(orderByParam.getOrder());
                if (Objects.nonNull(customField)) {
                    fieldSortBuilder = new FieldSortBuilder(customField);
                } else {
                    if (documentConfig.isMapUnderscoreToCamelCase()) {
                        fieldSortBuilder = new FieldSortBuilder(StringUtils.camelToUnderline(orderByParam.getOrder()));
                    } else {
                        fieldSortBuilder = new FieldSortBuilder(orderByParam.getOrder());
                    }
                }

                // 设置排序规则
                if (SortOrder.ASC.toString().equalsIgnoreCase(orderByParam.getSort())) {
                    fieldSortBuilder.order(SortOrder.ASC);
                }
                if (SortOrder.DESC.toString().equalsIgnoreCase(orderByParam.getSort())) {
                    fieldSortBuilder.order(SortOrder.DESC);
                }
                searchSourceBuilder.sort(fieldSortBuilder);
            });
        }

        // 设置用户自定义的sort
        if (CollectionUtils.isNotEmpty(wrapper.getSortBuilders())) {
            wrapper.getSortBuilders().forEach(searchSourceBuilder::sort);
        }

        // 设置得分排序规则
        Optional.ofNullable(wrapper.getSortOrder())
                .ifPresent(sortOrder -> searchSourceBuilder.sort(EsConstants.SCORE_FIELD, sortOrder));
    }
}
