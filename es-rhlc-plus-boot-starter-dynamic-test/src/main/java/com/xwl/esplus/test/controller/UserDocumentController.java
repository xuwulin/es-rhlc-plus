package com.xwl.esplus.test.controller;

import com.xwl.esplus.core.annotation.EsClient;
import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.document.WorkOrderDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import com.xwl.esplus.test.mapper.WorkOrderDocumentMapper;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.xwl.esplus.core.constant.EsGlobalConstants.CLIENT_PREFIX;

/**
 * @author xwl
 * @since 2022/4/13 16:50
 */
@RestController
@RequestMapping("/user")
public class UserDocumentController {

    @Resource
    private UserDocumentMapper userDocumentMapper;

    @Resource
    private WorkOrderDocumentMapper workOrderDocumentMapper;


    @Resource
    private RestHighLevelClient restHighLevelClient_master;

    @Resource
    private RestHighLevelClient restHighLevelClient_slave;

    @GetMapping("/selectSlaveList")
    @EsClient("slave")
    public Object selectSlaveList() {
        List<UserDocument> userDocuments = userDocumentMapper.list(Wrappers.lambdaQuery());
        return userDocuments;
    }

    @GetMapping("/selectMasterList")
    @EsClient("master")
    public Object selectMasterList() {
        List<WorkOrderDocument> workOrderDocuments = workOrderDocumentMapper.list(Wrappers.lambdaQuery());
        return workOrderDocuments;
    }
}
