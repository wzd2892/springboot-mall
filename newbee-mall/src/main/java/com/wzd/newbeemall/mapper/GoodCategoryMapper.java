package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.GoodsCategory;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.utils.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodCategoryMapper {
    int deleteByPrimaryKey(Long categoryId);

    int insert(GoodsCategory record);

    int insertSelective(GoodsCategory record);

    GoodsCategory selectByPrimaryKey(Long categoryId);

    int updateByPrimaryKeySelective(GoodsCategory record);

    int updateByPrimaryKey(GoodsCategory record);

    int getTotalCounts(Map param);

    List<GoodsCategory> findGoodCategoryList(PageUtil pageUtil);

    GoodsCategory selectByLevelAndName(Byte categoryLevel, String categoryName);

    int deleteBatch(Integer[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds,@Param("categoryLevel") int categoryLevel,@Param("number") int number);

}