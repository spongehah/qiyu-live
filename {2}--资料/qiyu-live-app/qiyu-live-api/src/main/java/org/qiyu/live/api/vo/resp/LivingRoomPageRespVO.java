package org.qiyu.live.api.vo.resp;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 18:34 2023/7/23
 * @Description
 */
public class LivingRoomPageRespVO {

    private List<LivingRoomRespVO> list;
    private boolean hasNext;

    public List<LivingRoomRespVO> getList() {
        return list;
    }

    public void setList(List<LivingRoomRespVO> list) {
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
        return "LivingRoomPageRespVO{" +
                "list=" + list +
                ", hasNext=" + hasNext +
                '}';
    }
}
