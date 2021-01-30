package com.wzd.newbeemall.service;

import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.controller.vo.OrderDetailVO;
import com.wzd.newbeemall.controller.vo.ShoppingCartItemVO;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;

import java.util.List;

public interface OrderService {

    PageResult getMyOrder(PageUtil pageUtil);

    String paySuccess(String orderNo, int payType);

    OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    String saveOrder(NewBeeMallUserVO user, List<ShoppingCartItemVO> myShoppingCartItems);
}
