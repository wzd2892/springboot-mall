package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminMapper {

    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    int addAdminUser(AdminUser adminUser);

    AdminUser getUserDetailById(@Param("login_user_id") Integer loginUserId);

    int updateByPrimaryKeySelective(AdminUser record);


}
