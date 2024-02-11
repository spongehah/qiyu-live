package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.HomePageVO;

/**
 * @Author idea
 * @Date: Created in 23:02 2023/7/19
 * @Description
 */
public interface IHomePageService {


    /**
     * 初始化页面获取的信息
     *
     * @param userId
     * @return
     */
    HomePageVO initPage(Long userId);


}
