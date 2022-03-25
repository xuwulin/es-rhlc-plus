package com.xwl.esplus.example.index;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import com.xwl.esplus.example.document.UserDocument;
import com.xwl.esplus.example.mapper.UserDocumentMapper;
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
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void test() {
        UserDocument userDocument = new UserDocument();
        userDocument.setNickname("雪芹");
        EsLambdaUpdateWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaUpdate()
                .eq(UserDocument::getNickname, "曹雪芹");
        userDocumentMapper.update(userDocument, wrapper);
    }
}
