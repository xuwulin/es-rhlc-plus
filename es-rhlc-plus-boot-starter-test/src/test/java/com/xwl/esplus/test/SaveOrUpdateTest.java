package com.xwl.esplus.test;

import com.xwl.esplus.core.toolkit.EsWrappers;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.elasticsearch.geometry.Point;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 更新文档测试
 *
 * @author xwl
 * @since 2022/3/18 10:34
 */
@SpringBootTest
public class SaveOrUpdateTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testSaveOrUpdate() throws ParseException {
        /*UserDocument userDocument = userDocumentMapper.getById("8iwoC4kBfUrASHwYis-q");
        userDocument.setNickname("张三疯5001==>testSaveOrUpdate");
        Integer integer = userDocumentMapper.saveOrUpdate(userDocument);
        System.out.println(integer);*/

        UserDocument userDocument = new UserDocument();
        userDocument.setId("111");
        userDocument.setNickname("张三疯1");
//            userDocument.setChineseName(new ChineseName().setFirstName("张").setLastName("三疯"));
        userDocument.setIdNumber(String.valueOf(1));
        userDocument.setAge(1);
        userDocument.setGender("男");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        userDocument.setBirthday(formatter.parse("1922-03-25 00:00:00"));
        userDocument.setCompanyName("四川云恒数联科技有限公司");
        userDocument.setCompanyAddress("成都市武侯区天府二街151号");
        userDocument.setCompanyLocation("30.584736,104.074091");
        Point point = new Point(10, 12);
        userDocument.setGeoLocation(point.toString());
        userDocument.setRemark("软件开发");
        userDocument.setCreatedTime(LocalDateTime.now());
        userDocument.setDeleted(false);

        Integer integer = userDocumentMapper.saveOrUpdate(userDocument);
        System.out.println(integer);

    }

    @Test
    public void testSaveOrUpdateByWrapper() {
        EsLambdaUpdateWrapper<UserDocument> wrapper = EsWrappers.<UserDocument>lambdaUpdate()
                .eq(UserDocument::getNickname, "张三疯5034");
        UserDocument userDocument = new UserDocument();
        userDocument.setNickname("张三疯5034==>testSaveOrUpdateByWrapper");

        Integer integer = userDocumentMapper.saveOrUpdate(userDocument, wrapper);
        System.out.println(integer);
    }

    @Test
    public void testSelectById() {
        UserDocument userDocument = userDocumentMapper.getById("yrhMC4kBFHMJJATcMNAK");
        System.out.println(userDocument);

        EsLambdaQueryWrapper<UserDocument> wrapper = EsWrappers.<UserDocument>lambdaQuery()
                .eq(UserDocument::getNickname, "testSaveOrUpdateBath==>save");
        List<UserDocument> list = userDocumentMapper.list(wrapper);
        System.out.println(list);
    }

    @Test
    public void testSaveOrUpdateBath() {
        UserDocument update = userDocumentMapper.getById("yrhMC4kBFHMJJATcMNAK");
        update.setNickname("testSaveOrUpdateBath==>update");

        UserDocument save = new UserDocument();
        save.setNickname("testSaveOrUpdateBath==>save");

        List<UserDocument> list = new ArrayList<>();
        list.add(update);
        list.add(save);
        Integer integer = userDocumentMapper.saveOrUpdateBatch(list);
        System.out.println(list);
        System.out.println(integer);
    }
}
