package spring.context.annotation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

/**
 * aspectjWeaving 属性
 * ENABLE 开启LTW
 * DISABLE 不开启LTW
 * AUTODETECT 如果类路径能读取META-INF/aop.xml则开启LTW，否则关闭
 *
 * 程序运行时需要加参数 -javaagent:spring-instrument\build\libs\spring-instrument-5.2.5.BUILD-SNAPSHOT.jar
 */
@Configuration
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
public class LoadTimeWeaverConfig {
}
