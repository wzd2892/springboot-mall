package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallIndexCarouselVO;
import com.wzd.newbeemall.mapper.CarouselMapper;
import com.wzd.newbeemall.model.entity.Carousel;
import com.wzd.newbeemall.model.entity.TestUser;
import com.wzd.newbeemall.service.NewBeeMallCarouselService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NewBeeMallCarouselServiceImpl implements NewBeeMallCarouselService {

    @Autowired
    CarouselMapper carouselMapper;

    @Override
    public PageResult getCarouselPage(PageUtil pageUtil) {
        //当前页码中的数据列表
        List<Carousel> carousels = carouselMapper.findCarouselList(pageUtil);
        //数据总条数 用于计算分页数据
        int total = carouselMapper.getTotalCarousels(pageUtil);
        PageResult pageResult = new PageResult(carousels, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        if(carouselMapper.insertSelective(carousel)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        if(ids.length<1){
            return false;
        }
        return carouselMapper.deleteBatch(ids)>0;
    }

    /**
     * 更新Carousel
     * @param carousel
     * @return
     */

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if(Objects.isNull(temp)){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(new Date());

        if(carouselMapper.updateByPrimaryKeySelective(carousel)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取首页的轮播图list
     * @param number
     * @return
     */
    @Override
    public List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<NewBeeMallIndexCarouselVO> newBeeMallIndexCarouselVOS = new ArrayList<>();
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if(!CollectionUtils.isEmpty(carousels)){
            newBeeMallIndexCarouselVOS = BeanUtil.copyList(carousels, NewBeeMallIndexCarouselVO.class);
        }
        return newBeeMallIndexCarouselVOS;
    }
}
