package spring.context.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.context.annotation.component.AopComponent;
import spring.context.annotation.config.BeanConfig;
import spring.context.annotation.config.ImportConfig;
import spring.context.annotation.config.PropertiesConfig;
import spring.context.annotation.config.ScanConfig;
import spring.context.annotation.domain.EventBean;
import spring.context.annotation.domain.NormalBean;

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
				= new AnnotationConfigApplicationContext(BeanConfig.class, PropertiesConfig.class, ScanConfig.class, ImportConfig.class);
		NormalBean normalBean = context.getBean("normalBean",NormalBean.class);
		NormalBean normal = context.getBean("normal",NormalBean.class);
		NormalBean inner = context.getBean("inner", NormalBean.class);
		System.out.println(normalBean+"\n"+normal+"\n"+inner);
		context.publishEvent(new EventBean(context,"event"));

		AopComponent aopComponent = context.getBean(AopComponent.class);

		aopComponent.aopMethod();

	}
}
