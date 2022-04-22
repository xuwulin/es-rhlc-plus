package com.xwl.esplus.core.toolkit;

import com.xwl.esplus.core.page.PageInfo;

import java.util.List;

/**
 * 分页工具
 *
 * @author xwl
 * @since 2022/4/7 15:10
 */
public class PageUtils {
    /**
     * @param list     数据列表
     * @param total    总数
     * @param pageNum  当前页
     * @param pageSize 总页数
     * @param <T>      数据类型
     * @return 分页信息
     */
    public static <T> PageInfo<T> getPageInfo(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageInfo<T> pageInfo = new PageInfo<>();
        int pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
        pageInfo.setList(list);
        pageInfo.setSize(list.size());
        pageInfo.setTotal(total);
        pageInfo.setPages(pages);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }
}
