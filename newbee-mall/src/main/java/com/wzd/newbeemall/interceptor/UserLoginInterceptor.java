package com.wzd.newbeemall.interceptor;

import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.service.ShoppingCartService;
import com.wzd.newbeemall.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    @Autowired
    ShoppingCartService shoppingCartService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入拦截器...");
        if (null == request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            request.getSession().setAttribute("errorMsg", "请登陆");
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        } else {
            if(null == request.getSession().getAttribute("itemsTotal")){
                NewBeeMallUserVO user = (NewBeeMallUserVO) request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY);
                int[] arr = shoppingCartService.getTotalItemAndPrice(user.getUserId());
                request.getSession().setAttribute("itemsTotal",arr[0]);
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
