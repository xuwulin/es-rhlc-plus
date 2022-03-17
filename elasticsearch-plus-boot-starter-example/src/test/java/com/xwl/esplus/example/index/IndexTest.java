package com.xwl.esplus.example.index;

import com.xwl.esplus.core.condition.LambdaEsIndexWrapper;
import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.example.document.TestDocument;
import com.xwl.esplus.example.mapper.TestDocumentBaseMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author xwl
 * @since 2022/3/11 18:22
 */
@SpringBootTest
public class IndexTest {
    @Resource
    private TestDocumentBaseMapper testDocumentMapper;

    @Test
    public void testExistsIndex() {
        // 测试是否存在指定名称的索引
        String indexName = TestDocument.class.getSimpleName().toLowerCase();
        boolean existsIndex = testDocumentMapper.existsIndex(indexName);
        Assert.assertTrue(existsIndex);
    }

    @Test
    public void testIndex() {
        LambdaEsIndexWrapper<TestDocument> wrapper = new LambdaEsIndexWrapper<>();
        wrapper.indexName(TestDocument.class.getSimpleName().toLowerCase());

        wrapper.mapping(TestDocument::getTitle, EsFieldTypeEnum.KEYWORD)
                .mapping(TestDocument::getLocation, EsFieldTypeEnum.GEO_POINT)
                .mapping(TestDocument::getGeoLocation, EsFieldTypeEnum.GEO_SHAPE)
                .mapping(TestDocument::getContent, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_SMART);

        // 设置分片及副本信息,可缺省
        wrapper.settings(3, 2);

        // 设置别名信息,可缺省
        wrapper.createAlias("hello_es_plus");
        // 创建索引，调用此方法时，会先去调用代理对象中的invoke方法，由代理对象去执行具体的逻辑（创建索引的方法）
        // 即，当我们调用 TestDocumentMapper.createIndex() 方法的时候，
        // 这个方法会被EsMapperProxy.invoke()方法被拦截，
        // 就会直接使用EsMapperProxy.invoke方法的返回值，而不会去走真实的 TestDocumentMapper.createIndex()方法；
        // 当然也不能直接调用TestDocumentMapper中的方法，因为TestDocumentMapper并没有被实例化
        boolean isOk = testDocumentMapper.createIndex(wrapper);
        Assert.assertTrue(isOk);
    }

    @Test
    public void testUpdateIndex() {
        // 测试更新索引
        LambdaEsIndexWrapper<TestDocument> wrapper = new LambdaEsIndexWrapper<>();
        // 指定要更新哪个索引
        String indexName = TestDocument.class.getSimpleName().toLowerCase();
        wrapper.indexName(indexName);
        wrapper.mapping(TestDocument::getCreator, EsFieldTypeEnum.KEYWORD);
        wrapper.mapping(TestDocument::getGmtCreate, EsFieldTypeEnum.DATE);
        boolean isOk = testDocumentMapper.updateIndex(wrapper);
        Assert.assertTrue(isOk);
    }

    @Test
    public void testDeleteIndex() {
        // 测试删除索引
        // 指定要删除哪个索引
        String indexName = TestDocument.class.getSimpleName().toLowerCase();
        boolean isOk = testDocumentMapper.deleteIndex(indexName);
        Assert.assertTrue(isOk);
    }
}
