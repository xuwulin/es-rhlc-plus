package com.xwl.esplus.example.index;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import com.xwl.esplus.example.document.TestDocument;
import com.xwl.esplus.example.mapper.TestDocumentBaseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author xwl
 * @since 2022/3/18 10:34
 */
@SpringBootTest
public class UpdateTest {
    @Resource
    private TestDocumentBaseMapper testDocumentMapper;

    @Test
    public void test() {
        TestDocument testDocument = new TestDocument();
        testDocument.setContent("滴不尽相思血泪抛红豆");
        EsLambdaUpdateWrapper<TestDocument> wrapper = Wrappers.<TestDocument>lambdaUpdate()
                .eq(TestDocument::getAuthor, "曹雪芹");
        testDocumentMapper.update(testDocument, wrapper);
    }
}
