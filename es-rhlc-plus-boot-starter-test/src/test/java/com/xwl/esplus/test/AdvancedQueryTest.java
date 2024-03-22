package com.xwl.esplus.test;

import com.alibaba.fastjson.JSON;
import com.xwl.esplus.core.cache.GlobalConfigCache;
import com.xwl.esplus.core.config.GlobalConfig;
import com.xwl.esplus.core.metadata.DocumentInfo;
import com.xwl.esplus.core.param.EsOrderByParam;
import com.xwl.esplus.core.toolkit.DocumentInfoUtils;
import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.test.document.EsUrbanBrainMapInfo;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.EsUrbanBrainMapInfoMapper;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.xwl.esplus.core.wrapper.processor.EsWrapperProcessor.buildSearchSourceBuilder;

/**
 * 进阶查询测试
 *
 * @author xwl
 * @since 2022/4/8 16:24
 */
@SpringBootTest
public class AdvancedQueryTest {

    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Resource
    private EsUrbanBrainMapInfoMapper esUrbanBrainMapInfoMapper;

    @Test
    public void testIncludes() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                // 只查询nickname,age字段
                .select(UserDocument::getNickname, UserDocument::getAge)
                .eq(UserDocument::getAge, 18);
        List<UserDocument> userDocuments = userDocumentMapper.list(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testExcludes() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                // 不查询nickname,age字段
                .notSelect(UserDocument::getNickname, UserDocument::getAge)
                .eq(UserDocument::getAge, 18);
        List<UserDocument> userDocuments = userDocumentMapper.list(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testExcludes2() {
        // 等价写法
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.select(UserDocument.class, ud -> !Objects.equals(ud.getFieldName(), "nickname"));
        List<UserDocument> userDocuments = userDocumentMapper.list(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testOrderBy() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .orderByDesc(UserDocument::getAge);
        List<UserDocument> userDocuments = userDocumentMapper.list(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testSortByScore() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.match(UserDocument::getCompanyName, "科技");
        wrapper.sortByScore(SortOrder.ASC);
        List<UserDocument> documents = userDocumentMapper.list(wrapper);
        System.out.println(documents);
    }

    @Test
    public void testOrderByParams() {
        String jsonParam = "[{\"order\":\"createdTime\",\"sort\":\"DESC\"},{\"order\":\"nickname\",\"sort\":\"ASC\"}]";
        List<EsOrderByParam> orderByParams = JSON.parseArray(jsonParam, EsOrderByParam.class);
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.match(UserDocument::getCompanyName, "科技")
                .orderBy(orderByParams);
        List<UserDocument> documents = userDocumentMapper.list(wrapper);
        System.out.println(documents);
    }

    @Test
    public void testSort() {
        // 随机获取数据
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.match(UserDocument::getCompanyName, "科技");
        Script script = new Script("Math.random()");
        ScriptSortBuilder scriptSortBuilder = new ScriptSortBuilder(script, ScriptSortBuilder.ScriptSortType.NUMBER);
        wrapper.sort(scriptSortBuilder);
        List<UserDocument> documents = userDocumentMapper.list(wrapper);
        System.out.println(documents);
    }

    @Test
    public void testMatch() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .match(UserDocument::getCompanyName, "科技");
        List<UserDocument> userDocuments = userDocumentMapper.list(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testWeight() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        float nicknameBoost = 5.0f;
        String nickname = "张三疯";
        wrapper.eq(UserDocument::getNickname, nickname, nicknameBoost);
        float companyNameBoost = 2.0f;
        String companyName = "科技";
        wrapper.match(UserDocument::getCompanyName, companyName, companyNameBoost);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testHighlight() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        String keyword = "科技";
        wrapper.match(UserDocument::getCompanyName, keyword);
        wrapper.highLight(UserDocument::getCompanyName);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testHighlight2() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        String keyword = "科技";
        wrapper.match(UserDocument::getCompanyName, keyword);
        wrapper.highLight(UserDocument::getCompanyName);
        DocumentInfo documentInfo = DocumentInfoUtils.getDocumentInfo(UserDocument.class);
        GlobalConfig globalConfig = GlobalConfigCache.getGlobalConfig();
        List<UserDocument> userDocuments = userDocumentMapper.list(wrapper);
        System.out.println(JSON.toJSONString(userDocuments));
    }

    @Test
    public void testBetween() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.between(UserDocument::getCreatedTime, "2022-03-01 00:00:00", "2022-04-11 00:00:00");
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testWrapperWithTrackTotalHits() {
        EsLambdaQueryWrapper<EsUrbanBrainMapInfo> wrapper = Wrappers.<EsUrbanBrainMapInfo>lambdaQuery();
        SearchResponse response = esUrbanBrainMapInfoMapper.search(wrapper, true);
        System.out.println(response);
    }

    @Test
    public void testSearchWithTrackTotalHits() {
        SearchRequest searchRequest = new SearchRequest("12345_urban_brain_map_info");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse res = esUrbanBrainMapInfoMapper.search(searchRequest, RequestOptions.DEFAULT, true);
        System.out.println(res);
    }

    @Test
    public void andOr() {
        EsLambdaQueryWrapper<EsUrbanBrainMapInfo> wrapper = Wrappers.<EsUrbanBrainMapInfo>lambdaQuery();
        wrapper.and(w -> w.and(tel -> tel.eq(EsUrbanBrainMapInfo::getWfType, "tel")
                        .eq(EsUrbanBrainMapInfo::getDCntName, "企业分类")
                        .matchPhrase(EsUrbanBrainMapInfo::getFromContent, "企业件"))
                .or()
                .and(mail -> mail.eq(EsUrbanBrainMapInfo::getWfType, "mail")
                        .eq(EsUrbanBrainMapInfo::getDCntName, "企业分类")
                        .eq(EsUrbanBrainMapInfo::getFromSex, 2)));
        SearchResponse search = esUrbanBrainMapInfoMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void andOr2() {
        EsLambdaQueryWrapper<EsUrbanBrainMapInfo> wrapper = Wrappers.<EsUrbanBrainMapInfo>lambdaQuery()
                // 时间
                .between(EsUrbanBrainMapInfo::getAppealTime, "2023-03-21 00:00:00", "2024-03-21 23:59:59")
                // 过滤无坐标数据
                .isNotNull(EsUrbanBrainMapInfo::getLonlat);

        SearchSourceBuilder searchSourceBuilder = buildSearchSourceBuilder(wrapper, EsUrbanBrainMapInfo.class);
        QueryBuilder query = searchSourceBuilder.query();

        TermsQueryBuilder topicTermsQueryBuilder = QueryBuilders.termsQuery("topic", "出租车", "易投诉人群");
        TermsQueryBuilder portraitTermsQueryBuilder = QueryBuilders.termsQuery("portrait", "出租车", "易投诉人群");
        BoolQueryBuilder topicBoolQuery = QueryBuilders.boolQuery();
        topicBoolQuery.should(topicTermsQueryBuilder).should(portraitTermsQueryBuilder);
//        topicBoolQuery.should();
//        topicBoolQuery.should(QueryBuilders.termsQuery("portrait", "出租车", "易投诉人群"));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(query).must(topicBoolQuery);

//        searchSourceBuilder.postFilter(boolQuery);
        searchSourceBuilder.query(boolQuery);


        SearchRequest searchRequest = new SearchRequest("12345_urban_brain_map_info");
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = esUrbanBrainMapInfoMapper.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search);
    }

//    private void getAppealObjType(Integer appealObjType, EsLambdaQueryWrapper<EsUrbanBrainMapInfo> queryWrapper) {
//        //企业条件
//        if (ObjectUtils.isNotEmpty(appealObjType) && 1 == appealObjType) {
//            queryWrapper.and(w -> w.and(tel -> tel.eq(EsUrbanBrainMapInfo::getWfType, "tel").eq(EsUrbanBrainMapInfo::getDCntName, "企业分类").matchPhrase(EsUrbanBrainMapInfo::getFromContent, "企业件"))
//                    .or()
//                    .and(mail -> mail.eq(EsUrbanBrainMapInfo::getWfType, "mail").eq(EsUrbanBrainMapInfo::getDCntName, "企业分类").eq(EsUrbanBrainMapInfo::getFromSex, 2)));
//
//        } else if (ObjectUtils.isNotEmpty(appealObjType) && 2 == appealObjType) {
//            queryWrapper.and(w -> w.and(tel -> tel.eq(EsUrbanBrainMapInfo::getWfType, "tel").ne(EsUrbanBrainMapInfo::getDCntName, "企业分类").notMatch(EsUrbanBrainMapInfo::getFromContent, "企业件"))
//                    .or()
//                    .and(mail -> mail.eq(EsUrbanBrainMapInfo::getWfType, "mail").ne(EsUrbanBrainMapInfo::getDCntName, "企业分类").ne(EsUrbanBrainMapInfo::getFromSex, 2)));
//        }
//        System.out.println(queryWrapper.toString());
//    }

    @Test
    public void andOr3() {
        EsLambdaQueryWrapper<EsUrbanBrainMapInfo> wrapper = new EsLambdaQueryWrapper<>();
        // 时间
        wrapper.between(EsUrbanBrainMapInfo::getAppealTime, "2023-03-21 00:00:00", "2024-03-21 23:59:59")
                // 过滤无坐标数据
                .isNotNull(EsUrbanBrainMapInfo::getLonlat);

        wrapper.and(t -> t
                .in(EsUrbanBrainMapInfo::getTopic, "出租车", "易投诉人群")
                .or()
                .in(EsUrbanBrainMapInfo::getPortrait, "出租车", "易投诉人群")
        );
        SearchResponse search = esUrbanBrainMapInfoMapper.search(wrapper);
        System.out.println(search);
    }
}
