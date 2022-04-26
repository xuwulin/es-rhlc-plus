package com.xwl.esplus.test.controller;

import com.xwl.esplus.core.toolkit.Wrappers;
import com.xwl.esplus.test.document.UserDocument;
import com.xwl.esplus.test.mapper.UserDocumentMapper;
import com.xwl.esplus.test.mapper.WorkOrderDocumentMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/selectList")
    public Object selectList() {
        List<UserDocument> userDocuments = userDocumentMapper.list(Wrappers.lambdaQuery());
        return userDocuments;
    }
}
