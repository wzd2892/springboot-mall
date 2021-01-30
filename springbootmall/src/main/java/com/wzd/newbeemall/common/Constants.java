package com.wzd.newbeemall.common;

/**
 * 相关配置
 */
public class Constants {
    public final static String FILE_UPLOAD_DIC = "/Users/wangzhengdong/Desktop/goods-img/";//上传文件的默认url前缀，根据部署设置自行修改

    public final static int SELL_STATUS_UP = 0;//商品上架状态
    public final static int SELL_STATUS_DOWN = 1;//商品下架状态

    public static final int INDEX_CAROUSEL_NUMBER = 5;


    public static final int INDEX_CATEGORY_NUMBER = 10; // 分类数量

    public static final int INDEX_HOT_GOODS_NUMBER = 5; // 热销商品数量
    public static final int INDEX_NEW_GOODS_NUMBER = 5; // 新品数量

    public static final int INDEX_RECOMMEND_GOODS_NUMBER = 5; // 推荐商品数量；

    public final static String MALL_USER_SESSION_KEY = "mallUser";//session中user的key


    public static final Object GOODS_SEARCH_PAGE_LIMIT = 10; // 搜索每页条数
    public static final int SEARCH_CATEGORY_NUMBER = 10;

    public static final int SHOPPING_CART_ITEM_LIMIT_NUMBER = 5;// 最大限制购物数量
    public static final int SHOPPING_CART_ITEM_TOTAL_NUMBER = 25; // 购物车总共多少个数量

    public static final int ERROR_CODE = -1;
    public static final Object ORDER_SEARCH_PAGE_LIMIT = 3;
}
