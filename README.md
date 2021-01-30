

### 1. 项目介绍

项目模块

- 会员模块
- 商品模块
- 订单模块
- 支付模块



#### 后端系统：

- 商城系统【用户使用】
- 商城后台管理系统【管理员使用】



![image-20201226135932800](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226135932800.png)



- 首页

	轮播图、设置的分类信息。热销的商品模块

- 浏览【搜索模块】

	浏览商品信息，根据分类和关键词去搜索商品

- 登陆和注册【用户登录注册模块】

	用户的登录注册的功能

- 选择商品【购物车模块】

	购物车功能 对商品的增删改以及商品的数量

- 提交订单【订单模块】

	把用户的收获信息记录

- 订单流程

	三个选择： 支付、不支付单保留、取消订单

- 确认收货

	<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226140743504.png" alt="image-20201226140743504" style="zoom:50%;" />



#### 后台管理系统



![image-20201226141036725](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226141036725.png)



基本就是增删改查的操作



- 轮播图管理
- 商品分类管理
- 热卖商品、推荐商品管理
- 商品管理系统
- 订单管理
- 会员管理
- 系统设置



![image-20201226141244049](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226141244049.png)



项目的基本技术选型都已经介绍完毕，后端框架使用 **Spring Boot 技术栈+ Thymeleaf + MyBatis** ，前端页面及交互更多的则是使用 **AdminLTE3 + BootStrap**，希望通过本篇文章的介绍，朋友们对我们的商城系统又有了更深的认识。



### 2. 页面布局详解

#### **后台管理系统布局的通用模板：**

![image-20201226142113429](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226142113429.png)



**基本信息区**：可以放置 Logo 图片、管理系统名称、在本区域的右方还可能放置其他辅助信息；

**导航栏区域**：后台管理系统的导航栏一般会被设计在整个版面的左侧，也有可能放在页面上方基本信息区域下，一般称之为菜单栏，用以实现页面跳转的管理；

**功能区**：这个区域会占用整个版面的大部分面积，后台管理系统的重要信息都在这里展示，绝大部分的页面逻辑、功能实现都会在这个区域里，因此是整个系统最重要的部分。

**页脚区域**：这个区域占用的面积较小，通常会在整个版面的底部一小部分区域，用来展示辅助信息，如版权信息、公司信息、项目版本号等等，不过这个区域并不是必须的。





#### 商城端

主页

![image-20201226142535298](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226142535298.png)

个人中心

![image-20201226142555022](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201226142555022.png)







### 3. 验证码的使用

使用的验证码的框架是 Google的 kaptcha框架

1. 添加依赖

	```
	<dependency>
	            <groupId>com.github.penggle</groupId>
	            <artifactId>kaptcha</artifactId>
	            <version>2.3.2</version>
	</dependency>
	```

	

2. 配置

	**将DefaultKaptcha注册到ioc容器中**

	新建。config -》 KaptchaConfig类。  用于设置各种验证码的参数

```java
package com.wzd.newbeemall.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KaptchaConfig {
    @Bean   // 使用bean把第三方类库装载到Spring中
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 图片边框
        properties.put("kaptcha.border", "no");
        // 字体颜色
        properties.put("kaptcha.textproducer.font.color", "black");
        // 图片宽
        properties.put("kaptcha.image.width", "160");
        // 图片高
        properties.put("kaptcha.image.height", "40");
        // 字体大小
        properties.put("kaptcha.textproducer.font.size", "30");
        // 验证码长度
        properties.put("kaptcha.textproducer.char.space", "5");
        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return  defaultKaptcha;
    }
}

```

然后创建控制器用于产生和验证验证码

产生验证码

```java
    @RequestMapping(value = "/kaptcha")
    public void defaultKaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        byte[] captchaOutputStream = null;  // 验证码输出流；
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();

        try {
            String verifyCode = captchaProducer.createText();  // 产生验证码字符串
            request.getSession().setAttribute("verityCode",verifyCode); // 保存在session作用域中
            BufferedImage challenge = captchaProducer.createImage(verifyCode); // 产生图片
            ImageIO.write(challenge,"jpg",imgOutputStream);         // 写入到imgOutputStream
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        captchaOutputStream = imgOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store"); // 通知从服务器到客户端内的所有缓存机制，表示它们是否可以缓存这个对象及缓存有效时间。其单位为秒
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0); // 指定一个日期/时间，超过该时间则认为此回应已经过期
        response.setContentType("image/jpeg");   // 当前内容的MIME类型

        ServletOutputStream responsegetOutputStream =  response.getOutputStream();
        responsegetOutputStream.write(captchaOutputStream);
        responsegetOutputStream.flush();
        responsegetOutputStream.close();
    }
```





验证：

```java
    @GetMapping(value = "/verify")
    @ResponseBody
    public String verify(@RequestParam("code") String code, HttpSession session){
        if(StringUtils.isEmpty(code)){
            return "验证码不能为空";
        }

        String kaptchCode = (String) session.getAttribute("verityCode");
        if(StringUtils.isEmpty(kaptchCode) || !code.equals(kaptchCode)){
            return "验证码错误";
        }
        return "验证成功";
    }
```





验证码功能在新峰商城项目的实际应用有三个地方：

- 管理系统端登陆功能
- 商城端用户注册功能
- 商城端用户登陆功能





### 4. 登陆功能

本次实践项目的登录状态我们是通过 **session** 来保存的，用户登录成功后我们将用户信息放到 session 对象中，之后再实现一个**拦截器**（后面一篇文章会介绍拦截器相关的知识并给出代码），在访问项目时判断 session 中是否有用户信息，有则放行请求，没有就跳转到登录页面。



已完成



### 5. 分页设计

后端必不可少的两个参数：

- 页面
- 每页的条数



mysql实现分页功能使用的是limit参数

```sql
//下面是mysql的实现语句：

select * from tb_xxxx limit 10,20
```

比如查询第 1 页每页 20 条数据就是查询数据库中从 0 到 20 条数据，查询第 4 页每页 10 条数据就是查询数据库中第 30 到 40 条数据，因此对于后端来说页码和条数两个参数就显得特别重要

再查询所有的数据

```sql
select count(*) from tb_xxxx
```

**分页功能实现：**

一：简单的sql语句

二：使用JqGrid分页插件进行分页

`JqGrid` 是一个用来显示网格数据的 `jQuery` 插件，通过使用 `jqGrid` 可以轻松实现前端页面与后台数据的 `Ajax` 异步通信并实现分页功能，特点如下：

- 兼容目前所有流行的 web 浏览器；
- 完善强大的分页功能；
- 支持多种数据格式解析，XML、JSON、数组等形式；
- 提供丰富的选项配置及方法事件接口；
- 支持表格排序，支持拖动列、隐藏列；
- 支持滚动加载数据；
- 开源免费



使用 JqGrid 时必要的文件如下：

```makefile
## js文件
jquery.jqGrid.js
grid.locale-cn.js
jquery.jqGrid.min.js

## 样式文件
ui.jqgrid-bootstrap-ui.css
ui.jqgrid-bootstrap.css
ui.jqgrid.css
```



步骤：

首先我们在html中导入JqGrid 所需文件：

```html
<link href="plugins/jqgrid-5.3.0/ui.jqgrid-bootstrap4.css" rel="stylesheet"/>
<!-- JqGrid依赖jquery，因此需要先引入jquery.min.js文件 -->
<script src="plugins/jquery/jquery.min.js"></script>

<script src="plugins/jqgrid-5.3.0/grid.locale-cn.js"></script>
<script src="plugins/jqgrid-5.3.0/jquery.jqGrid.min.js"></script>
```

 

然后在分页区中添加代码，用于初始化jqGrid

```html
<!-- JqGrid必要DOM,用于创建表格展示列表数据 -->  
<table id="jqGrid" class="table table-bordered"></table>
<!-- JqGrid必要DOM,分页信息区域 --> 
<div id="jqGridPager"></div>  
```

 

调用JqFrid分页插件的jqGird() 方法，用于渲染分页

1. 调用 JqGrid 分页插件的 jqGrid() 方法渲染分页展示区域，代码如下：

```js
$("#jqGrid").jqGrid({
        url: 'users/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, hidden: true, key: true},
            {label: '登录名', name: 'userName', index: 'userName', sortable: false, width: 80},
            {label: '添加时间', name: 'createTime', index: 'createTime', sortable: false, width: 80}
        ],
        height: 485,
        rowNum: 10,
        rowList: [10, 30, 50],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中...',
        rownumbers: true,
        rownumWidth: 35,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "data.list", 
            page: "data.currPage", 
            total: "data.totalPage", 
            records: "data.totalCount" 
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
```

参数释义：

```js
        url: // 请求后台json数据的url  
        datatype: // 后台返回的数据格式
        colModel: // 列表信息：表头 宽度 是否显示 渲染参数 等属性
        height: // 表格高度  可自行调节
        rowNum: // 默认一页显示多少条数据 可自行调节
        rowList: // 翻页控制条中 每页显示记录数可选集合
        styleUI: // 主题 这里选用的是Bootstrap主题
        loadtext: // 数据加载时显示的提示信息
        rownumbers: // 是否显示行号，默认值是false，不显示 
        rownumWidth: // 行号列的宽度
        autowidth: // 宽度自适应
        multiselect: // 是否可以多选
        pager: // 分页信息DOM
        jsonReader: {
            root:  //数据列表模型
            page:  //数据页码
            total:  //数据总页码
            records: //数据总记录数
        },
        // 向后台请求的参数
        prmNames: {
        },
        // 数据加载完成并且DOM创建完毕之后的回调函数 
        gridComplete: function () {
        }
    });
```

### 

由于 `JqGrid` 分页插件在实现分页功能时必须以下四个参数：当前页的所有数据列表、当前页的页码、总页码、总记录数量，因此我们封装了 `PageResult` 对象，并将其放入 `Result` 返回结果的 `data` 属性中，之后在 `JqGrid` 读取时直接读取对应的参数即可，这就是前后端进行数据交互时的格式定义，希望大家能够结合代码以及实际的分页效果进行理解和学习。



打开浏览器的开发者控制台，并进入 Network 面板，具体来看一下在翻页时执行了哪些 `XHR` 请求。通过调试过程我们发现，在每次点击分页按钮时都会向后端的 `users/list` 请求，这是一个 GET方式的请求，请求参数 `_search`、 `nd`、 `limit`、`page`、`sidx` 四个字段，这些参数都是 `JqGrid`插件内部封装的，我们后端在接受请求时只处理了 `limit` 和 `page` 参数，其他参数你也可以自行增加处理逻辑，这里不再继续讲解。







### 6. 分类管理模块

#### 6.1 介绍

分类是需要层级的，**先大类，再小类**

在左侧的一级中筛选到 “手机/数码” 这一个类目（一级分类）

然后在这个子分类中继续查找我们需要的物品。

我们采用 **“三级分类”**  和京东淘宝一致



#### 6.2 分类模块主要功能

- 分类数据的设置
- 商品与分类的挂靠以及关联



#### 6.3 分类模块的接口设计以及实现





### 7. 主页设计

<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20210105221103073.png" alt="image-20210105221103073" style="zoom:50%;" />



##### 7.1 轮播图

使用Swiper插件来进行轮播图的设计

之后将这两个文件引入到页面上：

```html
<link rel="stylesheet" th:href="@{/mall/css/swiper.min.css}">

<script th:src="@{/mall/js/swiper.min.js}" type="text/javascript"></script>
```

修改 IndexController 中的 indexPage() 方法，首先当然是注入 NewBeeMallCarouselService 对象，之后新增代码如下：

```java
List<NewBeeMallIndexCarouselVO> carousels = newBeeMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);

request.setAttribute("carousels", carousels);//轮播图
```

#### 轮播图数据渲染

最后是前端模板代码，使用 Thymeleaf 语法读取 `carousels` 轮播图列表对象，之后使用 th:each 循环将轮播图数据渲染到页面上，代码如下：

```html
        <div class="swiper-container fl">
            <div class="swiper-wrapper">
                <th:block th:unless="${#lists.isEmpty(carousels)}">
                    <th:block th:each="carousel : ${carousels}">
                        <div class="swiper-slide">
                            <a th:href="@{${carousel.redirectUrl}}">
                                <img th:src="@{${carousel.carouselUrl}}" alt="">
                            </a>
                        </div>
                    </th:block>
                </th:block>
            </div>
            <div class="swiper-pagination"></div>
            <div class="swiper-button-prev"></div>
            <div class="swiper-button-next"></div>
        </div>
```

#### 轮播效果实现

最后是通过 Swiper 插件实现轮播效果，所有的轮播图片已经渲染到页面中，我们在 js 目录中新建 index.js 并新增 Swiper 插件的初始化方法，代码如下：

```js
var newbeeSwiper = new Swiper('.swiper-container', {
    //设置自动播放
    autoplay: {
        delay: 2000,
        disableOnInteraction: false
    },
    //设置无限循环播放
    loop: true,
    //设置圆点指示器
    pagination: {
        el: '.swiper-pagination',
    },
    //设置上下页按钮
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    }
})
```

通过代码可以看出，我们将对 class 为 `swiper-container` 的 DOM 对象进行轮播效果的初始化，之后是设置自动播放，以及自动播放的间隔时间为 2 秒，同时也设置了手动轮播的按钮，轮播功能实现完成。



**关键 使用了一个vo对象，对于首页的轮播图而言只需要图片的url和redirect，不需要其他的数据**

