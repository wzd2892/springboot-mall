package com.wzd.newbeemall.controller.admin;


import com.wzd.newbeemall.common.NewBeeMallCategoryLevelEnum;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.model.entity.GoodsCategory;
import com.wzd.newbeemall.service.NewBeeMallGoodsCategoryService;
import com.wzd.newbeemall.utils.JsonData;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "/api/v1/pri/admin")
public class NewBeeGoodsCategoryController {

    @Autowired
    NewBeeMallGoodsCategoryService newBeeMallGoodsCategoryService;

    @GetMapping("/category")
    public String categoriesPage(HttpServletRequest request, @RequestParam("categoryLevel") Byte categoryLevel, @RequestParam("parentId") Long parentId, @RequestParam("backParentId") Long backParentId) {
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            return "error/error_5xx";
        }
        //path 字段主要实现导航栏中的选中效果
        request.setAttribute("path", "newbee_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/newbee_mall_category";
    }

    /**
     * 分页数据展示
     * @return
     */

    @GetMapping(value = "/category/list")
    @ResponseBody
    public JsonData list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return JsonData.buildError("参数异常！");
        }
        //查询列表数据
        PageUtil pageUtil = new PageUtil(params);
        return JsonData.buildSuccess(newBeeMallGoodsCategoryService.getCategoryPage(pageUtil));

    }

    /**
     * 保存
     * @param goodsCategory
     * @return
     */

    @PostMapping("/category/save")
    @ResponseBody
    public JsonData save(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return JsonData.buildError("参数异常");
        }

        String result = newBeeMallGoodsCategoryService.saveCategory(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult() == result){
            return JsonData.buildSuccess();
        }else{
            return JsonData.buildError(result);
        }
    }

    /**
     * 更新
     * @param goodsCategory
     * @return
     */
    @PostMapping("/category/update")
    @ResponseBody
    public JsonData update(@RequestBody GoodsCategory goodsCategory){
        if (Objects.isNull(goodsCategory.getCategoryId())
                || Objects.isNull(goodsCategory.getCategoryLevel())
                || StringUtils.isEmpty(goodsCategory.getCategoryName())
                || Objects.isNull(goodsCategory.getParentId())
                || Objects.isNull(goodsCategory.getCategoryRank())) {
            return JsonData.buildError("参数异常");
        }

        String result = newBeeMallGoodsCategoryService.updateCategory(goodsCategory);
        if(ServiceResultEnum.SUCCESS.getResult()==result){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError(result);
        }
    }

    @PostMapping("/category/delete")
    @ResponseBody
    public JsonData delete(@RequestBody Integer[] ids){
        if(ids.length<1){
            return JsonData.buildError("参数异常！");
        }

        if (newBeeMallGoodsCategoryService.deleteBatch(ids)) {
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildError("删除失败");
        }
    }

    @GetMapping("/coupling-test")
    public String couplingTest(HttpServletRequest request) {

        request.setAttribute("path","coupling-test");
        // 查询所有一级子目录
        List<GoodsCategory> firstLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<GoodsCategory> secondLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if(!CollectionUtils.isEmpty(secondLevelCategories)){
                List<GoodsCategory> thirdLevelCategories = newBeeMallGoodsCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                return "admin/coupling-test";
            }
        }

        return "error/error_5xx";
    }

    @RequestMapping("/coupling-test/listForSelect")
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





}
