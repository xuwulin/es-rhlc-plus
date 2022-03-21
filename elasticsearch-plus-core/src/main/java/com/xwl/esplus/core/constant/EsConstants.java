package com.xwl.esplus.core.constant;

/**
 * 常量
 *
 * @author xwl
 * @since 2022/3/11 15:49
 */
public class EsConstants {
    /**
     * 数字0
     */
    public static final Integer ZERO = 0;
    /**
     * 数字1
     */
    public static final Integer ONE = 1;
    /**
     * 高亮默认前缀标签
     */
    public static final String HIGH_LIGHT_PRE_TAG = "<em>";
    /**
     * 高亮默认后缀标签
     */
    public static final String HIGH_LIGHT_POST_TAG = "</em>";
    /**
     * 默认的当前页码
     */
    public static final Integer PAGE_NUM = 1;
    /**
     * 默认的每页显示条目数
     */
    public static final Integer PAGE_SIZE = 10;
    /**
     * 默认字段boost权重
     */
    public static final Float DEFAULT_BOOST = 1.0F;
    /**
     * 空字符串
     */
    public static final String EMPTY_STR = "";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    /**
     * get 方法前缀
     */
    public static final String GET_FUNC_PREFIX = "get";
    /**
     * set 方法前缀
     */
    public static final String SET_FUNC_PREFIX = "set";
    /**
     * 获取id方法名
     */
    public static final String GET_ID_FUNC = "getId";
    /**
     * 基本数据类型的get方法前缀
     */
    public static final String IS_FUNC_PREFIX = "Is";
    /**
     * 分片数量字段
     */
    public static final String NUMBER_OF_SHARDS = "number_of_shards";
    /**
     * 副本数量字段
     */
    public static final String NUMBER_OF_REPLICAS = "number_of_replicas";
    /**
     * 自定义分词器
     */
    public static final String ANALYSIS = "index.analysis";
    /**
     * 索引特性/子对象
     */
    public static final String PROPERTIES = "properties";
    /**
     * 字段类型
     */
    public static final String TYPE = "type";
    /**
     * 是否索引该字段，默认true
     */
    public static final String INDEX = "index";
    /**
     * ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，默认值256，
     * 超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     */
    public static final String IGNORE_ABOVE = "ignore_above";
    /**
     * 拷贝
     */
    public static final String COPY_TO = "copy_to";
    /**
     * 创建索引时的分词器
     */
    public static final String ANALYZER = "analyzer";
    /**
     * 多（子）字段
     */
    public static final String FIELDS = "fields";
    /**
     * 搜索时的查询分词器
     */
    public static final String SEARCH_ANALYZER = "search_analyzer";
    /**
     * 通配符
     */
    public static final String WILDCARD_SIGN = "*";
    /**
     * es默认schema
     */
    public static final String DEFAULT_SCHEMA = "http";
    /**
     * 默认返回数
     */
    public static final Integer DEFAULT_SIZE = 10000;
}
