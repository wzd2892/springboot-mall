package com.wzd.newbeemall.controller.admin;


import com.wzd.newbeemall.common.IndexConfigTypeEnum;
import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.model.entity.IndexConfig;
import com.wzd.newbeemall.service.NewBeeMallIndexConfigService;
import com.wzd.newbeemall.utils.JsonData;
import com.wzd.newbeemall.utils.PageResult;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/api/v1/pri/admin")
public class NewBeeMallGoodsIndexConfigController {

    @Autowired
    NewBeeMallIndexConfigService newBeeMallIndexConfigService;

    @GetMapping("/indexConfigs")
    public String indexConfigPage(HttpServletRequest request, @RequestParam("configType") int configType){
        request.setAttribute("path", IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType));
        request.setAttribute("configType",configType);
        return "admin/newbee_mall_index_config";
    }

    /**
     * 首页配置分页列表接口
     * 添加首页配置接口
     * 修改首页配置接口
     * 批量删除首页配置接口
     */

    /**
     * 首页配置分页列表接口
     */
    @GetMapping("/indexConfigs/list")
    @ResponseBody
    public JsonData list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return JsonData.buildError("参数异常！");
        }
        // 已经把所有的请求参数都按照map放入其中了
        PageUtil pageUtil = new PageUtil(params);
        PageResult pageResult = newBeeMallIndexConfigService.getConfigsPage(pageUtil);
        return JsonData.buildSuccess(pageResult);

    }

    /**
     * 保存接口
     * @param indexConfig
     * @return
     */
    @PostMapping("/indexConfigs/save")
    @ResponseBody
    public JsonData save(@RequestBody IndexConfig indexConfig){
        if(Objects.isNull(indexConfig.getConfigType())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())){
            return JsonData.buildError("参数异常");
        }
        String result = newBeeMallIndexConfigService.saveindexConfig(indexConfig);
        if(ServiceResultEnum.SUCCESS.getResult() == result){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError(result);
        }

    }

    /**
     * 删除接口
     */
    @PostMapping("/indexConfigs/delete")
    @ResponseBody
    public JsonData delete(@RequestBody Integer[] ids){
        if(ids.length<1){
            return  JsonData.buildError("参数异常");
        }

        if(newBeeMallIndexConfigService.deleteBatch(ids)){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError("删除失败");
        }
    }

    /**
     * update
     */
    @PostMapping("/indexConfigs/update")
    @ResponseBody
    public JsonData update(@RequestBody IndexConfig indexConfig){
        if(Objects.isNull(indexConfig.getConfigId())
                ||Objects.isNull(indexConfig.getConfigType())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())){
            return JsonData.buildError("参数异常");
        }

        String result = newBeeMallIndexConfigService.updateindexConfig(indexConfig);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError(result);
        }


    }

}
