package com.xwl.esplus.test;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 删除文档测试
 *
 * @author xwl
 * @since 2022/3/11 18:22
 */
@SpringBootTest
public class DeleteTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testDelete() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .eq(UserDocument::getAge, 98);
        Integer delete = userDocumentMapper.remove(wrapper);
        Assert.assertTrue(delete > 0);
    }

    @Test
    public void testDeleteById() {
        Integer delete = userDocumentMapper.removeById("o8SfQoIByjUntWdVYrkO");
        Assert.assertTrue(delete > 0);
    }

    @Test
    public void testDeleteBatchByIds() {
        List<String> ids = Arrays.asList("JWdbwX8Brppw3wlACcXk", "JGdbwX8Brppw3wlACcXk");
        Integer delete = userDocumentMapper.removeByIds(ids);
        Assert.assertTrue(delete > 0);
    }

    @Test
    public void testDeleteBatchByIds2() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery();
        wrapper.isNotNull(UserDocument::getNickname)
                .and(w -> w.match(UserDocument::getCompanyName, "乌拉").or().eq(UserDocument::getCompanyName, "魔鬼"));
        int successCount = userDocumentMapper.remove(wrapper);
        System.out.println(successCount);
    }
}
