### spring中的重要接口

Aware
------
标记Aware超类接口指示了bean有资格通过回调方法被spring容器所通知。实际的方法由子接口决定，通常无返回且只接受一个参数。
子接口包括`BeanNameAware`、`BeanClassLoaderAware`、`BeanFactoryAware`、`EnvironmentAware`、`EmbeddedValueResolverAware`、
`ResourceLoaderAware`、`ApplicationEventPublisherAware`、`MessageSourceAware`、`ApplicationContextAware`、`ServletContextAware`，
这些接口方便从上下文中获取当前的运行环境，BeanFactory应尽可能支持Bean的生命周期接口，且接口初始化方法以及标准顺序如下：

1. `BeanNameAware#setBeanName(String)`：bean factory通知实现bean的名称，编程中通常不使用，因为bean对象与名称存在脆弱依赖
2. `BeanClassLoaderAware#setBeanClassLoader(ClassLoader)`：向bean中注入加载该bean的class loader
3. `BeanFactoryAware#setBeanFactory(BeanFactory)`：将BeanFactory注入该bean中
4. `EnvironmentAware#setEnvironment(Environment)`：将Environment注入到该bean中
5. `EmbeddedValueResolverAware#setEmbeddedValueResolver(StringValueResolver)`：实现该接口，可以利用StringValueResolver来解析字符串表达式，
与@Value方式类似
6. `ResourceLoaderAware#setResourceLoader(ResourceLoader)`：注入当前bean的资源加载器************
7. `ApplicationEventPublisherAware#setApplicationEventPublisher(ApplicationEventPublisher)`：运行该对象时设置ApplicationEventPublisher
用于发布对象
8.  `MessageSourceAware#setMessageSource(MessageSource)`：MessageSourceAware接口还能用于获取任何已定义的MessageSource引用。
9.  `ApplicationContextAware#setApplicationContext(ApplicationContext)`：实现该接口可能时需要接入文件资源，或者获取资源以及发布事件，但不推荐使用，而是使用上面指定功能的Aware接口
10  `ServletContextAware#setServletContext(ServletContext)`：注入ServletContext上下文

InitializingBean
------
-   spring容器中的bean先实例化，而后设置属性，最后初始化；实例化是调用构造函数，初始化是调用初始化方法；
-   `InitializingBean#afterPropertiesSet()`：在BeanFactory设置完Bean的所有属性后调用，完成初始化工作，
xml中init-method、@PostConstruct、InitializingBean接口作用相同，执行顺序先注解、接口、后xml。

-   `DisposableBean#destroy()`：在bean销毁前调用，用于释放资源。如context.close()，xml的destroy-method、
@PreDestroy、DisposableBean接口作用相同，执行顺序先注解、接口、后xml。

-   `FactoryBean`：通过编程的方式，实例化bean的逻辑。getObject()返回的为实例化bean对象，在getBean获取factoryBean对象
需要在id前加上&，否则获取为实例化的Bean。常用于标准化组件的构建。

-   `BeanPostProcessor`；postProcessBeforInitialization和postProcessAfterInitialization，会在spring中每个bean的
初始化方法前后调用实现了该接口的类的这两个方法，添加处理逻辑。

-   `InstantiationAwareBeanPostProcessor`：会在每个bean实例化（即调用构造函数）之前、之后调用实现了该接口的类的postProcessBeforeInstantiation、
postProcessAfterInstantiation方法，postProcessProperties调用时机为postProcessAfterInstantiation执行之后并返回true, 返回的PropertyValues将作用于给定bean属性赋值.

-   `BeanFactoryPostProcessor`：可以在Bean被创建之前，获取容器中Bean的定义信息，并可以进行修改，实现类中的方法只会被执行一次，且先于BeanPostProcessor接口中的方法。

-   `ApplicationListener`：在ApplicationContext发布ApplicationEvent时，回调onApplicationEvent方法