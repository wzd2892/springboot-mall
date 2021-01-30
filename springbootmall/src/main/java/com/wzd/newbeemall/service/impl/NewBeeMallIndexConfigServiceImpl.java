package com.wzd.newbeemall.service.impl;

import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.wzd.newbeemall.mapper.GoodsInfoMapper;
import com.wzd.newbeemall.mapper.IndexConfigMapper;
import com.wzd.newbeemall.model.entity.GoodsInfo;
import com.wzd.newbeemall.model.entity.IndexConfig;
import com.wzd.newbeemall.service.NewBeeMallIndexConfigService;
import com.wzd.newbeemall.utils.BeanUtil;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class NewBeeMallIndexConfigServiceImpl implements NewBeeMallIndexConfigService {

    @Autowired
    IndexConfigMapper indexConfigMapper;

    @Autowired
    GoodsInfoMapper goodsInfoMapper;

    @Override
    public PageResult getConfigsPage(PageUtil pageUtil) {
        // 当前页码的数据
        List<IndexConfig> indexConfigList = indexConfigMapper.findIndexConfigList(pageUtil);
        // 获取总页数
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;


    }

    @Override
    public String saveindexConfig(IndexConfig indexConfig) {
        if(indexConfigMapper.insertSelective(indexConfig)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }

        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        if(ids.length<1){
            return false;
        }

        return indexConfigMapper.deleteBatch(ids)>0;
    }

    @Override
    public String updateindexConfig(IndexConfig indexConfig) {
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if(Objects.isNull(temp)){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        indexConfig.setUpdateTime(new Date());
        if(indexConfigMapper.updateByPrimaryKeySelective(indexConfig)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<NewBeeMallIndexConfigGoodsVO> newBeeMallIndexConfigGoodsVOS = new ArrayList<>();
        List<IndexConfig> indexConfigList = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);

        if(!CollectionUtils.isEmpty(indexConfigList)){
        // 取出所有的goodids
            List<Long> goodsIds = indexConfigList.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<GoodsInfo> newBeeMallGoods = goodsInfoMapper.selectByPrimaryKeys(goodsIds);
            newBeeMallIndexConfigGoodsVOS = BeanUtil.copyList(newBeeMallGoods, NewBeeMallIndexConfigGoodsVO.class);

            for (NewBeeMallIndexConfigGoodsVO newBeeMallIndexConfigGoodsVO : newBeeMallIndexConfigGoodsVOS) {
                String goodsName = newBeeMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    newBeeMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    newBeeMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return newBeeMallIndexConfigGoodsVOS;
    }
}
