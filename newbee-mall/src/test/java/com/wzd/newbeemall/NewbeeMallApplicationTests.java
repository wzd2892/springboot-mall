package com.wzd.newbeemall;

import com.wzd.newbeemall.model.entity.AdminUser;
import com.wzd.newbeemall.service.AdminUserService;
import com.wzd.newbeemall.utils.CommonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class NewbeeMallApplicationTests {

    @Autowired
    private DataSource defaultDataSource;

    @Test
    void contextLoads() throws SQLException {
        Connection conn = defaultDataSource.getConnection();
        System.out.println(conn!=null);
        conn.close();

    }

//    @Autowired
//    AdminUserService adminUserService;
//    @Test
//    void addAdminUserTest(){
//        AdminUser adminUser = new AdminUser();
//        adminUser.setLocked(0);
//        adminUser.setLoginUserName("1111");
//
//        String passwordMd5 = CommonUtils.MD5("1111");
//        adminUser.setLoginPassword(passwordMd5);
//
//        adminUser.setNickName("1111");
//        adminUserService.addAdmin(adminUser);
//    }

}
