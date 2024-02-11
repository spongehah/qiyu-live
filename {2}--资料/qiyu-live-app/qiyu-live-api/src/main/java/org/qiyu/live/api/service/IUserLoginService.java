package org.qiyu.live.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.common.interfaces.vo.WebResponseVO;

/**
 * @Author idea
 * @Date: Created in 10:50 2023/6/15
 * @Description
 */
public interface IUserLoginService {

    /**
     * 发送登录验证码
     *
     * @param phone
     * @return
     */
    WebResponseVO sendLoginCode(String phone);

    /**
     * 手机号+验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    WebResponseVO login(String phone, Integer code, HttpServletResponse response);
}
