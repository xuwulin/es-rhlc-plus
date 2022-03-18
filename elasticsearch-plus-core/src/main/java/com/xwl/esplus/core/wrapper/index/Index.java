package com.xwl.esplus.core.wrapper.index;

import com.xwl.esplus.core.enums.EsAnalyzerEnum;
import com.xwl.esplus.core.enums.EsFieldTypeEnum;

import java.io.Serializable;
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
     * @return 泛型
     */
    Children indexName(String indexName);

    /**
     * 设置索引的分片数和副本数
     *
     * @param shards   分片数
     * @param replicas 副本数
     * @return 泛型
     */
    Children settings(Integer shards, Integer replicas);

    /**
     * 设置创建别名信息
     *
     * @param aliasName 别名
     * @return 泛型
     */
    Children createAlias(String aliasName);

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @return 泛型
     */
    default Children mapping(R column, EsFieldTypeEnum fieldType) {
        return mapping(column, fieldType, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column    列
     * @param fieldType es中的类型
     * @param analyzer  分词器类型
     * @return 泛型
     */
    default Children mapping(R column, EsFieldTypeEnum fieldType, EsAnalyzerEnum analyzer) {
        return mapping(column, fieldType, analyzer, null);
    }

    /**
     * 设置mapping信息
     *
     * @param column         列
     * @param fieldType      es中的类型
     * @param analyzer       分词器类型
     * @param searchAnalyzer 查询分词器类型
     * @return 泛型
     */
    Children mapping(R column, EsFieldTypeEnum fieldType, EsAnalyzerEnum analyzer, EsAnalyzerEnum searchAnalyzer);

    /**
     * 用户自行指定mapping
     *
     * @param mapping mapping信息
     * @return 泛型
     */
    Children mapping(Map<String, Object> mapping);
}
