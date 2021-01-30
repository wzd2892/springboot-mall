package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByLoginName(String loginName);

    User selectByLoginNameAndPassword(@Param("loginName") String loginName, @Param("passwordMD5") String passwordMD5);
}