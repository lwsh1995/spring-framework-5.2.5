核心模块
------
核心容器由 spring-core ， spring-beans ， spring-context ， spring-context- support 和 spring-expression模块 组成。 
spring-core 和 spring-beans 模块提供了框架的基本部分，包括 IoC 和依赖注入功能。 BeanFactory 是一个复杂的工厂模式的实现。 
spring-context模块建立在 Core和Beans模块 提供的实体基础之上：它是一种以类 似于 JNDI注册表 的框架样式方式访问对象的手段。 
Context模块 从 Beans模块 继承其特性，并增加了对国际化，事件传播，资源加载以及通过例如 Servlet容器 的透明 创建上下文的支持。
Context模块 还支持 Java EE功能 ，如 EJB JMX 。 ApplicationContext 接口是 Context模块 的焦点。 spring-context-support 
提供支持将第三方库集成到 Spring 应用程序上下文中，特别是用于缓存（ EhCache ， JCache ）和 定时任务等（ CommonJ ， Quartz ）。
spring-expression 提供了一个强大的表达式语言，用于在运行时查询和操作对象图。 它是JSP 2.1规范中规定的统一表达式语言的扩展。 
该语言支持设置和获取属性值，属性赋值，方法调用，访问数组的内容，集合和索引器，逻辑和算术运算符，命名变量，以及通过 Spring IoC 容器中的名称检索对象。
spring-aop 模块提供了一个符合 AOP Alliance-compliant 的面向方面的编程实现，允许定义例如方法拦截器和切入点来干净地解耦实现应该分离的功能的代码。 
使用源代码级元数据功能.单独的 spring-aspects 模块提供与 AspectJ 的集成。 s
pring-instrument-tomcat 模块提供类仪器支持和类加载器实现以在某些应用服务器中使用。 spring-instrument-tomcat 模块包含 Spring 的 Tomcat 的工具代理。
数据访问/集成层由 JDBC ， ORM ， OXM ， JMS 和 Transaction 模块组成。 
spring-jdbc 模块提供了一个 JDBC 抽象层，消除了对繁琐的 JDBC 编码和解析数据库供应商 特定的错误代码的需要。 
spring-tx 模块支持实现特殊接口的类以及所有 POJO （普通 Java 对象）的编程和声明事务 管理。 
spring-orm 模块为流行的对象关系映射 API 提供集成层，包括 JPA 和 Hibernate 。使用spring-orm 模块，可以使用这些 O/R映射框架结合 Spring 提供的所有其他功能。
spring-oxm 模块提供了一个支持对象/XML映射实现的抽象层，例如JAXB，Castor，JiBX和 XStream。
spring-jms 模块（Java消息服务）包含用于生成和使用消息的功能 。从 Spring Framework 4.1 开始，它提供了与spring-messaging 模块的集成。 
Web 层由 spring-web ， spring-webmvc 和 spring-websocket 模块组成。
spring-web 模块提供基本的面向 Web 的集成功能，例如多部分文件上传功能和使用 Servlet监听器和面向 Web 的应用程序上下文来初始化 IoC 容器。 
它还包含一个 HTTP 客户端和 Web 的相关部分的 Spring 的远程支持。
spring-webmvc 模块（也称为 Web-Servlet 模块）包含用于 Web 应用程序的 Spring 的模型视图控制器（MVC）和REST Web服务实现。 
Spring 的 MVC 框架提供了 domain model （领域 模型）代码和 Web 表单之间的清晰分离，并且集成了 Spring Framework 所有的其他功能。
spring-test 模块支持使用 JUnit 或 TestNG 对 Spring 组件进行单元测试和集成测试。提供了 SpringApplicationContexts 的一致加载和这些上下文的缓存。