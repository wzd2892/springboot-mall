package com.wzd.newbeemall.service;

import com.wzd.newbeemall.controller.vo.SearchPageCategoryVO;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;

public interface NewBeeMallGoodsService {


    String saveNewBeeMallGoods(GoodsInfo goodsInfo);

    GoodsInfo getGoodsInfoById(Long goodsId);

    String updateNewBeeMallGoods(GoodsInfo goodsInfo);

    PageResult getNewBeeMallGoodsPage(PageUtil pageUtil);

    boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    PageResult searchNewBeeMallGoods(PageUtil pageUtil);

}
