package com.xwl.esplus.core.param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基本参数
 *
 * @author xwl
 * @since 2022/3/16 10:59
 */
public class EsBaseParam {
    /**
     * 存放必须满足的条件列表(必须满足,相当于MySQL中的and)
     */
    private List<FieldValueModel> mustList = new ArrayList<>();
    /**
     * 存放多字段单值的必须满足条件列表 multiMatchQuery
     */
    private List<FieldValueModel> mustMultiFieldList = new ArrayList<>();
    /**
     * 存放必须满足的条件列表,区别是不计算得分(必须满足,与must区别是不计算得分,效率更高)
     */
    private List<FieldValueModel> filterList = new ArrayList<>();
    /**
     * 存放或条件列表(或,相当于MySQL中的or)
     */
    private List<FieldValueModel> shouldList = new ArrayList<>();
    /**
     * 存放多字段单值的或条件列表
     */
    private List<FieldValueModel> shouldMultiFieldList = new ArrayList<>();
    /**
     * 存放必须不满足的条件列表(否,相当于MySQL中的!=)
     */
    private List<FieldValueModel> mustNotList = new ArrayList<>();
    /**
     * 存放大于的条件列表(大于,相当于mysql中的>)
     */
    private List<FieldValueModel> gtList = new ArrayList<>();
    /**
     * 存放小于的条件列表(小于,相当于mysql中的<)
     */
    private List<FieldValueModel> ltList = new ArrayList<>();
    /**
     * 存放大于等于的条件列表(大于等于,相当于mysql中的>=)
     */
    private List<FieldValueModel> geList = new ArrayList<>();
    /**
     * 存放小于等于的条件列表(小于等于,相当于mysql中的<=)
     */
    private List<FieldValueModel> leList = new ArrayList<>();
    /**
     * 存放必须符合的多值条件列表(相当于mysql中的in)
     */
    private List<FieldValueModel> inList = new ArrayList<>();
    /**
     * 存放必须不符合的多值条件列表(相当于mysql中的not in)
     */
    private List<FieldValueModel> notInList = new ArrayList<>();
    /**
     * 存放为null的条件列表(相当于mysql中的 is null)
     */
    private List<FieldValueModel> isNullList = new ArrayList<>();
    /**
     * 存放不为null的条件列表(相当于mysql中的 not null)
     */
    private List<FieldValueModel> notNullList = new ArrayList<>();
    /**
     * 存放不在指定范围内的条件列表(相当于mysql中的 between)
     */
    private List<FieldValueModel> betweenList = new ArrayList<>();
    /**
     * 存放不在指定范围内的条件列表(相当于mysql中的 not between)
     */
    private List<FieldValueModel> notBetweenList = new ArrayList<>();
    /**
     * 存放左模糊的条件列表(相当于mysql中的like %xxx)
     */
    private List<FieldValueModel> likeLeftList = new ArrayList<>();
    /**
     * 存放右模糊的条件列表(相当于mysql中的like xxx%)
     */
    private List<FieldValueModel> likeRightList = new ArrayList<>();
    /**
     * 参数类型 参见: BaseEsParamTypeEnum
     */
    private Integer type;
    /**
     * must条件转filter
     */
    protected Boolean enableMust2Filter;

    /**
     * 查询模型
     */
    public static class FieldValueModel {
        /**
         * 字段名
         */
        private String field;
        /**
         * 值
         */
        private Object value;
        /**
         * 左区间值 仅between,notBetween时使用
         */
        private Object leftValue;
        /**
         * 右区间值 仅between,notBetween时使用
         */
        private Object rightValue;
        /**
         * 位置距离
         */
        private Integer slop;
        /**
         * boost权重值
         */
        private Float boost;
        /**
         * 值列表(仅in操作时使用)
         */
        private List<Object> values;
        /**
         * 查询类型 参见:EsQueryTypeEnum
         */
        private Integer esQueryType;
        /**
         * 连接类型 参见:EsAttachTypeEnum 由于should 包含转换的情况 所以转换之后应仍使用原来的连接类型
         */
        private Integer originalAttachType;

        /**
         * 字段列表
         */
        private List<String> fields;
        /**
         * 拓展字段
         */
        private Object ext;
        /**
         * 最小匹配度 百分比
         */
        private int minimumShouldMatch;
        /**
         * nested path
         */
        private String path;
        /**
         * 得分模式or是否计算得分
         */
        private Object scoreMode;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getLeftValue() {
            return leftValue;
        }

        public void setLeftValue(Object leftValue) {
            this.leftValue = leftValue;
        }

        public Object getRightValue() {
            return rightValue;
        }

        public void setRightValue(Object rightValue) {
            this.rightValue = rightValue;
        }

        public Float getBoost() {
            return boost;
        }

        public void setBoost(Float boost) {
            this.boost = boost;
        }

        public Integer getSlop() {
            return slop;
        }

        public void setSlop(Integer slop) {
            this.slop = slop;
        }

        public List<Object> getValues() {
            return values;
        }

        public void setValues(List<Object> values) {
            this.values = values;
        }

        public Integer getEsQueryType() {
            return esQueryType;
        }

        public void setEsQueryType(Integer esQueryType) {
            this.esQueryType = esQueryType;
        }

        public Integer getOriginalAttachType() {
            return originalAttachType;
        }

        public void setOriginalAttachType(Integer originalAttachType) {
            this.originalAttachType = originalAttachType;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public Object getExt() {
            return ext;
        }

        public void setExt(Object ext) {
            this.ext = ext;
        }

        public int getMinimumShouldMatch() {
            return minimumShouldMatch;
        }

        public void setMinimumShouldMatch(int minimumShouldMatch) {
            this.minimumShouldMatch = minimumShouldMatch;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Object getScoreMode() {
            return scoreMode;
        }

        public void setScoreMode(Object scoreMode) {
            this.scoreMode = scoreMode;
        }
    }

    public List<FieldValueModel> getMustList() {
        return mustList;
    }

    public void setMustList(List<FieldValueModel> mustList) {
        this.mustList = mustList;
    }

    public List<FieldValueModel> getMustMultiFieldList() {
        return mustMultiFieldList;
    }

    public void setMustMultiFieldList(List<FieldValueModel> mustMultiFieldList) {
        this.mustMultiFieldList = mustMultiFieldList;
    }

    public List<FieldValueModel> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<FieldValueModel> filterList) {
        this.filterList = filterList;
    }

    public List<FieldValueModel> getShouldList() {
        return shouldList;
    }

    public void setShouldList(List<FieldValueModel> shouldList) {
        this.shouldList = shouldList;
    }

    public List<FieldValueModel> getShouldMultiFieldList() {
        return shouldMultiFieldList;
    }

    public void setShouldMultiFieldList(List<FieldValueModel> shouldMultiFieldList) {
        this.shouldMultiFieldList = shouldMultiFieldList;
    }

    public List<FieldValueModel> getMustNotList() {
        return mustNotList;
    }

    public void setMustNotList(List<FieldValueModel> mustNotList) {
        this.mustNotList = mustNotList;
    }

    public List<FieldValueModel> getGtList() {
        return gtList;
    }

    public void setGtList(List<FieldValueModel> gtList) {
        this.gtList = gtList;
    }

    public List<FieldValueModel> getLtList() {
        return ltList;
    }

    public void setLtList(List<FieldValueModel> ltList) {
        this.ltList = ltList;
    }

    public List<FieldValueModel> getGeList() {
        return geList;
    }

    public void setGeList(List<FieldValueModel> geList) {
        this.geList = geList;
    }

    public List<FieldValueModel> getLeList() {
        return leList;
    }

    public void setLeList(List<FieldValueModel> leList) {
        this.leList = leList;
    }

    public List<FieldValueModel> getInList() {
        return inList;
    }

    public void setInList(List<FieldValueModel> inList) {
        this.inList = inList;
    }

    public List<FieldValueModel> getNotInList() {
        return notInList;
    }

    public void setNotInList(List<FieldValueModel> notInList) {
        this.notInList = notInList;
    }

    public List<FieldValueModel> getIsNullList() {
        return isNullList;
    }

    public void setIsNullList(List<FieldValueModel> isNullList) {
        this.isNullList = isNullList;
    }

    public List<FieldValueModel> getNotNullList() {
        return notNullList;
    }

    public void setNotNullList(List<FieldValueModel> notNullList) {
        this.notNullList = notNullList;
    }

    public List<FieldValueModel> getBetweenList() {
        return betweenList;
    }

    public void setBetweenList(List<FieldValueModel> betweenList) {
        this.betweenList = betweenList;
    }

    public List<FieldValueModel> getNotBetweenList() {
        return notBetweenList;
    }

    public void setNotBetweenList(List<FieldValueModel> notBetweenList) {
        this.notBetweenList = notBetweenList;
    }

    public List<FieldValueModel> getLikeLeftList() {
        return likeLeftList;
    }

    public void setLikeLeftList(List<FieldValueModel> likeLeftList) {
        this.likeLeftList = likeLeftList;
    }

    public List<FieldValueModel> getLikeRightList() {
        return likeRightList;
    }

    public void setLikeRightList(List<FieldValueModel> likeRightList) {
        this.likeRightList = likeRightList;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getEnableMust2Filter() {
        return enableMust2Filter;
    }

    public void setEnableMust2Filter(Boolean enableMust2Filter) {
        this.enableMust2Filter = enableMust2Filter;
    }

    /**
     * 重置查询条件 主要用于处理or查询条件
     *
     * @param esBaseParam 基础参数
     */
    public static void setUp(EsBaseParam esBaseParam) {
        // 获取原查询条件
        List<FieldValueModel> mustList = esBaseParam.getMustList();
        List<FieldValueModel> mustNotList = esBaseParam.getMustNotList();
        List<FieldValueModel> mustMultiFieldList = esBaseParam.getMustMultiFieldList();
        List<FieldValueModel> filterList = esBaseParam.getFilterList();
        List<FieldValueModel> shouldList = esBaseParam.getShouldList();
        List<FieldValueModel> shouldMultiFieldList = esBaseParam.getShouldMultiFieldList();
        List<FieldValueModel> gtList = esBaseParam.getGtList();
        List<FieldValueModel> ltList = esBaseParam.getLtList();
        List<FieldValueModel> geList = esBaseParam.getGeList();
        List<FieldValueModel> leList = esBaseParam.getLeList();
        List<FieldValueModel> betweenList = esBaseParam.getBetweenList();
        List<FieldValueModel> notBetweenList = esBaseParam.getNotBetweenList();
        List<FieldValueModel> inList = esBaseParam.getInList();
        List<FieldValueModel> notInList = esBaseParam.getNotInList();
        List<FieldValueModel> isNullList = esBaseParam.getIsNullList();
        List<FieldValueModel> notNullList = esBaseParam.getNotNullList();
        List<FieldValueModel> likeLeftList = esBaseParam.getLikeLeftList();
        List<FieldValueModel> likeRightList = esBaseParam.getLikeRightList();

        // 把原来必须满足的条件转入should列表
        shouldList.addAll(mustList);
        shouldList.addAll(mustNotList);
        shouldList.addAll(filterList);
        shouldList.addAll(gtList);
        shouldList.addAll(ltList);
        shouldList.addAll(geList);
        shouldList.addAll(leList);
        shouldList.addAll(betweenList);
        shouldList.addAll(notBetweenList);
        shouldList.addAll(inList);
        shouldList.addAll(notInList);
        shouldList.addAll(isNullList);
        shouldList.addAll(notNullList);
        shouldList.addAll(likeLeftList);
        shouldList.addAll(likeRightList);
        shouldMultiFieldList.addAll(mustMultiFieldList);
        esBaseParam.setShouldList(shouldList);

        // 置空原必须满足的条件列表
        esBaseParam.setMustList(Collections.EMPTY_LIST);
        esBaseParam.setMustNotList(Collections.EMPTY_LIST);
        esBaseParam.setFilterList(Collections.EMPTY_LIST);
        esBaseParam.setGtList(Collections.EMPTY_LIST);
        esBaseParam.setLtList(Collections.EMPTY_LIST);
        esBaseParam.setGeList(Collections.EMPTY_LIST);
        esBaseParam.setLeList(Collections.EMPTY_LIST);
        esBaseParam.setBetweenList(Collections.EMPTY_LIST);
        esBaseParam.setNotBetweenList(Collections.EMPTY_LIST);
        esBaseParam.setInList(Collections.EMPTY_LIST);
        esBaseParam.setNotInList(Collections.EMPTY_LIST);
        esBaseParam.setIsNullList(Collections.EMPTY_LIST);
        esBaseParam.setNotNullList(Collections.EMPTY_LIST);
        esBaseParam.setLikeLeftList(Collections.EMPTY_LIST);
        esBaseParam.setLikeRightList(Collections.EMPTY_LIST);
        esBaseParam.setMustMultiFieldList(Collections.EMPTY_LIST);
    }
}
