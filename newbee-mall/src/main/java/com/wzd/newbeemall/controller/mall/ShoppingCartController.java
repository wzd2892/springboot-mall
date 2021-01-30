package com.wzd.newbeemall.controller.mall;


import com.wzd.newbeemall.common.Constants;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallUserVO;
import com.wzd.newbeemall.controller.vo.ShoppingCartItemVO;
import com.wzd.newbeemall.model.entity.ShoppingCartItem;
import com.wzd.newbeemall.service.ShoppingCartService;
import com.wzd.newbeemall.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("shop-cart")
    @ResponseBody
    public JsonData saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem,
                                                   HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(user.getUserId());
        String saveResult = shoppingCartService.saveCartItem(shoppingCartItem);

        int[] arr = shoppingCartService.getTotalItemAndPrice(user.getUserId());
        session.setAttribute("itemsTotal", arr[0]);

        // 添加成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(saveResult)){
            return JsonData.buildSuccess();
        }
        // 添加失败
        return JsonData.buildError(saveResult);
    }

    @GetMapping("shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<ShoppingCartItemVO> shoppingCartItemVOList = shoppingCartService.getShoppingCartItems(user.getUserId());
        if(!CollectionUtils.isEmpty(shoppingCartItemVOList)){
            // 购物的总数
            itemsTotal = shoppingCartItemVOList.stream().mapToInt(ShoppingCartItemVO::getGoodsCount).sum();
            if(itemsTotal<1){
                return "error/error_5xx";
            }

            // 总价
            for(ShoppingCartItemVO shoppingCartItemVO:shoppingCartItemVOList){
                priceTotal += shoppingCartItemVO.getGoodsCount() * shoppingCartItemVO.getSellingPrice();
            }
            if(priceTotal<1){
                return "error/error_5xx";
            }
        }
        session.setAttribute("itemsTotal", itemsTotal);

        //request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", shoppingCartItemVOList);
        return "mall/cart";
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public JsonData updateNewBeeMallShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem,
                                                     HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(user.getUserId());
        String updateResult = shoppingCartService.updateCartItem(shoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return JsonData.buildSuccess();
        }
        //修改失败
        return JsonData.buildError(updateResult);
    }

    @DeleteMapping("/shop-cart/{shoppingCartItemId}")
    @ResponseBody
    public JsonData updateNewBeeMallShoppingCartItem(@PathVariable("shoppingCartItemId") Long shoppingCartItemId,
                                                   HttpSession session) {
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = shoppingCartService.deleteById(shoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return JsonData.buildSuccess();
        }
        //删除失败
        return JsonData.buildError(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession session){
        int priceTotal = 0;
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)){
            return "/shop-cart";
        } else {
            // 总价
            for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                priceTotal += shoppingCartItemVO.getGoodsCount() * shoppingCartItemVO.getSellingPrice();
            }
            if(priceTotal<1){
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
