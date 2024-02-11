package org.qiyu.live.id.generate.service.bo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author idea
 * @Date: Created in 20:00 2023/5/25
 * @Description 有序id的BO对象
 */
public class LocalSeqIdBO {

    private int id;
    /**
     * 在内存中记录的当前有序id的值
     */
    private AtomicLong currentNum;

    /**
     * 当前id段的开始值
     */
    private Long currentStart;
    /**
     * 当前id段的结束值
     */
    private Long nextThreshold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AtomicLong getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(AtomicLong currentNum) {
        this.currentNum = currentNum;
    }

    public Long getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(Long currentStart) {
        this.currentStart = currentStart;
    }

    public Long getNextThreshold() {
        return nextThreshold;
    }

    public void setNextThreshold(Long nextThreshold) {
        this.nextThreshold = nextThreshold;
    }
}
