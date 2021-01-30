package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.TestUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestUserMapper {

    int getTotalUser(Map param);
    List<TestUser> findUsers(Map param);

}
