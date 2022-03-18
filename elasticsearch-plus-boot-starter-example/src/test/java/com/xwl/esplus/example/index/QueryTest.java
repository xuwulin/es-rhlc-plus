package com.xwl.esplus.example.index;

import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.example.document.WorkOrderDocument;
import com.xwl.esplus.example.mapper.WorkOrderDocumentMapper;
import lombok.SneakyThrows;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author xwl
 * @since 2022/3/16 17:14
 */
@SpringBootTest
public class QueryTest {

    @Resource
    private WorkOrderDocumentMapper workOrderDocumentMapper;

    @SneakyThrows
    @Test
    public void test() {
        EsLambdaQueryWrapper<WorkOrderDocument> wrapper = Wrappers.<WorkOrderDocument>lambdaQuery()
                .eq(WorkOrderDocument::getFromName, "@张")
                .eq(WorkOrderDocument::getFromStreet, "红牌楼街道");
        SearchResponse search = workOrderDocumentMapper.search(wrapper);
        String source = workOrderDocumentMapper.getSource(wrapper);
        System.out.println(source);
        System.out.println(search);
    }
}
