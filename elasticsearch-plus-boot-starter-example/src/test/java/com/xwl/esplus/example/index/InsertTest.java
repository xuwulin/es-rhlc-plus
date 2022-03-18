package com.xwl.esplus.example.index;
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
public class InsertTest {
    @Resource
    private TestDocumentBaseMapper testDocumentMapper;

    @Test
    public void test() {
        TestDocument testDocument = new TestDocument();
        testDocument.setTitle("测试es-plus");
        testDocument.setContent("一个是阆苑仙葩，一个是美玉无瑕");
        testDocument.setCreator("曹雪芹");
        testDocument.setNotExistsField("高鹗");
        testDocument.setLocation("30.643077,104.023769");
        testDocumentMapper.insert(testDocument);
    }
}
