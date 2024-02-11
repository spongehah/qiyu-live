package org.qiyu.live.common.interfaces.vo;

import lombok.Data;

/**
 * 统一返回给前端的VO对象
 */
@Data
public class WebResponseVO {

    private int code;
    private String msg;
    private Object data;

    public static WebResponseVO bizError(String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(501);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }

    public static WebResponseVO bizError(int code, String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(code);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }


    public static WebResponseVO sysError() {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(500);
        return webResponseVO;
    }

    public static WebResponseVO sysError(String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(500);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }

    public static WebResponseVO errorParam() {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(400);
        webResponseVO.setMsg("error-param");
        return webResponseVO;
    }

    public static WebResponseVO errorParam(String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(400);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }

    public static WebResponseVO success() {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(200);
        webResponseVO.setMsg("success");
        return webResponseVO;
    }

    public static WebResponseVO success(Object data) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setData(data);
        webResponseVO.setCode(200);
        webResponseVO.setMsg("success");
        return webResponseVO;
    }
}
