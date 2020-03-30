### Spring容器功能扩展

---------------------
ApplicationContext和BeanFactory都用于加载bean，前者提供了更多的扩展功能。
以AnnotationConfigApplicationContext为中心
```
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
	this();	//初始化扫描bean的工具类，以及注册后置处理器
	register(componentClasses);	//注册配置类
	refresh();	//处理bean
}
```
需要传入配置类，解析及功能实现都在refresh()中实现。

---------------------

注册配置类
---------------------
支持多个配置类同时传入
```
public void register(Class<?>... componentClasses) {
	for (Class<?> componentClass : componentClasses) {
		registerBean(componentClass);
	}
}
```

扩展功能
---------------------
```
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// 准备刷新的上下文环境
			prepareRefresh();
			// 初始化BeanFactory
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
			// 对BeanFactory进行各种功能填充
			prepareBeanFactory(beanFactory);

			try {
				// 子类覆盖方法做额外的处理
				postProcessBeanFactory(beanFactory);
				// 激活各种BeanFactory处理器
				invokeBeanFactoryPostProcessors(beanFactory);
				// 注册拦截bean创建的Bean处理器，这里只是注册，真正调用是在getBean的时候
				registerBeanPostProcessors(beanFactory);
				// 为上下文初始化Message源，即不同语言的消息体，国际化处理
				initMessageSource();
				// 初始化应用消息广播器，并放入applicationEventMulticaster bean中
				initApplicationEventMulticaster();
				// 留给子类初始化其他的bean
				onRefresh();
				// 在所有注册的bean中查找Listener bean，注册到消息广播器中
				registerListeners();
				// 初始化非懒加载的bean
				finishBeanFactoryInitialization(beanFactory);
				// 完成刷新过程，通知生命周期处理器lifecycleProcessor刷新过程，同时发出ContextRefreshEvent通知
				finishRefresh();
			}

			catch (BeansException ex) {
				destroyBeans();
				cancelRefresh(ex);
				throw ex;
			}
			finally {
				resetCommonCaches();
			}
		}
	}
```
---------------------

环境准备
---------------------

```
protected void prepareRefresh() {
	// Switch to active.
	this.startupDate = System.currentTimeMillis();
	this.closed.set(false);
	this.active.set(true);

	// 留给子类覆盖
	initPropertySources();

	// 验证需要的属性文件是否都已放入环境中
	getEnvironment().validateRequiredProperties();

	// 存储应用的监听器
	if (this.earlyApplicationListeners == null) {
		this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
	}
	else {
		this.applicationListeners.clear();
		this.applicationListeners.addAll(this.earlyApplicationListeners);
	}

	// 收集早期的应用事件，一旦广播其可用就开始发布事件
	this.earlyApplicationEvents = new LinkedHashSet<>();
}
```
1. initPropertySources为空实现，用户可自定义重写该方法，进行个性化的属性处理及设置。

2. validateRequiredProperties对属性的验证。如要使用定义的var变量，但系统环境变量中却没有，运行则会抛出异常并停止
```java
public class CustomAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {
	public CustomAnnotationConfigApplicationContext(Class<?>... componentClasses){
		super(componentClasses);
	}

	@Override
	protected void initPropertySources() {
		getEnvironment().setRequiredProperties("var");
	}

	public static void main(String[] args) {
		CustomAnnotationConfigApplicationContext context = new CustomAnnotationConfigApplicationContext(BeanConfig.class);

	}
}
```
重写initPropertySources，在执行getEnvironment().validateRequiredProperties()时检测没有var变量，则抛出异常。

---------------------

加载BeanFactory
---------------------
```
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
	//初始化BeanFactory
	refreshBeanFactory();
	//返回当前实体的BeanFactory属性
	return getBeanFactory();
}
```
refreshBeanFactory的实现为两种：
```
GenericApplicationContext{
	protected final void refreshBeanFactory() throws IllegalStateException {
		if (!this.refreshed.compareAndSet(false, true)) {
			throw new IllegalStateException(
					"GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
		}
		this.beanFactory.setSerializationId(getId());
	}
}
AbstractRefreshableApplicationContext{
	protected final void refreshBeanFactory() throws BeansException {
		if (hasBeanFactory()) {
			destroyBeans();
			closeBeanFactory();
		}
		try {
			//创建DefaultListableBeanFactory
			DefaultListableBeanFactory beanFactory = createBeanFactory();
			// 序列化id
			beanFactory.setSerializationId(getId());
			// 定制BeanFactory，设置相关属性，包括是否允许覆盖同名称的不同定义对象以及循环依赖
			customizeBeanFactory(beanFactory);
			// 解析bean
			loadBeanDefinitions(beanFactory);
			synchronized (this.beanFactoryMonitor) {
				this.beanFactory = beanFactory;
			}
		}
		catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}
}
```
---------------------

功能扩展
---------------------
```
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	//设置beanFactory的classloader为当前context的classloader
	beanFactory.setBeanClassLoader(getClassLoader());
	//设置表达式语言处理器，默认可以使用#{bean.xxx}的形式调用相关属性
	beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
	//添加默认的propertyEditor，主要是对bean属性等设置管理的一个工具
	beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

	//添加BeanPostProcessor
	beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

	//自动装配时忽略给定的依赖接口
	//spring特性：A有属性B，spring在获取A的Bean时如果B还没初始化，则spring会自动初始化B
	//在某些情况下，B不被初始化，其中一种情况B实现了指定的Aware接口
	beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
	beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
	beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
	beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
	beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

	//添加几个自动装配的特殊规则
	beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
	beanFactory.registerResolvableDependency(ResourceLoader.class, this);
	beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
	beanFactory.registerResolvableDependency(ApplicationContext.class, this);

	beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

	// 添加对AspectJ的支持
	if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
		beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
		beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
	}

	// 添加默认的系统环境bean
	if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
		beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
	}
	if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
		beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
	}
	if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
		beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
	}
}
```
1. 增加SpEL语言的支持

2. 添加属性注册编辑器
spring 注入时可以把普通属性注入进来，但Date类型无法被识别
```java
@Component
public class DateManager {
	@Value("${dateTime}")
	private Date date;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
```
此时运行程序会报错，date是Date类型，而resource中的dateTime为String类型。
- 使用自定义属性编辑器

```java
public class DatePropertyEditor extends PropertyEditorSupport {
	private String format = "yyyy-MM-dd";
	public void setFormat(String format) {
		this.format = format;
	}
	public void setAsText(String arg)  {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date parse = null;
		try {
			parse = simpleDateFormat.parse(arg);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setValue(parse);
	}
}
```
- 将自定义属性编辑器注册到spring中
```
@Bean
CustomEditorConfigurer customEditorConfigurer(){
	CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
	Map<Class<?>, Class<? extends PropertyEditor>> customEditors=new HashMap<>();
	customEditors.put(java.util.Date.class,DatePropertyEditor.class);
	customEditorConfigurer.setCustomEditors(customEditors);
	return customEditorConfigurer;
}
```
CustomEditorConfigurer加入自定义属性编辑器，当spring注入bean的属性时遇到Date类型会自动调用DatePropertyEditor解析器解析

beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()))

在ResourceEditorRegistrar#registerCustomEditors方法中
```
public void registerCustomEditors(PropertyEditorRegistry registry) {
	ResourceEditor baseEditor = new ResourceEditor(this.resourceLoader, this.propertyResolver);
	doRegisterEditor(registry, Resource.class, baseEditor);
	doRegisterEditor(registry, ContextResource.class, baseEditor);
	doRegisterEditor(registry, InputStream.class, new InputStreamEditor(baseEditor));
	doRegisterEditor(registry, InputSource.class, new InputSourceEditor(baseEditor));
	doRegisterEditor(registry, File.class, new FileEditor(baseEditor));
	doRegisterEditor(registry, Path.class, new PathEditor(baseEditor));
	doRegisterEditor(registry, Reader.class, new ReaderEditor(baseEditor));
	doRegisterEditor(registry, URL.class, new URLEditor(baseEditor));

	ClassLoader classLoader = this.resourceLoader.getClassLoader();
	doRegisterEditor(registry, URI.class, new URIEditor(classLoader));
	doRegisterEditor(registry, Class.class, new ClassEditor(classLoader));
	doRegisterEditor(registry, Class[].class, new ClassArrayEditor(classLoader));

	if (this.resourceLoader instanceof ResourcePatternResolver) {
		doRegisterEditor(registry, Resource[].class,
				new ResourceArrayPropertyEditor((ResourcePatternResolver) this.resourceLoader, this.propertyResolver));
	}
}

private void doRegisterEditor(PropertyEditorRegistry registry, Class<?> requiredType, PropertyEditor editor) {
	if (registry instanceof PropertyEditorRegistrySupport) {
		((PropertyEditorRegistrySupport) registry).overrideDefaultEditor(requiredType, editor);
	}
	else {
		registry.registerCustomEditor(requiredType, editor);
	}
}
```
在doRegisterEditor中有自定义属性中使用的关键代码registry.registerCustomEditor(requiredType, editor)。
ResourceEditorRegistrar核心功能注册一系列属性编辑器。registerCustomEditors调用链如下：

ResourceEditorRegistrar#registerCustomEditors -> AbstractBeanFactory#registerCustomEditors -> AbstractBeanFactory#initBeanWrapper

initBeanWrapper在bean初始化调用，将BeanDefinition转换为BeanWrapper后对属性的填充，填充环节使用编辑器进行属性解析。
BeanWrapperImpl实现BeanWrapper接口还继承PropertyEditorRegistrySupport，且有一个方法
```
	private void createDefaultEditors() {
		this.defaultEditors = new HashMap<>(64);
		this.defaultEditors.put(Charset.class, new CharsetEditor());
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(Class[].class, new ClassArrayEditor());
		this.defaultEditors.put(Currency.class, new CurrencyEditor());
		this.defaultEditors.put(File.class, new FileEditor());
		this.defaultEditors.put(InputStream.class, new InputStreamEditor());
		this.defaultEditors.put(InputSource.class, new InputSourceEditor());
		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Path.class, new PathEditor());
		this.defaultEditors.put(Pattern.class, new PatternEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
		this.defaultEditors.put(Reader.class, new ReaderEditor());
		this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
		this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
		this.defaultEditors.put(URI.class, new URIEditor());
		this.defaultEditors.put(URL.class, new URLEditor());
		this.defaultEditors.put(UUID.class, new UUIDEditor());
		this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());

		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
		this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
		this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));

		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());

		this.defaultEditors.put(char.class, new CharacterEditor(false));
		this.defaultEditors.put(Character.class, new CharacterEditor(true));

		this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
		this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

		this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
		this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
		this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
		this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
		this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

		if (this.configValueEditorsActive) {
			StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
			this.defaultEditors.put(String[].class, sae);
			this.defaultEditors.put(short[].class, sae);
			this.defaultEditors.put(int[].class, sae);
			this.defaultEditors.put(long[].class, sae);
		}
	}
```

3. 添加ApplicationContextAwareProcessor处理器
beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this))注册BeanPostProcessor，
在spring激活bean的init-method前后调用BeanPostProcessor
```
default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	return bean;
}
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
			bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
			bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)){
		return bean;
	}
	AccessControlContext acc = null;

	if (System.getSecurityManager() != null) {
		acc = this.applicationContext.getBeanFactory().getAccessControlContext();
	}

	if (acc != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			invokeAwareInterfaces(bean);
			return null;
		}, acc);
	}
	else {
		invokeAwareInterfaces(bean);
	}
	return bean;
}
```
postProcessBeforeInitialization调用invokeAwareInterfaces，实现Aware接口的bean在初始化后可以获取对应的资源。

---------------------
BeanFactory后处理
---------------------
1. 激活BeanFactoryPostProcessor
可以对bean的定义（元数据）进行处理。spring容器允许在实例化任何其他bean之前读取配置元数据，并修改。改变bean实例则使用BeanPostProcessor。
- BeanFactoryPostProcessor典型应用PropertyPlaceholderConfigurer，在实例化bean之前获得配置信息，解析bean的变量引用

- 使用自定义BeanFactoryPostProcessor

- 激活BeanFactoryPostProcessor

2. 注册BeanPostProcessor
此时只是注册，真正调用是在bean的实例化阶段进行。

3. 初始化消息资源

4. 初始化ApplicationEventMulticaster

5. 注册监听器

---------------------

初始化非延迟加载单例
---------------------

```
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(
					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		if (!beanFactory.hasEmbeddedValueResolver()) {
			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
		}

		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		beanFactory.setTempClassLoader(null);

		//冻结所有bean定义，说明注册的bean定义将不被修改或任何处理
		beanFactory.freezeConfiguration();

		// 初始化剩下非惰性的单例Bean
		beanFactory.preInstantiateSingletons();
	}
```

---------------------
finishRefresh
---------------------

```
	protected void finishRefresh() {
		// Clear context-level resource caches (such as ASM metadata from scanning).
		clearResourceCaches();

		// Initialize lifecycle processor for this context.
		initLifecycleProcessor();

		// Propagate refresh to lifecycle processor first.
		getLifecycleProcessor().onRefresh();

		// Publish the final event.
		publishEvent(new ContextRefreshedEvent(this));

		// Participate in LiveBeansView MBean, if active.
		LiveBeansView.registerApplicationContext(this);
	}
```
- initLifecycleProcessor：当ApplicationContext启动或停止时，会通过LifecycleProcessor与所有声明的bean的周期做状态更新，
在LifycycleProcessor使用前首先需要初始化
- onrefresh：启动事项LifeCycle接口的bean
- publishEvent：当ApplicationContext初始化时，发出ContextRefreshedEvent事件。
-------------------