package com.wzd.newbeemall.service;

import com.wzd.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.wzd.newbeemall.model.entity.IndexConfig;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;

import java.util.List;

public interface NewBeeMallIndexConfigService {


    PageResult getConfigsPage(PageUtil pageUtil);

    String saveindexConfig(IndexConfig indexConfig);

    boolean deleteBatch(Integer[] ids);

    String updateindexConfig(IndexConfig indexConfig);

    List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int type, int indexHotGoodsNumber);
}
