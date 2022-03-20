package com.xwl.esplus.example.document;

import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.annotation.IndexName;
import com.xwl.esplus.core.enums.EsFieldStrategyEnum;
import com.xwl.esplus.core.enums.EsIdTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * es 数据模型
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Data
@IndexName("test_document")
public class TestDocument {
    /**
     * es中的唯一id
     */
    @EsDocumentId(value = "id", type = EsIdTypeEnum.AUTO)
    private String id;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档内容
     */
    private String content;
    /**
     * 备注
     */
    private String remark;
    /**
     * 作者 加EsDocumentField注解,并指明strategy = FieldStrategyEnum.NOT_EMPTY 表示更新的时候的策略为 创建者不为空字符串时才更新
     */
    @EsDocumentField(strategy = EsFieldStrategyEnum.NOT_EMPTY)
    private String author;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 年龄
     */
    private String age;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * es中实际不存在的字段,但模型中加了,为了不和es映射,可以在此类型字段上加上 注解@DocumentField,并指明exist=false
     */
    @EsDocumentField(exist = false)
    private String notExistsField;
    /**
     * 地理位置纬经度坐标 例如: "30.643077,104.023769"
     */
    private String location;
    /**
     * 拷贝目标
     */
    private String all;
    /**
     * 地址
     */
    private String address;
    private String addr;
    /**
     * fullName
     */
    private String fullName;

    private String firstName;
    private String lastName;
    private String customize;
}
