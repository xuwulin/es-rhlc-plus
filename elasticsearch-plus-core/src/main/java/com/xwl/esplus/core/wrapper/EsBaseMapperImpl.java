package com.xwl.esplus.core.wrapper;

import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.constant.EsConstants;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.toolkit.DocumentInfoUtils;
import com.xwl.esplus.core.toolkit.ExceptionUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.xwl.esplus.core.wrapper.EsWrapperProcessor.buildSearchSourceBuilder;

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
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(wrapper.indexName);
        // 分片个副本信息
        Settings.Builder settings = Settings.builder();
        Optional.ofNullable(wrapper.shardsNum).ifPresent(shards -> settings.put(EsConstants.SHARDS_FIELD, shards));
        Optional.ofNullable(wrapper.replicasNum).ifPresent(replicas -> settings.put(EsConstants.REPLICAS_FIELD, replicas));
        createIndexRequest.settings(settings);

        // mapping信息
        if (Objects.isNull(wrapper.mapping)) {
            List<EsIndexParam> indexParamList = wrapper.esIndexParamList;
            if (!CollectionUtils.isEmpty(indexParamList)) {
                Map<String, Object> mapping = initMapping(indexParamList);
                createIndexRequest.mapping(mapping);
            }
        } else {
            // 用户手动指定的mapping
            createIndexRequest.mapping(wrapper.mapping);
        }

        // 别名信息
        Optional.ofNullable(wrapper.aliasName).ifPresent(aliasName -> {
            Alias alias = new Alias(aliasName);
            createIndexRequest.alias(alias);
        });
        try {
            // 执行
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            // 指示是否所有节点都已确认请求
            boolean acknowledged = createIndexResponse.isAcknowledged();
            log.info("create index [{}] result: {}", wrapper.indexName, acknowledged);
            return acknowledged;
        } catch (IOException e) {
            throw ExceptionUtils.epe("create index exception, indexName: %s", e, wrapper.indexName);
        }
    }

    @Override
    public Boolean updateIndex(EsLambdaIndexWrapper<T> wrapper) {
        boolean existsIndex = this.existsIndex(wrapper.indexName);
        if (!existsIndex) {
            throw ExceptionUtils.epe("index: %s not exists", wrapper.indexName);
        }

        // 更新mapping
        PutMappingRequest putMappingRequest = new PutMappingRequest(wrapper.indexName);
        if (Objects.isNull(wrapper.mapping)) {
            if (CollectionUtils.isEmpty(wrapper.esIndexParamList)) {
                // 空参数列表,则不更新
                return Boolean.FALSE;
            }
            Map<String, Object> mapping = initMapping(wrapper.esIndexParamList);
            putMappingRequest.source(mapping);
        } else {
            // 用户自行指定的mapping信息
            putMappingRequest.source(wrapper.mapping);
        }

        try {
            AcknowledgedResponse acknowledgedResponse = client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
            boolean acknowledged = acknowledgedResponse.isAcknowledged();
            log.info("update index [{}] result: {}", wrapper.indexName, acknowledged);
            return acknowledged;
        } catch (IOException e) {
            throw ExceptionUtils.epe("update index exception, indexName: %s", e, wrapper.indexName);
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
        return DocumentInfoUtils.getEntityInfo(entityClass).getIndexName();
    }
}
