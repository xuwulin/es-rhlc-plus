package com.xwl.esplus.test;

import com.xwl.esplus.core.page.PageInfo;
import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 查询文档测试
 *
 * @author xwl
 * @since 2022/3/16 17:14
 */
@SpringBootTest
public class QueryTest {

    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testSearch() {
        SearchRequest searchRequest = new SearchRequest("user_document");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("nickname", "张三疯"));

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse res = userDocumentMapper.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(res);
    }

    @Test
    public void testSearchEq() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .eq(UserDocument::getNickname, "张三疯")
                .eq(UserDocument::getAge, "100");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testSearchLike() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .like(UserDocument::getCompanyName, "科技");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testSearchLikeLeft() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .likeLeft(UserDocument::getCompanyAddress, "武侯区");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testSearchLikeRight() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .likeRight(UserDocument::getCompanyAddress, "成都");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testSearchReg() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .reg(UserDocument::getTel, "(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])[0-9]{8}");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testSelectCount() {
        Long count = userDocumentMapper.count();
        System.out.println(count);
    }

    @Test
    public void testSelectCountByWrapper() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        Long count = userDocumentMapper.count(wrapper);
        System.out.println(count);
    }

    @Test
    public void testSelectList() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .eq(UserDocument::getAge, 100);
        List<UserDocument> list = userDocumentMapper.list(wrapper);
        System.out.println(list);
    }

    @Test
    public void testSelectMaps() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .select(UserDocument::getNickname, UserDocument::getCompanyName)
                .eq(UserDocument::getAge, 100);
        List<Map<String, Object>> maps = userDocumentMapper.listMaps(wrapper);
        System.out.println(maps);
    }

    @Test
    public void testSelectById() {
        UserDocument userDocument = userDocumentMapper.getById("KGfY038Brppw3wlAosUB");
        System.out.println(userDocument);
    }

    @Test
    public void testSelectOne() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .highLight(UserDocument::getNickname)
                .like(UserDocument::getNickname, "张三疯")
                .limit(1);
        UserDocument userDocument = userDocumentMapper.getOne(wrapper);
        System.out.println(userDocument);
    }

    @Test
    public void testSelectBatchIds() {
        List<UserDocument> list = userDocumentMapper.listByIds(Arrays.asList("KWfY038Brppw3wlAosUB", "PGcJA4ABrppw3wlAGcXh"));
        System.out.println(list);
    }

    @Test
    public void testPageOriginal() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        PageInfo<SearchHit> searchHitPageInfo = userDocumentMapper.pageOriginal(wrapper);
        System.out.println(searchHitPageInfo);
    }

    @Test
    public void testPageOriginalWith() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        PageInfo<SearchHit> searchHitPageInfo = userDocumentMapper.pageOriginal(wrapper, 1, 10);
        System.out.println(searchHitPageInfo);
    }

    @Test
    public void testSelectPage() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        PageInfo<UserDocument> pageInfo = userDocumentMapper.page(wrapper);
        System.out.println(pageInfo);
    }

    @Test
    public void testSelectPageWith() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        PageInfo<UserDocument> pageInfo = userDocumentMapper.page(wrapper, 10, 10);
        System.out.println(pageInfo);
    }

    @Test
    public void testSelectMapsPage() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .select(UserDocument::getNickname, UserDocument::getAge);
        PageInfo<Map<String, Object>> mapPageInfo = userDocumentMapper.pageMaps(wrapper);
        System.out.println(mapPageInfo);
    }

    @Test
    public void testSelectMapsPageWith() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .select(UserDocument::getNickname, UserDocument::getAge);
        PageInfo<Map<String, Object>> mapPageInfo = userDocumentMapper.pageMaps(wrapper, 1, 5);
        System.out.println(mapPageInfo);
    }
}
