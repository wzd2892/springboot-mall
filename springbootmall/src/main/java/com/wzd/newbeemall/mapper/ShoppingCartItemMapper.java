package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(ShoppingCartItem record);

    int insertSelective(ShoppingCartItem record);

    ShoppingCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(ShoppingCartItem record);

    int updateByPrimaryKey(ShoppingCartItem record);

    ShoppingCartItem selectByUserIdAndGoodsId(@Param("userId") Long userId,@Param("goodsId") Long goodsId);

    int selectCountsByUserId( @Param("userId")Long userId);

    List<ShoppingCartItem> selectByUserId(@Param("userId") Long userId, @Param("number") int shoppingCartItemTotalNumber);

    int deleteBatch(@Param("ids") List<Long> ids);
}