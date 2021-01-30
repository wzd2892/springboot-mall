package com.wzd.newbeemall.service;


import com.wzd.newbeemall.controller.vo.ShoppingCartItemVO;
import com.wzd.newbeemall.model.entity.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartService {


    String saveCartItem(ShoppingCartItem shoppingCartItem);

    String updateCartItem(ShoppingCartItem shoppingCartItem);

    List<ShoppingCartItemVO> getShoppingCartItems(Long userId);

    Boolean deleteById(Long shoppingCartItemId);

    int[] getTotalItemAndPrice(Long userId);
}
