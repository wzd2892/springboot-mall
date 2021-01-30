[TOC]



### 1. 认识SpringBoot

#### 1.1 特性：

1. 创建独立的spring应用程序
2. 内嵌**Tomcat**， **Jetty** 或者 **Undertow** （无需部署WAR文件）
3. 提供starter POM文件，让 maven 依赖配置简单【减少依赖所造成的jar包冲突或者版本冲突】
4. 可以自动配置spring 和 springmvc 以及第三方库文件
5. 提供程序健康检查
6. 基本不需要xml配置文件，采用注解配置方式

**starter** ： 简单理解就是场景启动器，在不同的场景来使用不通的starter，方便导入各种依赖包。

<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201224194235561.png" alt="image-20201224194235561" style="zoom:50%;" />



#### 1.2 总结

SpringBoot 是用于简化spring配置与开发，使得基于Spring框架的开发更加的快速更加的方便。用于减少相关的配置，提高开发效率



### 2. SpringBoot 文件目录

```java
newbee-mall
    ├── src/main/java        程序开发目录
    ├── src/main/resources   配置文件目录
  							└──statics   存放静态资源
  							└──templates 存放模版文件目录
    ├── src/test/java				 测试文件目录
		└── pom.xm							 pom文件 项目依赖
```



### 3. SpringBoot相关特性

#### 3.1 约定优于配置（convention over configuration）

在项目中遵守命名规范可以减少所需要的配置。 自动化配置类在在类路径下`META-INF/spring.factories`文件中，可以通过使用`@EnableAutoConfiguration`注解加载到容器中并发挥作用

#### 3.2 依赖管理

所有的SpringBoot项目都有一个依赖的父项目：`spring-boot-starter-parent`, 用于规定JDK版本，项目编码和maven项目编译设置

该父项目有另一个父项目`spring-boot-dependecies`，定义了大量的依赖。这些依赖可以被子项目覆盖。

#### 3.3 web场景启动器

`spring-boot-starter-web`

定义了原本SpringMVC所需要的依赖和内置的TOMCAT的依赖

#### 3.4 SpringBoot main方法

**方法一：直接在主配置类设置main方法参数**

```java
//@SpringBootApplication --> @SpringBootConfiguration --> @Configuration
@SpringBootApplication  // 本质是一个配置类
public class DemoApplication{
  public static void main(String[] args){
    	// 启动spring ioc 容器
    	ApplicationContext context =SpringApplication.run(DemoApplication.class,args);
    	// 获取Bean对象
    	XXXService xxxService = context.getBean(XXXService.class);
    	// 调用方法
    	xxxService.doSome();
    
    	///// 方法二
    	SpringApplication app = new SpringApplication(DemoApplication.class);
    	app.setBannerMode(Banner.Mode.OFF);  // log 设置。 在这步可以进行一些相关的设置【也可以直接																					applicaition.properties 直接设置】
    	app.run();
  }
}
```

------

**@SpringBootApplication 注解**

点击 @SpringBootApplication 注解查看其源码，代码如下：

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
      @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
      @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
```

由注解源码可以看出，@SpringBootApplication 注解是一个复合注解，其中前面四个注解是 Java 元注解，含义分别为：

- @Target(ElementType.TYPE)：类、接口（包括注解类型）和 enum 声明
- @Retention(RetentionPolicy.RUNTIME)：运行时注解
- @Documented：注解添加到 Java doc 中
- @Inherited：允许继承

重要的是后面三个 Spring Boot 框架的自定义注解，含义分别为:

- @SpringBootConfiguration：Spring Boot 配置注解
- @EnableAutoConfiguration：启用自动配置注解
- @ComponentScan：组件自动扫描注解

 **@SpringBootApplication 就是 **

**@SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan 三个注解的合并**

- **@SpringBootConfiguration** 对于@Configuration 注解的包装，用于定义配置类，代替xml文件。定义为SpringBoot的配置文件。本质上还是@Configuration

- **@EnableAutoConfiguration**注解，用于开启自动配置

	- @AutoConfigurationPackage注解
		- 【将包含@Import，作用：将包含了该注解组件注册到Spring IOC容器中】
		- 【导入内容由`AutoConfigurationPackages.Registrar.class`决定】
		- 【是为什么有一些组件没有被扫描的原因。不在指定的目录下】
	- AutoConfigurationImportSelector 组件`@Import(AutoConfigurationImportSelector.class)`
		- 是自动配置的**核心内容**，负责返回自动配置的相关组件然后注册到IOC容器中
		- 会将所有需要导入的组件以全类名方式返回，把这些组件注册到IOC容器中。

	- **小总结：**SpringBoot在启动的时候，从类路径下的`META-INF/spring.factories`获取EnableAutoConfiguration的指定配置项，然后过滤在导入到spring ioc容器中。

- **@ComponentScan 注解**

	- 用于把标注了 @Controller 、 @Service 、 @Repository 、 @Component注解的类装载到Spring IOC容器中

		```xml
		<!-- 自动扫描 -->
		<context:component-scan base-package="com.ssm.demo.dao"/>    <context:component-scan base-package="com.ssm.demo.service"/>    <context:component-scan base-package="com.ssm.demo.controller"/>
		```

		

	这个相当于在spring配置文件中写组件扫描器。目的是让Spring容器知道在哪些包中获取需要注册的bean

------

**SpringApplication.run()方法**

步骤分析

1. 代码执行时间监控开启，SpringBoot应用成功后会自动打印时间

2. 配置headless属性

	【java.awt.headless是 J2SE 的一种模式，用于在缺失显示屏、鼠标或者键盘时的系统配置，默认为 true。】

3. 获取SpringApplicationRunListeners， 使用getRunListeners()。【会从`META-INF/spring.factories`中获取SpringApplicationRunListener 指定的类】

4. 回调所有SpringApplicationRunListeners 对象的 starting() 方法

5. environmentPrepared主要是完成对于ConfigurableEnvironment初始化工作

6. 打印SpringBoot 启动的Banner对象

7. 【使用**prepareContext**】创建 ApplicationContext 并配置 ApplicationContext 实例

8. refreshContext() 刷新 ApplicationContext 对象

9. 在 ApplicationContext 完成启动后，会对 ApplicationRunner 和 CommandLineRunner 进行回调处理,查找当前ApplicationContext中是否注册有 CommandLineRunner，如果有，则遍历执行它们。

10. 在 SpringApplication 启动过程中，如果出现问题会由异常处理器接管，对异常进行统一处理。

	

**方法二：在application.properties设置main方法参数**

```
在application.properties中

spring.main.banner-mode # 关闭log
spring.main.sources=com.wzd.DemoApplication # 主配置类
spring.main.web-application-type={none || reactive || servlet} # 分别对应 java || reactive || 																																					web 项目
```



### 4. SpringMVC自动配置

#### 4.1 Springmvc 回顾

spring项目配置springmvc步骤：

1. 在SpringMVC的配置文件中配置Bean
2. 在web.XML文件中配置中央调度器`DsipatcherServlet`以及它的`servlet-mapping`节点请求地址映射

配置文件如下:

```xml
    <!--Start spring mvc servlet-->
    <Servlet>
        <Servlet-name>springMVC</Servlet-name>
        <Servlet-class>org.springframework.web.Servlet.DispatcherServlet</Servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-mvc.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </Servlet>
    <!--End spring mvc servlet-->
    <!--Start Servlet-mapping -->
    <Servlet-mapping>
        <Servlet-name>springMVC</Servlet-name>
        <url-pattern>/</url-pattern>
    </Servlet-mapping>
    <!--End Servlet-mapping -->
```

3. 启动Tomcat（Servlet容器）去装载DispatcherSevlet】
4. 进行相关开发



#### 4.2 自动配置类 DispatcherServletAutoConfiguration

SpringMVC的DispatcherServlet是通过Spring Boot的自动装载机制注册到IOC容器中的。

自动配置类名称为DispatcherServletAutoConfiguration。位置是在spring-boot-autoconfigure-2.1.0.RELEASE.jar 中的 org.springframework.boot.autoconfigure.web 

自动配置启动时机： 当前是web项目，且当前 classpath 下存在 DispatcherServlet 类。@AutoConfigureAfter 注解又定义了自动配置类生效是在 ServletWebServerFactory 自动配置之后。

**DispatcherServletAutoConfiguration 向 IOC 容器中注入了什么?**



- dispatcherServlet 的bean 到Sring IOC容器中
- 注册一个名称为 “dispatcherServletRegistration” 的 Bean 到 Spring IOC 容器



#### 4.3 DispatcherServlet自动配置流程



- ##### 注册到ioc容器中

SpringBoot 在启动的时候会调用AbstractApplicationContext.refresh() 

refresh有 onRefresh()  方法

onRefresh() 方法中会调用 createWebServer() 方法，在 createWebServer() 方法中执行 `ServletContext ServletContext = getServletContext();` 获取 ServletContext 对象，该方法执行成功后会触发 DispatcherServletAutoConfiguration 进行 DispatcherServlet 的自动配置。

【原因是DispatcherServletAutoConfiguration上有一个 **@AutoConfigureAfter**(ServletWebServerFactoryAutoConfiguration.class)的注解】



- ##### 装载到Servlet容器中【完成注册后，进行对象装载】

**ServletWebServerApplicationContext**类中

1. 获取内嵌的ServletWebServerFactory
2. 通过Factory对象创建Servlet容器对象的创建，并且启动servlet容器对象。

**装载 Servlet 具体的方法调用链路如下：**

1. onStartup() 方法的实现在RegistrationBean 类中，方法中调用 register() 方法去注册和配置 bean
2. register() 方法的实现在 DynamicRegistrationBean 类中，方法执行时会调用 addRegistration() 方法，本文所讲的 DispatcherServlet 也就是在该方法内进行装载的。

3. addRegistration() 方法的实现在 ServletRegistrationBean 类中，方法执行时会调用 addServlet() 方法装载 Servlet
4. addServlet() 方法大家应该都有所了解，具体实现如下：

Servlet 容器启动之后会回调 selfInitialize()，在该方法中完成了 DispatcherServlet 的装载过程，方法调用链如下：

![img](https://user-gold-cdn.xitu.io/2019/10/13/16dc5d1472a3a549?imageslim)





### 5. SpringBoot 自动装载机制

通过官方文档的介绍我们可以发现，Spring Boot 还做了如下的默认配置：

- 自动配置了 ViewResolver 视图解析器
- 静态资源文件夹处理
- 自动注册了大量的转换器和格式化器
- 提供了 HttpMessageConverter 对请求参数和返回结果进行处理
- 自动注册了 MessageCodesResolver
- 默认欢迎页配置
- favicon 自动配置



#### 5.1 WebMvcAutoConfiguration

以上的自动配置都是在这个WebMvc自动配置类中定义的，该自动配置类定义同样也在 spring-boot-autoconfigure-2.1.0.RELEASE.jar 中的 。



- ####  ViewResolver视图解析器

视图解析器可以把各种的视图转化成物理视图，所有的视图解析器必须实现ViewResolver接口。

在WebMvcAutoConfigurationAdapter内部类中，满足条件会自动向ioc容器中注入三个视图解析器，分别是：

- InternalResourceViewResolver （当Controller返回值是String，去查找Bean的名称为返回字符串的 View 来渲染视图）

	```xml
	以前的web.xml 视图解析器的配置方法  
	<!-- 视图解析器 -->
	<bean id="viewResolver"
	          class="org.springframework.web.servlet.view.InternalResourceViewResolver">
	        <property name="prefix" value="/"/>
	        <property name="suffix" value=".jsp"></property>
	 </bean>
	```

- BeanNameViewResolver （主要通过设置前缀、后缀以及控制器中方法来返回视图名的字符串,以得到实际视图内容）

- ContentNegotiatingViewResolver（它并不会自己处理各种视图，而是委派给其他不同的 ViewResolver 来处理不同的 View，级别为最高。）





- ####  自动注册 Converter 、Formatter （自动类型转换）

在 WebMvcAutoConfigurationAdapter 内部类中，含有 addFormatters() 方法，该方法会向 FormatterRegistry 添加 IOC 容器中所有的 Converter、GenericConverter、Formatter 类型的 bean。

SpringMvc有数据类型转化，Http 请求传递的数据都是**字符串 String 类型**的， Controller 中定义，如果该方法对应的地址接收到到浏览器的请求的话，并且请求中含有 goodsName(String 类型)、weight(float类型)、type(int类型)、onSale(Boolean类型) 参数且都已经被进行正确的类型转换了，如果参数无法通过 String 强转的话也会报错，这就是文章中提到的 MessageCodesResolver 



- ####  消息转换器 HttpMessageConverter

HttpMessageConverter 的设置也是通过 WebMvcAutoConfigurationAdapter 完成的。

使用 @RequestBody、@ResponseBody 注解进行请求实体的转换和响应结果的格式化输出。用对象来和json数据进行相互转换。SpringMVC 内部维护了一套转换机制，也就是我们通常所说的“将 json 格式的请求信息转换为一个对象，将对象转换为 json 格式并输出为响应信息 ”，这些就是 HttpMessageConverter 的作用。



- ####  Spring Boot 对静态资源的映射规则



静态资源的映射是在 addResourceHandlers() 方法中进行映射配置的，类似于 SpringMVC xml配置文件中的：

```xml
<mvc:resources mapping="/images/**" location="/images/" />
```

其中 staticPathPattern 变量值为 "/**"，实际的静态资源存放目录通过 getResourceLocations() 方法获取

可以得到 Spring Boot 默认的静态资源处理目录为：

- "classpath:/META-INF/resources/",
- "classpath:/resources/"
- "classpath:/static/"
- "classpath:/public/"

访问当前项目的任何资源，都会去静态资源的文件夹中查找对应的资源，不存在资源则会显示相应的错误页面，因此我们在开发 web 项目时只需要包含这几个目录中的任意一个或者多个，之后将静态资源文件放入其中即可。

![static](https://user-gold-cdn.xitu.io/2019/10/13/16dc5d4220193182?imageView2/0/w/1280/h/960/ignore-error/1)



可以发现静态资源虽然在不同的目录中但是都能够被正确的返回，这就是 Spring Boot 对静态资源的拦截处理，当然，开发时也可以在配置文件中修改这些属性，比如我们将拦截路径改为 "/static/**"，并将静态资源目录修改为 resources/gitchat ,那么默认配置就会失效而使用开发者自定义的配置，修改 application.properties 文件，添加如下配置：

```xml
spring.mvc.static-path-pattern=/static/**   // 访问路径映射地址
spring.resources.static-locations=classpath:/gitchat/   // 静态资源存放地址
```

修改后，再访问以上三个资源文件将会报 404 的错误，如果想要正常访问则需要将静态资源文件移动到 gitchat 目录下





- #### 默认欢迎界面 index.jsp



- #### favicon 图标



favicon 的读取路径也是通过静态资源目录下的 favicon.ico 完成的，因此只要在静态资源目录中添加 favicon.ico 文件即可完成 favicon 的展示。



- #### 总结

通过这两篇文章的讲解和源码学习，我们可以发现 Spring Boot 在进行 web 项目开发时为开发者提供了如此全面而便利的默认自动设置，以往需要在 web.xml 或者 SpringMVC 配置文件中设置的内容，都改为以编码的方式进行自动注入和实现，开发者在使用 Spring Boot 进行项目开发时甚至一行配置都不用写就可以直接上手开发，不用做任何配置就已经有了视图解析器，也不用自行添加消息转换器，SpringMVC 需要的一些功能都已经默认加载完成。当然，如果这些默认配置你觉得不符合实际的业务需求，也可以自行配置，Spring Boot 也提供了配置键以及辅助类进行实现。





### 6. Thymeleaf模版引擎

#### 6.1 模版引擎介绍

模板引擎（这里特指用于 Web 开发的模板引擎）是为了使用户看到的页面与业务数据（内容）分离而产生的一种模板技术，它可以生成特定格式的文档，用于网站的模板引擎就会生生产出标准的 HTML 静态页面内容，在 Java Web 开发技术栈中，常见的模板引擎有 FreeMarker 、Velocity 、Thymeleaf 等，JSP 也可以理解为一种模板引擎技术。

工作原理如下

![img](https://user-gold-cdn.xitu.io/2019/10/15/16dce9613e9c546c?imageView2/0/w/1280/h/960/ignore-error/1)

使用模板引擎技术可以动态加载数据，并在控制器中将模板需要的数据组装好，之后将二者都交给模板引擎，模板引擎会根据数据和模板表达式语法解析并填充到指定位置进行页面渲染，最终生成 html 内容响应给客户端。

#### 6.2 原因

Jsp 是java代码 + 数据 + html 方式最终产生出html内容响应，本质上就是servlet，会产生class文件，和java语言、servlet有很强的绑定关系

选择模板引擎替代 JSP 技术的原因如下：

- **优点一：松耦合**
- **优点二：前后端分离更加彻底**
- **优点三：性能优化**
- **优点四：灵活度高**



#### 6.3 使用Thymeleaf

##### 1. 引入依赖

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId></dependency>
```

##### 2. 创建模版文件

在 resources/templates 目录新建模板文件 thymeleaf.html

首先在模板文件的 标签中导入 Thymeleaf 的名称空间：

```xml
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

导入该名称空间主要是为了 Thymeleaf 的语法提示和 Thymeleaf 标签的使用，之后我们在模板中增加如前文 JSP 中相同的显示内容，最终的模板文件如下：

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Thymeleaf demo</title>
</head>
<body>
<p>description字段值为：</p>
<p th:text="${description}">这里显示的是 description 字段内容</p>
</body>
</html>
```



![1](https://user-gold-cdn.xitu.io/2019/10/15/16dce9de783c345e?imageView2/0/w/1280/h/960/ignore-error/1)



如上图所示，该模板文件语句中包含三块内容：

1. html 标签
2. Thymeleaf 模板引擎的 th 标签
3. Thymeleaf 表达式



**Thymeleaf 模板文件的编写规则：**

- 任意的 Thymeleaf 属性标签 th:* 需要写在 html 标签体中( th:block 除外 )
- Thymeleaf 表达式写在 Thymeleaf 属性标签中



#### 6.4 Thymelead语法规则

- **表达式语法**

	- 变量表达式： `${...}`
	- 选择变量表达式： `*{...}`
	- 信息表达式： `#{...}`
	- 链接 URL 表达式： `@{...}`
	- 分段表达式： `~{...}`

- **字面量**

	- 字符串： 'one text', 'Another one!' ...
	- 数字： `0`, `34`, `3.0`, `12.3` ...
	- 布尔值： `true`, `false`
	- Null 值： `null`
	- 字面量标记：`one`, `sometext`, `main` ...

- **文本运算**

	- 字符串拼接： `+`
	- 字面量置换: `|The name is ${name}|`

- **算术运算**

	- 二元运算符： `+`, `-`, `*`, `/`, `%`
	- 负号（一元运算符）： (unary operator): `-`

- **布尔运算**

	- 二元运算符： `and`, `or`
	- 布尔非（一元运算符）： `!`, `not`

- **比较运算**

	- 比较： `>`, `<`, `>=`, `<=` (`gt`, `lt`, `ge`, `le`)
	- 相等运算符： `==`, `!=` (`eq`, `ne`)

	比较运算符也可以使用转义字符，比如大于号，可以使用 Thymeleaf 语法 `gt` 也可以使用转义字符`>`

- **条件运算符**

	- If-then: `(if) ? (then)`
	- If-then-else: `(if) ? (then) : (else)`
	- Default: `(value) ?: (defaultvalue)`

- **特殊语法**

	- 无操作： `_`







### 7. Spring Boot 数据源自动配置

#### 7.1 关系型数据库和非关系型数据库

- **关系型数据库**

采用关系模型来组织数据的数据库，具体而言就是二维表格模型，一个关系型数据库就是由二维表格以及其之间的联系所组成的一个数据组织

优点：

1. 海量数据高效读写
2. 高扩展型和可用性

缺点：

1. 事务一致性
2. 读写实时性
3. 复杂SQL，多表关联查询

##### 当今十大主流的关系型数据库

**[Oracle](https://www.oracle.com/database/index.html)，[Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/)，[MySQL](https://www.mysql.com/)，[PostgreSQL](https://www.postgresql.org/)，[DB2](https://www.ibm.com/analytics/us/en/db2/)，
[Microsoft Access](https://products.office.com/zh-cn/access)， [SQLite](https://www.sqlite.org/)，[Teradata](https://www.teradata.com.cn/)，[MariaDB](https://mariadb.org/)(MySQL的一个分支)，[SAP](https://www.sap.com/)**



- 非关系型数据库

	**非关系型数据库以键值对存储，且结构不固定，每一个元组可以有不一样的字段，每个元组可以根据需要增加一些自己的键值对，不局限于固定的结构，可以减少一些时间和空间的开销。**

	- 面向高性能并发的key-value数据库【Redies，Cabinet，Flare】
	- 面向海量数据访问的面型文档的数据库【MongoDB，CouchDB】
	- 面向可拓展性的分布式数据库



#### 7.2 JDBC回顾

Java程序在和MySQL的连接时需要通过JDBC来实现，JDBC全称为 Java Data Base Connectivity（Java数据库连接），由接口组成，用于执行SQL语句的Java Api。

<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201225091918150.png" alt="image-20201225091918150" style="zoom:50%;" />

Java程序，使用URL方式制定不同的数据库的Driver驱动程序，然后建立特定的Connection连接，然后可以安装JDBC规范对不同类型的数据库进行数据操作。



**JDBC操作的流程：**

1. 注册驱动

	注册驱动有三种方式：

	  　　1.   Class.forName(“com.mysql.jdbc.Driver”);

	　　     推荐这样的方式，不会对详细的驱动类产生依赖

	  　　2. DriverManager.registerDriver(com.mysql.jdbc.Driver);

	 　　    会对详细的驱动类产生依赖

	  　　3. System.setProperty(“jdbc.drivers”, “driver1:driver2”);

	   　  尽管不会对详细的驱动类产生依赖；但注冊不太方便。所以非常少使用

2. 建立连接

	`　　Connection conn =DriverManager.getConnection(url, user, password);`

3. 创建允许的SQL语句

	`　　Statement st = connection.createStatement();`

	还有衍生出来的两个接口类`PreparedStatement`和`CallableStatement`，常用的有`PreparedStatement`，用于防止SQL注入。

	```java
	PreparedStatement  ps=connection.prepareStatement("update user set id=? where username=?” );
	ps.setObject(1, object);
	ps.setObject(2, object);
	```

	`?`是通配符，可以注入数据的。而且预编译的结果可以存在PreparedStatement对象中，可以提高效率。

4. 运行语句

	```java
	ResultSet  rs =st.executeQuery(sql); // 查询
	int flag = st.executeUpdate(sql);    // 增删改
	```

5. 处理运行的结果(ResultSet)

	可以遍历数据集，得到相应结果

6. 释放资源

	释放数据库连接资源





#### 7.3 SpringBoot 连接MySql步骤

1. 加入mysql依赖和jdbc依赖

	```xml
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-jdbc</artifactId>
	        </dependency>
	        <dependency>
	            <groupId>mysql</groupId>
	            <artifactId>mysql-connector-java</artifactId>
	            <scope>runtime</scope>
	        </dependency>
	```

2. 在application.properties 添加配置

	```properties
	#spring.datasource.name=newbee-mall-datasource
	spring.datasource.url=jdbc:mysql://localhost:3306/wzd?useUnicode=true&serverTimezone=GMT&characterEncoding=utf8&autoReconnect=true&useSSL=false
	spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
	spring.datasource.username=root
	spring.datasource.password=1234
	```

3. 测试链接

	```java
	// 注入数据源对象
		@Autowired
		private DataSource defaultDataSource;
	
		@Test
		public void datasourceTest() throws SQLException {
			// 获取数据库连接对象
			Connection connection = defaultDataSource.getConnection();
			System.out.print("获取连接：");
			// 判断连接对象是否为空
			System.out.println(connection != null);
			connection.close();
		}
	```



#### 7.4 自动配置源码详解

首先，我们可以得知，SpringBoot向ioc容器中注入了 `默认数据源为：class com.zaxxer.hikari.HikariDataSource`一个数据类型对象。

- **Spring Boot 如何将 DataSource 对象注册到 IOC 容器中的？**

Spring Boot 项目中关于数据源的自动配置类名称为 DataSourceAutoConfiguration，该路径下还有数据源配置类、数据源属性类、事务管理配置类等等。

源码

```java
@Configuration // 配置类
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class }) // 自动配置条件
@EnableConfigurationProperties(DataSourceProperties.class) //属性值配置
@Import({ DataSourcePoolMetadataProvidersConfiguration.class,
		DataSourceInitializationConfiguration.class })//引入两个配置类
public class DataSourceAutoConfiguration {
	...省略部分代码
	@Configuration
	@Conditional(PooledDataSourceCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import({ DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class,
			DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.Generic.class,
			DataSourceJmxConfiguration.class }) // 导入数据源配置类
	protected static class PooledDataSourceConfiguration {
	}
```



第三个注解`@EnableConfigurationProperties(DataSourceProperties.class)` 表示使用DataSourceProperties类来进行相关的**属性配置**。

```java
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
    ...省略部分代码
}
```

`@ConfigurationProperties(prefix = "spring.datasource")` 表示通过绑定配置文件中以 spring.datasource 开头的属性到配置类中。 【在application.properties主配置文件中】



在DataSourceAutoConfiguration自动配置类中定义了 PooledDataSourceConfiguration 内部类，这个内部类本身没有实现代码，而是使用@Import 注解引入了DataSourceConfiguration 类。

而这个DataSourceConfiguration类中有四个内部类，上面都有条件注解@Conditional，所以最后创建的是**Hikari 内部类**。（满足了所有的条件）

**三个条件**

1. **判断当前 classpath 下是否存在指定类 HikariDataSource.class**

在 pom 文件中我们引入了 spring-boot-starter-jdbc 依赖，默认装载了这个DataSource

2. **beanFactory 中不存在 DataSource 类型的 bean**

由于没有在配置文件中进行数据源指定，也没有进行自定义数据源的注入，因此 beanFactory 中肯定不存在 DataSource 类型的对象，该条件满足。

3. **判断当前绑定属性中 spring.datasource.type 的值**

条件成立



### 8. SpringBoot 数据库操作实例

平常开发者使用的是**ORM框架**来实现对数据库的操作的

**ORM介绍**

Object Relational Mapping模式，用于解决面向对象和关系数据库存在互相不匹配的现象的技术。通过描述对象和数据库之间映射的元数据，把程序中的对象自动持久化到关系数据库中。使用**持久化类来对应一张表**。我们操作这个对象就可以直接操作数据库的内容了。

**JdbcTemplate**

JdbcTemplate 是 Spring 对 JDBC 的封装，目的是使 JDBC 更加易于使用，更为关键的一点是，**jdbcTemplate 对象也是通过自动配置机制注册到 IOC 容器中的**，JdbcTemplate 的自动配置类是 JdbcTemplateAutoConfiguration。

源码：JdbcTemplateAutoConfiguration类

```java
@Configuration(
    proxyBeanMethods = false
)
@ConditionalOnClass({DataSource.class, JdbcTemplate.class})
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({JdbcProperties.class})
@Import({JdbcTemplateConfiguration.class, NamedParameterJdbcTemplateConfiguration.class})
public class JdbcTemplateAutoConfiguration {
    public JdbcTemplateAutoConfiguration() {
    }
```

JdbcTemplateConfiguration类

在这个类中，使用了bean注解创建jdbcTemplate对象，把这个对象注入到了IOC容器中



```java
   Controller类：

//自动配置，因此可以直接通过 @Autowired 注入进来
    @Autowired
    JdbcTemplate jdbcTemplate;

    // 查询所有记录
    @GetMapping("/queryAll")
    public List<Map<String, Object>> queryAll() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from jdbc_test");
        return list;
    }
```



### 9. MyBatis

#### 9.1 Mybatis介绍

MyBatis 是支持定制化 SQL、存储过程以及高级映射的优秀的持久层框架，我们只需要关注SQL本身，而不用去关注复杂的JBDBC实现的操作。

<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201225110112682.png" alt="image-20201225110112682" style="zoom:50%;" />



MyBatis 的优点如下：

- 封装了 JDBC 大部分操作，减少开发人员工作量；
- 相比一些自动化的 ORM 框架，“半自动化”使得开发人员可以自由的编写 SQL 语句，灵活度更高；
- Java 代码与 SQL 语句分离，降低维护难度；
- 自动映射结果集，减少重复的编码工作；
- 开源社区十分活跃，文档齐全，学习成本不高。



#### 9.2 Mybatis-springboot-starter介绍

MyBatis 整合 Spring Boot 项目时的场景启动器（starter）

作用：

- 构建独立的 MyBatis 应用程序
- 零模板
- 更少的 XML 配置代码甚至无 XML 配置

##### mybatis-spring-boot-starter 自动配置类

跟入 MybatisAutoConfiguration 类，其源码及注释如下：

~~~java
@Configuration  // 配置类
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class}) // 判断当前 classpath 下是否存在指定类，若存在则将当前的配置装载入 Spring 容器
@ConditionalOnBean({DataSource.class}) // 在当前 IOC 容器中存在 DataSource 数据源对象时
@EnableConfigurationProperties({MybatisProperties.class}) // 配置类文件为 MybatisProperties
@AutoConfigureAfter({DataSourceAutoConfiguration.class}) // 自动配置时机
public class MybatisAutoConfiguration {
  ```````````````
  ```````````````
    @Bean // 注册 SqlSessionFactory 到 IOC 容器中
    @ConditionalOnMissingBean // 在当前 IOC 容器中不存在 SqlSessionFactory 类型的 bean 时注册
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
      	// ... 省略部分代码
        return factory.getObject();
    }

    @Bean // 注册 SqlSessionTemplate 到 IOC 容器中
    @ConditionalOnMissingBean // 在当前 IOC 容器中不存在 SqlSessionTemplate 类型的 bean 时注册
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        return executorType != null ? new SqlSessionTemplate(sqlSessionFactory, executorType) : new SqlSessionTemplate(sqlSessionFactory);
    }
    // ... 省略部分代码
~~~



Mybatis 自动配置类，是在DataSourceAutoConfiguration自动配置后，且 DataSourceAutoConfiguration 自动配置成功并向 IOC 容器中注册 DataSource 对象，条件成立后，Mybatis 自动配置类就会开始进行自动配置操作，最终，Mybatis 自动配置类会向 Spring IOC 容器中注册 SqlSessionFactory 对象和 SqlSessionTemplate 对象



#### 9.3 Spring Boot 整合 MyBatis 过程

1. 添加依赖

2. 设置application.properties配置

	```
	mybatis.mapper-locations=classpath:mapper/*Dao.xml
	```

	也可以直接接口和DaoMapper放在一起

3. 启动类添加Mapper扫描器

	**@MapperScan**("指定的mapper包的名字") *//添加 @Mapper 注解* 





### 10 项目介绍

```dictionary
newbee-mall
    ├── src/main/java
         └── ltd.newbee.mall
         		├── common // 存放相关的常量配置及枚举类
         		├── config // 存放 web 配置类
         		├── controller // 存放控制类，包括商城端和后台管理系统中的 controller 类
         		      	├── admin // 存放后台管理系统中的 controller 类
                        ├── common // 存放通用的 controller 类
                        └── mall // 存放商城端的 controller 类
         		├── dao // 存放数据层接口
         		├── entity // 存放实体类
         		├── interceptor // 存放拦截器
         		├── service // 存放业务层方法
         		├── util // 存放工具类
         		└── NewBeeMallApplication // Spring Boot 项目主类
    ├── src/main/resources
         ├── mapper // 存放 MyBatis 的通用 Mapper文件
         ├── static // 默认的静态资源文件目录
               	├── admin // 存放后台管理系统端的静态资源文件目录
         						└── mall // 存放商城端的静态资源文件目录
         ├── templates
                ├── admin // 存放后台管理系统端页面的模板引擎目录
         						└── mall // 存放商城端页面的模板引擎目录
         ├── application.properties // 项目配置文件
         ├── newbee_mall_schema.sql // 项目所需的 SQL 文件 
         └── upload.zip // 商品图片
    └── pom.xml // Maven 配置文件
```



### 11 Spring Boot 文件上传流程

文件上传是比较常用的模块。

#### 11.1 Spring MVC 文件上传流程

**源码调用链**

SpringMVC上传功能与**MultipartResolver**的设置有关，这是一个Spring MVC用于实现文件上传的工具类，这个工具类只在上传文件时间起作用。

1. SpringMVC的中央调度器DispatcherServlet会在处理请求是调用MultipartResolver 中的方法**判断**此请求是不是文件上传请求

2. 如果是，DispatcherServlet 将调用 MultipartResolver 的 resolveMultipart(request) 方法对该请求对象进行装饰并返回一个新的 MultipartHttpServletRequest 供后继处理流程使用。

3. 此时的请求对象会由 HttpServletRequest 类型转换成 MultipartHttpServletRequest 类型（或者 MultipartHttpServletRequest 的实现类），这个类中会包含所上传的文件对象，可供后续流程直接使用，



根据这一过程，十三绘制了如下代码调用时序图：



![image-20201227202334322](/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20201227202334322.png)



如上图所示，当收到请求时，DispatcherServlet 的 checkMultipart() 方法会调用 MultipartResolver 的 **isMultipart()** 方法判断请求中是否包含文件。

其中 `this.multipartResolver.isMultipart(request)` 则是调用 StandardServletMultipartResolver 的 `isMultipart()` 方法，源码如下：

```java
	public boolean isMultipart(HttpServletRequest request) {
		return StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/");
	}
```

对请求头中的 contentType 对象进行判断，请求的 contentType 不为空且 contentType 的值以 `multipart/` 开头，此时会返回 true，否则将不会将这次请求标示为文件上传请求



如果请求数据中包含文件，则调用 MultipartResolver 的 resolveMultipart() 方法对请求的数据进行解析，然后将文件数据解析成 MultipartFile 并封装在 MultipartHttpServletRequest（继承了 HttpServletRequest）对象中，最后传递给 Controller 控制器。





#### 11.2 Spring Boot 文件上传功能实现



前端页面：

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Spring Boot 文件上传测试</title>
</head>
<body>
<form action="/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="file" />
    <input type="submit" value="文件上传" />
</form>
</body>
</html>
```



Controller

```
@Controller
public class UploadController {
    // 文件保存路径为 D 盘下的 upload 文件夹，可以按照自己的习惯来修改
    private final static String FILE_UPLOAD_PATH = "D:\\upload\\";
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败";
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();
        try {
            // 保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_PATH + newFileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传成功";
    }
}
```







#### 11.3 Spring Boot 文件上传路径回显



我们上传文件是要实际应用到业务中的，比如图片上传，上传后我们需要知道它的路径，最好能够在页面中直接看到它的回显效果

通常的做法是**使用自定义静态资源映射目录，以此来实现文件上传整个流程的闭环**

在文件上传到 upload 目录后，增加一个自定义静态资源映射，使得 upload 下的静态资源可以通过该映射地址被访问到，新建 config 包，并在包中新增 SpringBootWebMvcConfigurer 类

```java
package ltd.newbee.mall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class NeeBeeMallWebMvcConfigurer implements WebMvcConfigurer {

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:\\upload\\");
      
      
        //        addResourceHandler是指你想在url请求的路径
        // addResourceLocations是图片存放的真实路
    }
}
```







### 12 富文本编辑器

富文本编辑器的概念如下：

> 富文本编辑器，是一种可内嵌于浏览器，所见即所得的文本编辑器。 富文本编辑器不同于文本编辑器(如 textarea 标签、input 标签)，也可以叫做图文编辑器，在富文本编辑器里可以编辑非常丰富的内容，如文字、图片、表情、代码……应有尽有，满足你的大部分需求。 像一些新闻排版，基本是以图文排版为主，而淘宝京东这些电商的商品详情页，基本都是多张已经排版好的设计图拼接而来的，富文本编辑器可以很完美的支持者两种需求。



#### 为什么要使用富文本编辑器

以下是使用富文本编辑器的原因，也是富文本编辑器的优点：

- 需求变更导致，业务方提出的编辑需求越来越复杂
- 编辑的内容变得越来越复杂、越来越丰富
- 比起编辑 html，富文本编辑器更灵活
- 富文本编辑器功能丰富，满足大部分需求





步骤：

- 引入KindEdit相关文件

```
<!-- kindeditor -->
<script th:src="@{/admin/plugins/kindeditor-4.1.10/kindeditor-all.js}"></script>
<script th:src="@{/admin/plugins/kindeditor-4.1.10/lang/zh_CN.js}"></script>
```

- 创建编辑框DOM 

在页面中创建 id 为 editor 的 `<textarea>` 元素，定义它主要是为了后续编辑器的初始化工作，这里我将它的 id 为 editor，该值是可以自行修改的，不一定非要是 editor。

```
<div class="form-group">
    <label class="control-label">内容:</label>
    //编辑框宽高设置
    <textarea class="form-control" id="editor" style="width:700px;height:450px;"></textarea>
</div>
```

- 初始化 KindEditor 对象

初始化这个动作一般是在页面加载时，也就是 $(function () {}) 方法体，添加如下代码：

```
	var editor;

    //详情编辑器
    editor = KindEditor.create('textarea[id="editor"]', {
        items: ['source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
            'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
            'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
            'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
            'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
            'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'multiimage',
            'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
            'anchor', 'link', 'unlink'],
        uploadJson: '/admin/upload/file',
        filePostName: 'file'
    });
```



参数释义：

- **KindEditor.create('textarea[id="editor"]'**

	初始化 id wa 为"editor"的 textarea 标签为 KindEditor

	就是要让指尖

- **items** 配置编辑器的工具栏，前文中我们演示富文本编辑器功能时，就是点击这里的一个个工具项

- **uploadJson**

	指定图片上传的服务器端 url，之前我们写过统一的文件上传处理器，直接用那个就可以

- **filePostName**

	指定图片上传 form 名称

#### 获取文档内容

在整理好富文本内容并写到编辑器后，我们还需要将 KindEditor 编辑器中输入的文字内容取出来，并传给后端以进行逻辑处理，提供了 html() 方法来获取其中的内容，比如我们获取输入的商品详情内容，就可以用如下代码来获取：

```
var goodsDetailContent = editor.html();
```

之后就能够将 goodsDetailContent 字段进行封装，并与后端接口进行交互了







### 13. 多层级的数据联动效果

##### 常见场景

二级联动、三级联动或者更多层级的数据联动，这是互联网中比较常见的交互方式，这种方式可以提升用户的使用体验，本来需要用户一个一个输入的文字内容，可以直接借助这种选择框联动的方式进行选择，为用户提供了很大的便利；另外一方面，这种基于联动的选择框，**可以限制用户随意输入一些内容，规范用户提交的数据。**

通过网站提供的这个四级联动进行区域的选择，避免了我们手动输入的麻烦，同时，也防止用户随意的输入不规范的地址数据。



使用多级联动来进行商品信息的处理

有两个表N    M

如果N表中的数据发生了改变，那么M表中的数据也会随之发生改变

##### 实现方式

针对于数据种类的不同，代码实现的方式也会有不同，比如上述所举例的省市区联动，由于这些行政区数据都是固定且改动可能较小的数据，就可以直接在前端把所有数据写死，并通过下拉选择框的 change 事件来实现联动功能，省市区这些数据是已经确定的内容，可以直接初始化数据并进行联动。

新蜂商城的商品类目数据发生更改的频率较高，增加、编辑、删除等操作都会造成列表数据的变化，所以我们在新蜂商城的三级联动功能实现时会考虑做成动态的三级联动，即实时的通过数据库读取数据，并通过下拉选择框的 change 事件来实现联动功能。



多级联动需要的参数

一级分类的下拉框每一次改变，

都会使得二级分类的下拉框的数据发生改变，

然后三级分类中显示的是二级分类第一个条目中所有的三级子分类

**初始化**

一级目录： 所有的一级目录

二级目录：一级分类下拉选择框中的第一条一级分类数据的所有二级分类

三级目录：同上





### 14. 搜索功能的实现

<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20210110215601690.png" alt="image-20210110215601690" style="zoom:50%;" />



**分成3个模块**

一个是顶部的导航栏 （公共区域）

一个是商品列表区域

一个是底部的页脚区域（公共区域）

**中心区域包括**

1. **搜索页副标题区域**：这个区域处于整个搜索页面功能区的顶部，用于显示搜索信息，因为根据分类搜索和根据关键字搜索这两个功能也都是用到该页面作为展示页面，在显示搜索信息的同时也对搜索功能进行区分，如果是根据关键字搜索，这个区域只会显示用户搜索的关键字，如果是根据分类搜索，这里还会显示分类信息的筛选。

	有分类搜索和关键子搜索两个分别作用

2. **商品列表区域**：用于展示商品列表，显示商品的概览信息，这是该页面最主要的部分，包括商品图片、商品价格、商品简介等内容。展示这些信息在页面上供用户查看和筛选，点击单个商品后会跳转到对应的商品详情页面。

3. **分页导航区域**：放置分页按钮，用于分页跳转功能，这是分页功能必不可少的一部分。



##### 数据格式的定义：

首先，商品分类一定是一个list对象

其次，由于是分页信息，使用返回的一定是PageReuslt信息

我们分类对象字段需要包含：

<img src="/Users/wangzhengdong/Library/Application Support/typora-user-images/image-20210110220422126.png" alt="image-20210110220422126" style="zoom:50%;" />









### 15. SpringBoot 事务

下面这个配置文件是普通的 SSM 框架整合时的事务配置，相信大家都比较熟悉这段配置代码：

```xml
	<!-- 事务管理 -->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 配置事务通知属性 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!-- 定义事务传播属性 -->
        <tx:attributes>
            <tx:method name="insert*" propagation="REQUIRED"/>
            <tx:method name="import*" propagation="REQUIRED"/>
            <tx:method name="update*" propagation="REQUIRED"/>
            <tx:method name="upd*" propagation="REQUIRED"/>
            <tx:method name="add*" propagation="REQUIRED"/>
            <tx:method name="set*" propagation="REQUIRED"/>
            <tx:method name="remove*" propagation="REQUIRED"/>
            <tx:method name="delete*" propagation="REQUIRED"/>
            <tx:method name="get*" propagation="REQUIRED" read-only="true"/>
            <tx:method name="*" propagation="REQUIRED" read-only="true"/>
        </tx:attributes>
    </tx:advice>

    <!-- 配置事务切面 -->
    <aop:config>
        <aop:pointcut id="serviceOperation"
                      expression="(execution(* com.ssm.demo.service.*.*(..)))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="serviceOperation"/>
    </aop:config>
```



**通过这段代码我们也能够看出声明式事务的配置过程：**

1. **配置事务管理器**
2. **配置事务通知属性**
3. **配置事务切面**

#### Spring Boot 项目中的事务控制

在SpringBoot中，建议采用 `@Transactional` 注解进行事务的控制，只需要在需要进行事务管理的方法或者类上添加 `@Transactional` 注解即可，接下来我们来通过代码讲解。

通过购物车中的数据生成订单数据时我们需要做以下几步操作：首先是执行 delete 语句删除购物项数据，之后是生成订单数据并执行 insert 语句向数据库中新增一条订单记录，当然可能还有修改商品的库存数据或者其他业务处理，但是我们可以确定的是，该业务方法中不止执行了一条 SQL 语句，也不止操作了一张表，如果没有事务管理机制的话可能就会出现如下的几种混乱情况：

- 订单没有生成，但是我的购物车中已经没有了购物项数据
- 订单生成了，我的购物车中依然有数据
- 库存数据没有修改
- 其他业务数据的错误

**事务的作用主要体现在两个方面：**

> 1. 为数据库操作提供了一个从失败中恢复到正常状态的方法，同时提供了数据库即使在异常状态下仍能保持一致性的方法
> 2. 当多个应用程序在并发访问数据库时，可以在这些应用程序之间提供一个隔离方法，以防止彼此的操作互相干扰



#### Sprint Boot 事务管理器自动配置

------
