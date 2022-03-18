package com.xwl.esplus.core.mapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.xwl.esplus.core.cache.BaseCache;
import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.metadata.DocumentFieldInfo;
import com.xwl.esplus.core.metadata.DocumentInfo;
import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.enums.EsIdTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.param.EsUpdateParam;
import com.xwl.esplus.core.toolkit.DocumentInfoUtils;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import com.xwl.esplus.core.toolkit.FieldUtils;
import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.xwl.esplus.core.wrapper.processor.EsWrapperProcessor.buildSearchSourceBuilder;
import static com.xwl.esplus.core.wrapper.processor.EsWrapperProcessor.initBoolQueryBuilder;

/**
 * 核心，EsBaseMapper接口实现，获得常用的CRUD功能，（需要和EsWrapper类在同一包下，因为EsWrapper类中的属性是protected）
 *
 * @author xwl
 * @since 2022/3/11 19:59
 */
public class EsBaseMapperImpl<T> implements EsBaseMapper<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * restHighLevel client
     */
    private RestHighLevelClient client;

    /**
     * T 对应的类
     */
    private Class<T> entityClass;

    public void setClient(RestHighLevelClient client) {
        this.client = client;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Boolean existsIndex(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            throw ExceptionUtils.epe("indexName can not be empty");
        }
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        try {
            return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw ExceptionUtils.epe("existIndex exception", e);
        }
    }

    @Override
    public Boolean createIndex(EsLambdaIndexWrapper<T> wrapper) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(wrapper.getIndexName());
        // 分片个副本信息
        Settings.Builder settings = Settings.builder();
        Optional.ofNullable(wrapper.getShardsNum()).ifPresent(shards -> settings.put(EsConstants.SHARDS_FIELD, shards));
        Optional.ofNullable(wrapper.getReplicasNum()).ifPresent(replicas -> settings.put(EsConstants.REPLICAS_FIELD, replicas));
        createIndexRequest.settings(settings);

        // mapping信息
        if (Objects.isNull(wrapper.getMapping())) {
            List<EsIndexParam> indexParamList = wrapper.getEsIndexParamList();
            if (!CollectionUtils.isEmpty(indexParamList)) {
                Map<String, Object> mapping = initMapping(indexParamList);
                createIndexRequest.mapping(mapping);
            }
        } else {
            // 用户手动指定的mapping
            createIndexRequest.mapping(wrapper.getMapping());
        }

        // 别名信息
        Optional.ofNullable(wrapper.getAliasName()).ifPresent(aliasName -> {
            Alias alias = new Alias(aliasName);
            createIndexRequest.alias(alias);
        });
        try {
            // 执行
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            // 指示是否所有节点都已确认请求
            boolean acknowledged = createIndexResponse.isAcknowledged();
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
            throw ExceptionUtils.epe("index: %s not exists", wrapper.getIndexName());
        }

        // 更新mapping
        PutMappingRequest putMappingRequest = new PutMappingRequest(wrapper.getIndexName());
        if (Objects.isNull(wrapper.getMapping())) {
            if (CollectionUtils.isEmpty(wrapper.getEsIndexParamList())) {
                // 空参数列表,则不更新
                return Boolean.FALSE;
            }
            Map<String, Object> mapping = initMapping(wrapper.getEsIndexParamList());
            putMappingRequest.source(mapping);
        } else {
            // 用户自行指定的mapping信息
            putMappingRequest.source(wrapper.getMapping());
        }

        try {
            AcknowledgedResponse acknowledgedResponse = client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
            boolean acknowledged = acknowledgedResponse.isAcknowledged();
            log.info("update index [{}] result: {}", wrapper.getIndexName(), acknowledged);
            return acknowledged;
        } catch (IOException e) {
            throw ExceptionUtils.epe("update index exception, indexName: %s", e, wrapper.getIndexName());
        }
    }

    @Override
    public Boolean deleteIndex(String indexName) {
        if (StringUtils.isEmpty(indexName)) {
            throw ExceptionUtils.epe("indexName can not be empty");
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            AcknowledgedResponse response = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            log.info("delete index [{}] result: {}", indexName, acknowledged);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw ExceptionUtils.epe("delete index exception, indexName: %s", e, indexName);
        }
    }

    @Override
    public SearchResponse search(EsLambdaQueryWrapper<T> wrapper) throws IOException {
        // 构建es restHighLevel 查询参数
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper);
        searchRequest.source(searchSourceBuilder);
        if (GlobalConfigCache.getGlobalConfig().isLogEnable()) {
            log.info("DSL:\n==> {}", searchRequest.source().toString());
        }
        // 执行查询
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    @Override
    public SearchSourceBuilder getSearchSourceBuilder(EsLambdaQueryWrapper<T> wrapper) throws IOException {
        return buildSearchSourceBuilder(wrapper);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions) throws IOException {
        return client.search(searchRequest, requestOptions);
    }

    @Override
    public String getSource(EsLambdaQueryWrapper<T> wrapper) throws IOException {
        // 获取由本框架生成的es查询参数 用于验证生成语法的正确性
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper);
        searchRequest.source(searchSourceBuilder);
        return Optional.ofNullable(searchRequest.source())
                .map(SearchSourceBuilder::toString)
                .orElseThrow(() -> ExceptionUtils.epe("get search source exception"));
    }

    @Override
    public Integer insert(T entity) {
        // 构建请求入参
        IndexRequest indexRequest = buildIndexRequest(entity);
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            if (Objects.equals(indexResponse.status(), RestStatus.CREATED)) {
                setId(entity, indexResponse.getId());
                return EsConstants.ONE;
            } else if (Objects.equals(indexResponse.status(), RestStatus.OK)) {
                // 该id已存在,数据被更新的情况
                return EsConstants.ZERO;
            } else {
                throw ExceptionUtils.epe("insert failed, result:%s entity:%s", indexResponse.getResult(), entity);
            }
        } catch (IOException e) {
            throw ExceptionUtils.epe("insert exception:%s entity:%s", e, entity);
        }
    }

    @Override
    public Integer update(T entity, EsLambdaUpdateWrapper<T> updateWrapper) {
        if (Objects.isNull(entity) && CollectionUtils.isEmpty(updateWrapper.getUpdateParamList())) {
            return EsConstants.ZERO;
        }

        // 构建查询条件
        SearchRequest searchRequest = new SearchRequest(getIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = initBoolQueryBuilder(updateWrapper.getBaseParamList());
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        // 查询id列表
        List<String> idList = this.selectIdList(searchRequest);
        if (CollectionUtils.isEmpty(idList)) {
            return EsConstants.ZERO;
        }

        // 获取更新文档内容
        String jsonData = Optional.ofNullable(entity)
                .map(this::buildJsonIndexSource)
                .orElseGet(() -> buildJsonDoc(updateWrapper));

        // 批量更新
        BulkRequest bulkRequest = new BulkRequest();
        String index = getIndexName();
        idList.forEach(id -> {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.id(id).index(index);
            updateRequest.doc(jsonData, XContentType.JSON);
            bulkRequest.add(updateRequest);
        });
        return doBulkRequest(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 初始化索引mapping
     *
     * @param indexParamList 索引参数列表
     * @return 索引mapping
     */
    private Map<String, Object> initMapping(List<EsIndexParam> indexParamList) {
        Map<String, Object> mapping = new HashMap<>(1);
        Map<String, Object> properties = new HashMap<>(indexParamList.size());
        indexParamList.forEach(indexParam -> {
            Map<String, Object> info = new HashMap<>();
            info.put(EsConstants.TYPE, indexParam.getFieldType());
            // 设置分词器
            if (EsFieldTypeEnum.TEXT.getType().equals(indexParam.getFieldType())) {
                Optional.ofNullable(indexParam.getAnalyzer())
                        .ifPresent(analyzer -> info.put(EsConstants.ANALYZER, indexParam.getAnalyzer().toString().toLowerCase()));
                Optional.ofNullable(indexParam.getSearchAnalyzer())
                        .ifPresent(searchAnalyzer -> info.put(EsConstants.SEARCH_ANALYZER, indexParam.getSearchAnalyzer().toString().toLowerCase()));
            }
            properties.put(indexParam.getFieldName(), info);
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
     * 构建创建数据请求参数
     *
     * @param entity 实体
     * @return es请求参数
     */
    private IndexRequest buildIndexRequest(T entity) {
        IndexRequest indexRequest = new IndexRequest();

        // id预处理,除下述情况,其它情况使用es默认的id
        DocumentInfo entityInfo = DocumentInfoUtils.getDocumentInfo(entity.getClass());
        if (!StringUtils.isEmpty(entityInfo.getId())) {
            if (EsIdTypeEnum.UUID.equals(entityInfo.getIdType())) {
                indexRequest.id(UUID.randomUUID().toString());
            } else if (EsIdTypeEnum.CUSTOMIZE.equals(entityInfo.getIdType())) {
                indexRequest.id(getIdValue(entityClass, entity));
            }
        }

        // 构建插入的json格式数据
        String jsonData = buildJsonIndexSource(entity);

        indexRequest.index(entityInfo.getIndexName());
        indexRequest.source(jsonData, XContentType.JSON);
        return indexRequest;
    }

    /**
     * 获取实体对象的id值
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
            Object value = keyField.get(entity);
            return Optional.ofNullable(value)
                    .map(Object::toString)
                    .orElseThrow(() -> ExceptionUtils.epe("the entity id must not be null"));
        } catch (IllegalAccessException e) {
            throw ExceptionUtils.epe("get id value exception", e);
        }
    }

    /**
     * 构建,插入/更新 的JSON对象
     *
     * @param entity 实体
     * @return json
     */
    private String buildJsonIndexSource(T entity) {
        // 获取所有字段列表
        Class<?> entityClass = entity.getClass();
        Map<String, EsFieldStrategyEnum> columnMap = DocumentInfoUtils
                .getDocumentInfo(entityClass)
                .getFieldList()
                .stream()
                .collect(Collectors.toMap(DocumentFieldInfo::getColumn, DocumentFieldInfo::getFieldStrategy));

        // 根据字段配置的策略 决定是否加入到实际es处理字段中
        Set<String> goodColumn = new HashSet<>(columnMap.size());
        columnMap.forEach((fieldName, fieldStrategy) -> {
            Method invokeMethod = BaseCache.getEsEntityInvokeMethod(entityClass, fieldName);
            Object invoke;
            try {
                if (EsFieldStrategyEnum.IGNORED.equals(fieldStrategy) || EsFieldStrategyEnum.DEFAULT.equals(fieldStrategy)) {
                    // 忽略及无字段配置, 无全局配置 默认加入Json
                    goodColumn.add(fieldName);
                } else if (EsFieldStrategyEnum.NOT_NULL.equals(fieldStrategy)) {
                    invoke = invokeMethod.invoke(entity);
                    if (Objects.nonNull(invoke)) {
                        goodColumn.add(fieldName);
                    }
                } else if (EsFieldStrategyEnum.NOT_EMPTY.equals(fieldStrategy)) {
                    invoke = invokeMethod.invoke(entity);
                    if (Objects.nonNull(invoke) && invoke instanceof String) {
                        String value = (String) invoke;
                        if (!StringUtils.isEmpty(value)) {
                            goodColumn.add(fieldName);
                        }
                    }
                }
            } catch (Exception e) {
                throw ExceptionUtils.epe("buildJsonIndexSource exception, entity:%s", e, entity);
            }
        });

        SimplePropertyPreFilter simplePropertyPreFilter = getSimplePropertyPreFilter(entity.getClass(), goodColumn);
        return JSON.toJSONString(entity, simplePropertyPreFilter, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 设置fastjson toJsonString字段
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
     * @param entity 实体
     * @param id     主键
     */
    private void setId(T entity, String id) {
        String setMethodName = FieldUtils.generateSetFunctionName(getRealIdFieldName());
        Method invokeMethod = BaseCache.getEsEntityInvokeMethod(entityClass, setMethodName);
        try {
            invokeMethod.invoke(entity, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取id实际字段名称
     *
     * @return id实际字段名称
     */
    private String getRealIdFieldName() {
        return DocumentInfoUtils.getDocumentInfo(entityClass).getKeyProperty();
    }

    /**
     * 查询id列表
     *
     * @param searchRequest 查询参数
     * @return id列表
     */
    private List<String> selectIdList(SearchRequest searchRequest) {
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = parseSearchHit(searchResponse);
            return Arrays.stream(searchHits)
                    .map(SearchHit::getId)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw ExceptionUtils.epe("selectIdList exception", e);
        }
    }

    /**
     * 从ES返回结果中解析出SearchHit[]
     *
     * @param searchResponse es返回的响应体
     * @return 响应体中的Hit列表
     */
    private SearchHit[] parseSearchHit(SearchResponse searchResponse) {
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
     * 执行bulk请求,并返回成功个数
     *
     * @param bulkRequest    批量请求参数
     * @param requestOptions 类型
     * @return 成功个数
     */
    private int doBulkRequest(BulkRequest bulkRequest, RequestOptions requestOptions) {
        int totalSuccess = 0;
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, requestOptions);
            Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
            while (iterator.hasNext()) {
                if (Objects.equals(iterator.next().status(), RestStatus.OK)) {
                    totalSuccess++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return totalSuccess;
    }
}
