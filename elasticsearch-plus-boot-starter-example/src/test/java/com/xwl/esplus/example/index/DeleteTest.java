package com.xwl.esplus.example.index;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.example.document.UserDocument;
import com.xwl.esplus.example.mapper.UserDocumentMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
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
        Integer delete = userDocumentMapper.delete(wrapper);
        Assert.assertTrue(delete > 0);
    }

    @Test
    public void testDeleteById() {
        Integer delete = userDocumentMapper.deleteById("I2f7wH8Brppw3wlArMVX");
        Assert.assertTrue(delete > 0);
    }

    @Test
    public void testDeleteBatchByIds() {
        List<String> ids = Arrays.asList("JWdbwX8Brppw3wlACcXk", "JGdbwX8Brppw3wlACcXk");
        Integer delete = userDocumentMapper.deleteBatchByIds(ids);
        Assert.assertTrue(delete > 0);
    }
}
