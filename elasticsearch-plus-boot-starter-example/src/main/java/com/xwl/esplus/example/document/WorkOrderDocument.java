package com.xwl.esplus.example.document;

import com.xwl.esplus.core.annotation.IndexName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author xwl
 * @date 2021/8/14 14:56
 * @description 工单index
 */
@Data
@NoArgsConstructor
@IndexName("work_order")
public class WorkOrderDocument {
    /**
     * id
     */
    private String id;

    /**
     * 诉求时间
     */
    private Date appealTime;

    /**
     * 工单状态中文说明
     */
    private String wfStatusCn;

    /**
     * 工单主题
     */
    private String wfTopic;

    /**
     * 地区名称
     */
    private String fromAreaName;

    /**
     * 诉求人名称
     */
    private String fromName;

    /**
     * 诉求人性别：0男1女
     */
    private Integer fromSex;

    /**
     * 性别中文
     */
    private String fromSexCn;

    /**
     * 诉求来源地址
     */
    private String fromAddress;

    /**
     * 经纬度
     */
    private String location;

    /**
     * 诉求来源街道
     */
    private String fromStreet;

    /**
     * 诉求人电话
     */
    private String fromTel;

    /**
     * 诉求内容
     */
    private String fromContent;

    /**
     * 诉求类别名称
     */
    private String fromClaName;

    /**
     * 统一分类名称（最终）
     */
    private String classifyNameFinal;

    /**
     * 广告标识
     */
    private Boolean isAD;

    /**
     * 拼音补全字段，是一个集合/数组
     */
    private List<String> suggestion;

    /**
     * 距离
     */
    private Object distance;
}
