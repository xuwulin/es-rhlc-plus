package com.xwl.esplus.core.wrapper.index;

import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;
import com.xwl.esplus.core.param.EsIndexParam;
import com.xwl.esplus.core.param.EsIndexSettingParam;
import com.xwl.esplus.core.toolkit.FieldUtils;

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
    Children settings(Integer shards,
                      Integer replicas);

    /**
     * 设置setting信息：分片数、副本数、自定义分词器等
     *
     * @param shards   分片数
     * @param replicas 副本数
     * @param analysis 自定义分词器配置，JSON格式
     * @return
     */
    Children settings(Integer shards,
                      Integer replicas,
                      String analysis);

    /**
     * 设置setting信息：分片数、副本数、自定义分词器等
     *
     * @param shards   分片数
     * @param replicas 副本数
     * @param analysis 自定义分词器配置，Map格式
     * @return
     */
    Children settings(Integer shards,
                      Integer replicas,
                      Map<String, Object> analysis);

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        return mapping(fieldName, type, null, null, null, null, null, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param createAnalyzer 创建索引时的分词器类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             EsAnalyzerEnum createAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String analyzerType = createAnalyzer.getType();
        return mapping(fieldName, type, null, null, null, null, analyzerType, null, null, null);
    }

    /**
     * 设置mapping信息（date类型）
     *
     * @param column     列
     * @param dateFormat 日期格式化（date类型）
     * @return
     */
    default Children mapping(R column,
                             String dateFormat) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = EsFieldTypeEnum.DATE.getType();
        return mapping(fieldName, type, null, null, dateFormat, null, null, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param createAnalyzer 创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             EsAnalyzerEnum createAnalyzer,
                             EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String analyzerType = createAnalyzer.getType();
        String searchAnalyzerType = searchAnalyzer.getType();
        return mapping(fieldName, type, null, null, null, null, analyzerType, searchAnalyzerType, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param createAnalyzer 创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             String createAnalyzer,
                             String searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        return mapping(fieldName, type, null, null, null, null, createAnalyzer, searchAnalyzer, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param createAnalyzer 创建索引时的分词器类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             String createAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        return mapping(fieldName, type, null, null, null, null, createAnalyzer, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @param index     是否索引，默认true
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             boolean index) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        return mapping(fieldName, type, index, null, null, null, null, null, null, null);
    }

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
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             boolean index,
                             Integer ignoreAbove) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        return mapping(fieldName, type, index, ignoreAbove, null, null, null, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @param copyTo    拷贝至哪个字段
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             R copyTo) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String copy = FieldUtils.getFieldName(copyTo);
        return mapping(fieldName, type, null, null, null, copy, null, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param copyTo         拷贝至哪个字段
     * @param createAnalyzer 创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             R copyTo,
                             EsAnalyzerEnum createAnalyzer,
                             EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String copy = FieldUtils.getFieldName(copyTo);
        String analyzerType = createAnalyzer.getType();
        String searchAnalyzerType = searchAnalyzer.getType();
        return mapping(fieldName, type, null, null, null, copy, analyzerType, searchAnalyzerType, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param copyTo         拷贝至哪个字段
     * @param createAnalyzer 创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             R copyTo,
                             String createAnalyzer,
                             EsAnalyzerEnum searchAnalyzer) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String copy = FieldUtils.getFieldName(copyTo);
        String searchAnalyzerType = searchAnalyzer.getType();
        return mapping(fieldName, type, null, null, null, copy, createAnalyzer, searchAnalyzerType, null, null);
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
     * @param createAnalyzer 创建索引时的分词器类型
     * @param searchAnalyzer 查询时的分词器类型
     * @param fields         多（子）字段信息列表
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             boolean index,
                             Integer ignoreAbove,
                             R copyTo,
                             EsAnalyzerEnum createAnalyzer,
                             EsAnalyzerEnum searchAnalyzer,
                             List<EsIndexParam> fields) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String copy = FieldUtils.getFieldName(copyTo);
        String analyzerType = createAnalyzer.getType();
        String searchAnalyzerType = searchAnalyzer.getType();
        return mapping(fieldName, type, index, ignoreAbove, null, copy, analyzerType, searchAnalyzerType, null, fields);
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
     * @param createAnalyzer 创建索引时的分词器类型，字符串，自定义分词器
     * @param searchAnalyzer 查询时的分词器类型
     * @param fields         多（子）字段信息列表
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             boolean index,
                             Integer ignoreAbove,
                             R copyTo,
                             String createAnalyzer,
                             EsAnalyzerEnum searchAnalyzer,
                             List<EsIndexParam> fields) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        String copy = FieldUtils.getFieldName(copyTo);
        String searchAnalyzerType = searchAnalyzer.getType();
        return mapping(fieldName, type, index, ignoreAbove, null, copy, createAnalyzer, searchAnalyzerType, null, fields);
    }

    /**
     * 设置mapping信息，对象（object类型）
     *
     * @param column     列
     * @param properties 对象信息列表
     * @return
     */
    default Children mapping(R column,
                             List<EsIndexParam> properties) {
        String fieldName = FieldUtils.getFieldName(column);
        return mapping(fieldName, null, null, null, null, null, null, null, properties, null);

    }

    /**
     * 设置mapping信息，可指定为nested嵌套对象
     *
     * @param column     列
     * @param fieldType  es中的类型
     * @param properties 对象信息列表
     * @return
     */
    default Children mapping(R column,
                             EsFieldTypeEnum fieldType,
                             List<EsIndexParam> properties) {
        String fieldName = FieldUtils.getFieldName(column);
        String type = fieldType.getType();
        return mapping(fieldName, type, null, null, null, null, null, null, properties, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column    列名
     * @param fieldType es中的类型
     * @return 泛型
     */
    default Children mapping(String column,
                             EsFieldTypeEnum fieldType) {
        String type = fieldType.getType();
        return mapping(column, type, null, null, null, null, null, null, null, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列名
     * @param fieldType      es中的类型
     * @param createAnalyzer 分词器类型
     * @return 泛型
     */
    default Children mapping(String column,
                             EsFieldTypeEnum fieldType,
                             String createAnalyzer) {
        String type = fieldType.getType();
        return mapping(column, type, null, null, null, null, createAnalyzer, null, null, null);
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
     * @param createAnalyzer 创建索引时的分词器类型，字符串，自定义分词器
     * @param searchAnalyzer 查询时的分词器类型
     * @param fields         多（子）字段信息列表
     * @return
     */
    default Children mapping(String column,
                             EsFieldTypeEnum fieldType,
                             Boolean index,
                             Integer ignoreAbove,
                             String copyTo,
                             String createAnalyzer,
                             String searchAnalyzer,
                             List<EsIndexParam> fields) {
        String type = fieldType.getType();
        return mapping(column, type, index, ignoreAbove, null, copyTo, createAnalyzer, searchAnalyzer, null, fields);
    }

    /**
     * 设置mapping信息，properties和fields不能同时存在
     *
     * @param fieldName      es中的字段名称
     * @param fieldType      es中的类型
     * @param index          是否索引，默认true
     * @param ignoreAbove    ignoreAbove：字符串长度限定（针对keyword），keyword类型下，字符过于长，检索意义不大，索引会被禁用，数据不可被检索，
     *                       默认值256，超出这个长度的字段将不会被索引，但是会存储。这里的不被索引是这个字段不被索引
     * @param dateFormat     日期类型格式
     * @param copyTo         拷贝至哪个字段
     * @param createAnalyzer 创建索引时的分词器类型，字符串，自定义分词器
     * @param searchAnalyzer 查询时的分词器类型
     * @param properties     对象字段信息列表
     * @param fields         多（子）字段信息列表
     * @return
     */
    Children mapping(String fieldName,
                     String fieldType,
                     Boolean index,
                     Integer ignoreAbove,
                     String dateFormat,
                     String copyTo,
                     String createAnalyzer,
                     String searchAnalyzer,
                     List<EsIndexParam> properties,
                     List<EsIndexParam> fields);

    /**
     * 用户自行指定mapping
     *
     * @param mapping mapping信息
     * @return
     */
    Children mapping(Map<String, Object> mapping);
}
