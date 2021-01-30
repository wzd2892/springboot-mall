package com.wzd.newbeemall.service;

import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.model.entity.User;

import javax.servlet.http.HttpSession;

public interface NewBeeMallUserService {
    String register(String loginName, String password);

    String login(String loginName, String passwordMD5, HttpSession session);

    NewBeeMallUserVO updateUserInfo(User mallUser, HttpSession httpSession);
}
