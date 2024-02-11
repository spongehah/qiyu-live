package org.qiyu.live.user.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.qiyu.live.user.constants.UserTagsEnum;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.provider.service.IUserPhoneService;
import org.qiyu.live.user.provider.service.IUserService;
import org.qiyu.live.user.provider.service.IUserTagService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户中台服务提供者
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class UserProviderApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);//Dubbo不使用tomcat，使用netty
        springApplication.run(args);
    }
    
    @Resource
    private IUserTagService userTagService;

    @Resource
    private IUserService userService;
    
    @Resource
    private IUserPhoneService userPhoneService;

    @Override
    public void run(String... args) throws Exception {
        String phone = "17341741178";
        UserLoginDTO userLoginDTO = userPhoneService.login(phone);
        System.out.println(userLoginDTO);
        System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
        System.out.println(userPhoneService.queryByPhone(phone));
        System.out.println(userPhoneService.queryByPhone(phone));
    }
}
