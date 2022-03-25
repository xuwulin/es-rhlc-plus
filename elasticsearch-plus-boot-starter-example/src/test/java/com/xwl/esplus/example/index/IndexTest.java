package com.xwl.esplus.example.index;

import com.alibaba.fastjson.JSONObject;
import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.example.document.UserDocument;
import com.xwl.esplus.example.mapper.UserDocumentMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

/**
 * @author xwl
 * @since 2022/3/11 18:22
 */
@SpringBootTest
public class IndexTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testExistsIndex() {
        // 测试是否存在指定名称的索引
        String indexName = "user_document";
        boolean existsIndex = userDocumentMapper.existsIndex(indexName);
        Assert.assertTrue(existsIndex);
    }

    @Test
    public void testCreateIndex() {
        EsLambdaIndexWrapper<UserDocument> wrapper = Wrappers.lambdaIndex();
        String analysis = "{\n" +
                "    \"analysis\": {\n" +
                "        \"analyzer\": {\n" +
                "            \"text_anlyzer\": {\n" +
                "                \"tokenizer\": \"ik_max_word\",\n" +
                "                \"filter\": \"py\"\n" +
                "            },\n" +
                "            \"completion_analyzer\": {\n" +
                "                \"tokenizer\": \"keyword\",\n" +
                "                \"filter\": \"py\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"filter\": {\n" +
                "            \"py\": {\n" +
                "                \"type\": \"pinyin\",\n" +
                "                \"keep_full_pinyin\": false,\n" +
                "                \"keep_joined_full_pinyin\": true,\n" +
                "                \"keep_original\": true,\n" +
                "                \"limit_first_letter_length\": 16,\n" +
                "                \"remove_duplicated_term\": true,\n" +
                "                \"none_chinese_pinyin_tokenize\": false\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        Map<String, Object> analysisMap = JSONObject.parseObject(analysis, Map.class);

        EsIndexParam firstNameParam = new EsIndexParam();
        firstNameParam.setFieldName("firstName");
        firstNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
        EsIndexParam lastNameParam = new EsIndexParam();
        lastNameParam.setFieldName("lastName");
        lastNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());

        EsIndexParam deptNameParam = new EsIndexParam();
        deptNameParam.setFieldName("allName");
        deptNameParam.setFieldType(EsFieldTypeEnum.KEYWORD.getType());
        deptNameParam.setIndex(true);
        deptNameParam.setIgnoreAbove(50);
        wrapper.indexName("user_document")
                .alias("hello_user")
                .settings(1, 1, analysisMap)
                .mapping(UserDocument::getId, EsFieldTypeEnum.KEYWORD)
                .mapping(UserDocument::getNickname, EsFieldTypeEnum.KEYWORD, true)
                .mapping(UserDocument::getFullName, Arrays.asList(firstNameParam, lastNameParam))
                .mapping(UserDocument::getIdNumber, EsFieldTypeEnum.KEYWORD, false, 18)
                .mapping(UserDocument::getAge, EsFieldTypeEnum.INTEGER)
                .mapping(UserDocument::getGender, EsFieldTypeEnum.KEYWORD, false)
                .mapping(UserDocument::getBirthdate, "yyyy-MM-dd")
                .mapping(UserDocument::getCompanyName, EsFieldTypeEnum.TEXT, true, null, UserDocument::getAll, "text_anlyzer", EsAnalyzerEnum.IK_MAX_WORD, Arrays.asList(deptNameParam))
                .mapping(UserDocument::getCompanyAddress, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(UserDocument::getCompanyLocation, EsFieldTypeEnum.GEO_POINT)
                .mapping(UserDocument::getRemark, EsFieldTypeEnum.TEXT, UserDocument::getAll, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD)
                .mapping(UserDocument::getCreatedTime, "yyyy-MM-dd HH:mm:ss")
                .mapping(UserDocument::getAll, EsFieldTypeEnum.TEXT, EsAnalyzerEnum.IK_SMART, EsAnalyzerEnum.IK_MAX_WORD);

        // 创建索引，调用此方法时，会先去调用代理对象中的invoke方法，由代理对象去执行具体的逻辑（创建索引的方法）
        // 即，当我们调用 UserDocumentMapper.createIndex() 方法的时候，
        // 这个方法会被EsMapperProxy.invoke()方法被拦截，
        // 就会直接使用EsMapperProxy.invoke方法的返回值，而不会去走真实的 UserDocumentMapper.createIndex()方法；
        // 当然也不能直接调用UserDocumentMapper中的方法，因为UserDocumentMapper并没有被实例化
        boolean isOk = userDocumentMapper.createIndex(wrapper);
        Assert.assertTrue(isOk);
    }

    @Test
    public void testUpdateIndex() {
        String indexName = "user_document";
        EsLambdaIndexWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaIndex()
                .indexName(indexName)
                .mapping(UserDocument::getNickname, EsFieldTypeEnum.KEYWORD);
        boolean isOk = userDocumentMapper.updateIndex(wrapper);
        Assert.assertTrue(isOk);
    }

    @Test
    public void testDeleteIndex() {
        String indexName = "user_document";
        boolean isOk = userDocumentMapper.deleteIndex(indexName);
        Assert.assertTrue(isOk);
    }
}
