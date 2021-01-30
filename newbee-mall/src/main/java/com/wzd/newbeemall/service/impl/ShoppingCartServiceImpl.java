package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.ShoppingCartItemVO;
import com.wzd.newbeemall.mapper.GoodsInfoMapper;
import com.wzd.newbeemall.mapper.ShoppingCartItemMapper;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.model.entity.ShoppingCartItem;
import com.wzd.newbeemall.service.ShoppingCartService;
import com.wzd.newbeemall.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartItemMapper shoppingCartItemMapper;

    @Autowired
    GoodsInfoMapper goodsInfoMapper;

    @Override
    public String saveCartItem(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem temp = shoppingCartItemMapper.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        if(!Objects.isNull(temp)){
            temp.setGoodsCount(temp.getGoodsCount()+shoppingCartItem.getGoodsCount());
            return updateCartItem(temp);
        }

        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(shoppingCartItem.getGoodsId());
        // 商品为空
        if(goodsInfo == null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartItemMapper.selectCountsByUserId(shoppingCartItem.getUserId());
        // 超出允许的最大数量 指的是 不同的种类的数量
        if(totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }

        // 保存记录
        if (shoppingCartItemMapper.insertSelective(shoppingCartItem)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCartItem(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem temp = shoppingCartItemMapper.selectByPrimaryKey(shoppingCartItem.getCartItemId());
        if(Objects.isNull(temp)){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        //超出单个商品的最大数量
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }


        //todo 数量相同不会进行修改
        if(temp.getGoodsCount() == shoppingCartItem.getGoodsCount()){
            return ServiceResultEnum.SHOPPING_COUNT_SAME.getResult();
        }
        //todo userId不同不能修改

        if(!temp.getUserId().equals(shoppingCartItem.getUserId())){
            return ServiceResultEnum.DIFFERENT_USER_ID.getResult();
        }

        temp.setGoodsCount(shoppingCartItem.getGoodsCount());
        temp.setUpdateTime(new Date());
        //修改记录
        if (shoppingCartItemMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }


    public List<ShoppingCartItemVO> getShoppingCartItems(Long userId){
        List<ShoppingCartItemVO> shoppingCartItemVOS = new ArrayList<>();
        // 根据用户的id查找购物项数据
        List<ShoppingCartItem> shoppingCartItems = shoppingCartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);

        if(!CollectionUtils.isEmpty(shoppingCartItems)){
            // 获取商品的id
            List<Long> goodsIds = shoppingCartItems.stream().map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
            // 查看商品信息并做数据转换 转化成Map
            List<GoodsInfo> goods = goodsInfoMapper.selectByPrimaryKeys(goodsIds);
            Map<Long, GoodsInfo> goodsMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(goods)){
                goodsMap = goods.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            // 封装返回的数据 把cartitem中的数据封装起来
            for(ShoppingCartItem shoppingCartItem:shoppingCartItems){
                ShoppingCartItemVO shoppingCartItemVO = new ShoppingCartItemVO();
                BeanUtil.copyProperties(shoppingCartItem,shoppingCartItemVO);
                // 将goods和购物车中的表匹配
                if(goodsMap.containsKey(shoppingCartItem.getGoodsId())){
                    GoodsInfo goodsTemp = goodsMap.get(shoppingCartItem.getGoodsId());
                    shoppingCartItemVO.setGoodsCoverImg(goodsTemp.getGoodsCoverImg());
                    String goodsName = goodsTemp.getGoodsName();
                    if(goods.size()>20){
                        goodsName = goodsName.substring(0,20)+"...";
                    }
                    shoppingCartItemVO.setGoodsName(goodsName);
                    shoppingCartItemVO.setSellingPrice(goodsTemp.getSellingPrice());
                    shoppingCartItemVOS.add(shoppingCartItemVO);
                }
            }
        }
        return shoppingCartItemVOS;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId) {
        //todo userId不同不能删除
        return shoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public int[] getTotalItemAndPrice(Long userId) {
        List<ShoppingCartItemVO> shoppingCartItemVOS = getShoppingCartItems(userId);

        int itemsTotal = 0;
        int priceTotal = 0;
        if(!CollectionUtils.isEmpty(shoppingCartItemVOS)){
            // 购物的总数
            itemsTotal = shoppingCartItemVOS.stream().mapToInt(ShoppingCartItemVO::getGoodsCount).sum();
            if(itemsTotal<0){
                itemsTotal = 0;
            }

            // 总价
            for(ShoppingCartItemVO shoppingCartItemVO:shoppingCartItemVOS){
                priceTotal += shoppingCartItemVO.getGoodsCount() * shoppingCartItemVO.getSellingPrice();
            }
            if(priceTotal<0){
                priceTotal = -1;
            }
        }
        return new int[]{itemsTotal,priceTotal};
    }


}
