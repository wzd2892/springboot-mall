package com.wzd.newbeemall.mapper;

import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.model.entity.StockNumDTO;
import com.wzd.newbeemall.utils.PageUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsInfoMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(GoodsInfo record);

    int insertSelective(GoodsInfo record);

    GoodsInfo selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(GoodsInfo record);

    int updateByPrimaryKeyWithBLOBs(GoodsInfo record);

    int updateByPrimaryKey(GoodsInfo record);

    List<GoodsInfo> findNewBeeMallGoodsList(Map param);

    int getTotalNewBeeMallGoods(Map param);

    int batchUpdateSellStatus(@Param("ids")Long[] ids, @Param("sellStatus")int sellStatus);

    List<GoodsInfo> selectByPrimaryKeys(@Param("goodsIds") List<Long> goodsIds);

    List<GoodsInfo> findNewBeeMallGoodsListBySearch(Map param);

    int getTotalNewBeeMallGoodsBySearch(Map param);

    int updateStockNum(@Param("stockNumDTOS")List<StockNumDTO> stockNumDTOS);
}