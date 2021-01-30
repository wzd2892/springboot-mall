package com.wzd.newbeemall.controller.mall;
import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.MallException;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.controller.vo.OrderDetailVO;
import com.wzd.newbeemall.controller.vo.ShoppingCartItemVO;
import com.wzd.newbeemall.service.OrderService;
import com.wzd.newbeemall.service.ShoppingCartService;
import com.wzd.newbeemall.utils.JsonData;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params,
                                HttpServletRequest request,
                                HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if(StringUtils.isEmpty(params.get("page"))){
            params.put("page",1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        // 封装我的订单数据
        PageUtil pageUtil = new PageUtil(params);
        request.setAttribute("orderPageResult", orderService.getMyOrder(pageUtil));
        request.setAttribute("path", "order");
        return "mall/my-orders";
    }

    /**
     * 订单详情
     * @param request
     * @param orderNo
     * @param httpSession
     * @return
     */

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request,
                                  @PathVariable("orderNo") String orderNo,
                                  HttpSession httpSession){
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        OrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }


    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        NewBeeMallUserVO user = (NewBeeMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getShoppingCartItems(user.getUserId());
        if (StringUtils.isEmpty(user.getAddress().trim())) {
            //无收货地址
            MallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = orderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }


    @GetMapping("/paySuccess")
    @ResponseBody
    public JsonData paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType){
        String payResult = orderService.paySuccess(orderNo, payType);
        if(ServiceResultEnum.SUCCESS.getResult().equals(payResult)){
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildError(payResult);
        }
    }


}
