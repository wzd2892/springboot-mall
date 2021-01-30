package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.IndexConfig;
import com.wzd.newbeemall.utils.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IndexConfigMapper {
    int deleteByPrimaryKey(Long configId);

    int insert(IndexConfig record);

    int insertSelective(IndexConfig record);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig record);

    int updateByPrimaryKey(IndexConfig record);

    int getTotalIndexConfigs(Map param);

    List<IndexConfig> findIndexConfigList(Map param);

    int deleteBatch(@Param("ids") Integer[] ids);

    List<IndexConfig> findIndexConfigsByTypeAndNum(@Param("configType") int configType,@Param("number") int number);
}