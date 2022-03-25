package com.xwl.esplus.core.mapper;

import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 核心，继承该接口后，即可获得常用的CRUD功能
 *
 * @author xwl
 * @since 2022/3/11 19:31
 */
public interface EsBaseMapper<T> {
    /**
     * 判断索引是否存在
     *
     * @param indexName 索引名称
     * @return true-存在，false-不存在
     */
    Boolean existsIndex(String indexName);

    /**
     * 创建索引
     *
     * @param wrapper 条件
     * @return true-创建成功，false-创建失败
     */
    Boolean createIndex(EsLambdaIndexWrapper<T> wrapper);

    /**
     * 更新索引
     *
     * @param wrapper 条件
     * @return true-更新成功，false-更新失败
     */
    Boolean updateIndex(EsLambdaIndexWrapper<T> wrapper);

    /**
     * 删除指定索引
     *
     * @param indexName 索引名称
     * @return true-删除成功，false-删除失败
     */
    Boolean deleteIndex(String indexName);

    /**
     * 插入一条记录
     *
     * @param entity 插入的数据对象
     * @return 成功条数
     */
    Integer insert(T entity);

    /**
     * 批量插入
     *
     * @param entityList 插入的数据对象列表
     * @return 成功条数
     */
    Integer insertBatch(Collection<T> entityList);

    /**
     * 根据 updateWrapper 条件，更新记录
     *
     * @param entity        更新对象
     * @param updateWrapper 更新条件
     * @return 成功条数
     */
    Integer update(T entity, EsLambdaUpdateWrapper<T> updateWrapper);

    /**
     * 根据 ID 更新
     *
     * @param entity 更新对象
     * @return 成功条数
     */
    Integer updateById(T entity);

    /**
     * 根据ID 批量更新
     *
     * @param entityList 更新对象列表
     * @return 成功条数
     */
    Integer updateBatchById(Collection<T> entityList);

    /**
     * 根据 entity 条件，删除记录
     *
     * @param wrapper 条件
     * @return 总成功条数
     */
    Integer delete(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据 ID 删除
     *
     * @param id 主键
     * @return 成功条数
     */
    Integer deleteById(Serializable id);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键列表
     * @return 总成功条数
     */
    Integer deleteBatchByIds(Collection<? extends Serializable> idList);

    /**
     * 标准查询
     *
     * @param wrapper 条件
     * @return es标准结果
     * @throws IOException IO异常
     */
    SearchResponse search(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 获取SearchSourceBuilder,可用于本框架生成基础查询条件,不支持的高阶语法用户可通过SearchSourceBuilder 进一步封装
     *
     * @param wrapper 条件
     * @return 查询参数
     */
    SearchSourceBuilder getSearchSourceBuilder(EsLambdaQueryWrapper<T> wrapper);

    /**
     * es原生查询
     *
     * @param searchRequest  查询请求参数
     * @param requestOptions 类型
     * @return es原生返回结果
     * @throws IOException IO异常
     */
    SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions) throws IOException;

    /**
     * 获取通过本框架生成的查询参数,可用于检验本框架生成的查询参数是否正确
     *
     * @param wrapper 条件
     * @return 查询JSON格式参数
     */
    String getSource(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 未指定返回类型,未指定分页参数
     *
     * @param wrapper 条件
     * @return 原生分页返回
     * @throws IOException IO异常
     */
//    PageInfo<SearchHit> pageQueryOriginal(EsLambdaQueryWrapper<T> wrapper) throws IOException;

    /**
     * 未指定返回类型,指定分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页显示条数
     * @return 原生分页返回
     * @throws IOException IO异常
     */
//    PageInfo<SearchHit> pageQueryOriginal(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize) throws IOException;

    /**
     * 指定返回类型,但未指定分页参数
     *
     * @param wrapper 条件
     * @return 指定的返回类型
     */
//    PageInfo<T> pageQuery(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 指定返回类型及分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 指定的返回类型
     */
//    PageInfo<T> pageQuery(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);

    /**
     * 获取总数
     *
     * @param wrapper 条件
     * @return 总数
     */
//    Long selectCount(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据 ID 查询
     *
     * @param id 主键
     * @return 指定的返回对象
     */
//    T selectById(Serializable id);

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键列表
     * @return 指定的返回对象列表
     */
//    List<T> selectBatchIds(Collection<? extends Serializable> idList);

    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param wrapper 条件
     * @return 指定的返回对象
     */
//    T selectOne(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据条件查询
     *
     * @param wrapper 条件
     * @return 对象列表
     */
    List<T> selectList(EsLambdaQueryWrapper<T> wrapper);
}
