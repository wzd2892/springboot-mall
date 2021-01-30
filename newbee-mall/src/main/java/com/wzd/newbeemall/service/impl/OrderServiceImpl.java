package com.wzd.newbeemall.service.impl;

import com.sun.tools.corba.se.idl.constExpr.Or;
import com.wzd.newbeemall.common.*;
import com.wzd.newbeemall.controller.vo.*;
import com.wzd.newbeemall.mapper.GoodsInfoMapper;
import com.wzd.newbeemall.mapper.OrderItemMapper;
import com.wzd.newbeemall.mapper.OrderMapper;
import com.wzd.newbeemall.mapper.ShoppingCartItemMapper;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.model.entity.Order;
import com.wzd.newbeemall.model.entity.OrderItem;
import com.wzd.newbeemall.model.entity.StockNumDTO;
import com.wzd.newbeemall.service.OrderService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.NumberUtil;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    GoodsInfoMapper goodsInfoMapper;

    @Autowired
    ShoppingCartItemMapper shoppingCartItemMapper;

    @Override
    public PageResult getMyOrder(PageUtil pageUtil) {
        int total = orderMapper.getTotalOrders(pageUtil);
        List<Order> orders = orderMapper.findOrderList(pageUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();

        if(total>0){
            // 数据转换 将实体类转化成VO
            orderListVOS = BeanUtil.copyList(orders,OrderListVO.class);
            // 设置订单状态，显示中文
            for(OrderListVO orderListVO:orderListVOS){
                orderListVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderListVO.getOrderStatus()).getName());
            }
            // 获取订单的所有的id
            List<Long> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
            // 如果订单的util不为空, 那么封装每一个订单对象,
            // 由一个orderid 来获取每一个order下面的所有的order item
            if(!CollectionUtils.isEmpty(orderIds)){
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIds(orderIds);
                // List to Map; 使用 groupingby
                Map<Long, List<OrderItem>> itemByOrderId = orderItems.stream().collect(groupingBy(OrderItem::getOrderId));
                // 给每一个orderListVO 把包含的item给赋予了对应的orderListVO
                for(OrderListVO orderListVO:orderListVOS){

                    if(itemByOrderId.containsKey(orderListVO.getOrderId())){
                        List<OrderItem> orderItemListTemp = itemByOrderId.get(orderListVO.getOrderId());
                        List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItemListTemp, OrderItemVO.class);
                        // 赋予每一个订单的  商品列表！！
                        orderListVO.setOrderListVOS(orderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS,total,pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order!=null){
            order.setOrderStatus((byte)OrderStatusEnum.OREDER_PAID.getOrderStatus());
            order.setPayType((byte)payType);
            order.setPayStatus((byte)OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            if(orderMapper.updateByPrimaryKeySelective(order)>0){
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            // order 包含order items
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
                OrderDetailVO orderDetailVO = new OrderDetailVO();
                BeanUtil.copyProperties(order, orderDetailVO);
                orderDetailVO.setOrderStatusString(OrderStatusEnum.getOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
                orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
                orderDetailVO.setOrderItemVOS(orderItemVOS);
                return orderDetailVO;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public String saveOrder(NewBeeMallUserVO user, List<ShoppingCartItemVO> myShoppingCartItems) {
        // 获取使用item的id
        List<Long> itemIdList = myShoppingCartItems.stream().map(ShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        // 获取使用的goodsid
        List<Long> goodsIds = myShoppingCartItems.stream().map(ShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<GoodsInfo> newBeeMallGoods = goodsInfoMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<GoodsInfo> goodsListNotSelling = newBeeMallGoods.stream()
                .filter(goodsTemp -> goodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            MallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }

        /* list 到 map*/
        Map<Long, GoodsInfo> goodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!goodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > goodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }

        //删除购物项 把购物车的表单清空
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(newBeeMallGoods)) {
            // 删除购物项成功
            if (shoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                /*修改库存所需要的list*/
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                // 更新库存
                int updateStockNumResult = goodsInfoMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                Order order = new Order();
                order.setOrderNo(orderNo);
                order.setUserId(user.getUserId());
                order.setUserAddress(user.getAddress());
                //总价
                for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += shoppingCartItemVO.getGoodsCount() * shoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                order.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (orderMapper.insertSelective(order) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                        OrderItem orderItem = new OrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(shoppingCartItemVO, orderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        // order Id 中会有很多
                        orderItem.setOrderId(order.getOrderId());
                        orderItems.add(orderItem);
                    }
                    //保存至数据库
                    if (orderItemMapper.insertBatch(orderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }
}
