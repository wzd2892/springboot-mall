package com.wzd.newbeemall.service;


import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;

public interface TestUserService {

    /**
     * 分页功能
     *
     * @param pageUtil
     * @return
     */
    PageResult getAdminUserPage(PageUtil pageUtil);
}
