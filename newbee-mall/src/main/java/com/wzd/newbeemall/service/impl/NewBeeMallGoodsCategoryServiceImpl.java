package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.NewBeeMallCategoryLevelEnum;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.*;
import com.wzd.newbeemall.mapper.GoodCategoryMapper;
import com.wzd.newbeemall.model.entity.GoodsCategory;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.service.NewBeeMallGoodsCategoryService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Service
public class NewBeeMallGoodsCategoryServiceImpl implements NewBeeMallGoodsCategoryService {

    @Autowired
    GoodCategoryMapper goodCategoryMapper;

    @Override
    public PageResult getCategoryPage(PageUtil pageUtil) {
        // 找到当前页码的数据
        List<GoodsCategory> goodCategories = goodCategoryMapper.findGoodCategoryList(pageUtil);
        // 计算全部的页码
        int total = goodCategoryMapper.getTotalCounts(pageUtil);
        PageResult pageResult = new PageResult(goodCategories,total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(),goodsCategory.getCategoryName());
        if(temp!=null){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if(goodCategoryMapper.insertSelective(goodsCategory)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp1 = goodCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if(temp1==null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(),goodsCategory.getCategoryName());
        // 同名但不同的id   我们确保  名字是唯一的！！！
        if( temp2!=null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())){
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {

        if(ids.length<1)
            return false;
        else
            return goodCategoryMapper.deleteBatch(ids)>0;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);// 0表示查询所有
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long categoryId) {
        return goodCategoryMapper.selectByPrimaryKey(categoryId);
    }


    /**
     * 返回分类的数据，用在首页的调用
     * @return
     */
    @Override
    public List<NewBeeMallIndexCategoryVO> getCategoriesForIndex() {
        List<NewBeeMallIndexCategoryVO> newBeeMallIndexCategoryVOS = new ArrayList<>();

        // 先获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0l), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        // 得到数据后
        if(!CollectionUtils.isEmpty(firstLevelCategories)){
            // 获取一级目录的所有的id 之前都是单个的调用，现在是一下子查询多个ids一起返回给前端
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            // number 为0 意思是全查
            // 查询二级目录
            List<GoodsCategory> secondLevelCategories = goodCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds,NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);

            if(!CollectionUtils.isEmpty(secondLevelCategories)){
                // 二级目录的所有的parent
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                // 获取三级目录
                List<GoodsCategory> thirdLevelCategories = goodCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds,NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if(!CollectionUtils.isEmpty(thirdLevelCategories)){
                    // 开始存放相关数据
                    // 先处理三级目录， 将三级目录和对应的parentId相结合
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));

                    // 处理二级分类！！！ 二级分类所有进行封装
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    for(GoodsCategory secondLevelCategory:secondLevelCategories){
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        // secondLevelCategoryVO 中还有三级目录的属性
                        // 需要把子目录也添加进来
                        if(thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())){
                            // 根据二级分类的id取出thirdLevelCategoryMap 分类
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }

                    // 处理一级分类
                    if(!CollectionUtils.isEmpty(secondLevelCategoryVOS)){
                        // 根据parentId 将二级分类分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for(GoodsCategory firstCategory:firstLevelCategories){
                            NewBeeMallIndexCategoryVO newBeeMallIndexCategoryVO = new NewBeeMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory,newBeeMallIndexCategoryVO);
                            // 如果该一级分类下有数据，则放入newBeeMallIndexCategoryVOS 对象中
                            if(secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())){
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                newBeeMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                newBeeMallIndexCategoryVOS.add(newBeeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newBeeMallIndexCategoryVOS;
        }else {
            return null;
        }

    }

    /**
     * 由三级的分类id 回退之前的所有信息
     * @param categoryId
     * @return
     */

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdLevelGoodsCategory = goodCategoryMapper.selectByPrimaryKey(categoryId);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            //获取当前三级分类的二级分类
            GoodsCategory secondLevelGoodsCategory = goodCategoryMapper.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
                //获取当前二级分类下的三级分类List
                List<GoodsCategory> thirdLevelCategories = goodCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelGoodsCategory.getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }
}
