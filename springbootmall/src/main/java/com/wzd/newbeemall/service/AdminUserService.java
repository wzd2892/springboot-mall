package com.wzd.newbeemall.service;


import com.wzd.newbeemall.model.entity.AdminUser;

public interface AdminUserService {


    AdminUser login(String userName,String password);

    //int addAdmin(AdminUser adminUser);

    AdminUser getUserDetailById(Integer loginUserId);

    boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);

    boolean updateName(Integer loginUserId, String loginUserName, String nickName);
}
