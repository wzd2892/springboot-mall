package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.mapper.TestUserMapper;
import com.wzd.newbeemall.model.entity.TestUser;
import com.wzd.newbeemall.service.TestUserService;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestUserServiceImpl implements TestUserService {

    @Autowired
    private TestUserMapper testUserMapper;

    @Override
    public PageResult getAdminUserPage(PageUtil pageUtil) {
        //当前页码中的数据列表
        List<TestUser> users = testUserMapper.findUsers(pageUtil);
        //数据总条数 用于计算分页数据
        int total = testUserMapper.getTotalUser(pageUtil);
        PageResult pageResult = new PageResult(users, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
