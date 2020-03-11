package spring;

import org.junit.Test;

import java.util.Map;
import java.util.Properties;

public class SystemEnvPropTest {
	
	@Test
	public void SysEnvProp(){
		// 获取操作系统的环境变量
		Map<String, String> getenv = System.getenv();
		// 获取jvm的环境变量
		Properties properties = System.getProperties();

		System.out.println(getenv+" "+properties);
	}
}
