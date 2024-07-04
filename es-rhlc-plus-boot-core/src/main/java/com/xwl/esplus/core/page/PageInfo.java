package com.xwl.esplus.core.page;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xwl
 * @since 2022/3/11 19:21
 */
public class PageInfo<T> extends PageSerializable<T> {
    public static final int DEFAULT_NAVIGATE_PAGES = 8;

    public static final PageInfo EMPTY = new PageInfo(Collections.emptyList(), 0);

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 当前页的数量
     */
    private int size;

    /**
     * 由于startRow和endRow不常用，这里说个具体的用法
     * 可以在页面中"显示startRow到endRow 共size条数据"
     * 当前页面第一个元素在数据库中的行号
     */
    private long startRow;

    /**
     * 当前页面最后一个元素在数据库中的行号
     */
    private long endRow;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 前一页
     */
    private int prePage;

    /**
     * 下一页
     */
    private int nextPage;

    /**
     * 是否为第一页
     */
    private boolean isFirstPage = false;

    /**
     * 是否为最后一页
     */
    private boolean isLastPage = false;

    /**
     * 是否有前一页
     */
    private boolean hasPreviousPage = false;

    /**
     * 是否有下一页
     */
    private boolean hasNextPage = false;

    /**
     * 导航页码数
     */
    private int navigatePages;

    /**
     * 所有导航页号
     */
    private int[] navigatePageNums;

    /**
     * 导航条上的第一页
     */
    private int navigateFirstPage;

    /**
     * 导航条上的最后一页
     */
    private int navigateLastPage;

    /**
     * 包装Page对象
     *
     * @param list 数据
     */
    public PageInfo(List<T> list) {
        this(list, DEFAULT_NAVIGATE_PAGES);
    }

    /**
     * 包装Page对象
     *
     * @param list          数据
     * @param navigatePages 页码数量
     */
    public PageInfo(List<T> list, int navigatePages) {
        super(list);
        this.pageNum = 1;
        this.pageSize = list.size();

        this.pages = this.pageSize > 0 ? 1 : 0;
        this.size = list.size();
        this.startRow = 0;
        this.endRow = list.size() > 0 ? list.size() - 1 : 0;

        this.navigatePages = navigatePages;
        // 计算导航页
        calcNavigatePageNums();
        // 计算前后页，第一页，最后一页
        calcPage();
        // 判断页面边界
        judgePageBoudary();
    }

    /**
     * 类上的泛型T 不能配合静态方法使用，静态方法需要单独定义自己的泛型：public static <T>
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> of(List<T> list) {
        return new PageInfo<T>(list);
    }

    public static <T> PageInfo<T> of(List<T> list, int navigatePages) {
        return new PageInfo<T>(list, navigatePages);
    }

    /**
     * 返回一个空的 Pageinfo 对象
     *
     * @return
     */
    public static <T> PageInfo<T> emptyPageInfo() {
        return EMPTY;
    }

    public void calcByNavigatePages(int navigatePages) {
        setNavigatePages(navigatePages);
        // 计算导航页
        calcNavigatePageNums();
        // 计算前后页，第一页，最后一页
        calcPage();
        // 判断页面边界
        judgePageBoudary();
    }

    /**
     * 计算导航页
     */
    private void calcNavigatePageNums() {
        // 当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatePageNums = new int[pages];
            for (int i = 0; i < pages; i++) {
                navigatePageNums[i] = i + 1;
            }
        } else {
            // 当总页数大于导航页码数时
            navigatePageNums = new int[navigatePages];
            int startNum = pageNum - navigatePages / 2;
            int endNum = pageNum + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                // (最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatePageNums[i] = startNum++;
                }
            } else if (endNum > pages) {
                endNum = pages;
                // 最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatePageNums[i] = endNum--;
                }
            } else {
                // 所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatePageNums[i] = startNum++;
                }
            }
        }
    }

    /**
     * 计算前后页，第一页，最后一页
     */
    private void calcPage() {
        if (navigatePageNums != null && navigatePageNums.length > 0) {
            navigateFirstPage = navigatePageNums[0];
            navigateLastPage = navigatePageNums[navigatePageNums.length - 1];
            if (pageNum > 1) {
                prePage = pageNum - 1;
            }
            if (pageNum < pages) {
                nextPage = pageNum + 1;
            }
        }
    }

    /**
     * 判定页面边界
     */
    private void judgePageBoudary() {
        isFirstPage = pageNum == 1;
        isLastPage = pageNum == pages || pages == 0;
        hasPreviousPage = pageNum > 1;
        hasNextPage = pageNum < pages;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", size=" + size +
                ", startRow=" + startRow +
                ", endRow=" + endRow +
                ", pages=" + pages +
                ", prePage=" + prePage +
                ", nextPage=" + nextPage +
                ", isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                ", hasPreviousPage=" + hasPreviousPage +
                ", hasNextPage=" + hasNextPage +
                ", navigatePages=" + navigatePages +
                ", navigatepageNums=" + Arrays.toString(navigatePageNums) +
                ", navigateFirstPage=" + navigateFirstPage +
                ", navigateLastPage=" + navigateLastPage +
                ", total=" + total +
                ", list=" + list +
                '}';
    }

    public PageInfo() {

    }

    public PageInfo(List<T> list, int pageNum, int pageSize, int size, long startRow, long endRow, int pages, int prePage, int nextPage, boolean isFirstPage, boolean isLastPage, boolean hasPreviousPage, boolean hasNextPage, int navigatePages, int[] navigatePageNums, int navigateFirstPage, int navigateLastPage) {
        super(list);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.size = size;
        this.startRow = startRow;
        this.endRow = endRow;
        this.pages = pages;
        this.prePage = prePage;
        this.nextPage = nextPage;
        this.isFirstPage = isFirstPage;
        this.isLastPage = isLastPage;
        this.hasPreviousPage = hasPreviousPage;
        this.hasNextPage = hasNextPage;
        this.navigatePages = navigatePages;
        this.navigatePageNums = navigatePageNums;
        this.navigateFirstPage = navigateFirstPage;
        this.navigateLastPage = navigateLastPage;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getStartRow() {
        return startRow;
    }

    public void setStartRow(long startRow) {
        this.startRow = startRow;
    }

    public long getEndRow() {
        return endRow;
    }

    public void setEndRow(long endRow) {
        this.endRow = endRow;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int[] getNavigatePageNums() {
        return navigatePageNums;
    }

    public void setNavigatePageNums(int[] navigatePageNums) {
        this.navigatePageNums = navigatePageNums;
    }

    public int getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(int navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public int getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(int navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }
}
