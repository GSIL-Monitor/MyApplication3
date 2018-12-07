package com.cxy.childstory.model;

import java.util.List;

/**
 * Created by cxy on 2018/12/5
 */
public class PageBean<T> {
    //总记录数
    private long totalElements;
    //总页数
    private int totalPages;
    //当前页
    private int currentPage;
    //每页的大小
    private int pageSize;
    //数据
    private List<T> content;

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
