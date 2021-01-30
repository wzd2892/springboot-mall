package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Long orderItemId);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);
}