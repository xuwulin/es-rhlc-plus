package com.xwl.esplus.test;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 条件构造器测试
 *
 * @author xwl
 * @since 2022/5/7 12:18
 */
@SpringBootTest
public class WrapperTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testEq() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .eq(UserDocument::getNickname, "张三疯");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testEn() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .ne(UserDocument::getNickname, "张三疯");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testMatch() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .match(UserDocument::getCompanyAddress, "成都市");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testNotMatch() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .notMatch(UserDocument::getCompanyAddress, "成都市");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testGt() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .gt(UserDocument::getAge, 11);
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testGe() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .ge(UserDocument::getAge, 100);
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testLt() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .lt(UserDocument::getAge, 100);
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testLe() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .le(UserDocument::getAge, 100);
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testBetween() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .between(UserDocument::getCreatedTime, "2022-01-01 00:00:00", "2022-06-01 00:00:00");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testNotBetween() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .notBetween(UserDocument::getCreatedTime, "2022-01-01 00:00:00", "2022-06-01 00:00:00");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testLike() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .like(UserDocument::getNickname, "张");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testNotLike() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .notLike(UserDocument::getNickname, "张");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testLikeLeft() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .likeLeft(UserDocument::getNickname, "三疯");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }

    @Test
    public void testLikeRight() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .likeRight(UserDocument::getNickname, "张");
        SearchResponse search = userDocumentMapper.search(wrapper);
        System.out.println(search);
    }
}
