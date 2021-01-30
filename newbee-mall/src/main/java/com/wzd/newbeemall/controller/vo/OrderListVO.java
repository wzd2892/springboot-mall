package com.wzd.newbeemall.controller.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

// 这才是真正的order!, 包含很多订单的order item
// 用户id也在这边展示
public class OrderListVO implements Serializable {

    private Long orderId;



    // 订单号
    private String orderNo;

    // 订单总价
    private Integer totalPrice;

    // 支付方式
    private Byte payType;


    //订单状态
    private Byte orderStatus;

    // 支付方式对应的文案
    private String orderStatusString;

    // 交易时间
    private Date createTime;

    // 订单项列表
    private List<OrderItemVO> orderListVOS;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Byte getPayType() {
        return payType;
    }

    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    public String getOrderStatusString() {
        return orderStatusString;
    }

    public void setOrderStatusString(String orderStatusString) {
        this.orderStatusString = orderStatusString;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<OrderItemVO> getOrderListVOS() {
        return orderListVOS;
    }

    public void setOrderListVOS(List<OrderItemVO> orderListVOS) {
        this.orderListVOS = orderListVOS;
    }

    public Byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
