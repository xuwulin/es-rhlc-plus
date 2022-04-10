package com.xwl.esplus.test.document;

import com.alibaba.fastjson.annotation.JSONField;
import com.xwl.esplus.core.annotation.EsDocument;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.enums.EsIdTypeEnum;
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
    @EsDocumentId(value = "_id", type = EsIdTypeEnum.AUTO)
    private String id;
    private String nickname;
    /**
     * fullName，子对象字段
     */
    private FullName fullName;
    private String idNumber;
    private Integer age;
    private String gender;
    @JSONField(format="yyyy-MM-dd")
    private Date birthdate;
    private String companyName;
    private String companyAddress;
    /**
     * 公司地址纬经度（30.643077,104.023769）
     */
    private String companyLocation;

    /**
     * 图形
     */
    private String geoLocation;

    @EsDocumentField(exist = false)
    private String remark;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String all;

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
