package com.wzd.newbeemall.service;

import com.wzd.newbeemall.controller.vo.NewBeeMallIndexCarouselVO;
import com.wzd.newbeemall.model.entity.Carousel;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;

import java.util.List;

public interface NewBeeMallCarouselService {

    PageResult getCarouselPage(PageUtil pageUtil);

    String saveCarousel(Carousel carousel);

    boolean deleteBatch(Integer[] ids);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number);
}
