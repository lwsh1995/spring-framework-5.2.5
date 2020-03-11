## spring设计模式

单例模式
------
单例模式的构造函数必须为私有，不能让外部访问
- 饿汉模式：声明为final的静态变量，在jvm启动时，就初始化静态变量
```
public class HungrySingleton {
	private final static HungrySingleton instance=new HungrySingleton();
	private HungrySingleton(){}
	public static HungrySingleton getInstance() {
		return instance;
	}
}
```
- 懒汉模式：在类加载不初始化变量，而是在第一次使用进行初始化，有效降低对资源的使用
```
public class LazySingleton {
	private static LazySingleton lazySingleton;
	private LazySingleton(){}
	public static LazySingleton getLazySingleton() {
		if (lazySingleton==null){
			synchronized (LazySingleton.class){
				if (lazySingleton==null){
					lazySingleton=new LazySingleton();
				}
			}
		}
		return lazySingleton;
	}
}
```
- spring中的单例模式使用
spring获取单例调用AbstractApplicationContext#getBean()方法，实际调用为BeanFactory实现类的getBean()方法
```
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    	return getBeanFactory().getBean(name, requiredType);
    }

    public Object getBean(String name, Object... args) throws BeansException {
    	return getBeanFactory().getBean(name, args);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
    	return getBeanFactory().getBean(requiredType);
    }

    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
    	return getBeanFactory().getBean(requiredType, args);
    }
```
doGetBean()由AbstractBeanFactory实现

```
protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
			@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

		final String beanName = transformedBeanName(name);
		Object bean;

        //返回bean名称，必要时去掉工厂引用前缀，并将别名解析为规范名称。
		//尝试从缓存中获取单列
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null && args == null) {
			if (logger.isTraceEnabled()) {
				if (isSingletonCurrentlyInCreation(beanName)) {
					logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
							"' that is not fully initialized yet - a consequence of a circular reference");
				}
				else {
					logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
				}
			}
			// 尝试从缓存中获取单列
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}
		// 首次加载注册的Bean
		else {
		    创建bean，并解决bean之间的依赖
		}
		return (T) bean;
	}
```
getSingleton(String beanName, boolean allowEarlyReference)查询单例缓存，allowEarlyReference早期引用是否允许创建，默认为true
```
	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
		// 查早单例bean是否存在
		Object singletonObject = this.singletonObjects.get(beanName);
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
			synchronized (this.singletonObjects) {
				// 查询缓存是否存在bean实例
				singletonObject = this.earlySingletonObjects.get(beanName);
				// bean为空并且允许懒加载即早期的空对象null的引用
				if (singletonObject == null && allowEarlyReference) {
					ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
					if (singletonFactory != null) {
						singletonObject = singletonFactory.getObject();
						this.earlySingletonObjects.put(beanName, singletonObject);
						this.singletonFactories.remove(beanName);
					}
				}
			}
		}
		return singletonObject;
	}
```