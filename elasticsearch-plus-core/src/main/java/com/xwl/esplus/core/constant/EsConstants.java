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
    public static final String SHARDS_FIELD = "index.number_of_shards";
    /**
     * 副本数量字段
     */
    public static final String REPLICAS_FIELD = "index.number_of_replicas";
    /**
     * 索引特性
     */
    public static final String PROPERTIES = "properties";
    /**
     * 字段类型
     */
    public static final String TYPE = "type";
    /**
     * 分词器
     */
    public static final String ANALYZER = "analyzer";
    /**
     * 查询分词器
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
