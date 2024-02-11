package org.qiyu.live.living.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author idea
 * @Date: Created in 21:22 2023/7/19
 * @Description
 */
@SpringBootApplication
@EnableDubbo
public class LivingProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LivingProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
