package com.xwl.esplus.example.index;

import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.example.document.TestDocument;
import com.xwl.esplus.example.mapper.TestDocumentBaseMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
        String indexName = "test_document";
        boolean existsIndex = testDocumentMapper.existsIndex(indexName);
        Assert.assertTrue(existsIndex);
    }

    @Test
    public void testCreateIndex() {
        EsLambdaIndexWrapper<TestDocument> wrapper = new EsLambdaIndexWrapper<>();
        wrapper.indexName("test_document");
        List<EsIndexParam> fields = new ArrayList<>();
        EsIndexParam field = new EsIndexParam();
        field.setFieldName("first");
        field.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
        field.setIgnoreAbove(20);
        fields.add(field);
        wrapper.mapping(TestDocument::getId, EsFieldTypeEnum.KEYWORD)
                .mapping(TestDocument::getTitle, EsFieldTypeEnum.KEYWORD, true)
                .mapping(TestDocument::getContent, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(TestDocument::getRemark, EsFieldTypeEnum.TEXT, TestDocument::getAll, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(TestDocument::getAuthor, EsFieldTypeEnum.KEYWORD)
                .mapping(TestDocument::getNickname, EsFieldTypeEnum.KEYWORD, false, 20)
                .mapping(TestDocument::getAge, EsFieldTypeEnum.INTEGER, false)
                .mapping(TestDocument::getCreatedTime, EsFieldTypeEnum.DATE)
                .mapping(TestDocument::getLocation, EsFieldTypeEnum.GEO_POINT)
                .mapping(TestDocument::getAll, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(TestDocument::getAddress, EsFieldTypeEnum.TEXT, true, null, TestDocument::getAll, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD, new EsLambdaIndexWrapper<TestDocument>()
                        .mapping(TestDocument::getAddr, EsFieldTypeEnum.KEYWORD, true, 20)
                        .getEsIndexParamList())
                .mapping(TestDocument::getFullName, new EsLambdaIndexWrapper<TestDocument>()
                        .mapping(TestDocument::getFirstName, EsFieldTypeEnum.KEYWORD, true, 20)
                        .mapping(TestDocument::getLastName, EsFieldTypeEnum.KEYWORD, false, 50)
                        .getEsIndexParamList())
                .mapping(TestDocument::getCustomize, EsFieldTypeEnum.TEXT, true, null, null, "customize", EsAnalyzerEnum.IK_SMART, null);

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
        EsLambdaIndexWrapper<TestDocument> wrapper = new EsLambdaIndexWrapper<>();
        // 指定要更新哪个索引
        String indexName = "test_document";
        wrapper.indexName(indexName);
        wrapper.mapping(TestDocument::getAuthor, EsFieldTypeEnum.KEYWORD);
        boolean isOk = testDocumentMapper.updateIndex(wrapper);
        Assert.assertTrue(isOk);
    }

    @Test
    public void testDeleteIndex() {
        // 测试删除索引
        // 指定要删除哪个索引
        String indexName = "test_document";
        boolean isOk = testDocumentMapper.deleteIndex(indexName);
        Assert.assertTrue(isOk);
    }
}
