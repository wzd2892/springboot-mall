package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.Carousel;
import com.wzd.newbeemall.utils.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CarouselMapper {
    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel record);

    int insertSelective(Carousel record);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimaryKey(Carousel record);

    List<Carousel> findCarouselList(Map param);

    int getTotalCarousels(Map param);

    int deleteBatch(Integer[] ids);

    List<Carousel> findCarouselsByNum(@Param("number") int number);
}