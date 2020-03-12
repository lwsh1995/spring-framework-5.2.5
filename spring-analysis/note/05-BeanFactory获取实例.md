doGetBean
------------------------
从BeanFactory中获取一个对象，如果缓存中有该对象，则直接返回，否则创建该对象，以下为创建对象的主要步骤

``` java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
		throws BeanCreationException {

	// 调用构造器实例化bean
	instanceWrapper = createBeanInstance(beanName, mbd, args);
    ......
	// 填充Bean的属性，如@Autowired注入对象
	populateBean(beanName, mbd, instanceWrapper);
	// 初始化对象，一般用于工厂回调、initMethod、post processor后置处理器
	exposedObject = initializeBean(beanName, exposedObject, mbd);
}
```

-----------------------------------

createBeanInstance
-----------------------------------
创建Bean的实例，主要通过对构造方法的推断，进行对象的实例化

``` java 
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
	// FactoryBean实例化
	if (mbd.getFactoryMethodName() != null) {
		return instantiateUsingFactoryMethod(beanName, mbd, args);
	}

	// 推断构造参数
	Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
	if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
			mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
		return autowireConstructor(beanName, mbd, ctors, args);
	}

	// Preferred constructors for default construction?
	ctors = mbd.getPreferredConstructors();
	if (ctors != null) {
		return autowireConstructor(beanName, mbd, ctors, null);
	}

	// No special handling: simply use no-arg constructor.
	return instantiateBean(beanName, mbd);
}
```

-----------------------------------

populateBean
-----------------------------------
填充实例对象的注入对象以及Propertie属性，如@Autowired

``` java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
	PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);
    // 解析注入对象
	int resolvedAutowireMode = mbd.getResolvedAutowireMode();
	if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
		MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
		// Add property values based on autowire by name if applicable.
		if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
			autowireByName(beanName, mbd, bw, newPvs);
		}
		// Add property values based on autowire by type if applicable.
		if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
			autowireByType(beanName, mbd, bw, newPvs);
		}
		pvs = newPvs;
	}
    ......
    // 应用properties属性
	applyPropertyValues(beanName, mbd, bw, pvs);
}
```

-----------------------------------

initializeBean
-----------------------------------
在Bean实例化后，在Bean的初始化的前后进行处理，在initializeBean(beanName, exposedObject, mbd)方法中被调用，
该方法在doGetBean()获取一个Bean调用，如果BeanPostProcessor接口的方法返回一个新对象，则在context上下文使用新对象代替旧对象
如果返回null，则context中继续使用对象
 
- initializeBean(beanName, exposedObject, mbd)方法流程
- ->invokeAwareMethods(beanName, bean)	调用Aware接口
- ->wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName) 接口返回新对象，则代替旧对象
- ->invokeInitMethods(beanName, wrappedBean, mbd)	调用initMethod方法
- ->wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName)	接口返回新对象，则代替旧对象

``` java
protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
	invokeAwareMethods(beanName, bean);

	wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    ......
	invokeInitMethods(beanName, wrappedBean, mbd);

	wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);

	return wrappedBean;
}
```