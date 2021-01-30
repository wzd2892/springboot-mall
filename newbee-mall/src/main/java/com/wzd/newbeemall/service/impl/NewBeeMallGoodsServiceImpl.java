package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallSearchGoodsVO;
import com.wzd.newbeemall.mapper.GoodsInfoMapper;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.service.NewBeeMallGoodsService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

    @Autowired
    GoodsInfoMapper goodsInfoMapper;

    /**
     * 保存goodinfo的实现方法
     * @param goodsInfo
     * @return
     */
    @Override
    public String saveNewBeeMallGoods(GoodsInfo goodsInfo) {
        // 保存东西的流程
        if(goodsInfoMapper.insertSelective(goodsInfo) > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    /**
     * 根据主键或者goods
     * @param goodsId
     * @return
     */

    @Override
    public GoodsInfo getGoodsInfoById(Long goodsId) {
        return goodsInfoMapper.selectByPrimaryKey(goodsId);
    }

    /**
     * 更新的 service
     * * @param goodsInfo
     * @return
     */

    @Override
    public String updateNewBeeMallGoods(GoodsInfo goodsInfo) {
        GoodsInfo temp = goodsInfoMapper.selectByPrimaryKey(goodsInfo.getGoodsId());
        if(temp == null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if(goodsInfoMapper.updateByPrimaryKeySelective(goodsInfo)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PageResult getNewBeeMallGoodsPage(PageUtil pageUtil) {
        List<GoodsInfo> goodsInfoList = goodsInfoMapper.findNewBeeMallGoodsList(pageUtil);
        int total = goodsInfoMapper.getTotalNewBeeMallGoods(pageUtil);
        PageResult pageResult =  new PageResult(goodsInfoList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsInfoMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchNewBeeMallGoods(PageUtil pageUtil) {
        List<GoodsInfo> goodsList = goodsInfoMapper.findNewBeeMallGoodsListBySearch(pageUtil);

        int total = goodsInfoMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
            for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;    }


}
