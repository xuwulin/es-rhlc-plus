package com.xwl.esplus.core.wrapper.index;

import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.param.EsIndexSettingParam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 索引信息封装
 *
 * @author xwl
 * @since 2022/3/11 17:17
 */
public interface Index<Children, R> extends Serializable {

    /**
     * 设置索引名称
     *
     * @param indexName 索引名称
     * @return
     */
    Children indexName(String indexName);

    /**
     * 设置创建别名信息
     *
     * @param alias 别名
     * @return
     */
    Children alias(String alias);

    /**
     * 设置setting信息：分片数、副本数、自定义分词器等
     *
     * @param settingParam setting参数
     * @return
     */
    Children settings(EsIndexSettingParam settingParam);

    /**
     * 设置setting信息：分片数、副本数
     *
     * @param shards   分片数
     * @param replicas 副本数
     * @return
     */
    Children settings(Integer shards, Integer replicas);

    /**
     * 设置setting信息：分片数、副本数、自定义分词器等
     *
     * @param shards   分片数
     * @param replicas 副本数
     * @param analysis 自定义分词器配置，JSON格式
     * @return
     */
    Children settings(Integer shards, Integer replicas, String analysis);

    /**
     * 设置setting信息：分片数、副本数、自定义分词器等
     *
     * @param shards   分片数
     * @param replicas 副本数
     * @param analysis 自定义分词器配置，Map格式
     * @return
     */
    Children settings(Integer shards, Integer replicas, Map<String, Object> analysis);

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @return
     */
    default Children mapping(R column, EsFieldTypeEnum fieldType) {
        return mapping(column, fieldType, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @param analyzer  创建索引时的分词器类型
     * @return
     */
    default Children mapping(R column, EsFieldTypeEnum fieldType, EsAnalyzerEnum analyzer) {
        return mapping(column, fieldType, analyzer, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column     列
     * @param dateFormat 日期格式化
     * @return
     */
    Children mapping(R column, String dateFormat);

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param analyzer       创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @return
     */
    Children mapping(R column,
                     EsFieldTypeEnum fieldType,
                     EsAnalyzerEnum analyzer,
                     EsAnalyzerEnum searchAnalyzer);

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @param index     是否索引，默认true
     * @return
     */
    Children mapping(R column,
                     EsFieldTypeEnum fieldType,
                     boolean index);

    /**
     * 设置mapping信息
     *
     * @param column      列
     * @param fieldType   es中的类型
     * @param index       是否索引，默认true
     * @param ignoreAbove ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，
     *                    默认值256，超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     * @return
     */
    Children mapping(R column,
                     EsFieldTypeEnum fieldType,
                     boolean index,
                     Integer ignoreAbove);

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param copyTo         拷贝至哪个字段
     * @param analyzer       创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @return
     */
    Children mapping(R column,
                     EsFieldTypeEnum fieldType,
                     R copyTo,
                     EsAnalyzerEnum analyzer,
                     EsAnalyzerEnum searchAnalyzer);

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param index          是否索引，默认true
     * @param ignoreAbove    ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，
     *                       默认值256，超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     * @param copyTo         拷贝至哪个字段
     * @param analyzer       创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @param fields         多（子）字段信息列表
     * @return
     */
    Children mapping(R column,
                     EsFieldTypeEnum fieldType,
                     boolean index,
                     Integer ignoreAbove,
                     R copyTo,
                     EsAnalyzerEnum analyzer,
                     EsAnalyzerEnum searchAnalyzer,
                     List<EsIndexParam> fields);

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param index          是否索引，默认true
     * @param ignoreAbove    ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，
     *                       默认值256，超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     * @param copyTo         拷贝至哪个字段
     * @param analyzer       创建索引时的分词器类型，字符串，自定义分词器
     * @param searchAnalyzer 查询时的分词器类型
     * @param fields         多（子）字段信息列表
     * @return
     */
    Children mapping(R column,
                     EsFieldTypeEnum fieldType,
                     boolean index,
                     Integer ignoreAbove,
                     R copyTo,
                     String analyzer,
                     EsAnalyzerEnum searchAnalyzer,
                     List<EsIndexParam> fields);

    /**
     * 设置mapping信息，子对象
     *
     * @param column     列
     * @param properties 子对象信息列表
     * @return
     */
    Children mapping(R column, List<EsIndexParam> properties);

    /**
     * 设置mapping信息
     *
     * @param column    列名
     * @param fieldType es中的类型
     * @return 泛型
     */
    default Children mapping(String column, EsFieldTypeEnum fieldType) {
        return mapping(column, fieldType, true, null, null, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column    列名
     * @param fieldType es中的类型
     * @param analyzer  分词器类型
     * @return 泛型
     */
    default Children mapping(String column, EsFieldTypeEnum fieldType, String analyzer) {
        return mapping(column, fieldType, true, null, null, analyzer, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param index          是否索引，默认true
     * @param ignoreAbove    ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，
     *                       默认值256，超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     * @param copyTo         拷贝至哪个字段
     * @param analyzer       创建索引时的分词器类型，字符串，自定义分词器
     * @param searchAnalyzer 查询时的分词器类型
     * @param fields         多（子）字段信息列表
     * @return
     */
    Children mapping(String column,
                     EsFieldTypeEnum fieldType,
                     Boolean index,
                     Integer ignoreAbove,
                     String copyTo,
                     String analyzer,
                     String searchAnalyzer,
                     List<EsIndexParam> fields);

    /**
     * 用户自行指定mapping
     *
     * @param mapping mapping信息
     * @return
     */
    Children mapping(Map<String, Object> mapping);
}
