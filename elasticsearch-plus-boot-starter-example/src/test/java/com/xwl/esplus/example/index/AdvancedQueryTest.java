package com.xwl.esplus.example.index;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.example.document.UserDocument;
import com.xwl.esplus.example.mapper.UserDocumentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

    @Test
    public void testIncludes() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                // 只查询nickname,age字段
                .select(UserDocument::getNickname, UserDocument::getAge)
                .eq(UserDocument::getAge, 18);
        List<UserDocument> userDocuments = userDocumentMapper.selectList(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testExcludes() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                // 不查询nickname,age字段
                .notSelect(UserDocument::getNickname, UserDocument::getAge)
                .eq(UserDocument::getAge, 18);
        List<UserDocument> userDocuments = userDocumentMapper.selectList(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testExcludes2() {
        // 等价写法
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.select(UserDocument.class, ud -> !Objects.equals(ud.getColumn(), "nickname"));
        List<UserDocument> userDocuments = userDocumentMapper.selectList(wrapper);
        System.out.println(userDocuments);
    }

    @Test
    public void testOrderBy() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .orderByDesc(UserDocument::getAge);
        List<UserDocument> userDocuments = userDocumentMapper.selectList(wrapper);
        System.out.println(userDocuments);
    }
}
