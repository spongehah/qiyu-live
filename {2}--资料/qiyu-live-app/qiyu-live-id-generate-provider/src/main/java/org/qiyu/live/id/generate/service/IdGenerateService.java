package org.qiyu.live.id.generate.service;

/**
 * @Author idea
 * @Date: Created in 19:58 2023/5/25
 * @Description
 */
public interface IdGenerateService {

    /**
     * 获取有序id
     *
     * @param id
     * @return
     */
    Long getSeqId(Integer id);

    /**
     * 获取无序id
     *
     * @param id
     * @return
     */
    Long getUnSeqId(Integer id);
}
