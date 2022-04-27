package com.xwl.esplus.test.document;

import com.alibaba.fastjson.annotation.JSONField;
import com.xwl.esplus.core.annotation.EsDocument;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.annotation.EsHighLightField;
import com.xwl.esplus.core.enums.EsKeyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author xwl
 * @since 2022/3/22 16:48
 */
@Data
@EsDocument(value = "user_document", keepGlobalIndexPrefix = false)
public class UserDocument {
    @EsDocumentId(value = "_id", type = EsKeyTypeEnum.AUTO)
    private String id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * fullName，子对象字段
     */
    private FullName fullName;
    /**
     * 身份证号码
     */
    private String idNumber;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 性别
     */
    private String gender;
    /**
     * 生日
     */
    @JSONField(format="yyyy-MM-dd")
    private Date birthdate;
    /**
     * 公司名称
     */
    @EsHighLightField
    @EsDocumentField(value = "company_name")
    private String companyName;
    /**
     * 公司地址
     */
    @EsHighLightField
    private String companyAddress;
    /**
     * 公司地址纬经度（30.643077,104.023769）
     */
    private String companyLocation;
    /**
     * 图形，测试geo_shape类型
     */
    private String geoLocation;
    /**
     * 备注
     */
    private String remark;
    /**
     * 拷贝字段
     */
    private String all;
    /**
     * 其他，es中不存在此字段
     */
    @EsDocumentField(exist = false)
    private String other;
    /**
     * 创建日期
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    /**
     * 是否删除
     */
    private boolean deleted;

    /**
     * 子对象
     */
    @Data
    @Accessors(chain = true)
    public static class FullName {
        private String firstName;
        private String lastName;
    }
}
