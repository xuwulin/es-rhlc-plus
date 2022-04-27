package com.xwl.esplus.core.mapper;

import com.xwl.esplus.core.page.PageInfo;
import com.xwl.esplus.core.wrapper.index.EsLambdaIndexWrapper;
import com.xwl.esplus.core.wrapper.query.EsLambdaQueryWrapper;
import com.xwl.esplus.core.wrapper.update.EsLambdaUpdateWrapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * 插入文档
     *
     * @param entity es索引对应的实体类
     * @return 成功条数
     */
    Integer save(T entity);

    /**
     * 批量插入文档
     *
     * @param entityList es对应的实体类列表
     * @return 成功条数
     */
    Integer saveBatch(Collection<T> entityList);

    /**
     * 根据条件更新文档
     *
     * @param entity  es索引对应的实体类
     * @param wrapper 更新条件
     * @return 成功条数
     */
    Integer update(T entity, EsLambdaUpdateWrapper<T> wrapper);

    /**
     * 根据id更新文档
     *
     * @param entity es索引对应的实体类
     * @return 成功条数
     */
    Integer updateById(T entity);

    /**
     * 根据id批量更新文档
     *
     * @param entityList es对应的实体类列表
     * @return 成功条数
     */
    Integer updateBatchById(Collection<T> entityList);

    /**
     * 根据条件删除文档
     *
     * @param wrapper 删除查询条件
     * @return 成功条数
     */
    Integer remove(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据id删除文档
     *
     * @param id 文档主键
     * @return 成功条数
     */
    Integer removeById(Serializable id);

    /**
     * 根据id批量删除文档
     *
     * @param idList 文档主键列表
     * @return 成功条数
     */
    Integer removeByIds(Collection<? extends Serializable> idList);

    // TODO 新增、更新、删除后立马刷新、查询时参数可以是一段脚本代码

    /**
     * RestHighLevelClient原生查询
     *
     * @param searchRequest  查询请求参数
     * @param requestOptions 请求选项
     * @return SearchResponse
     */
    SearchResponse search(SearchRequest searchRequest, RequestOptions requestOptions);

    /**
     * 条件查询返回SearchResponse
     *
     * @param wrapper 条件
     * @return SearchResponse
     */
    SearchResponse search(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 获取SearchSourceBuilder
     *
     * @param wrapper 条件
     * @return 查询参数
     */
    SearchSourceBuilder getSearchSourceBuilder(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 获取查询DSL
     *
     * @param wrapper 条件
     * @return 查询DSL（JSON格式）
     */
    String getSource(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 获取总数
     *
     * @return 总数
     */
    Long count();

    /**
     * 获取总数
     *
     * @param wrapper 条件
     * @return 总数
     */
    Long count(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 查询一条记录
     *
     * @param wrapper 条件
     * @return 指定的返回对象
     */
    T getOne(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 根据ID查询
     *
     * @param id 主键
     * @return 指定的返回对象
     */
    T getById(Serializable id);

    /**
     * 查询（根据ID集合批量查询）
     *
     * @param idList 主键列表
     * @return 指定的返回对象列表
     */
    List<T> listByIds(Collection<? extends Serializable> idList);

    /**
     * 条件查询
     *
     * @param wrapper 条件
     * @return 指定的返回对象列表
     */
    List<T> list(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 条件查询，返回map集合
     *
     * @param wrapper 条件
     * @return map集合
     */
    List<Map<String, Object>> listMaps(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：不指定返回类型及分页参数
     *
     * @param wrapper 条件
     * @return 分页对象（SearchHit）
     */
    PageInfo<SearchHit> pageOriginal(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：不指定返回类型，指定分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页对象（SearchHit）
     */
    PageInfo<SearchHit> pageOriginal(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);

    /**
     * 分页查询：指定返回类型，不指定分页参数
     *
     * @param wrapper 条件
     * @return 分页对象（指定的返回对象）
     */
    PageInfo<T> page(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：指定返回类型及分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页对象（指定的返回对象）
     */
    PageInfo<T> page(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);

    /**
     * 分页查询：指定返回类型，不指定分页参数
     *
     * @param wrapper 条件
     * @return 分页对象（Map<String, Object>）
     */
    PageInfo<Map<String, Object>> pageMaps(EsLambdaQueryWrapper<T> wrapper);

    /**
     * 分页查询：指定返回类型及分页参数
     *
     * @param wrapper  条件
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @return 分页对象（Map<String, Object>）
     */
    PageInfo<Map<String, Object>> pageMaps(EsLambdaQueryWrapper<T> wrapper, Integer pageNum, Integer pageSize);
}
