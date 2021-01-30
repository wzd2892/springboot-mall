package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.Order;
import com.wzd.newbeemall.utils.PageUtil;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    int getTotalOrders(Map map);

    List<Order> findOrderList(Map map);

    Order selectByOrderNo(String orderNo);
}