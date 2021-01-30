package com.wzd.newbeemall.config;

import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.interceptor.AdminLoginInterceptor;
import com.wzd.newbeemall.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    AdminLoginInterceptor adminLoginInterceptor(){
        return new AdminLoginInterceptor();
    }

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPathPatterns={"/api/v1/pri/*/*/**"};
        String[] excludePathPatterns = {"/api/v1/pri/admin/login","/api/v1/pri/admin/login_test"};


        registry.addInterceptor(adminLoginInterceptor()).addPathPatterns(addPathPatterns)
                .excludePathPatterns(excludePathPatterns);

        registry.addInterceptor(userLoginInterceptor)
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout")
                .addPathPatterns("/goods/detail/**")
                .addPathPatterns("/shop-cart")
                .addPathPatterns("/shop-cart/**")
                .addPathPatterns("/saveOrder")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/personal")
                .addPathPatterns("/personal/updateInfo")
                .addPathPatterns("/selectPayType")
                .addPathPatterns("/payPage");

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+ Constants.FILE_UPLOAD_DIC);
        registry.addResourceHandler("/goods-img/**").addResourceLocations("file:"+ Constants.FILE_UPLOAD_DIC);

    }
}
