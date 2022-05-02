package com.autotest.LiuMa.common.utils;


public class Pager<T> {
    private T list;
    private long total;

    public Pager() {
    }

    public Pager(T list, long total, long pageCount) {
        this.list = list;
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) { this.list = list; }
}
