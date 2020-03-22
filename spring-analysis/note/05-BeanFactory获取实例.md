doGetBean
---------------------------
```
protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
			@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

		//提取对应的beanName
		final String beanName = transformedBeanName(name);
		Object bean;

		/**
		 * 检查缓存中或实例工厂是否有对应的实例
		 * 首先使用这段代码的原因
		 * 因为在创建单例bean时存在依赖注入的情况，而在创建依赖时避免循环依赖，
		 * spring在创建bean的原则是不等bean创建完成就会将创建bean的ObjectFactory提早曝光
		 * 即将ObjectFactory加入缓存，一旦下个bean创建时需要依赖上个bean就直接使用ObjectFactory
		 * 直接尝试从缓存中或则singletonFactories中的ObjectFactory中获取
		 */
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null && args == null) {
			//返回对应的实例，有时存在如BeanFactory的情况并不是直接返回实例本身而是返回指定方法返回的实例
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}
		else {
			//只有但单例情况才会尝试解决循环依赖，原型模式情况下，如果存在A中有B的属性，B中有A的属性
			//当依赖注入时，就会产生当A还未创建完成的时候，因为对B的创建再次返回创建A，造成循环依赖
			if (isPrototypeCurrentlyInCreation(beanName)) {
				throw new BeanCurrentlyInCreationException(beanName);
			}

			// Check if bean definition exists in this factory.
			BeanFactory parentBeanFactory = getParentBeanFactory();
			//如果beanDefinitionMap中所有已加载的类中不包括beanName则尝试从parentBeanFactory中检测
			if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
				// Not found -> check parent.
				String nameToLookup = originalBeanName(name);
				if (parentBeanFactory instanceof AbstractBeanFactory) {
					return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
							nameToLookup, requiredType, args, typeCheckOnly);
				}
				//递归到BeanFactory中寻早
				else if (args != null) {
					// Delegation to parent with explicit args.
					return (T) parentBeanFactory.getBean(nameToLookup, args);
				}
				else if (requiredType != null) {
					// No args -> delegate to standard getBean method.
					return parentBeanFactory.getBean(nameToLookup, requiredType);
				}
				else {
					return (T) parentBeanFactory.getBean(nameToLookup);
				}
			}

			//如果不是仅仅做类型检查则是创建bean，则进行记录
			if (!typeCheckOnly) {
				markBeanAsCreated(beanName);
			}

			try {
				//GenericBeanDefinition转换为RootBeanDefinition，如果指定BeanName是子Bean的话同时会合并父类相关属性
				final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
				checkMergedBeanDefinition(mbd, beanName, args);

				// Guarantee initialization of beans that the current bean depends on.
				//若存在依赖需要递归实例化依赖的bean
				String[] dependsOn = mbd.getDependsOn();
				if (dependsOn != null) {
					for (String dep : dependsOn) {
						//缓存依赖调用
						registerDependentBean(dep, beanName);
						try {
							getBean(dep);
						}
						catch (NoSuchBeanDefinitionException ex) {
							throw new BeanCreationException(mbd.getResourceDescription(), beanName,
									"'" + beanName + "' depends on missing bean '" + dep + "'", ex);
						}
					}
				}

				//实例化依赖的bean后可以实例化自身
				if (mbd.isSingleton()) {
					sharedInstance = getSingleton(beanName, () -> {
						try {
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							destroySingleton(beanName);
							throw ex;
						}
					});
					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}

				else if (mbd.isPrototype()) {
					// It's a prototype -> create a new instance.
					Object prototypeInstance = null;
					try {
						beforePrototypeCreation(beanName);
						prototypeInstance = createBean(beanName, mbd, args);
					}
					finally {
						afterPrototypeCreation(beanName);
					}
					bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
				}

				else {
					String scopeName = mbd.getScope();
					final Scope scope = this.scopes.get(scopeName);
					if (scope == null) {
						throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
					}
					try {
						Object scopedInstance = scope.get(beanName, () -> {
							beforePrototypeCreation(beanName);
							try {
								return createBean(beanName, mbd, args);
							}
							finally {
								afterPrototypeCreation(beanName);
							}
						});
						bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
					}
					catch (IllegalStateException ex) {
						throw new BeanCreationException(beanName,
								"Scope '" + scopeName + "' is not active for the current thread; consider " +
								"defining a scoped proxy for this bean if you intend to refer to it from a singleton",
								ex);
					}
				}
			}
			catch (BeansException ex) {
				cleanupAfterBeanCreationFailure(beanName);
				throw ex;
			}
		}

		// Check if required type matches the type of the actual bean instance.
		if (requiredType != null && !requiredType.isInstance(bean)) {
			try {
				T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
				if (convertedBean == null) {
					throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
				}
				return convertedBean;
			}
			catch (TypeMismatchException ex) {
				if (logger.isTraceEnabled()) {
					logger.trace("Failed to convert bean '" + name + "' to required type '" +
							ClassUtils.getQualifiedName(requiredType) + "'", ex);
				}
				throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
			}
		}
		return (T) bean;
	}
```
spring加载bean的步骤：
1. transformedBeanName(name)转换对应beanName
传入参数可能是别名，也可能是FactoryBean，所以要进行解析
- 去除FactoryBean的修饰符，如将“&a"去除&
- 取出指定alias所表示的最终beanName，如别名A指向B的bean则返回B
2. getSingleton(beanName)尝试从缓存中加载单例
单例在容器中值只创建一次，后续直接从缓存中获取。并且在创建单例解决循环依赖时，将依赖以ObjectFactory曝光在缓存中
3. getObjectForBeanInstance(sharedInstance, name, beanName, null)bean的实例化
如果从缓存中得到了bean的原始状态，则需要对bean进行实例化。缓存中记录的只是最原始的bean状态，不一定是想要获取的bean。
如对工厂bean进行处理，得到的是工厂bean的初始状态，真正需要的是工厂bean中定义的factory-method返回的bean，
getObjectForBeanInstance就是完成该工作
4. 原型模式的依赖检查
只有在单例模式下才尝试解决循环依赖。
5. getParentBeanFactory()检测parentFactory
如果当前factory不包含bean，且parentFactory不为null，则在parentFactory中递归调用getBean
6. GenericBeanDefinition转为RootBeanDefinition
读取的bean信息存储在GenericBeanDefinition中，但所有后续bean处理都是针对RootBeanDefinition，需要进行转换，
转换的同时如果父类bean不为空，则合并父类的属性
7. 寻找依赖
在spring的加载顺序中，在初始化bean时会首先初始化这个bean所对应的依赖
8. 针对不同scope进行bean的创建
9. return (T) bean类型转换
通常对该方法的调用参数requiredType为空，但可能存在返回的bean是个String，但requiredType却传入Integer类型，
这时该步骤就会将bean转换为指定类型。可以自定义扩展转换器。
------------------------

FactoryBean
------------------------
一般情况spring通过反射实例化bean。在某些情况实例化bean过程复杂，传统方式需要提供大量配置信息且灵活性受限，
采用编码可能会提供简单的方案。FactoryBean接口实现定制实例化bean的逻辑。

```java
public interface FactoryBean<T> {
	String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";

	T getObject() throws Exception;
	
	Class<?> getObjectType();

	default boolean isSingleton() {
		return true;
	}
}
```
- T getObject()：返回FactoryBean创建的bean实例，如果isSingleton返回true，则该实例会放到spring容器中单实例缓存池
- boolean isSingleton：是否是单例，默认返回true
- Class<T> getObjectType()：返回bean类型
getBean方法返回的不是FactoryBean本身，而是FactoryBean#getObject方法返回的对象，相当于getObject代理了getBean
getBean(beanName)在加上&即getBean(&beanName)则获取FactoryBean本身
------------------------

缓存中获取单例bean
------------------------
spring单例只创建一次，首先尝试从缓存中获取，然后再尝试从singletonFactories中加载。为了避免循环依赖，会创建bean的
ObjectFactory进行提早曝光，一旦下个bean依赖某个bean，则直接使用ObjectFactory。
``` 
public Object getSingleton(String beanName) {
	// 设置为true标识允许早期依赖
	return getSingleton(beanName, true);
}

@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
	// 检查缓存中是否存在实例
	Object singletonObject = this.singletonObjects.get(beanName);
	if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
		// 如果为空，则锁定全局变量并处理
		synchronized (this.singletonObjects) {
			// 如果bean正在加载则不处理
			singletonObject = this.earlySingletonObjects.get(beanName);
			// bean为空并且允许懒加载即早期的空对象null的引用
			if (singletonObject == null && allowEarlyReference) {
				// 当某些方法需要提前初始化的时则会调用addSingletonFactory方法将对应的ObjectFactory初始化策略存储在singleFactories
				ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
				if (singletonFactory != null) {
					// 调用预先设定的getObject方法
					singletonObject = singletonFactory.getObject();
					// 记录在缓存中，earlySingletonObjects与singletonFactories互斥
					this.earlySingletonObjects.put(beanName, singletonObject);
					this.singletonFactories.remove(beanName);
				}
			}
		}
	}
	return singletonObject;
}
```
首先尝试从singletonObjects中获取实例，获取不到再从earlySingletonObjects中获取，还获取不到，再尝试从singletonFactories中获取
beanName对应的ObjectFactory，然后调用其getObject创建bean。
- singletonObjects：保存beanName和创建bean实例
- singletonFactories：保存beanName和创建bean工厂
- earlySingletonObjects：保存beanName和创建bean实例，与singletonObjects不同在于，当实例bean放在这里后，bean创建过程中，可以
通过getBean获取到，其目的用来检测循环依赖
- registeredSingletons：保存所有注册的bean

------------------------

从bean的实例中获取对象 getObjectForBeanInstance
------------------------
再获取bean需要调用该方法检测正确性，其实检测bean是否是FactoryBean类型的bean，如果是则需要调用FactoryBean#getObject()作为返回值。
```
	protected Object getObjectForBeanInstance(
			Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

	// 如果指定name是工厂相关（&前缀）
	if (BeanFactoryUtils.isFactoryDereference(name)) {
		if (beanInstance instanceof NullBean) {
			return beanInstance;
		}
		//不是FactoryBean类型则抛出异常
		if (!(beanInstance instanceof FactoryBean)) {
			throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
		}
		if (mbd != null) {
			mbd.isFactoryBean = true;
		}
		return beanInstance;
	}

	// Now we have the bean instance, which may be a normal bean or a FactoryBean.
	// If it's a FactoryBean, we use it to create a bean instance, unless the
	// caller actually wants a reference to the factory.
	//bean实例可能是正常的bean或者FactoryBean，如果是非FactoryBean则直接返回
	if (!(beanInstance instanceof FactoryBean)) {
		return beanInstance;
	}
	//加载FactoryBean
	Object object = null;
	if (mbd != null) {
		mbd.isFactoryBean = true;
	}
	else {
		// 尝试从缓存中加载bean
		object = getCachedObjectForFactoryBean(beanName);
	}
	if (object == null) {
		FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
		//containsBeanDefinition检查是否定义beanName
		if (mbd == null && containsBeanDefinition(beanName)) {
			//将GenericBeanDefinition转换为RootBeanDefinition，如果为子bean则合并父类属性
			mbd = getMergedLocalBeanDefinition(beanName);
		}
		// 是否为用户定义而不是程序定义
		boolean synthetic = (mbd != null && mbd.isSynthetic());
		object = getObjectFromFactoryBean(factory, beanName, !synthetic);
	}
	return object;
}
```
- 对FactoryBean正确性验证
- 对非FactoryBean不做处理
- 对bean进行转换
- 将Factory中解析的bean的工作委托给getObjectFromFactoryBean
```
protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
	// 如果是单例模式
	if (factory.isSingleton() && containsSingleton(beanName)) {
		synchronized (getSingletonMutex()) {
			Object object = this.factoryBeanObjectCache.get(beanName);
			if (object == null) {
				object = doGetObjectFromFactoryBean(factory, beanName);
				if (alreadyThere != null) {
					object = alreadyThere;
				}
				else {
					if (shouldPostProcess) {
						if (isSingletonCurrentlyInCreation(beanName)) {
							return object;
						}
						beforeSingletonCreation(beanName);
						try {
							//调用ObjectFactory的后置处理器
							object = postProcessObjectFromFactoryBean(object, beanName);
						}
						catch (Throwable ex) {
							throw new BeanCreationException(beanName,
									"Post-processing of FactoryBean's singleton object failed", ex);
						}
						finally {
							afterSingletonCreation(beanName);
						}
					}
					if (containsSingleton(beanName)) {
						this.factoryBeanObjectCache.put(beanName, object);
					}
				}
			}
			return object;
		}
	}
	else {
		Object object = doGetObjectFromFactoryBean(factory, beanName);
		if (shouldPostProcess) {
			try {
				object = postProcessObjectFromFactoryBean(object, beanName);
			}
			catch (Throwable ex) {
				throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
			}
		}
		return object;
	}
}
```
此时做了一件事，如果bean是单例就必须保证全局唯一。
- doGetObjectFromFactoryBean调用getObject方法
```
	private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
			throws BeanCreationException {
		Object object;
		try {
			// 权限验证
			if (System.getSecurityManager() != null) {
				AccessControlContext acc = getAccessControlContext();
				try {
					object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
				//直接调用getObject方法
				object = factory.getObject();
			}
		}
		return object;
	}

```
- AbstractAutowireCapableBeanFactory#postProcessObjectFromFactoryBean
```
protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
	return applyBeanPostProcessorsAfterInitialization(object, beanName);
}
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessAfterInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}
```
在bean初始化后调用注册的BeanPostProcessor的postProcessAfterInitialization方法进行处理

------------------------

获取单例
------------------------
如果缓存不存在已加载的bean，就需要从头开始bean的加载过程，spring使用getSingleton重载方法实现bean的加载
```
sharedInstance = getSingleton(beanName, () -> {
						try {
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							// Explicitly remove instance from singleton cache: It might have been put there
							// eagerly by the creation process, to allow for circular reference resolution.
							// Also remove any beans that received a temporary reference to the bean.
							destroySingleton(beanName);
							throw ex;
						}
					});

public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
	//全局变量同步
	synchronized (this.singletonObjects) {
		//首先检查对应的bean是否已经加载
		Object singletonObject = this.singletonObjects.get(beanName);
		//为空才进行singleton的bean的初始化
		if (singletonObject == null) {
			if (this.singletonsCurrentlyInDestruction) {
				throw new BeanCreationNotAllowedException(beanName,
						"Singleton bean creation not allowed while singletons of this factory are in destruction " +
						"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
			}
			beforeSingletonCreation(beanName);
			boolean newSingleton = false;
			boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
			if (recordSuppressedExceptions) {
				this.suppressedExceptions = new LinkedHashSet<>();
			}
			try {
				//初始化bean
				singletonObject = singletonFactory.getObject();
				newSingleton = true;
			}
			catch (IllegalStateException ex) {
				// Has the singleton object implicitly appeared in the meantime ->
				// if yes, proceed with it since the exception indicates that state.
				singletonObject = this.singletonObjects.get(beanName);
				if (singletonObject == null) {
					throw ex;
				}
			}
			catch (BeanCreationException ex) {
				if (recordSuppressedExceptions) {
					for (Exception suppressedException : this.suppressedExceptions) {
						ex.addRelatedCause(suppressedException);
					}
				}
				throw ex;
			}
			finally {
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = null;
				}
				afterSingletonCreation(beanName);
			}
			if (newSingleton) {
				//加入缓存
				addSingleton(beanName, singletonObject);
			}
		}
		return singletonObject;
	}
}
```
真正获取单例bean在ObjectFactory类型的singletonFactory中实现的。包括：
1. 检查缓存是否已经加载
2. 若没有加载，则记录beanName的正在加载状态
3. 加载单例前记录加载状态,beforeSingletonCreation记录加载状态，通过this.singletionsCurrentlyInCreation.add(beanName)将
当前正要创建的bean记录在缓存中，便于对循环依赖进行检测。
```
	protected void beforeSingletonCreation(String beanName) {
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
			throw new BeanCurrentlyInCreationException(beanName);
		}
	}
```
4. 调用传入的ObjectFactory的个体Object方法实例化bean
5. 加载单例后的处理方法调用，当bean加载结束后移除对该bean的正在加载状态
```
	protected void afterSingletonCreation(String beanName) {
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
			throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
		}
	}
```
6. 将结果记录至缓存并删除加载bean过程中所记录的各种辅助状态
```
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
			this.singletonObjects.put(beanName, singletonObject);
			this.singletonFactories.remove(beanName);
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.add(beanName);
		}
	}
```
7. 返回处理结果，ObjectFactory核心只是调用了createBean
```
sharedInstance = getSingleton(beanName, () -> {
						try {
							return createBean(beanName, mbd, args);
						}
						catch (BeansException ex) {
							// Explicitly remove instance from singleton cache: It might have been put there
							// eagerly by the creation process, to allow for circular reference resolution.
							// Also remove any beans that received a temporary reference to the bean.
							destroySingleton(beanName);
							throw ex;
						}
					});
```
------------------------

准备创建bean
------------------------
createBean只是从全局角度做统筹工作
```
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
		throws BeanCreationException {
	// 锁定class，根据设置的class属性或根据className来解析class
	Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
	if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
		mbdToUse = new RootBeanDefinition(mbd);
		mbdToUse.setBeanClass(resolvedClass);
	}

	// Prepare method overrides.
	try {
		//验证及准备覆盖的方法
		mbdToUse.prepareMethodOverrides();
	}
	catch (BeanDefinitionValidationException ex) {
		throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
				beanName, "Validation of method overrides failed", ex);
	}

	try {
		//给BeanPostProcessors一个机会返回代理来替代真正的实例	
		Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
		if (bean != null) {
			return bean;
		}
	}
	catch (Throwable ex) {
		throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
				"BeanPostProcessor before instantiation of bean failed", ex);
	}

	try {
		Object beanInstance = doCreateBean(beanName, mbdToUse, args);
		return beanInstance;
	}
	catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
		throw ex;
	}
	catch (Throwable ex) {
		throw new BeanCreationException(
				mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
	}
}
```
------------------------

处理override属性
------------------------
查看源码中AbstractBeanDefinition类的prepareMethodOverrides方法：
```
public void prepareMethodOverrides() throws BeanDefinitionValidationException {
	// Check that lookup methods exist and determine their overloaded status.
	if (hasMethodOverrides()) {
		getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
	}
}
protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
	//获取对应类中对应方法名的个数
	int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
	if (count == 0) {
		throw new BeanDefinitionValidationException(
				"Invalid method override: no method with name '" + mo.getMethodName() +
				"' on class [" + getBeanClassName() + "]");
	}
	else if (count == 1) {
		//标记MethodOverride暂未被覆盖，避免参数类型检查的开销
		mo.setOverloaded(false);
	}
}
```
在Spring配置中存在lookup-method和replace-method两个配置功能，统一存放在BeanDefinition的methodOverrides属性中。
bean实例化时根据method属性，动态的为当前bean生成代理并使用对应的拦截器为bean做增强处理。
------------------------

实例化的前置处理
------------------------
在调用doCreate创建bean的实例前使用resolveBeforeInstantiation(beanName,mbd)对BeanDefinition中属性做前置处理。
函数中还提供了一个短路判断
```
if (bean != null) {
	return bean;
}
```
当前置处理后返回结果不为空，则会直接略过后续的bean创建而直接返回结果。这一特性很重要，aop功能就基于这里判断。
```
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
	Object bean = null;
	//如果尚未被解析
	if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
		// Make sure bean class is actually resolved at this point.
		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			Class<?> targetType = determineTargetType(beanName, mbd);
			if (targetType != null) {
				bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
				if (bean != null) {
					bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
				}
			}
		}
		mbd.beforeInstantiationResolved = (bean != null);
	}
	return bean;
}
```
applyBeanPostProcessorsBeforeInstantiation 和 applyBeanPostProcessorsAfterInitialization是对后处理器中的所有
InstantiationAwareBeanProcessor类型的后处理器进行postProcessBeforeInstantiation方法和BeanPostProcessor的
postProcessAfterInitialization方法的调用。
1. 实例化前的后处理器应用
bean的实例化前调用，也就是将AbstractBeanDefinition转换为BeanWrapper前的处理。给子类修改BeanDefinition的机会，
调用过这个方法后，bean已经不是原先定义的bean，或许为处理过的代理bean。
```
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
	for (BeanPostProcessor bp : getBeanPostProcessors()) {
		if (bp instanceof InstantiationAwareBeanPostProcessor) {
			InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
			Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
			if (result != null) {
				return result;
			}
		}
	}
	return null;
}
```
2. 实例化的后处理器应用
spring规则是在bean初始化后尽可能保证将注册的后处理器的postProcessAfterInitialization方法应用到该bean中，如果返回bean
不为空，则不会经历bean的创建过程。
```
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessAfterInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}
```
------------------------

创建bean
------------------------
当调用resolveBeforeInstantiation方法后，程序有两个选择，如果创建了代理或者重写了InstatiationAwareBeanProcessor的
postProcessBeforeInstantiation并改变了bean，则直接返回即可，否则需要进行常规的bean创建，在doCreatBean中完成。
```
	protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
			throws BeanCreationException {
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		if (instanceWrapper == null) {
			// 根据指定bean使用对应的策略创建新的实例，如：工厂方法、构造函数自动注入、简单初始化
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		final Object bean = instanceWrapper.getWrappedInstance();
		Class<?> beanType = instanceWrapper.getWrappedClass();
		if (beanType != NullBean.class) {
			mbd.resolvedTargetType = beanType;
		}

		synchronized (mbd.postProcessingLock) {
			if (!mbd.postProcessed) {
				try {
					applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Post-processing of merged bean definition failed", ex);
				}
				mbd.postProcessed = true;
			}
		}

		// 是否需要提早曝光：单例&允许循环依赖&当前bean正在创建中，检测循环依赖
		boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
		if (earlySingletonExposure) {
			if (logger.isTraceEnabled()) {
				logger.trace("Eagerly caching bean '" + beanName +
						"' to allow for resolving potential circular references");
			}
			// 为避免后期循环依赖，可以在bean初始化完成前将创建实例的ObjectFactory加入工厂
			addSingletonFactory(beanName,
					//对bean再一次依赖引用，主要应用SmartInstantiationAware BeanPostProcessor
					//熟悉的aop就在这里将advice动态织入bean中，如没有则直接返回bean，不做任何处理
					() -> getEarlyBeanReference(beanName, mbd, bean));
		}

		// Initialize the bean instance.
		Object exposedObject = bean;
		try {
			// 对bean进行填充，将各个属性注入，可能存在依赖其他bean的属性，则会递归初始依赖bean
			populateBean(beanName, mbd,instanceWrapper);
			// 调用初始化方法，比如init-method
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}
		catch (Throwable ex) {
			if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
				throw (BeanCreationException) ex;
			}
			else {
				throw new BeanCreationException(
						mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
			}
		}

		if (earlySingletonExposure) {
			Object earlySingletonReference = getSingleton(beanName, false);
			// earlySingletonReference只有在检测到有循环依赖的情况下不为空
			if (earlySingletonReference != null) {
				// 如果exposedObject没有在初始化方法中被改变，即没有增强
				if (exposedObject == bean) {
					exposedObject = earlySingletonReference;
				}
				else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
					for (String dependentBean : dependentBeans) {
						//依赖检测
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
					/**
					 * 因为bean创建后所依赖的bean一定是已创建的，actualDependentBeans不为空表示当前bean创建后
					 * 其依赖的bean却没有全部创建完，即存在循环依赖
					 */
					if (!actualDependentBeans.isEmpty()) {
						throw new BeanCurrentlyInCreationException(beanName,
								"Bean with name '" + beanName + "' has been injected into other beans [" +
								StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
								"] in its raw version as part of a circular reference, but has eventually been " +
								"wrapped. This means that said other beans do not use the final version of the " +
								"bean. This is often the result of over-eager type matching - consider using " +
								"'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
					}
				}
			}
		}

		// Register bean as disposable.
		try {
			// 根据scope注册bean
			registerDisposableBeanIfNecessary(beanName, bean, mbd);
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
		}

		return exposedObject;
	}
```
1. 创建bean实例
```
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
	// Make sure bean class is actually resolved at this point.
	Class<?> beanClass = resolveBeanClass(mbd, beanName);

	if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
		throw new BeanCreationException(mbd.getResourceDescription(), beanName,
				"Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
	}
	//如果存在callback回调，通过callback创建bean
	Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
	if (instanceSupplier != null) {
		return obtainFromSupplier(instanceSupplier, beanName);
	}

	// 存在工厂方法则使用工厂方法初始化策略
	if (mbd.getFactoryMethodName() != null) {
		return instantiateUsingFactoryMethod(beanName, mbd, args);
	}

	boolean resolved = false;
	boolean autowireNecessary = false;
	if (args == null) {
		synchronized (mbd.constructorArgumentLock) {
			// 一个类有多个构造函数，每个构造函数都有不同的参数，所以调用前需要先根据参数锁定构造函数或对应的工厂方法
			if (mbd.resolvedConstructorOrFactoryMethod != null) {
				resolved = true;
				autowireNecessary = mbd.constructorArgumentsResolved;
			}
		}
	}
	//如果已解析过则使用解析好的构造函数方法不需要再次锁定
	if (resolved) {
		if (autowireNecessary) {
			//构造函数自动注入
			return autowireConstructor(beanName, mbd, null, null);
		}
		else {
			//使用默认构造函数构造
			return instantiateBean(beanName, mbd);
		}
	}

	//需要根据参数解析构造函数
	Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
	if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
			mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
		//构造函数自动注入
		return autowireConstructor(beanName, mbd, ctors, args);
	}

	ctors = mbd.getPreferredConstructors();
	if (ctors != null) {
		//构造函数自动注入
		return autowireConstructor(beanName, mbd, ctors, null);
	}

	//默认构造函数
	return instantiateBean(beanName, mbd);
}
```

- autowireConstructor
spring创建实例分为通用的实例化和带有参数的实例化。
```
	public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd,
			@Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {

		BeanWrapperImpl bw = new BeanWrapperImpl();
		this.beanFactory.initBeanWrapper(bw);

		Constructor<?> constructorToUse = null;
		ArgumentsHolder argsHolderToUse = null;
		Object[] argsToUse = null;

		//explicitArgs通过getBean方法传入，如果getBean方法调用时指定方法参数直接使用
		if (explicitArgs != null) {
			argsToUse = explicitArgs;
		}
		else {
			//如果未指定参数，则尝试从配置中解析
			Object[] argsToResolve = null;
			//尝试从缓存中获取
			synchronized (mbd.constructorArgumentLock) {
				constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
				if (constructorToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached constructor...
					//从缓存中取
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						//配置的构造函数
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			//如果缓存中存在
			if (argsToResolve != null) {
				//解析参数类型，如给定的构造函数A(int,int)则通过此方法后就把配置的("1","1")转换为(1,1)
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve, true);
			}
		}

		//如果没有被缓存
		if (constructorToUse == null || argsToUse == null) {
			// Take specified constructors, if any.
			Constructor<?>[] candidates = chosenCtors;
			if (candidates == null) {
				Class<?> beanClass = mbd.getBeanClass();
				try {
					candidates = (mbd.isNonPublicAccessAllowed() ?
							beanClass.getDeclaredConstructors() : beanClass.getConstructors());
				}
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Resolution of declared constructors on bean Class [" + beanClass.getName() +
							"] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
				}
			}

			if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				Constructor<?> uniqueCandidate = candidates[0];
				if (uniqueCandidate.getParameterCount() == 0) {
					synchronized (mbd.constructorArgumentLock) {
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						mbd.constructorArgumentsResolved = true;
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			boolean autowiring = (chosenCtors != null ||
					mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			ConstructorArgumentValues resolvedValues = null;

			int minNrOfArgs;
			if (explicitArgs != null) {
				minNrOfArgs = explicitArgs.length;
			}
			else {
				// 提取配置的构造函数参数
				ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
				// 用于承载解析后的构造函数参数的值
				resolvedValues = new ConstructorArgumentValues();
				// 能解析到的参数个数
				minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
			}

			// 排序给定的构造函数，public构造函数优先参数数量降序、非pubic构造函数参数数量降序
			AutowireUtils.sortConstructors(candidates);
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Set<Constructor<?>> ambiguousConstructors = null;
			LinkedList<UnsatisfiedDependencyException> causes = null;

			for (Constructor<?> candidate : candidates) {

				int parameterCount = candidate.getParameterCount();

				if (constructorToUse != null && argsToUse != null && argsToUse.length > parameterCount) {
					// Already found greedy constructor that can be satisfied ->
					// do not look any further, there are only less greedy constructors left.
					//如果已经找到选用的构造函数或者需要的参数个数小于当前的构造函数参数个数终止
					break;
				}
				if (parameterCount < minNrOfArgs) {
					//参数个数不相等
					continue;
				}

				ArgumentsHolder argsHolder;
				Class<?>[] paramTypes = candidate.getParameterTypes();
				if (resolvedValues != null) {
					// 有参数则根据值构造对应参数类型的参数
					try {
						// 获取ConstructorProperties注释上的参数名称
						String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, parameterCount);
						if (paramNames == null) {
							// 获取参数名称探索器
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
								//获取指定构造函数的参数名称
								paramNames = pnd.getParameterNames(candidate);
							}
						}
						//根据名称和数据类型创建参数持有者
						argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames,
								getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
					}
					catch (UnsatisfiedDependencyException ex) {
						if (logger.isTraceEnabled()) {
							logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex);
						}
						if (causes == null) {
							causes = new LinkedList<>();
						}
						causes.add(ex);
						continue;
					}
				}
				else {
					if (parameterCount != explicitArgs.length) {
						continue;
					}
					// 构造函数没有参数的情况
					argsHolder = new ArgumentsHolder(explicitArgs);
				}

				//探测是否有不确定性的构造函数存在，例如不同构造函数的参数为父子关系
				int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
						argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
				//如果代表当前最接近的匹配则选择作为构造函数
				if (typeDiffWeight < minTypeDiffWeight) {
					constructorToUse = candidate;
					argsHolderToUse = argsHolder;
					argsToUse = argsHolder.arguments;
					minTypeDiffWeight = typeDiffWeight;
					ambiguousConstructors = null;
				}
				else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
					if (ambiguousConstructors == null) {
						ambiguousConstructors = new LinkedHashSet<>();
						ambiguousConstructors.add(constructorToUse);
					}
					ambiguousConstructors.add(candidate);
				}
			}

			if (constructorToUse == null) {
				if (causes != null) {
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Could not resolve matching constructor " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
			}
			else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous constructor matches found in bean '" + beanName + "' " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousConstructors);
			}

			if (explicitArgs == null && argsHolderToUse != null) {
				//将解析的构造函数加入缓存
				argsHolderToUse.storeCache(mbd, constructorToUse);
			}
		}

		Assert.state(argsToUse != null, "Unresolved constructor arguments");
		//将构建的实例加入BeanWrapper中
		bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
		return bw;
	}
```

- instantiateBean
不带构造参数的构造函数实例化，直接调用实例化策略实例化
```
	protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
		try {
			Object beanInstance;
			final BeanFactory parent = this;
			if (System.getSecurityManager() != null) {
				beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>) () ->
						getInstantiationStrategy().instantiate(mbd, beanName, parent),
						getAccessControlContext());
			}
			else {
				beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
			}
			BeanWrapper bw = new BeanWrapperImpl(beanInstance);
			initBeanWrapper(bw);
			return bw;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
		}
	}
```

- 实例化策略
spring首先判断methodOverrides是否为空，如果为空直接使用反射方式。如果存在该属性使用动态代理将包含两个特性所对应的逻辑
的拦截器设置进去，返回值包含拦截器的代理实例。
```
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
	// Don't override the class with CGLIB if no overrides.
	//如果有需要覆盖或者动态替换的方法则使用cglib进行动态代理，在创建代理的同时将动态方法织入类中
	//如果没有动态改变的方法，直接反射获取
	if (!bd.hasMethodOverrides()) {
		Constructor<?> constructorToUse;
		synchronized (bd.constructorArgumentLock) {
			constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
			if (constructorToUse == null) {
				final Class<?> clazz = bd.getBeanClass();
				if (clazz.isInterface()) {
					throw new BeanInstantiationException(clazz, "Specified class is an interface");
				}
				try {
					if (System.getSecurityManager() != null) {
						constructorToUse = AccessController.doPrivileged(
								(PrivilegedExceptionAction<Constructor<?>>) clazz::getDeclaredConstructor);
					}
					else {
						constructorToUse = clazz.getDeclaredConstructor();
					}
					bd.resolvedConstructorOrFactoryMethod = constructorToUse;
				}
				catch (Throwable ex) {
					throw new BeanInstantiationException(clazz, "No default constructor found", ex);
				}
			}
		}
		return BeanUtils.instantiateClass(constructorToUse);
	}
	else {
		// Must generate CGLIB subclass.
		return instantiateWithMethodInjection(bd, beanName, owner);
	}
}
private Class<?> createEnhancedSubclass(RootBeanDefinition beanDefinition) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(beanDefinition.getBeanClass());
		enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
		if (this.owner instanceof ConfigurableBeanFactory) {
			ClassLoader cl = ((ConfigurableBeanFactory) this.owner).getBeanClassLoader();
			enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(cl));
		}
		enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(beanDefinition));
		enhancer.setCallbackTypes(CALLBACK_TYPES);
		return enhancer.createClass();
	}
}
```

2. 记录创建bean的ObjectFactory
```
boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
		isSingletonCurrentlyInCreation(beanName));
if (earlySingletonExposure) {
	if (logger.isTraceEnabled()) {
		logger.trace("Eagerly caching bean '" + beanName +
				"' to allow for resolving potential circular references");
	}
	// 为避免后期循环依赖，可以在bean初始化完成前将创建实例的ObjectFactory加入工厂
	addSingletonFactory(beanName,
			//对bean再一次依赖引用，主要应用SmartInstantiationAware BeanPostProcessor
			//熟悉的aop就在这里将advice动态织入bean中，如没有则直接返回bean，不做任何处理
			() -> getEarlyBeanReference(beanName, mbd, bean));
}
```
- earlySingletonExposure：提早曝光单例
- mdg.isSingleton：是否为单例
- this.allowCircularReferences：是否允许循环依赖
- isSingletonCurrentlyInCreation：该bean是否在创建中。
earlySingletonExposure是否是单例、允许循环依赖、bean正在创建中的条件的综合。

3. 属性注入
```
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
	if (bw == null) {
		if (mbd.hasPropertyValues()) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
		}
		else {
			// 没有可填充的属性
			return;
		}
	}
	// 给InstantiationAwareBeanPostProcessor最后一次机会在属性设置前改变bean
	if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				// 返回值是否继续填充bean
				if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
					return;
				}
			}
		}
	}

	PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

	int resolvedAutowireMode = mbd.getResolvedAutowireMode();
	if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
		MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
		//根据名称自动注入
		if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
			autowireByName(beanName, mbd, bw, newPvs);
		}
		//根据类型自动注入
		if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
			autowireByType(beanName, mbd, bw, newPvs);
		}
		pvs = newPvs;
	}

	//后置处理器已经初始化
	boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
	//需要依赖检查
	boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

	PropertyDescriptor[] filteredPds = null;
	if (hasInstAwareBpps) {
		if (pvs == null) {
			pvs = mbd.getPropertyValues();
		}
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
				if (pvsToUse == null) {
					if (filteredPds == null) {
						filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
					}
					//对所有需要依赖检查的属性进行后处理
					pvsToUse = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
					if (pvsToUse == null) {
						return;
					}
				}
				pvs = pvsToUse;
			}
		}
	}
	if (needsDepCheck) {
		if (filteredPds == null) {
			filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
		}
		checkDependencies(beanName, mbd, filteredPds, pvs);
	}

	if (pvs != null) {
		// 将属性应用到bean中
		applyPropertyValues(beanName, mbd, bw, pvs);
	}
}
```
- InstantiationAwareBeanPostProcessor处理器的postProcessAfterInstantiation函数，可以控制程序是否继续填充属性
- 根据注入类byName或byType，提取依赖bean，并统一存入PropertyValues中
- InstantiationAwareBeanPostProcessor处理器的postProcessPropertyValues方法，对属性获取完毕填充前对属性再次处理，
如RequiredAnnotationBeanPostProcessor类对属性的验证
- 将所有PropertyValues中的属性填充至BeanWrapper中

4. 初始化bean
spring执行bean的实例化以及属性的填充后，将调用用户设定的初始化方法
```
protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
	if (System.getSecurityManager() != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			invokeAwareMethods(beanName, bean);
			return null;
		}, getAccessControlContext());
	}
	else {
		// 对特殊bean处理：Aware、BeanClassLoaderAware、BeanFactoryAware
		invokeAwareMethods(beanName, bean);
	}

	Object wrappedBean = bean;
	if (mbd == null || !mbd.isSynthetic()) {
		// 应用后处理器
		wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
	}
	try {
		//激活用户自定义的init方法
		invokeInitMethods(beanName, wrappedBean, mbd);
	}
	catch (Throwable ex) {
		throw new BeanCreationException(
				(mbd != null ? mbd.getResourceDescription() : null),
				beanName, "Invocation of init method failed", ex);
	}
	if (mbd == null || !mbd.isSynthetic()) {
		//后处理器应用
		wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
	}
	return wrappedBean;
}
```
- Aware方法
spring提供了Aware相关接口，如BeanFactoryAware、ApplicationContextAware、ResourceLoaderAware、ServletContextAware等，
实现Aware接口的bean在初始化后，可以取得相对应的资源。

- 处理器的应用
调用客户自定义初始化方法前以及调用自定初始化方法后分别调用processorsBeforeInitialization和processorsAfterInitialization，
根据业务需求进行响应

- 激活自定义init方法
定制的初始化方法除了配置的init-method外，还有自定义的bean实现InitializingBean接口，并在afterPropertiesSet实现业务逻辑。
执行顺序afterPropertiesSet先执行，init-method后执行。


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