package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.mapper.UserMapper;
import com.wzd.newbeemall.model.entity.User;
import com.wzd.newbeemall.service.NewBeeMallUserService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.CommonUtils;
import com.wzd.newbeemall.utils.NewBeeMallUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;


@Service
public class NewBeeMallUserServiceImpl implements NewBeeMallUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public String register(String loginName, String password) {
        if(userMapper.selectByLoginName(loginName)!=null){
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        User registerUser = new User();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = CommonUtils.MD5(password);
        registerUser.setPasswordMd5(passwordMD5);
        if(userMapper.insertSelective(registerUser)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();

    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession session) {
        User user = userMapper.selectByLoginNameAndPassword(loginName, passwordMD5);
        if (user != null && session != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
            BeanUtil.copyProperties(user, newBeeMallUserVO);
            //设置购物车中的数量
            session.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);

            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public NewBeeMallUserVO updateUserInfo(User mallUser, HttpSession httpSession) {
        NewBeeMallUserVO userTemp = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        User userFromDB = userMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null) {
            if (!StringUtils.isEmpty(mallUser.getNickName())) {
                userFromDB.setNickName(NewBeeMallUtils.cleanString(mallUser.getNickName()));
            }
            if (!StringUtils.isEmpty(mallUser.getAddress())) {
                userFromDB.setAddress(NewBeeMallUtils.cleanString(mallUser.getAddress()));
            }
            if (!StringUtils.isEmpty(mallUser.getIntroduceSign())) {
                userFromDB.setIntroduceSign(NewBeeMallUtils.cleanString(mallUser.getIntroduceSign()));
            }
            if (userMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
                userFromDB = userMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(userFromDB, newBeeMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
                return newBeeMallUserVO;
            }
        }
        return null;
    }
}
