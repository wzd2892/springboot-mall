package com.wzd.newbeemall.controller.mall;


import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.IndexConfigTypeEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallIndexCarouselVO;
import com.wzd.newbeemall.controller.vo.NewBeeMallIndexCategoryVO;
import com.wzd.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.wzd.newbeemall.service.NewBeeMallCarouselService;
import com.wzd.newbeemall.service.NewBeeMallGoodsCategoryService;
import com.wzd.newbeemall.service.NewBeeMallIndexConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    NewBeeMallCarouselService newBeeMallCarouselService;

    @Autowired
    NewBeeMallGoodsCategoryService newBeeMallGoodsCategoryService;

    @Autowired
    NewBeeMallIndexConfigService newBeeMallIndexConfigService;

    @GetMapping({"/index","/","index.html"})
    public String indexPage(HttpServletRequest request){

        List<NewBeeMallIndexCarouselVO> newBeeMallIndexCarouselVOS = newBeeMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<NewBeeMallIndexCategoryVO> newBeeMallIndexCategotyVOS = newBeeMallGoodsCategoryService.getCategoriesForIndex();

        List<NewBeeMallIndexConfigGoodsVO> hotGoods = newBeeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(),Constants.INDEX_HOT_GOODS_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> newGoods = newBeeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(),Constants.INDEX_NEW_GOODS_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> recommendGoods = newBeeMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(),Constants.INDEX_RECOMMEND_GOODS_NUMBER);


        request.setAttribute("carousels", newBeeMallIndexCarouselVOS);
        request.setAttribute("categories", newBeeMallIndexCategotyVOS);

        request.setAttribute("hotGoods", hotGoods);//热销商品

        request.setAttribute("newGoods", newGoods);//新品
        request.setAttribute("recommendGoods", recommendGoods);//推荐商品


        return "mall/index";
    }
}
