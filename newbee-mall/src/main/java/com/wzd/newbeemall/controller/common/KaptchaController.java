package com.wzd.newbeemall.controller.common;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@RequestMapping(value = "/api/v1/pub/common")
public class KaptchaController {


    @Autowired
    private DefaultKaptcha captchaProducer;


    @RequestMapping(value = "/kaptcha")
    public void defaultKaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        byte[] captchaOutputStream = null;  // 验证码输出流；
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();

        try {
            String verifyCode = captchaProducer.createText();  // 产生验证码字符串
            request.getSession().setAttribute("verityCode",verifyCode); // 保存在session作用域中
            BufferedImage challenge = captchaProducer.createImage(verifyCode); // 产生图片
            ImageIO.write(challenge,"jpg",imgOutputStream);         // 写入到imgOutputStream
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        captchaOutputStream = imgOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store"); // 通知从服务器到客户端内的所有缓存机制，表示它们是否可以缓存这个对象及缓存有效时间。其单位为秒
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); // 指定一个日期/时间，超过该时间则认为此回应已经过期
        response.setContentType("image/jpeg");   // 当前内容的MIME类型

        ServletOutputStream responsegetOutputStream =  response.getOutputStream();
        responsegetOutputStream.write(captchaOutputStream);
        responsegetOutputStream.flush();
        responsegetOutputStream.close();
    }

    @GetMapping(value = "/verify")
    @ResponseBody
    public String verify(@RequestParam("code") String code, HttpSession session){
        if(StringUtils.isEmpty(code)){
            return "验证码不能为空";
        }

        String kaptchCode = (String) session.getAttribute("verityCode");
        if(StringUtils.isEmpty(kaptchCode) || !code.equals(kaptchCode)){
            return "验证码错误";
        }
        return "验证成功";
    }

}
