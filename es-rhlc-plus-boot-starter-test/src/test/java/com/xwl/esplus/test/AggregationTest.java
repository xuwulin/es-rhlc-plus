package com.xwl.esplus.test;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.core.wrapper.query.SubAggregation;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.test.document.EsUrbanBrainMapInfo;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.document.WorkOrderDocument;
import com.xwl.esplus.test.mapper.EsUrbanBrainMapInfoMapper;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import com.xwl.esplus.test.mapper.WorkOrderDocumentMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.List;

/**
 * 聚合查询测试
 *
 * @author xwl
 * @since 2022/4/8 16:24
 */
@SpringBootTest
public class AggregationTest {

    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Resource
    private WorkOrderDocumentMapper workOrderDocumentMapper;

    @Test
    public void testMax() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .max(UserDocument::getAge)
                .size(0);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testMin() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .min(UserDocument::getAge);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testAvg() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .avg(UserDocument::getAge);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testSum() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .sum(UserDocument::getAge);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testStats() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .stats(UserDocument::getAge).size(0);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testGroupBy() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .groupBy(UserDocument::getAge)
                .size(0);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testTermsAggregation() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .termsAggregation(UserDocument::getNickname).size(0);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testSubAggregation() {
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .termsAggregation(UserDocument::getNickname,
                        SubAggregation.termsAggregation(UserDocument::getAge))
                .size(0);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testDateHistogram() {
        DateHistogramInterval interval = DateHistogramInterval.DAY;
        String format = "yyyy-MM-dd";
        long minDocCount = 0;
        ExtendedBounds extendedBounds = new ExtendedBounds("2022-03-25", "2022-04-08");
        ZoneId timeZone = ZoneId.systemDefault();
        EsLambdaQueryWrapper<UserDocument> wrapper = Wrappers.<UserDocument>lambdaQuery()
                .dateHistogram(interval, format, minDocCount, extendedBounds, timeZone, UserDocument::getCreatedTime)
                .size(0);
        SearchResponse response = userDocumentMapper.search(wrapper);
        System.out.println(response);
    }

    @Test
    public void testGroupByMulti() {
        EsLambdaQueryWrapper<WorkOrderDocument> wrapper = Wrappers.<WorkOrderDocument>lambdaQuery()
                .groupBy(WorkOrderDocument::getFromAreaName)
                .groupBy(WorkOrderDocument::getWfStatusCn);
        List<WorkOrderDocument> list = workOrderDocumentMapper.list(wrapper);
        System.out.println(list);
    }
}
