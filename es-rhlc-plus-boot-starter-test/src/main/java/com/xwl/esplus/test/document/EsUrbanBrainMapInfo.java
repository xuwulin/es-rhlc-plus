package com.xwl.esplus.test.document;

import com.xwl.esplus.core.annotation.EsDocument;
import com.xwl.esplus.core.annotation.EsDocumentField;
import com.xwl.esplus.core.annotation.EsDocumentId;
import com.xwl.esplus.core.enums.EsKeyTypeEnum;
import com.xwl.esplus.core.model.GeoPoint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @Description:
 * @Author: hl
 * @Date: 2022/3/31 15:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@EsDocument(value = "12345_urban_brain_map_info", keepGlobalIndexPrefix = false)
public class EsUrbanBrainMapInfo implements Serializable {

    @EsDocumentId(value = "_id", type = EsKeyTypeEnum.CUSTOMIZE)
    private String id;

    /**
     * 来源工单id
     */
    @EsDocumentField(value = "wfId")
    private Long wfId;

    /**
     * 来源话单id
     */
    @EsDocumentField(value = "originFk")
    private String originFk;

    /**
     * 诉求时间
     */
    @EsDocumentField(value = "appealTime")
    private Date appealTime;

    /**
     * 多诉时间
     */
    @EsDocumentField(value = "multiAppealTime")
    private Date multiAppealTime;

    /**
     * 工单状态编码
     */
    @EsDocumentField(value = "wfStatus")
    private String wfStatus;

    /**
     * 工单状态中文说明
     */
    @EsDocumentField(value = "wfStatusCn")
    private String wfStatusCn;


    /**
     * 工单类型：tel电话mail信件msg短信
     */
    @EsDocumentField(value = "wfType")
    private String wfType;

    /**
     * 工单类型中文
     */
    @EsDocumentField(value = "wfTypeCn")
    private String wfTypeCn;

    /**
     * 工单编号
     */
    @EsDocumentField(value = "wfFormNo")
    private String wfFormNo;

    /**
     * 工单主题
     */
    @EsDocumentField(value = "wfTopic")
    private String wfTopic;

    /**
     * 号码归属地
     */
    @EsDocumentField(value = "location")
    private String location;


    /**
     * 地区编号
     */
    @EsDocumentField(value = "fromAreaNo")
    private String fromAreaNo;

    /**
     * 地区名称
     */
    @EsDocumentField(value = "fromAreaName")
    private String fromAreaName;


    /**
     * 街道编号
     */
    @EsDocumentField(value = "fromStreetNo")
    private String fromStreetNo;
    /**
     * 街道名称
     */
    @EsDocumentField(value = "fromStreetName")
    private String fromStreetName;

    /**
     * 诉求人名称
     */
    @EsDocumentField(value = "fromName")
    private String fromName;

    /**
     * 诉求人性别：0男1女
     */
    @EsDocumentField(value = "fromSex")
    private Integer fromSex;

    /**
     * 一事多诉标签为1表示多诉工单
     */
    @EsDocumentField(value = "multiLitigation")
    private Integer multiLitigation;

    /**
     * 专题
     */
    @EsDocumentField(value = "topic")
    private Set<String> topic;
    /**
     * 主体（对象）
     */
    @EsDocumentField(value = "subject")
    private String subject;
    /**
     * 画像
     */
    @EsDocumentField(value = "portrait")
    private Set<String> portrait;

    /**
     * 车牌号
     */
    @EsDocumentField(value = "licensePlate")
    private Set<String> licensePlate;

    /**
     * 性别中文
     */
    @EsDocumentField(value = "fromSexCn")
    private String fromSexCn;

    /**
     * 诉求来源地址
     */
    @EsDocumentField(value = "fromAddress")
    private String fromAddress;


    /**
     * 坐标名称
     */
    @EsDocumentField(value = "objectName")
    private String objectName;

    /**
     * 坐标位置[经纬度]
     */
    @EsDocumentField(value = "lonlat", isObj = true)
    private GeoPoint lonlat;

    /**
     * 诉求人电话
     */
    @EsDocumentField(value = "fromTel")
    private String fromTel;

    /**
     * 诉求人电话
     */
    @EsDocumentField(value = "fromTelHash")
    private String fromTelHash;

    /**
     * 诉求内容
     */
    @EsDocumentField(value = "fromContent")
    private String fromContent;

    /**
     * 诉求类别编码
     */
    @EsDocumentField(value = "formClaNo")
    private String formClaNo;

    /**
     * 诉求类别名称
     */
    @EsDocumentField(value = "fromClaName")
    private String fromClaName;

    /**
     * 是否超期
     */
    @EsDocumentField(value = "isOverdue")
    private Integer isOverdue;

    /**
     * 是否延期
     */
    @EsDocumentField(value = "isDelay")
    private Integer isDelay;

    /**
     * 是否满意
     */
    @EsDocumentField(value = "isSatisfied")
    private Integer isSatisfied;

    /**
     * 是否办结（0：未办结 1：办结）
     */
    @EsDocumentField(value = "isDeal")
    private Integer isDeal;


    /**
     * 内容类别编码
     */
    @EsDocumentField(value = "contentFirstCode")
    private String contentFirstCode;
    /**
     * 内容类别编码
     */
    @EsDocumentField(value = "contentSecondCode")
    private String contentSecondCode;
    /**
     * 内容类别编码
     */
    @EsDocumentField(value = "contentFinalCode")
    private String contentFinalCode;

    /**
     * 内容类别编码
     */
    @EsDocumentField(value = "contentFirstName")
    private String contentFirstName;
    /**
     * 内容类别编码
     */
    @EsDocumentField(value = "contentSecondName")
    private String contentSecondName;
    /**
     * 内容类别编码
     */
    @EsDocumentField(value = "contentFinalName")
    private String contentFinalName;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFirstCode")
    private String newContentFirstCode;

    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFirstName")
    private String newContentFirstName;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentSecondCode")
    private String newContentSecondCode;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentSecondName")
    private String newContentSecondName;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentThirdCode")
    private String newContentThirdCode;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentThirdName")
    private String newContentThirdName;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFourthCode")
    private String newContentFourthCode;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFourthName")
    private String newContentFourthName;

    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFifthCode")
    private String newContentFifthCode;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFifthName")
    private String newContentFifthName;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFinalCode")
    private String newContentFinalCode;


    /**
     * 内容类别编码（新编码5级）
     */
    @EsDocumentField(value = "newContentFinalName")
    private String newContentFinalName;


    /**
     * 办理部门（字符）
     */
    @EsDocumentField(value = "handleOrgs", isObj = true)
    private Set<String> handleOrgs;

    /**
     * 办理部门（对象）
     */
    @EsDocumentField(value = "handleOrgSet", isObj = true, isNested = true)
    private Set<HandleOrg> handleOrgSet;

    /**
     * 聚类eventId
     */
    @EsDocumentField(value = "eventId")
    private String eventId;

    /**
     * 聚类eventName
     */
    @EsDocumentField(value = "eventName")
    private String eventName;

    /**
     * 入库时间
     */
    @EsDocumentField(value = "extTimestamp")
    private Date extTimestamp;

    /**
     * 手工标签
     */
    @EsDocumentField(value = "tags", isObj = true)
    private Set<String> tags;


    @EsDocumentField(value = "workOrderSource")
    private Integer workOrderSource;

    @EsDocumentField(value = "dCntCode")
    private Integer dCntCode;

    @EsDocumentField(value = "dCntName")
    private String dCntName;

    @EsDocumentField(value = "pDdCntCode")
    private Integer pDdCntCode;

    @EsDocumentField(value = "pDCntName")
    private String pDCntName;


    @EsDocumentField(value = "appealType")
    private String appealType;

    @EsDocumentField(value = "mark")
    private String mark;

    @EsDocumentField(value = "dFmClaName")
    private String dFmClaName;

    @EsDocumentField(value = "dFmClaNo")
    private String dFmClaNo;

    @EsDocumentField(value = "crtStepId")
    private String crtStepId;

    @EsDocumentField(value = "crtStepName")
    private String crtStepName;


    @Data
    @Accessors(chain = true)
    public static class HandleOrg {
        /**
         * 转办机构（
         */
        @EsDocumentField(value = "assignOrg")
        private String assignOrg;

        /**
         * 转办机构
         */
        @EsDocumentField(value = "assignOrgId")
        private String assignOrgId;

        @EsDocumentField(value = "backTime")
        private Date backTime;

        @EsDocumentField(value = "getTime")
        private Date getTime;
    }
}
