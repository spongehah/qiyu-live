package org.qiyu.live.web.starter.context;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.qiyu.live.web.starter.config.RequestLimit;
import org.qiyu.live.web.starter.error.QiyuErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 对于重复请求，要有专门的拦截器去处理，进行相同用户下的限流
 */
public class RequestLimitInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLimitInterceptor.class);

    // @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;//测试时代码，直接返回true，部署时请删掉此行代码，并打开下面代码
        // if (handler instanceof HandlerMethod) {
        //     HandlerMethod handlerMethod = (HandlerMethod) handler;
        //     // 查看有无 @RequestLimit 注解标识该方法
        //     boolean hasLimit = handlerMethod.getMethod().isAnnotationPresent(RequestLimit.class);
        //     if (hasLimit) {
        //         RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);
        //         Long userId = QiyuRequestContext.getUserId();
        //         // 没有userId标识是网关通过的白名单请求，放行
        //         if (userId == null) {
        //             return true;
        //         }
        //         //(userId + url + requestValue) base64 -> String(key)
        //         String cacheKey = applicationName + ":" + userId + ":" + request.getRequestURI();
        //         int limit = requestLimit.limit();// 限制访问数量上限
        //         int second = requestLimit.second();// 时间窗口
        //         Integer reqTime = (Integer) Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey)).orElse(0);
        //         if (reqTime == 0) {
        //             redisTemplate.opsForValue().set(cacheKey, 1, second, TimeUnit.SECONDS);
        //             return true;
        //         } else if (reqTime < limit) {
        //             redisTemplate.opsForValue().increment(cacheKey, 1);
        //             return true;
        //         }
        //         // 超过限流数量上限
        //         // 直接抛出全局异常，让异常捕获器处理
        //         LOGGER.error("[RequestLimitInterceptor] userId is {}, req too much", userId);
        //         throw new QiyuErrorException(-10001, requestLimit.msg());
        //     }
        // }
        // return true;
    }
}
 