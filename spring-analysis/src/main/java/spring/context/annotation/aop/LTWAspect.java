package spring.context.annotation.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LTWAspect {
	@Pointcut("execution(public * spring.context.annotation.component.LTWBean.*(..))")
	public void pointCut(){}

	@Around("pointCut()")
	public void advice(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		System.out.println(signature+" start");
		joinPoint.proceed();
		System.out.println(signature+" end");

	}
}
