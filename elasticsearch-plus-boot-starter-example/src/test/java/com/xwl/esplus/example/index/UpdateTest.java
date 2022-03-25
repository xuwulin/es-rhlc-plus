package com.xwl.esplus.example.index;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import com.xwl.esplus.example.document.UserDocument;
import com.xwl.esplus.example.mapper.UserDocumentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xwl
 * @since 2022/3/18 10:34
 */
@SpringBootTest
public class UpdateTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testUpdate() {
        UserDocument userDocument = new UserDocument();
        userDocument.setAge(99);
        EsLambdaUpdateWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaUpdate()
                .eq(UserDocument::getAge, "100");
        userDocumentMapper.update(userDocument, wrapper);
    }

    @Test
    public void testUpdateById() {
        UserDocument userDocument = new UserDocument();
        userDocument.setId("Imf7wH8Brppw3wlArMVX");
        userDocument.setAge(100);
        userDocumentMapper.updateById(userDocument);
    }

    @Test
    public void testUpdateBatchById() {
        UserDocument userDocument = new UserDocument();
        userDocument.setId("Imf7wH8Brppw3wlArMVX");
        userDocument.setAge(98);

        UserDocument userDocument2 = new UserDocument();
        userDocument2.setId("I2f7wH8Brppw3wlArMVX");
        userDocument2.setAge(99);
        List<UserDocument> list = new ArrayList<>();
        list.add(userDocument);
        list.add(userDocument2);
        userDocumentMapper.updateBatchById(list);
    }
}
