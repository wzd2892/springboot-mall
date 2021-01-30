package com.wzd.newbeemall.controller.admin;


import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.NewBeeMallCategoryLevelEnum;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.model.entity.GoodsCategory;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.service.NewBeeMallGoodsCategoryService;
import com.wzd.newbeemall.service.NewBeeMallGoodsService;
import com.wzd.newbeemall.utils.JsonData;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping(value = "/api/v1/pri/admin")
public class NewBeeGoodsController {

    @Autowired
    NewBeeMallGoodsCategoryService newBeeMallGoodsCategoryService;

    @Autowired
    NewBeeMallGoodsService newBeeMallGoodsService;

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request){
        request.setAttribute("path","edit");
        // 查询所有一级子目录
        List<GoodsCategory> firstLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<GoodsCategory> secondLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if(!CollectionUtils.isEmpty(secondLevelCategories)){
                List<GoodsCategory> thirdLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                return "admin/newbee_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @RequestMapping("/goods/listForSelect")
    @ResponseBody
    public JsonData listForSelect(@RequestParam("categoryId") Long categoryId){
        if(categoryId == null || categoryId < 1){
            return JsonData.buildError("缺少参数");
        }
        GoodsCategory goodsCategory = newBeeMallGoodsCategoryService.getGoodsCategoryById(categoryId);
        // 既不是一级分类也不是二级分类，（意思是三级分类，则不返回数据）
        if(goodsCategory == null || goodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()){
            return JsonData.buildError("参数异常");
        }
        Map categoryResult = new HashMap(2);

        if(goodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()){
            List<GoodsCategory> secondLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategory.getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if(!CollectionUtils.isEmpty(secondLevelCategories)){
                List<GoodsCategory> thirdLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if(goodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()){
            List<GoodsCategory> thirdLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return JsonData.buildSuccess(categoryResult);
    }

    @PostMapping("/goods/save")
    @ResponseBody
    public JsonData save(@RequestBody GoodsInfo goodsInfo, HttpSession session){
        if(StringUtils.isEmpty(goodsInfo.getGoodsName())
        || StringUtils.isEmpty(goodsInfo.getGoodsIntro())
        || Objects.isNull(goodsInfo.getGoodsCategoryId())
        || StringUtils.isEmpty(goodsInfo.getTag())
        || Objects.isNull(goodsInfo.getOriginalPrice())
        || Objects.isNull(goodsInfo.getSellingPrice())
        || Objects.isNull(goodsInfo.getStockNum())
        || Objects.isNull(goodsInfo.getGoodsSellStatus())
        || StringUtils.isEmpty(goodsInfo.getGoodsDetailContent())
        || StringUtils.isEmpty(goodsInfo.getGoodsCoverImg())){
            return JsonData.buildError("参数异常");
        }

        goodsInfo.setCreateTime(new Date());
        Integer userId = (Integer)session.getAttribute("loginUserId");
        goodsInfo.setCreateUser(userId);

        String result = newBeeMallGoodsService.saveNewBeeMallGoods(goodsInfo);
        if(ServiceResultEnum.SUCCESS.getResult() == result){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError(result);
        }
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request,@PathVariable("goodsId") Long goodsId){
        request.setAttribute("path","edit");
        GoodsInfo goodsInfo = newBeeMallGoodsService.getGoodsInfoById(goodsId);
        if(goodsInfo == null){
            return "error/error_400";
        }
        if(goodsInfo.getGoodsCategoryId()>0){
            if(goodsInfo.getGoodsCategoryId()!=null||goodsInfo.getGoodsCategoryId()>0){
                // 有分类字段则查询相关分类数据，返回前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = newBeeMallGoodsCategoryService.getGoodsCategoryById(goodsInfo.getGoodsCategoryId());
                // 这个查询的商品必须是三级分类
                if(currentGoodsCategory!=null && currentGoodsCategory.getCategoryLevel()==NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()){
                    // 查询所有一级分类
                    List<GoodsCategory> firstLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L),NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    // 现在有一个三级目录 有他的parentId 我们查询这个parentId下面所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    // 查询这个当前三级目录的二级父目录
                    GoodsCategory secondLevelCategory = newBeeMallGoodsCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if(secondLevelCategory!=null){
                        // 现在有了这个直属的二级目录，我们希望找到这个直属一级目录下，所有的二级目录
                        List<GoodsCategory> secondLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategory.getParentId()),NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        // 查询直属的一级分类
                        GoodsCategory firstLevelCategory = newBeeMallGoodsCategoryService.getGoodsCategoryById(secondLevelCategory.getParentId());
                        if(firstLevelCategory!=null){
                            // 将所有的对象放入到request作用域中
                            request.setAttribute("firstLevelCategories",firstLevelCategories);
                            request.setAttribute("secondLevelCategories",secondLevelCategories);
                            request.setAttribute("thirdLevelCategories",thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId",firstLevelCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId",secondLevelCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId",currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        // 未设置商品分类，默认分类为0
        if(goodsInfo.getGoodsCategoryId() == 0){
            // 查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                List<GoodsCategory> secondLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if(!CollectionUtils.isEmpty(secondLevelCategories)){
                    List<GoodsCategory> thirdLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods",goodsInfo);
        request.setAttribute("path","goods-edit");
        return "admin/newbee_mall_goods_edit";
    }

    @PostMapping("/goods/update")
    @ResponseBody
    public JsonData update(@RequestBody GoodsInfo goodsInfo, HttpSession session){
        if(StringUtils.isEmpty(goodsInfo.getGoodsName())
                || StringUtils.isEmpty(goodsInfo.getGoodsIntro())
                || Objects.isNull(goodsInfo.getGoodsCategoryId())
                || StringUtils.isEmpty(goodsInfo.getTag())
                || Objects.isNull(goodsInfo.getOriginalPrice())
                || Objects.isNull(goodsInfo.getSellingPrice())
                || Objects.isNull(goodsInfo.getStockNum())
                || Objects.isNull(goodsInfo.getGoodsSellStatus())
                || StringUtils.isEmpty(goodsInfo.getGoodsDetailContent())
                || StringUtils.isEmpty(goodsInfo.getGoodsCoverImg())){
            return JsonData.buildError("参数异常");
        }

        goodsInfo.setUpdateTime(new Date());
        Integer userId = (Integer)session.getAttribute("loginUserId");
        goodsInfo.setUpdateUser(userId);

        String result = newBeeMallGoodsService.updateNewBeeMallGoods(goodsInfo);

        if(ServiceResultEnum.SUCCESS.getResult() == result){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError(result);
        }
    }

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    /**
     * 列表查询
     */
    @GetMapping("/goods/list")
    @ResponseBody
    public JsonData list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return JsonData.buildError("参数异常！");
        }
        PageUtil pageUtil = new PageUtil(params);
        return JsonData.buildSuccess(newBeeMallGoodsService.getNewBeeMallGoodsPage(pageUtil));
    }

    /**
     * 批量修改销售状态
     *  sellStutus 具体是原本是什么状态
     * @return
     */

    @PutMapping("/goods/status/{sellStatus}")
    @ResponseBody
    public JsonData delete(@RequestBody Long[] ids,@PathVariable("sellStatus")int sellStatus ){
        if(ids.length<1){
            return JsonData.buildError("参数异常！");
        }
        if(sellStatus != Constants.SELL_STATUS_UP && sellStatus!=Constants.SELL_STATUS_DOWN){
            return JsonData.buildError("状态异常！");
        }
        if (newBeeMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildError("修改失败");
        }
    }


}
