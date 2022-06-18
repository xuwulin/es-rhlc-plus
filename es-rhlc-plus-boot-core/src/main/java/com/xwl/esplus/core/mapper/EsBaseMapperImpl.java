package com.xwl.esplus.core.mapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.xwl.esplus.core.cache.BaseCache;
import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.enums.EsKeyTypeEnum;
import com.xwl.esplus.core.metadata.DocumentFieldInfo;
import com.xwl.esplus.core.metadata.DocumentInfo;
import com.xwl.esplus.core.page.PageInfo;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.param.EsIndexSettingParam;
import com.xwl.esplus.core.param.EsUpdateParam;
import com.xwl.esplus.core.toolkit.*;
import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.core.wrapper.processor.EsWrapperProcessor;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.xwl.esplus.core.constant.EsConstants.EMPTY_STR;
import static com.xwl.esplus.core.wrapper.processor.EsWrapperProcessor.buildBoolQueryBuilder;
import static com.xwl.esplus.core.wrapper.processor.EsWrapperProcessor.buildSearchSourceBuilder;

/**
 * 核心，EsBaseMapper接口实现，获得常用的CRUD功能，（需要和EsWrapper类在同一包下，因为EsWrapper类中的属性是protected）
 *
 * @author xwl
 * @since 2022/3/11 19:59
 */
public class EsBaseMapperImpl<T> implements EsBaseMapper<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * T对应的实体类（es索引对应的实体类）
     */
    private Class<T> entityClass;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    /**
     * restHighLevel client
     */
    private RestHighLevelClient restHighLevelClient;

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public Boolean existsIndex(String indexName) {
        if (StringUtils.isBlank(indexName)) {
            throw ExceptionUtils.epe("indexName can not be null or empty");
        }
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        try {
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw ExceptionUtils.epe("exists index exception, indexName: %s", e, indexName);
        }
    }

    @Override
    public Boolean createIndex(EsLambdaIndexWrapper<T> wrapper) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(wrapper.getIndexName());
        // alias
        Optional.ofNullable(wrapper.getAlias())
                .ifPresent(aliasName -> {
                    Alias alias = new Alias(aliasName);
                    createIndexRequest.alias(alias);
                });

        // settings
        Settings.Builder settings = Settings.builder();
        EsIndexSettingParam settingParam = wrapper.getSetting();
        if (Objects.nonNull(settingParam)) {
            // 分片数
            Optional.ofNullable(settingParam.getNumberOfShards())
                    .ifPresent(shards -> settings.put(EsConstants.NUMBER_OF_SHARDS, shards));
            // 副本数
            Optional.ofNullable(settingParam.getNumberOfReplicas())
                    .ifPresent(replicas -> settings.put(EsConstants.NUMBER_OF_REPLICAS, replicas));
            // 自定义分词器（包含分词器、分词过滤器等）
            Optional.ofNullable(settingParam.getAnalysis())
                    .ifPresent(analysis -> settings.loadFromSource(JSONObject.toJSONString(analysis), XContentType.JSON));
            createIndexRequest.settings(settings);
        }

        // mappings
        Map<String, Object> mapping;
        if (Objects.isNull(wrapper.getMapping())) {
            List<EsIndexParam> indexParamList = wrapper.getEsIndexParamList();
            if (CollectionUtils.isNotEmpty(indexParamList)) {
                // 根据参数构建索引mapping
                mapping = buildMapping(indexParamList);
                createIndexRequest.mapping(mapping);
            }
        } else {
            // 手动指定mapping，优先级高
            mapping = wrapper.getMapping();
            createIndexRequest.mapping(mapping);
        }

        try {
            CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            log.info("create index [{}] result: {}", wrapper.getIndexName(), acknowledged);
            return acknowledged;
        } catch (IOException e) {
            throw ExceptionUtils.epe("create index exception, indexName: %s", e, wrapper.getIndexName());
        }
    }

    @Override
    public Boolean updateIndex(EsLambdaIndexWrapper<T> wrapper) {
        boolean existsIndex = this.existsIndex(wrapper.getIndexName());
        if (!existsIndex) {
            throw ExceptionUtils.epe("index not exists, indexName: %s", wrapper.getIndexName());
        }

        PutMappingRequest putMappingRequest = new PutMappingRequest(wrapper.getIndexName());
        if (Objects.isNull(wrapper.getMapping())) {
            if (CollectionUtils.isEmpty(wrapper.getEsIndexParamList())) {
                // 空参数列表，不更新
                return false;
            }
            // 根据参数构建索引mapping
            Map<String, Object> mapping = buildMapping(wrapper.getEsIndexParamList());
            putMappingRequest.source(mapping);
        } else {
            // 手动指定mapping，优先级高
            putMappingRequest.source(wrapper.getMapping());
        }

        try {
            AcknowledgedResponse response = restHighLevelClient.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            log.info("update index [{}] result: {}", wrapper.getIndexName(), acknowledged);
            return acknowledged;
        } catch (IOException e) {
            throw ExceptionUtils.epe("update index exception, indexName: %s", e, wrapper.getIndexName());
        }
    }

    @Override
    public Boolean deleteIndex(String indexName) {
        if (StringUtils.isBlank(indexName)) {
            throw ExceptionUtils.epe("indexName can not be null or empty");
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            log.info("delete index [{}] result: {}", indexName, acknowledged);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw ExceptionUtils.epe("delete index exception, indexName: %s", e, indexName);
        }
    }

    @Override
    public Integer save(T entity) {
        IndexRequest indexRequest = buildIndexRequest(entity);
        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            if (Objects.equals(indexResponse.status(), RestStatus.CREATED)) {
                // 插入成功，设置文档实体的id
                setId(entity, indexResponse.getId());
                return EsConstants.ONE;
            } else if (Objects.equals(indexResponse.status(), RestStatus.OK)) {
                // id已存在，相当于更新
                return EsConstants.ZERO;
            } else {
                throw ExceptionUtils.epe("insert failed, result: %s, entity: %s", indexResponse.getResult(), JSONObject.toJSONString(entity));
            }
        } catch (IOException e) {
            throw ExceptionUtils.epe("insert exception, entity: %s", e, JSONObject.toJSONString(entity));
        }
    }

    @Override
    public Integer saveBatch(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return EsConstants.ZERO;
        }
        BulkRequest bulkRequest = new BulkRequest();
        entityList.forEach(entity -> {
            IndexRequest indexRequest = buildIndexRequest(entity);
            bulkRequest.add(indexRequest);
        });
        return doBulkRequest(bulkRequest, RequestOptions.DEFAULT, entityList);
    }

    @Override
    public Integer update(T entity, EsLambdaUpdateWrapper<T> wrapper) {
        if (Objects.isNull(entity) && CollectionUtils.isEmpty(wrapper.getUpdateParamList())) {
            return EsConstants.ZERO;
        }

        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = buildBoolQueryBuilder(wrapper.getBaseParamList(), entityClass);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        // 根据条件查询要更新的id集合
        List<String> ids = this.selectIds(searchRequest);
        if (CollectionUtils.isEmpty(ids)) {
            return EsConstants.ZERO;
        }

        // 更新文档内容
        String jsonData = Optional.ofNullable(entity)
                .map(this::buildJsonSource)
                .orElseGet(() -> buildJsonDoc(wrapper));
        BulkRequest bulkRequest = new BulkRequest();
        String indexName = getIndexName();
        ids.forEach(id -> {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.id(id).index(indexName);
            updateRequest.doc(jsonData, XContentType.JSON);
            bulkRequest.add(updateRequest);
        });
        return doBulkRequest(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public Integer updateById(T entity) {
        String idValue = getIdValue(entityClass, entity);
        UpdateRequest updateRequest = buildUpdateRequest(entity, idValue);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            if (Objects.equals(updateResponse.status(), RestStatus.OK)) {
                return EsConstants.ONE;
            }
        } catch (IOException e) {
            throw ExceptionUtils.epe("updateById exception, entity: %s", e, JSONObject.toJSONString(entity));
        }
        return EsConstants.ZERO;
    }

    @Override
    public Integer updateBatchById(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return EsConstants.ZERO;
        }
        BulkRequest bulkRequest = new BulkRequest();
        entityList.forEach(entity -> {
            String idValue = getIdValue(entityClass, entity);
            UpdateRequest updateRequest = buildUpdateRequest(entity, idValue);
            bulkRequest.add(updateRequest);
        });
        return doBulkRequest(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public Integer remove(EsLambdaQueryWrapper<T> wrapper) {
        List<T> list = this.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return EsConstants.ZERO;
        }
        BulkRequest bulkRequest = new BulkRequest();
        Method getId = BaseCache.getEsEntityGetterMethod(entityClass, getRealIdFieldName());
        list.forEach(entity -> {
            try {
                Object id = getId.invoke(entity);
                if (Objects.nonNull(id)) {
                    DeleteRequest deleteRequest = new DeleteRequest();
                    deleteRequest.id(id.toString());
                    deleteRequest.index(getIndexName());
                    bulkRequest.add(deleteRequest);
                }
            } catch (Exception e) {
                throw ExceptionUtils.epe("delete exception, indexName: %s", e, getIndexName());
            }
        });
        return doBulkRequest(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public Integer removeById(Serializable id) {
        if (Objects.isNull(id) || StringUtils.isBlank(id.toString())) {
            throw ExceptionUtils.epe("id can not be null or empty");
        }
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.id(id.toString());
        deleteRequest.index(getIndexName());
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (Objects.equals(deleteResponse.status(), RestStatus.OK)) {
                return EsConstants.ONE;
            }
        } catch (IOException e) {
            throw ExceptionUtils.epe("deleteById exception, indexName: s%, id: %s", e, getIndexName(), id);
        }
        return EsConstants.ZERO;
    }

    @Override
    public Integer removeByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw ExceptionUtils.epe("idList can not be null or empty");
        }
        BulkRequest bulkRequest = new BulkRequest();
        idList.forEach(id -> {
            if (Objects.isNull(id) || StringUtils.isBlank(id.toString())) {
                throw ExceptionUtils.epe("id can not be null or empty");
            }
            DeleteRequest deleteRequest = new DeleteRequest();
            deleteRequest.id(id.toString());
            deleteRequest.index(getIndexName());
            bulkRequest.add(deleteRequest);
        });
        return doBulkRequest(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions) {
        try {
            // 记录日志
            logQueryDSL(searchRequest.source());
            return restHighLevelClient.search(searchRequest, requestOptions);
        } catch (IOException e) {
            throw ExceptionUtils.epe("original search exception", e);
        }
    }

    @Override
    public SearchResponse search(EsLambdaQueryWrapper<T> wrapper) {
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper, entityClass);
        searchRequest.source(searchSourceBuilder);
        try {
            // 记录日志
            logQueryDSL(wrapper);
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw ExceptionUtils.epe("search exception", e);
        }
    }

    @Override
    public SearchSourceBuilder getSearchSourceBuilder(EsLambdaQueryWrapper<T> wrapper) {
        return buildSearchSourceBuilder(wrapper, entityClass);
    }

    @Override
    public String getSource(EsLambdaQueryWrapper<T> wrapper) {
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper, entityClass);
        searchRequest.source(searchSourceBuilder);
        return Optional.ofNullable(searchRequest.source())
                .map(SearchSourceBuilder::toString)
                .orElseThrow(() -> ExceptionUtils.epe("get search source exception"));
    }

    @Override
    public Long count() {
        return this.count(Wrappers.lambdaQuery());
    }

    @Override
    public Long count(EsLambdaQueryWrapper<T> wrapper) {
        CountRequest countRequest = new CountRequest(getIndexName());
        BoolQueryBuilder boolQueryBuilder = buildBoolQueryBuilder(wrapper, entityClass);
        countRequest.query(boolQueryBuilder);
        CountResponse count;
        try {
            // 记录日志
            logQueryCountDSL(wrapper);
            count = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw ExceptionUtils.epe("select count exception", e);
        }
        return Optional.ofNullable(count)
                .map(CountResponse::getCount)
                .orElseThrow(() -> ExceptionUtils.epe("select count exception"));
    }

    @Override
    public T getOne(EsLambdaQueryWrapper<T> wrapper) {
        long count = this.count(wrapper);
        if (count > EsConstants.ONE && (wrapper.getSize() == null || wrapper.getSize() > EsConstants.ONE)) {
            throw ExceptionUtils.epe("Expected one result (or null) to be returned by selectOne(), but found: %d. please use limit function to limit 1", count);
        }
        SearchHit[] searchHits = getSearchHitArray(wrapper);
        if (CollectionUtils.isEmpty(searchHits)) {
            return null;
        }
        return parseOne(searchHits[0], wrapper);
    }

    @Override
    public T getById(Serializable id) {
        if (Objects.isNull(id) || StringUtils.isEmpty(id.toString())) {
            throw ExceptionUtils.epe("id must not be null or empty");
        }
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery(getIdFieldName(), id));
        searchRequest.source(searchSourceBuilder);
        // 请求es获取数据
        SearchHit[] searchHits = getSearchHitArray(searchRequest);
        if (CollectionUtils.isEmpty(searchHits)) {
            return null;
        }
        // 解析数据
        return parseOne(searchHits[0]);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw ExceptionUtils.epe("id collection must not be null or empty");
        }
        List<String> stringIdList = idList.stream().map(Object::toString).collect(Collectors.toList());
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termsQuery(getIdFieldName(), stringIdList));
        searchRequest.source(sourceBuilder);
        // 请求es获取数据
        SearchHit[] searchHitArray = getSearchHitArray(searchRequest);
        if (CollectionUtils.isEmpty(searchHitArray)) {
            return new ArrayList<>(0);
        }

        // 批量解析数据
        return Arrays.stream(searchHitArray)
                .map(this::parseOne)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> list(EsLambdaQueryWrapper<T> wrapper) {
        // 请求es获取数据
        SearchHit[] searchHits = getSearchHitArray(wrapper);
        if (CollectionUtils.isEmpty(searchHits)) {
            return new ArrayList<>(0);
        }

        // 批量解析
        return Arrays.stream(searchHits)
                .map(searchHit -> parseOne(searchHit, wrapper))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> listMaps(EsLambdaQueryWrapper<T> wrapper) {
        List<T> list = this.list(wrapper);
        String jsonString = JSONObject.toJSONString(list);
        List<Map<String, Object>> maps = JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {
        });
        return maps;
    }

    @Override
    public PageInfo<SearchHit> pageOriginal(EsLambdaQueryWrapper<T> wrapper) {
        return this.pageOriginal(wrapper, EsConstants.PAGE_NUM, EsConstants.PAGE_SIZE);
    }

    @Override
    public PageInfo<SearchHit> pageOriginal(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize) {
        PageInfo<SearchHit> pageInfo = new PageInfo<>();
        long total = this.count(wrapper);
        if (total <= EsConstants.ZERO) {
            return pageInfo;
        }

        // 查询数据
        SearchHit[] searchHitArray = new SearchHit[0];
        try {
            searchHitArray = getSearchHitArray(wrapper, pageNum, pageSize);
        } catch (IOException e) {
            throw ExceptionUtils.epe("pageOriginal exception", e);
        }
        List<SearchHit> list = Arrays.stream(searchHitArray).collect(Collectors.toList());
        pageInfo.setList(list);
        pageInfo.setSize(list.size());
        pageInfo.setTotal(total);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public PageInfo<T> page(EsLambdaQueryWrapper<T> wrapper) {
        return initPageInfo(wrapper, EsConstants.PAGE_NUM, EsConstants.PAGE_SIZE);
    }

    @Override
    public PageInfo<T> page(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize) {
        return initPageInfo(wrapper, pageNum, pageSize);
    }

    @Override
    public PageInfo<Map<String, Object>> pageMaps(EsLambdaQueryWrapper<T> wrapper) {
        return this.pageMaps(wrapper, EsConstants.PAGE_NUM, EsConstants.PAGE_SIZE);
    }

    @Override
    public PageInfo<Map<String, Object>> pageMaps(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize) {
        PageInfo<T> pageInfo = initPageInfo(wrapper, pageNum, pageSize);
        List<T> list = pageInfo.getList();
        String jsonString = JSONObject.toJSONString(list);
        List<Map<String, Object>> maps = JSON.parseObject(jsonString, new TypeReference<List<Map<String, Object>>>() {
        });
        PageInfo<Map<String, Object>> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(maps);
        return result;
    }

    public Settings.Builder buildSettings() {
        Settings.Builder settings = Settings.builder();
        return settings;
    }

    /**
     * 根据索引参数构建索引mapping
     *
     * @param indexParamList 索引参数列表
     * @return 索引mapping
     */
    private Map<String, Object> buildMapping(List<EsIndexParam> indexParamList) {
        Map<String, Object> mapping = new HashMap<>(1);
        Map<String, Object> properties = new HashMap<>(indexParamList.size());
        DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(entityClass);
        Map<String, String> mappingColumnMap = documentInfo.getFieldColumnMap();
        indexParamList.forEach(indexParam -> {
            Map<String, Object> fieldInfo = new HashMap<>();
            // 设置字段类型
            Optional.ofNullable(indexParam.getFieldType())
                    .ifPresent(fieldType -> fieldInfo.put(EsConstants.TYPE, fieldType));
            // 设置是否索引该字段，有些类型没有index属性，比如图形geo_shape
            Optional.ofNullable(indexParam.getIndex())
                    .ifPresent(index -> fieldInfo.put(EsConstants.INDEX, index));
            // 设置ignoreAbove，只有keyword类型才有此属性
            if (StringUtils.equals(indexParam.getFieldType(), EsFieldTypeEnum.KEYWORD.getType())) {
                Optional.ofNullable(indexParam.getIgnoreAbove())
                        .ifPresent(ignoreAbove -> fieldInfo.put(EsConstants.IGNORE_ABOVE, ignoreAbove));
            }
            // 设置日期格式format
            if (StringUtils.equals(indexParam.getFieldType(), EsFieldTypeEnum.DATE.getType())) {
                Optional.ofNullable(indexParam.getFormat())
                        .ifPresent(format -> fieldInfo.put(EsConstants.FORMAT, format));
            }
            // 设置copy_to
            Optional.ofNullable(indexParam.getCopyTo())
                    .ifPresent(copyTo -> fieldInfo.put(EsConstants.COPY_TO, copyTo));
            // 设置分词器，只有text类型才有此属性
            if (StringUtils.equals(indexParam.getFieldType(), EsFieldTypeEnum.TEXT.getType())) {
                // 创建索引时的分词器
                Optional.ofNullable(indexParam.getAnalyzer())
                        .ifPresent(analyzer -> fieldInfo.put(EsConstants.ANALYZER, analyzer));
                // 搜索时的分词器
                Optional.ofNullable(indexParam.getSearchAnalyzer())
                        .ifPresent(searchAnalyzer -> fieldInfo.put(EsConstants.SEARCH_ANALYZER, searchAnalyzer));
            }
            // 对象properties
            Optional.ofNullable(indexParam.getProperties())
                    .ifPresent(property -> fieldInfo.put(EsConstants.PROPERTIES, buildMapping(property).get(EsConstants.PROPERTIES)));
            // 多（子）字段fields
            Optional.ofNullable(indexParam.getFields())
                    .ifPresent(field -> fieldInfo.put(EsConstants.FIELDS, buildMapping(field).get(EsConstants.PROPERTIES)));

            String fieldName = indexParam.getFieldName();
            // 注解 > 配置
            // 创建索引时，全局驼峰转下划线配置和使用注解指定es字段名，能同时生效，注解优先级高，加了注解的字段使用注解中的值，没加注解的使用驼峰转下划线
            // 根据全局配置确定es字段名是否要进行驼峰转下划线
            if (this.globalConfig.getDocumentConfig().isMapUnderscoreToCamelCase()) {
                fieldName = StringUtils.camelToUnderline(fieldName);
            }
            // 根据字段上的注解确定es的字段名（优先级高） TODO 待解决：对象字段解析时会报错
            String columnName = mappingColumnMap.get(indexParam.getFieldName());
            if (StringUtils.isNotBlank(columnName)) {
                fieldName = columnName;
            }

            properties.put(fieldName, fieldInfo);
        });
        mapping.put(EsConstants.PROPERTIES, properties);
        return mapping;
    }

    /**
     * 获取索引名称
     *
     * @return 索引名称
     */
    private String getIndexName() {
        return DocumentInfoUtils.getDocumentInfo(entityClass).getIndexName();
    }

    /**
     * 构建IndexRequest
     *
     * @param entity es索引对应的实体类
     * @return IndexRequest
     */
    private IndexRequest buildIndexRequest(T entity) {
        IndexRequest indexRequest = new IndexRequest();
        // id处理，除下述情况，其它情况使用es默认的id，注解 > 全局配置
        DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(entity.getClass());
        if (StringUtils.isNotBlank(documentInfo.getId())) {
            if (EsKeyTypeEnum.UUID.equals(documentInfo.getKeyType())) {
                indexRequest.id(UUID.randomUUID().toString());
            } else if (EsKeyTypeEnum.CUSTOMIZE.equals(documentInfo.getKeyType())) {
                indexRequest.id(getIdValue(entityClass, entity));
            }
        }

        // 构建插入的json格式数据
        String jsonData = buildJsonSource(entity);
        indexRequest.index(documentInfo.getIndexName()).source(jsonData, XContentType.JSON);
        return indexRequest;
    }

    /**
     * 获取文档实体对象的id值
     *
     * @param entityClass 实体类
     * @param entity      实体对象
     * @return id值
     */
    private String getIdValue(Class<T> entityClass, T entity) {
        try {
            DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(entityClass);
            Field keyField = Optional.ofNullable(documentInfo.getKeyField())
                    .orElseThrow(() -> ExceptionUtils.epe("the entity id field not found"));
            // 获取id值
            Object value = keyField.get(entity);
            return Optional.ofNullable(value)
                    .map(Object::toString)
                    .orElseThrow(() -> ExceptionUtils.epe("the entity id can not be null"));
        } catch (IllegalAccessException e) {
            throw ExceptionUtils.epe("get id value exception", e);
        }
    }

    /**
     * 构建,插入/更新 的JSON对象
     *
     * @param entity es索引对应的实体类
     * @return json
     */
    private String buildJsonSource(T entity) {
        Class<?> entityClass = entity.getClass();
        // 根据字段配置的策略，决定是否加入到实际es处理字段中
        DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(entityClass);
        List<DocumentFieldInfo> fieldList = documentInfo.getFieldList();
        Map<String, String> fieldColumnMap = documentInfo.getFieldColumnMap();
        Set<String> fieldNameSet = new HashSet<>(fieldList.size());

        fieldList.forEach(field -> {
            String fieldName = field.getFieldName();
            Method invokeMethod = BaseCache.getEsEntityGetterMethod(entityClass, fieldName);
            Object invoke;
            EsFieldStrategyEnum fieldStrategy = field.getFieldStrategy();
            try {
                if (EsFieldStrategyEnum.IGNORED.equals(fieldStrategy) || EsFieldStrategyEnum.DEFAULT.equals(fieldStrategy)) {
                    // 忽略及无字段配置, 无全局配置 默认加入Json
                    fieldNameSet.add(fieldName);
                } else if (EsFieldStrategyEnum.NOT_NULL.equals(fieldStrategy)) {
                    invoke = invokeMethod.invoke(entity);
                    if (Objects.nonNull(invoke)) {
                        fieldNameSet.add(fieldName);
                    }
                } else if (EsFieldStrategyEnum.NOT_EMPTY.equals(fieldStrategy)) {
                    invoke = invokeMethod.invoke(entity);
                    if (Objects.nonNull(invoke) && invoke instanceof String) {
                        String value = (String) invoke;
                        if (StringUtils.isNotEmpty(value)) {
                            fieldNameSet.add(fieldName);
                        }
                    }
                }
            } catch (Exception e) {
                throw ExceptionUtils.epe("buildJsonIndexSource exception, entity:%s", e, entity);
            }
        });

        // 过滤掉实体类中不需要的属性
        SimplePropertyPreFilter simplePropertyPreFilter = getSimplePropertyPreFilter(entity.getClass(), fieldNameSet);
        // 序列化过滤器
        SerializeFilter[] filters = {simplePropertyPreFilter, documentInfo.getSerializeFilter()};

        // 日期格式：所有的日期格式需要转换为全局配置的日期格式，但是如果有@JSONField(format="")注解的，按照注解的format配置内容来进行格式化
        String globalDateFormat = this.globalConfig.getDocumentConfig().getDateFormat();
        if (StringUtils.isNotBlank(globalDateFormat)) {
            // 指定fastjson的全局日期格式
            JSON.DEFFAULT_DATE_FORMAT = globalDateFormat;
            /**
             * SerializerFeature.DisableCircularReferenceDetect：消除循环引用
             * SerializerFeature.WriteMapNullValue：返回结果保留null值
             * SerializerFeature.WriteNullStringAsEmpty：将返回值为null的字符串转变成""
             * SerializerFeature.WriteNullListAsEmpty：List字段如果为null，输出为[]，而非null
             */
            return JSON.toJSONString(entity, filters, SerializerFeature.WriteDateUseDateFormat);
        }
        return JSON.toJSONString(entity, filters);
    }

    /**
     * 使用fastjson的SimplePropertyPreFilter过滤属性，过滤掉实体类中不需要的属性
     *
     * @param clazz  类
     * @param fields 字段列表
     * @return
     */
    private SimplePropertyPreFilter getSimplePropertyPreFilter(Class<?> clazz, Set<String> fields) {
        return new SimplePropertyPreFilter(clazz, fields.toArray(new String[fields.size()]));
    }

    /**
     * 设置id值
     *
     * @param entity es索引对应的实体类
     * @param id     主键值
     */
    private void setId(T entity, String id) {
        Method invokeMethod = BaseCache.getEsEntitySetterMethod(entityClass, getRealIdFieldName());
        // 将es返回的String类型id还原为字段实际的id类型,比如Long,否则反射会报错
        Class<?> idClass = DocumentInfoUtils.getDocumentInfo(entityClass).getKeyClass();
        Object value = ReflectionUtils.getValue(idClass, id);
        try {
            // 方法反射调用：方法.invoke(目标对象, 参数);
            invokeMethod.invoke(entity, value);
        } catch (Throwable e) {
            throw ExceptionUtils.epe("setId Exception", e);
        }
    }

    /**
     * 获取id实际字段名称
     *
     * @return id实际字段名称
     */
    private String getRealIdFieldName() {
        return DocumentInfoUtils.getDocumentInfo(entityClass).getKeyFieldName();
    }

    /**
     * 根据条件查询要更新的id集合
     *
     * @param searchRequest 查询参数
     * @return id集合
     */
    private List<String> selectIds(SearchRequest searchRequest) {
        try {
            // 记录日志
            logQueryDSL(searchRequest.source());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = parseSearchHitArray(searchResponse);
            return Arrays.stream(searchHits)
                    .map(SearchHit::getId)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw ExceptionUtils.epe("selectIdList exception", e);
        }
    }

    /**
     * 解析搜索响应
     *
     * @param searchResponse 搜索响应
     * @return 搜索命中
     */
    private SearchHit[] parseSearchHitArray(SearchResponse searchResponse) {
        return Optional.ofNullable(searchResponse)
                .map(SearchResponse::getHits)
                .map(SearchHits::getHits)
                .orElse(new SearchHit[0]);
    }

    /**
     * 构建更新文档的json
     *
     * @param updateWrapper 条件
     * @return json
     */
    private String buildJsonDoc(EsLambdaUpdateWrapper<T> updateWrapper) {
        List<EsUpdateParam> updateParamList = updateWrapper.getUpdateParamList();
        JSONObject jsonObject = new JSONObject();
        updateParamList.forEach(esUpdateParam -> jsonObject.put(esUpdateParam.getField(), esUpdateParam.getValue()));
        return JSON.toJSONString(jsonObject, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 执行批量操作
     *
     * @param bulkRequest    批量请求参数
     * @param requestOptions 类型
     * @return 操作成功数量
     */
    private int doBulkRequest(BulkRequest bulkRequest, RequestOptions requestOptions) {
        int totalSuccess = 0;
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, requestOptions);
            if (bulkResponse.hasFailures()) {
                throw ExceptionUtils.epe("bulkResponse has failures");
            }
            Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
            while (iterator.hasNext()) {
                if (Objects.equals(iterator.next().status(), RestStatus.OK)) {
                    totalSuccess++;
                }
            }
        } catch (IOException e) {
            throw ExceptionUtils.epe("doBulkRequest exception", e);
        }
        return totalSuccess;
    }

    /**
     * 执行批量操作，批量插入，插入成功后设置id
     *
     * @param bulkRequest    批量请求参数
     * @param requestOptions 类型
     * @param entityList     实体列表
     * @return 操作成功数量
     */
    private int doBulkRequest(BulkRequest bulkRequest, RequestOptions requestOptions, Collection<T> entityList) {
        int totalSuccess = 0;
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, requestOptions);
            if (bulkResponse.hasFailures()) {
                throw ExceptionUtils.epe("bulkResponse has failures");
            }
            Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
            while (iterator.hasNext()) {
                BulkItemResponse next = iterator.next();
                if (Objects.equals(next.status(), RestStatus.CREATED)) {
                    setId((T) entityList.toArray()[totalSuccess], next.getId());
                    totalSuccess++;
                }
            }
        } catch (IOException e) {
            throw ExceptionUtils.epe("doBulkRequest exception", e);
        }
        return totalSuccess;
    }

    /**
     * 构建更新数据请求参数
     *
     * @param entity  es索引对应的实体类
     * @param idValue id值
     * @return 更新请求参数
     */
    private UpdateRequest buildUpdateRequest(T entity, String idValue) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.id(idValue);
        updateRequest.index(getIndexName());
        String jsonData = buildJsonSource(entity);
        updateRequest.doc(jsonData, XContentType.JSON);
        return updateRequest;
    }

    /**
     * 从es获取到的数据中解析出对应类型的数组 默认设置id
     *
     * @param searchResponse es返回的响应体
     * @return 指定的返回类型数据列表
     */
    private List<T> parseResultList(SearchResponse searchResponse) {
        SearchHit[] searchHits = parseSearchHitArray(searchResponse);
        if (CollectionUtils.isEmpty(searchHits)) {
            return new ArrayList<>(0);
        }
        return Arrays.stream(searchHits)
                .map(hit -> {
                    T entity = JSON.parseObject(hit.getSourceAsString(), entityClass);
                    setId(entity, hit.getId());
                    return entity;
                }).collect(Collectors.toList());
    }

    /**
     * 从es获取到的数据中解析出对应类型的数组 id根据查询/不查询条件决定是否设置
     *
     * @param searchResponse es返回的响应体
     * @param wrapper        条件
     * @return 指定的返回类型数据列表
     */
    private List<T> parseResultList(SearchResponse searchResponse, EsLambdaQueryWrapper<T> wrapper) {
        SearchHit[] searchHits = parseSearchHitArray(searchResponse);
        if (CollectionUtils.isEmpty(searchHits)) {
            return new ArrayList<>(0);
        }
        return hitsToArray(searchHits, wrapper);
    }

    /**
     * 将es返回结果集解析为数组
     *
     * @param searchHits es返回结果集
     * @param wrapper    条件
     * @return
     */
    private List<T> hitsToArray(SearchHit[] searchHits, EsLambdaQueryWrapper<T> wrapper) {
        return Arrays.stream(searchHits)
                .map(hit -> {
                    T entity = JSON.parseObject(hit.getSourceAsString(), entityClass);
                    if (!CollectionUtils.isEmpty(wrapper.getHighLightParamList())) {
                        Map<String, String> highlightFieldMap = getHighlightFieldMap();
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        highlightFields.forEach((key, value) -> {
                            String highLightValue = Arrays.stream(value.getFragments()).findFirst().map(Text::string).orElse(EMPTY_STR);
                            setHighlightValue(entity, highlightFieldMap.get(key), highLightValue);
                        });
                    }
                    boolean includeId = EsWrapperProcessor.includeId(getRealIdFieldName(), wrapper);
                    if (includeId) {
                        setId(entity, hit.getId());
                    }
                    return entity;
                }).collect(Collectors.toList());
    }

    /**
     * 获取需要高亮的字段属性
     *
     * @return 高亮字段Map
     */
    private Map<String, String> getHighlightFieldMap() {
        return DocumentInfoUtils.getDocumentInfo(entityClass).getHighlightFieldMap();
    }

    /**
     * 设置高亮字段的值
     *
     * @param entity         实体类
     * @param highlightField 高亮返回字段
     * @param value          高亮结果值
     */
    private void setHighlightValue(T entity, String highlightField, String value) {
        String setMethodName = FieldUtils.generateSetFunctionName(highlightField);
        Method invokeMethod = BaseCache.getEsEntityMethod(entityClass, setMethodName);
        try {
            invokeMethod.invoke(entity, value);
        } catch (Exception e) {
            throw ExceptionUtils.epe("setHighlightValue exception", e);
        }
    }

    /**
     * 获取查询结果数组
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return es返回结果体
     * @throws IOException IO异常
     */
    private SearchHit[] getSearchHitArray(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize) throws IOException {
        wrapper.from((pageNum - 1) * pageSize);
        wrapper.size(pageSize);
        SearchResponse response = search(wrapper);
        return Optional.ofNullable(response)
                .map(SearchResponse::getHits)
                .map(SearchHits::getHits)
                .orElseThrow(() -> ExceptionUtils.epe("get searchHits exception,the response from es is null"));
    }

    /**
     * 从es获取到的数据中解析出对应的对象 id根据查询/不查询条件决定是否设置
     *
     * @param searchResponse es返回的响应体
     * @param wrapper        条件
     * @return 指定的返回类型数据
     */
    private T parseResult(SearchResponse searchResponse, EsLambdaQueryWrapper<T> wrapper) {
        SearchHit[] searchHits = parseSearchHitArray(searchResponse);
        if (CollectionUtils.isEmpty(searchHits)) {
            return null;
        }
        T entity = JSON.parseObject(searchHits[0].getSourceAsString(), entityClass);
        boolean includeId = EsWrapperProcessor.includeId(getRealIdFieldName(), wrapper);
        if (includeId) {
            setId(entity, searchHits[0].getId());
        }
        return entity;
    }

    /**
     * 从es获取到的数据中解析出对应的对象 默认设置id
     *
     * @param searchResponse es返回的响应体
     * @return 指定的返回类型数据
     */
    private T parseResult(SearchResponse searchResponse) {
        SearchHit[] searchHits = parseSearchHitArray(searchResponse);
        if (CollectionUtils.isEmpty(searchHits)) {
            return null;
        }
        T entity = JSON.parseObject(searchHits[0].getSourceAsString(), entityClass);
        setId(entity, searchHits[0].getId());
        return entity;
    }

    /**
     * 获取id字段名称(注解中的)
     *
     * @return id字段名称
     */
    private String getIdFieldName() {
        return DocumentInfoUtils.getDocumentInfo(entityClass).getKeyColumnName();
    }

    /**
     * 初始化分页数据
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页数据
     */
    private PageInfo<T> initPageInfo(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize) {
        PageInfo<T> pageInfo = new PageInfo<>();
        long total = this.count(wrapper);
        if (total <= EsConstants.ZERO) {
            return pageInfo;
        }

        // 请求es获取数据
        pageNum = pageNum == null || pageNum <= EsConstants.ZERO ? EsConstants.PAGE_NUM : pageNum;
        pageSize = pageSize == null || pageSize <= EsConstants.ZERO ? EsConstants.PAGE_SIZE : pageSize;
        SearchHit[] searchHitArray;
        try {
            searchHitArray = getSearchHitArray(wrapper, pageNum, pageSize);
        } catch (IOException e) {
            throw ExceptionUtils.epe("page select exception:%s", e);
        }

        // 解析请求结果
        List<T> list = Arrays.stream(searchHitArray)
                .map(searchHit -> parseOne(searchHit, wrapper))
                .collect(Collectors.toList());
        return PageUtils.getPageInfo(list, total, pageNum, pageSize);
    }

    /**
     * 根据配置获取完整的indexName
     *
     * @param indexName
     * @return tablePrefix + indexName
     */
    private String getFullIndexName(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            throw ExceptionUtils.epe("indexName can not be empty");
        }
        GlobalConfig.DocumentConfig documentConfig = GlobalConfigCache.getGlobalConfig().getDocumentConfig();
        String tablePrefix = Optional.ofNullable(documentConfig.getIndexPrefix()).orElse(EMPTY_STR);
        return tablePrefix + indexName;
    }


    /**
     * 从searchHit中解析一条数据
     *
     * @param searchHit es返回数据
     * @return 实际想要的数据
     */
    private T parseOne(SearchHit searchHit) {
        T entity = JSON.parseObject(searchHit.getSourceAsString(), entityClass,
                DocumentInfoUtils.getDocumentInfo(entityClass).getExtraProcessor());
        setId(entity, searchHit.getId());
        return entity;
    }

    /**
     * 从搜索命中中解析一条数据
     *
     * @param searchHit 搜索命中
     * @param wrapper   查询参数
     * @return es对应的实体
     */
    private T parseOne(SearchHit searchHit, EsLambdaQueryWrapper<T> wrapper) {
        // fastjson实体中不存在的字段处理器（处理多余字段，即json中有字段，但是在实体中不存在）
        ExtraProcessor extraProcessor = DocumentInfoUtils.getDocumentInfo(entityClass).getExtraProcessor();
        // 解析json
        T entity = JSON.parseObject(searchHit.getSourceAsString(), entityClass, extraProcessor);
        // 高亮字段处理
        if (CollectionUtils.isNotEmpty(wrapper.getHighLightParamList())) {
            Map<String, String> highlightFieldMap = getHighlightFieldMap();
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            highlightFields.forEach((key, value) -> {
                String highLightValue = Arrays.stream(value.getFragments()).findFirst().map(Text::string).orElse(EMPTY_STR);
                setHighlightValue(entity, highlightFieldMap.get(key), highLightValue);
            });
        }
        // id处理
        boolean includeId = EsWrapperProcessor.includeId(getRealIdFieldName(), wrapper);
        if (includeId) {
            setId(entity, searchHit.getId());
        }
        return entity;
    }

    /**
     * 获取搜索命中数组
     *
     * @param searchRequest 搜索请求
     * @return 搜索命中数组
     */
    private SearchHit[] getSearchHitArray(SearchRequest searchRequest) {
        SearchResponse searchResponse;
        try {
            logQueryDSL(searchRequest.source());
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw ExceptionUtils.epe("getSearchHitArray exception,searchRequest:%s", e, searchRequest);
        }
        return parseSearchHitArray(searchResponse);
    }

    /**
     * 获取搜索命中
     *
     * @param wrapper 查询参数
     * @return 搜索命中数组
     */
    private SearchHit[] getSearchHitArray(EsLambdaQueryWrapper<T> wrapper) {
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper, entityClass);
        searchRequest.source(searchSourceBuilder);
        logQueryDSL(wrapper);
        SearchResponse response;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw ExceptionUtils.epe("getSearchHitArray exception", e);
        }
        return parseSearchHitArray(response);
    }

    /**
     * 记录查询DSL
     *
     * @param wrapper 查询参数包装类
     */
    private void logQueryDSL(EsLambdaQueryWrapper<T> wrapper) {
        if (this.globalConfig.isEnableDsl()) {
            SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper, entityClass);
            logPrettyQueryDSL(searchSourceBuilder);
        }
    }

    /**
     * 记录查询DSL
     *
     * @param searchSourceBuilder es查询请求参数
     */
    private void logQueryDSL(SearchSourceBuilder searchSourceBuilder) {
        if (this.globalConfig.isEnableDsl()) {
            logPrettyQueryDSL(searchSourceBuilder);
        }
    }

    /**
     * 根据全局配置决定是否控制台打印CountDSL语句
     *
     * @param wrapper 查询参数包装类
     */
    private void logQueryCountDSL(EsLambdaQueryWrapper<T> wrapper) {
        if (this.globalConfig.isEnableDsl()) {
            CountRequest countRequest = new CountRequest(getIndexName());
            BoolQueryBuilder boolQueryBuilder = buildBoolQueryBuilder(wrapper, entityClass);
            countRequest.query(boolQueryBuilder);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(countRequest.query());
            logPrettyQueryDSL(searchSourceBuilder);
        }
    }

    /**
     * 格式化DSL语句
     *
     * @param searchSourceBuilder
     */
    private void logPrettyQueryDSL(SearchSourceBuilder searchSourceBuilder) {
        JSONObject object = JSONObject.parseObject(String.valueOf(searchSourceBuilder));
        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat);
        log.info("Elasticsearch Query DSL:\n{}", pretty);
    }
}
