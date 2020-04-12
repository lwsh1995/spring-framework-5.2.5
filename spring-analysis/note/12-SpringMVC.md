自动装配web程序
实现AbstractAnnotationConfigDispatcherServletInitializer，容器会自动装载该类，并装载DispathcerServlet

声明注解@EnableWebMvc
会将注解上导入的类DelegatingWebMvcConfiguration托管给容器
类DelegatingWebMvcConfiguration上声明了注解@Configuration表示其是一个完全配置类，拥有配置Bean的能力。
它的父类WebMvcConfigurationSupport中使用@Bean标注了方法requestMappingHandlerMapping，表示想要将该方法的返回值托管给容器，最终将类RequestMappingHandlerMapping托管给容器。
在容器初始化类RequestMappingHandlerMapping时，由于它的基类实现了接口InitializingBean，所以它的初始化方法afterPropertiesSet会被调用。
最终在基类AbstractHandlerMethodMapping的afterPropertiesSet方法中调用了initHandlerMethods方法。完成了对所有包含@Controller注解或者@RequestMapping注解的类的处理，将这些类中的带有@RequestMapping注解的方法与其类合并为完全的RequestMappingInfo，保存在AbstractHandlerMethodMapping的属性中。
