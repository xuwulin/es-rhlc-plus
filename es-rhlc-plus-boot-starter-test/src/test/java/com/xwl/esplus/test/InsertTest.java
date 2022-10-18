package com.xwl.esplus.test;

import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.document.UserDocument.ChineseName;
import com.xwl.esplus.test.document.UserDocument.EnglishName;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import org.elasticsearch.geometry.Point;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 插入文档测试
 *
 * @author xwl
 * @since 2022/3/18 10:34
 */
@SpringBootTest
public class InsertTest {
    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Test
    public void testInsert() throws ParseException {
        UserDocument userDocument = new UserDocument();
        userDocument.setNickname("哈哈哈");
        userDocument.setChineseName(new ChineseName().setFirstName("王").setLastName("三"));
//        userDocument.setEnglishName(new EnglishName[]{new EnglishName().setFirstName("zhang").setLastName("san"), new EnglishName().setFirstName("li").setLastName("si")});
        userDocument.setEnglishName(Arrays.asList(new EnglishName().setFirstName("wang").setLastName("san")));
        userDocument.setIdNumber("1003");
        userDocument.setAge(88);
        userDocument.setGender("男");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        userDocument.setBirthday(formatter.parse("1922-03-25 00:00:00"));
        userDocument.setCompanyName("四川云恒数联科技有限公司");
        userDocument.setCompanyAddress("成都市武侯区天府二街151号");
        userDocument.setCompanyLocation("30.584736,104.074091");
        Point point = new Point(10, 12);
        userDocument.setGeoLocation(point.toString());
        userDocument.setRemark("软件开发");
        userDocument.setHireDate(LocalDate.now());
        userDocument.setCreatedTime(LocalDateTime.now());
        userDocument.setUpdatedTime(new Date());
        userDocument.setDeleted(false);

        userDocumentMapper.save(userDocument);
    }

    @Test
    public void testInsertBatch() throws ParseException {
        List<UserDocument> list = new ArrayList<>();
        for (int i = 5001; i <= 10009; i++) {
            UserDocument userDocument = new UserDocument();
            userDocument.setNickname("张三疯" + i);
//            userDocument.setChineseName(new ChineseName().setFirstName("张").setLastName("三疯"));
            userDocument.setIdNumber(String.valueOf(i));
            userDocument.setAge(i);
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
            list.add(userDocument);
        }
        userDocumentMapper.saveBatch(list);
    }
}
