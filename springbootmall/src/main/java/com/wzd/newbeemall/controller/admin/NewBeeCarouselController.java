package com.wzd.newbeemall.controller.admin;


import com.wzd.newbeemall.common.ServiceResultEnum;
import com.wzd.newbeemall.model.entity.Carousel;
import com.wzd.newbeemall.service.NewBeeMallCarouselService;
import com.wzd.newbeemall.utils.JsonData;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/api/v1/pri/admin")
public class NewBeeCarouselController {

    @Autowired
    NewBeeMallCarouselService newBeeMallCarouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_carousel");
        return "admin/newbee_mall_carousel";
    }

    /**
     * 列表接口
     * @param params
     * @return
     */
    @GetMapping("/carousels/list")
    @ResponseBody
    public JsonData list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return JsonData.buildError("参数异常！");
        }
        //查询列表数据
        PageUtil pageUtil = new PageUtil(params);
        return JsonData.buildSuccess(newBeeMallCarouselService.getCarouselPage(pageUtil));
    }

    /**
     *     轮播图保存接口 接收的参数为 carouselUrl 字段、redirectUrl 字段和 carouselRank字段
      */
    @PostMapping("/carousels/save")
    @ResponseBody
    public JsonData save(@RequestBody Carousel carousel){
        if (StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return JsonData.buildError("参数异常！");
        }

        String result = newBeeMallCarouselService.saveCarousel(carousel);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return JsonData.buildSuccess();
        }else{
            return JsonData.buildError(result);
        }
    }

    /**
     * 删除接口   接收参数为主键值
     * @param ids
     * @return
     */
    @PostMapping("/carousels/delete")
    @ResponseBody
    public JsonData delete(@RequestBody Integer[] ids){
        if(ids.length<1){
            return JsonData.buildError("参数异常！");
        }

        if (newBeeMallCarouselService.deleteBatch(ids)) {
            return JsonData.buildSuccess();
        } else {
            return JsonData.buildError("删除失败");
        }
    }

    @PostMapping(value = "/carousels/update")
    @ResponseBody
    public JsonData update(@RequestBody Carousel carousel ){
        if(Objects.isNull(carousel.getCarouselId())
                ||StringUtils.isEmpty(carousel.getCarouselUrl())
                ||Objects.isNull(carousel.getCarouselRank())){
            return JsonData.buildError("参数异常");
        }

        String result = newBeeMallCarouselService.updateCarousel(carousel);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return JsonData.buildSuccess();
        }else {
            return JsonData.buildError(result);
        }
    }

    /**
     * 查询详情
     */
    @GetMapping(value = "/carousels/info/{id}")
    @ResponseBody
    public JsonData info(@PathVariable("id") Integer id ){
        if(Objects.isNull(id)){
            return JsonData.buildError("参数异常");
        }
        Carousel carousel = newBeeMallCarouselService.getCarouselById(id);
        if(Objects.isNull(carousel)){
            return JsonData.buildError(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return JsonData.buildSuccess(carousel);

    }

}
