package com.xwl.esplus.test;

import com.alibaba.fastjson.JSONObject;
import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 索引查询、创建、修改、删除测试
 *
 * @author xwl
 * @since 2022/3/11 18:22
 */
@SpringBootTest
public class IndexTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testExistsIndex() {
        String indexName = "user_document";
        boolean existsIndex = userDocumentMapper.existsIndex(indexName);
        System.out.println(existsIndex);
    }

    @Test
    public void testCreateIndex() {
        EsLambdaIndexWrapper<UserDocument> wrapper = Wrappers.lambdaIndex();
        // 自定义分词器
        String analysis = "{\"analysis\":{\"filter\":{\"py\":{\"keep_joined_full_pinyin\":true,\"none_chinese_pinyin_tokenize\":false,\"keep_original\":true,\"remove_duplicated_term\":true,\"type\":\"pinyin\",\"limit_first_letter_length\":16,\"keep_full_pinyin\":false}},\"analyzer\":{\"completion_analyzer\":{\"filter\":\"py\",\"tokenizer\":\"keyword\"},\"text_analyzer\":{\"filter\":\"py\",\"tokenizer\":\"ik_max_word\"}}}}";
        Map<String, Object> analysisMap = JSONObject.parseObject(analysis, Map.class);

        EsIndexParam cnFirstNameParam = new EsIndexParam();
        cnFirstNameParam.setFieldName("firstName");
        cnFirstNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
        EsIndexParam cnLastNameParam = new EsIndexParam();
        cnLastNameParam.setFieldName("lastName");
        cnLastNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());

        EsIndexParam enFirstNameParam = new EsIndexParam();
        enFirstNameParam.setFieldName("firstName");
        enFirstNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
        EsIndexParam enLastNameParam = new EsIndexParam();
        enLastNameParam.setFieldName("lastName");
        enLastNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());

        EsIndexParam deptNameParam = new EsIndexParam();
        deptNameParam.setFieldName("allName");
        deptNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
        deptNameParam.setIndex(true);
        deptNameParam.setIgnoreAbove(50);
        wrapper.indexName("user_document")
                .alias("hello_user")
                .settings(1, 1, analysisMap)
                .mapping(UserDocument::getId, EsFieldTypeEnum.KEYWORD)
                .mapping(UserDocument::getNickname, EsFieldTypeEnum.KEYWORD, true)
                .mapping(UserDocument::getChineseName, Arrays.asList(cnFirstNameParam, cnLastNameParam))
                .mapping(UserDocument::getEnglishName, EsFieldTypeEnum.NESTED, Arrays.asList(enFirstNameParam, enLastNameParam))
                .mapping(UserDocument::getIdNumber, EsFieldTypeEnum.KEYWORD, false, 18)
                .mapping(UserDocument::getAge, EsFieldTypeEnum.INTEGER)
                .mapping(UserDocument::getGender, EsFieldTypeEnum.KEYWORD, false)
                .mapping(UserDocument::getBirthday, "yyyy-MM-dd")
                .mapping(UserDocument::getCompanyName, EsFieldTypeEnum.TEXT, true, null, UserDocument::getAll, "text_analyzer", EsAnalyzerEnum.IK_MAX_WORD, Arrays.asList(deptNameParam))
//                .mapping("enterprise", "text", true, null, "all", "text_analyzer", "ik_max_word", Arrays.asList(deptNameParam))
                .mapping(UserDocument::getCompanyAddress, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(UserDocument::getCompanyLocation, EsFieldTypeEnum.GEO_POINT)
                .mapping(UserDocument::getGeoLocation, EsFieldTypeEnum.GEO_SHAPE)
                .mapping(UserDocument::getRemark, EsFieldTypeEnum.TEXT, UserDocument::getAll, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(UserDocument::getAll, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                // "format": "yyyy-MM-dd HH:mm:ss || yyyy-MM-dd HH:mm:ss.SSS || yyyy-MM-dd || epoch_millis || strict_date_optional_time || yyyy-MM-dd'T'HH:mm:ss'+'08:00"
                .mapping(UserDocument::getHireDate, "yyyy-MM-dd")
                .mapping(UserDocument::getCreatedTime, "yyyy-MM-dd HH:mm:ss")
                .mapping(UserDocument::getUpdatedTime, "yyyy-MM-dd HH:mm:ss")
                .mapping(UserDocument::isDeleted, EsFieldTypeEnum.BOOLEAN);

        // 创建索引，调用此方法时，会先去调用代理对象中的invoke方法，由代理对象去执行具体的逻辑（创建索引的方法）
        // 即，当我们调用 UserDocumentMapper.createIndex() 方法的时候，
        // 这个方法会被EsMapperProxy.invoke()方法被拦截，
        // 就会直接使用EsMapperProxy.invoke方法的返回值，而不会去走真实的 UserDocumentMapper.createIndex()方法；
        // 当然也不能直接调用UserDocumentMapper中的方法，因为UserDocumentMapper并没有被实例化
        boolean isOk = userDocumentMapper.createIndex(wrapper);
        System.out.println(isOk);
    }

    @Test
    public void testUpdateIndex() {
        String indexName = "user_document";
        EsLambdaIndexWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaIndex()
                .indexName(indexName)
                .mapping(UserDocument::getGeoLocation, EsFieldTypeEnum.GEO_SHAPE);
        boolean isOk = userDocumentMapper.updateIndex(wrapper);
        System.out.println(isOk);
    }

    @Test
    public void testDeleteIndex() {
        String indexName = "user_document";
        boolean isOk = userDocumentMapper.deleteIndex(indexName);
        System.out.println(isOk);
    }

    @Test
    public void testDeleteIndexs() throws IOException {
        List<String> list = Arrays.asList("uia_skywalking_alarm_record-20230415",
                "uia_skywalking_alarm_record-20230416",
                "uia_skywalking_alarm_record-20230417",
                "uia_skywalking_browser_error_log-20230415",
                "uia_skywalking_browser_error_log-20230416",
                "uia_skywalking_browser_error_log-20230417",
                "uia_skywalking_endpoint_relation_server_side-20230414",
                "uia_skywalking_endpoint_relation_server_side-20230415",
                "uia_skywalking_endpoint_relation_server_side-20230416",
                "uia_skywalking_endpoint_relation_server_side-20230417",
                "uia_skywalking_endpoint_traffic-20230414",
                "uia_skywalking_endpoint_traffic-20230415",
                "uia_skywalking_endpoint_traffic-20230416",
                "uia_skywalking_endpoint_traffic-20230417",
                "uia_skywalking_events-20230412",
                "uia_skywalking_events-20230414",
                "uia_skywalking_events-20230415",
                "uia_skywalking_events-20230416",
                "uia_skywalking_events-20230417",
                "uia_skywalking_instance_traffic-20230414",
                "uia_skywalking_instance_traffic-20230415",
                "uia_skywalking_instance_traffic-20230416",
                "uia_skywalking_instance_traffic-20230417",
                "uia_skywalking_log-20230415",
                "uia_skywalking_log-20230416",
                "uia_skywalking_log-20230417",
                "uia_skywalking_meter-avg-20230414",
                "uia_skywalking_meter-avg-20230415",
                "uia_skywalking_meter-avg-20230416",
                "uia_skywalking_meter-avg-20230417",
                "uia_skywalking_meter-avglabeled-20230414",
                "uia_skywalking_meter-avglabeled-20230415",
                "uia_skywalking_meter-avglabeled-20230416",
                "uia_skywalking_meter-avglabeled-20230417",
                "uia_skywalking_metrics-all-20230417",
                "uia_skywalking_metrics-apdex-20230414",
                "uia_skywalking_metrics-apdex-20230415",
                "uia_skywalking_metrics-apdex-20230416",
                "uia_skywalking_metrics-apdex-20230417",
                "uia_skywalking_metrics-count-20230412",
                "uia_skywalking_metrics-count-20230414",
                "uia_skywalking_metrics-count-20230415",
                "uia_skywalking_metrics-count-20230416",
                "uia_skywalking_metrics-count-20230417",
                "uia_skywalking_metrics-cpm-20230414",
                "uia_skywalking_metrics-cpm-20230415",
                "uia_skywalking_metrics-cpm-20230416",
                "uia_skywalking_metrics-cpm-20230417",
                "uia_skywalking_metrics-doubleavg-20230414",
                "uia_skywalking_metrics-doubleavg-20230415",
                "uia_skywalking_metrics-doubleavg-20230416",
                "uia_skywalking_metrics-doubleavg-20230417",
                "uia_skywalking_metrics-histogram-20230414",
                "uia_skywalking_metrics-histogram-20230415",
                "uia_skywalking_metrics-histogram-20230416",
                "uia_skywalking_metrics-histogram-20230417",
                "uia_skywalking_metrics-longavg-20230414",
                "uia_skywalking_metrics-longavg-20230415",
                "uia_skywalking_metrics-longavg-20230416",
                "uia_skywalking_metrics-longavg-20230417",
                "uia_skywalking_metrics-max-20230414",
                "uia_skywalking_metrics-max-20230415",
                "uia_skywalking_metrics-max-20230416",
                "uia_skywalking_metrics-max-20230417",
                "uia_skywalking_metrics-percent-20230414",
                "uia_skywalking_metrics-percent-20230415",
                "uia_skywalking_metrics-percent-20230416",
                "uia_skywalking_metrics-percent-20230417",
                "uia_skywalking_metrics-percentile-20230414",
                "uia_skywalking_metrics-percentile-20230415",
                "uia_skywalking_metrics-percentile-20230416",
                "uia_skywalking_metrics-percentile-20230417",
                "uia_skywalking_metrics-rate-20230414",
                "uia_skywalking_metrics-rate-20230415",
                "uia_skywalking_metrics-rate-20230416",
                "uia_skywalking_metrics-rate-20230417",
                "uia_skywalking_metrics-sum-20230414",
                "uia_skywalking_metrics-sum-20230415",
                "uia_skywalking_metrics-sum-20230416",
                "uia_skywalking_metrics-sum-20230417",
                "uia_skywalking_network_address_alias-20230414",
                "uia_skywalking_network_address_alias-20230415",
                "uia_skywalking_network_address_alias-20230416",
                "uia_skywalking_network_address_alias-20230417",
                "uia_skywalking_profile_task-20230415",
                "uia_skywalking_profile_task-20230416",
                "uia_skywalking_profile_task-20230417",
                "uia_skywalking_profile_task_log-20230415",
                "uia_skywalking_profile_task_log-20230416",
                "uia_skywalking_profile_task_log-20230417",
                "uia_skywalking_profile_task_segment_snapshot-20230415",
                "uia_skywalking_profile_task_segment_snapshot-20230416",
                "uia_skywalking_profile_task_segment_snapshot-20230417",
                "uia_skywalking_segment-20230415",
                "uia_skywalking_segment-20230416",
                "uia_skywalking_segment-20230417",
                "uia_skywalking_service_instance_relation_client_side-20230414",
                "uia_skywalking_service_instance_relation_client_side-20230415",
                "uia_skywalking_service_instance_relation_client_side-20230416",
                "uia_skywalking_service_instance_relation_client_side-20230417",
                "uia_skywalking_service_instance_relation_server_side-20230414",
                "uia_skywalking_service_instance_relation_server_side-20230415",
                "uia_skywalking_service_instance_relation_server_side-20230416",
                "uia_skywalking_service_instance_relation_server_side-20230417",
                "uia_skywalking_service_relation_client_side-20230414",
                "uia_skywalking_service_relation_client_side-20230415",
                "uia_skywalking_service_relation_client_side-20230416",
                "uia_skywalking_service_relation_client_side-20230417",
                "uia_skywalking_service_relation_server_side-20230414",
                "uia_skywalking_service_relation_server_side-20230415",
                "uia_skywalking_service_relation_server_side-20230416",
                "uia_skywalking_service_relation_server_side-20230417",
                "uia_skywalking_service_traffic-20230414",
                "uia_skywalking_service_traffic-20230415",
                "uia_skywalking_service_traffic-20230416",
                "uia_skywalking_service_traffic-20230417",
                "uia_skywalking_top_n_database_statement-20230415",
                "uia_skywalking_top_n_database_statement-20230416",
                "uia_skywalking_top_n_database_statement-20230417",
                "uia_skywalking_ui_template",
                "uia_skywalking_zipkin_span-20230415",
                "uia_skywalking_zipkin_span-20230416",
                "uia_skywalking_zipkin_span-20230417");
        for (String s : list) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(s);
            AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = response.isAcknowledged();
            System.out.println(acknowledged);
        }
    }
}
