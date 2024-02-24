// package org.qiyu.live.api.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * 跨域配置类，已在网关地filter中进行了跨域配置，不能重复配置
//  */
// @Configuration
// public class CorsConfig {
//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration config = new CorsConfiguration();
//         config.addAllowedMethod("*");
//         config.addAllowedOrigin("*");
//         config.addAllowedHeader("*");
//
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", buildConfig());
//         return new CorsFilter(source);
//     }
//
//     private CorsConfiguration buildConfig() {
//         CorsConfiguration corsConfiguration = new CorsConfiguration();
//         // sessionId 多次访问一致
//         corsConfiguration.setAllowCredentials(true);
//         // 允许访问的客户端域名
//         List<String> allowedOriginPatterns = new ArrayList<>();
//         allowedOriginPatterns.add("*");
//         corsConfiguration.setAllowedOriginPatterns(allowedOriginPatterns);
//         // 允许任何头
//         corsConfiguration.addAllowedHeader("*");
//         // 允许任何方法(post、get等)
//         corsConfiguration.addAllowedMethod("*");
//         return corsConfiguration;
//     }
// }
