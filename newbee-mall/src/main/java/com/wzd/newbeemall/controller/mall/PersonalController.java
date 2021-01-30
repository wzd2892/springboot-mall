package com.wzd.newbeemall.controller.mall;


import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.model.entity.User;
import com.wzd.newbeemall.service.NewBeeMallUserService;
import com.wzd.newbeemall.service.ShoppingCartService;
import com.wzd.newbeemall.utils.CommonUtils;
import com.wzd.newbeemall.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class PersonalController {

    @Autowired
    NewBeeMallUserService newBeeMallUserService;

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 登陆界面跳转
     */

    @GetMapping({"/login","login.html"})
    public String loginPage(){
        return "mall/login";
    }

    /**
     * 注册界面跳转
     */
    @GetMapping({"/register","register.html"})
    public String registerPage(){
        return "mall/register";
    }

    @PostMapping("/register")
    @ResponseBody
    public JsonData register(@RequestParam("loginName") String loginName,
                             @RequestParam("verifyCode") String verifyCode,
                             @RequestParam("password") String password,
                                HttpSession session){
        if (StringUtils.isEmpty(loginName)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }

        String kaptchaCode = session.getAttribute("verityCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }

        // 开始注册
        String registerResult = newBeeMallUserService.register(loginName, password);
        if(ServiceResultEnum.SUCCESS.getResult() == registerResult){
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildError(registerResult);
        }

    }



    @PostMapping("/login")
    @ResponseBody
    public JsonData login(@RequestParam("loginName") String loginName,
                             @RequestParam("verifyCode") String verifyCode,
                             @RequestParam("password") String password,
                             HttpSession session){
        if (StringUtils.isEmpty(loginName)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }

        String kaptchaCode = session.getAttribute("verityCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return JsonData.buildError(ServiceResultEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }

        String loginResult = newBeeMallUserService.login(loginName, CommonUtils.MD5(password), session);
        //登录成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(loginResult)) {
            NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
            int[] arr = shoppingCartService.getTotalItemAndPrice(user.getUserId());
            session.setAttribute("itemsTotal",arr[0]);


            return JsonData.buildSuccess();
        }
        //登录失败
        return JsonData.buildError(loginResult);


    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public JsonData updateInfo(@RequestBody User mallUser, HttpSession httpSession) {
        NewBeeMallUserVO mallUserTemp = newBeeMallUserService.updateUserInfo(mallUser,httpSession);
        if (mallUserTemp == null) {
            return JsonData.buildError("修改失败");
        } else {
            //返回成功
            return  JsonData.buildSuccess();
        }
    }

}
