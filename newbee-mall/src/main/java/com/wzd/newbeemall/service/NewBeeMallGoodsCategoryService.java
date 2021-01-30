package com.wzd.newbeemall.service;

import com.wzd.newbeemall.controller.vo.NewBeeMallIndexCategoryVO;
import com.wzd.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.wzd.newbeemall.controller.vo.SearchPageCategoryVO;
import com.wzd.newbeemall.model.entity.GoodsCategory;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;

import java.util.List;

public interface NewBeeMallGoodsCategoryService {
    PageResult getCategoryPage(PageUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    String updateCategory(GoodsCategory goodsCategory);

    boolean deleteBatch(Integer[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int level);

    GoodsCategory getGoodsCategoryById(Long categoryId);

    List<NewBeeMallIndexCategoryVO> getCategoriesForIndex();

    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);

}
