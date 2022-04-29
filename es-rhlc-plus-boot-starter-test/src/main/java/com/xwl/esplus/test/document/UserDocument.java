package com.xwl.esplus.test.document;

import com.alibaba.fastjson.annotation.JSONField;
import com.xwl.esplus.core.annotation.EsDocument;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.enums.EsKeyTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
     * chineseName，对象字段，必须添加自定义注解@EsDocumentField(isObj = true)
     * 支持集合、数组类型
     */
    @EsDocumentField(isObj = true)
    private ChineseName chineseName;
    /**
     * englishName，嵌套对象字段，必须添加自定义注解@EsDocumentField(nested = true)
     * 支持集合、数组类型
     */
    @EsDocumentField(isNested = true)
    private List<EnglishName> englishName;
//    private EnglishName[] englishName;
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
    private Date birthday;
    /**
     * 公司名称
     */
//    @EsHighLightField
    @EsDocumentField(value = "enterprise", isHighLight = true)
    private String companyName;
    /**
     * 公司地址
     */
//    @EsHighLightField
    @EsDocumentField(isHighLight = true)
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
     * 拷贝字段：es存在此字段，但是无需序列化，即新增或修改时无需给此字段赋值
     */
    private transient String all;
    /**
     * 其他，es中不存在此字段，使用@EsDocumentField(exist = false)标注
     */
    @EsDocumentField(exist = false)
    private String other;
    /**
     * 入职时间
     */
    @JSONField(format="yyyy-MM-dd")
    private LocalDate hireDate;
    /**
     * 创建日期
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;
    /**
     * 是否删除
     */
    @EsDocumentField(value = "is_deleted")
    private boolean deleted;

    /**
     * 对象：ChineseName
     */
    @Data
    @Accessors(chain = true)
    public static class ChineseName {
        private String firstName;
        private String lastName;
    }

    /**
     * 对象：EnglishName
     */
    @Data
    @Accessors(chain = true)
    public static class EnglishName {
        private String firstName;
        private String lastName;
    }

}
