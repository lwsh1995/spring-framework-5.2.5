package spring.context.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.context.annotation.component.AopComponent;
import spring.context.annotation.component.BeanModifyBeanFactoryPostProcessor;
import spring.context.annotation.component.DateManager;
import spring.context.annotation.component.LTWBean;
import spring.context.annotation.config.*;
import spring.context.annotation.dao.InfoBeanDao;
import spring.context.annotation.mapper.UserMapper;
import spring.context.annotation.domain.*;

public class SpringAnnotationContext {

	public static void main(String[] args) {
		/*		第一种方式
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// 注册的两种方式
		context.registerBean("awareBean", AwareBean.class);
		context.scan("spring.base.annotation.config");
		context.refresh();*/
		//		第二种方式
		AnnotationConfigApplicationContext context
				= new AnnotationConfigApplicationContext(BeanConfig.class,
				PropertiesConfig.class,
				ScanConfig.class,
				AspectJConfig.class,
				LoadTimeWeaverConfig.class,
				ImportConfig.class,
				DataSourceConfig.class,
				MybatisConfig.class);

		LTWBean ltwBean = context.getBean(LTWBean.class);
		ltwBean.ltw();

		DateManager dateManager = context.getBean(DateManager.class);
		System.out.println(dateManager.getDate());

		BeanModifyBeanFactoryPostProcessor beanModify = context.getBean(BeanModifyBeanFactoryPostProcessor.class);
		System.out.println(beanModify.getName());


		NormalBean normalBean = context.getBean("normalBean",NormalBean.class);
		NormalBean normal = context.getBean("normal",NormalBean.class);
		NormalBean inner = context.getBean("inner", NormalBean.class);
		System.out.println(normalBean+"\n"+normal+"\n"+inner);
		context.publishEvent(new EventBean(context,"event"));


		AopComponent aopComponent = context.getBean(AopComponent.class);
		aopComponent.aopMethod();

		// factory bean
		InfoBean bean = (InfoBean) context.getBean("infoFactoryBean");
		System.out.println(bean);
		InfoFactoryBean infoFactoryBean = (InfoFactoryBean) context.getBean("&infoFactoryBean");
		System.out.println(infoFactoryBean.getInfo());


		InfoBeanDao infoBeanDao = context.getBean(InfoBeanDao.class);
//		infoBeanDao.insertUser(new InfoBean(1,"lwsh"));
		infoBeanDao.deleteById(1);

		UserMapper userMapper = context.getBean(UserMapper.class);
		UserBean user = userMapper.getUser(1);
		System.out.println(user.getName()+" "+user.getAge());
	}
}
