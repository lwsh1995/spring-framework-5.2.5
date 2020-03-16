package spring.context.annotation.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SpringAspect {

	@Pointcut("execution(* spring.context.annotation.component.AopComponent.*(..))")
	public void aop(){

	}

	@Before("aop()")
	public void beforeMethod(){
		System.out.println("before aop");
	}

	@Around("aop()")
	public Object aroundAop(ProceedingJoinPoint joinPoint){
		System.out.println("before around");
		Object o=null;
		try {
			o = joinPoint.proceed();
		}catch(Throwable e){
			e.printStackTrace();
		}
		System.out.println("after around");
		return o;
	}

	@After("aop()")
	public void afterMethod(){
		System.out.println("after aop");
	}

}
