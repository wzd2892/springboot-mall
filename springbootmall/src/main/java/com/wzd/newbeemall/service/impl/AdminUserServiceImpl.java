package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.mapper.AdminMapper;
import com.wzd.newbeemall.mapper.AdminUserMapper;
import com.wzd.newbeemall.model.entity.AdminUser;
import com.wzd.newbeemall.service.AdminUserService;
import com.wzd.newbeemall.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(String userName, String password) {
        String passwordMd5 = CommonUtils.MD5(password);

        return adminUserMapper.login(userName,passwordMd5);
    }

//    @Override
//    public int addAdmin(AdminUser adminUser) {
//        return adminUserMapper.(adminUser);
//    }

    @Override
    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {

        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        if(adminUser != null){

            String oldPasswordMD5 = CommonUtils.MD5(originalPassword);
            String newPasswordMD5 = CommonUtils.MD5(newPassword);
            if(oldPasswordMD5.equals(adminUser.getLoginPassword())){
                adminUser.setLoginPassword(newPasswordMD5);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                    //修改成功则返回true
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                //修改成功则返回true
                return true;
            }
        }
        return false;
    }
}
