package org.qiyu.live.common.interfaces.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询所用的包装类
 *
 * @Author idea
 * @Date: Created in 18:41 2023/7/23
 * @Description
 */
public class PageWrapper<T> implements Serializable {

    private List<T> list;
    private boolean hasNext;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return "PageWrapper{" +
                "list=" + list +
                ", hasNext=" + hasNext +
                '}';
    }
}
