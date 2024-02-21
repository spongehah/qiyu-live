package org.qiyu.live.web.starter.config;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {

    /**
     * 允许请求的量
     *
     * @return
     */
    int limit();

    /**
     * 指定时间范围，单位秒
     *
     * @return
     */
    int second();

    /**
     * 如果出现了拦截，那么就按照msg文案进行提示
     *
     * @return
     */
    String msg() default "请求过于频繁";
}
