package com.wzd.newbeemall.controller.mall;


import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.controller.vo.NewBeeMallGoodsDetailVO;
import com.wzd.newbeemall.controller.vo.SearchPageCategoryVO;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.service.NewBeeMallGoodsCategoryService;
import com.wzd.newbeemall.service.NewBeeMallGoodsService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
public class GoodsController {

    @Autowired
    private NewBeeMallGoodsService newBeeMallGoodsService;

    @Autowired
    private NewBeeMallGoodsCategoryService newBeeMallGoodsCategoryService;


    @GetMapping({"/search","search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request){
        // 先看请求有页数，没有就默认为1
        if(StringUtils.isEmpty(params.get("page"))){
            params.put("page",1); // 没有，默认是1
        }

        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);

        // 封装分类数据
        if(params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId")+"")){
            Long categoryId = Long.valueOf(params.get("goodsCategoryId")+"");
            // 搜索界面的VO类 是分类搜索
            SearchPageCategoryVO searchPageCategoryVO = newBeeMallGoodsCategoryService.getCategoriesForSearch(categoryId);
            if(searchPageCategoryVO!=null){
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }

        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //封装商品数据
        PageUtil pageUtil = new PageUtil(params);
        request.setAttribute("pageResult", newBeeMallGoodsService.searchNewBeeMallGoods(pageUtil));
        return "mall/search";



    }


    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request){
        if(goodsId<1){
            return "error/error_5xx";
        }

        GoodsInfo goodsInfo = newBeeMallGoodsService.getGoodsInfoById(goodsId);
        if (Objects.isNull(goodsInfo)){
            return "error/error_404";
        }

        NewBeeMallGoodsDetailVO goodsDetailVO = new NewBeeMallGoodsDetailVO();
        BeanUtil.copyProperties(goodsInfo, goodsDetailVO);
        request.setAttribute("goodsDetail",goodsDetailVO);
        return "mall/detail";

    }





}
