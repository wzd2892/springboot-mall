package com.wzd.newbeemall.controller.test;

import com.wzd.newbeemall.service.TestUserService;
import com.wzd.newbeemall.utils.JsonData;
import com.wzd.newbeemall.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class TestUserControler {

    @Autowired
    private TestUserService testUserService;

    /**
     * 分页功能测试
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonData list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return JsonData.buildError("参数异常！");
        }
        //查询列表数据
        PageUtil pageUtil = new PageUtil(params);
        return JsonData.buildSuccess(testUserService.getAdminUserPage(pageUtil));
    }

}