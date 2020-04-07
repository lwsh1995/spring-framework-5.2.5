package spring.context.annotation.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class MybatisConfig {

	@Bean("sqlSessionFactory")
	SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("classpath*:mapper/*Mapper.xml");
		sqlSessionFactory.setMapperLocations(resources);
		sqlSessionFactory.setDataSource(dataSource);
		return sqlSessionFactory;
	}
/*	使用单一的配置方式
	@Bean
	MapperFactoryBean<UserMapper> userMapper(SqlSessionFactory sqlSessionFactory){
		MapperFactoryBean<UserMapper> userMapper = new MapperFactoryBean<>();
		userMapper.setMapperInterface(UserMapper.class);
		userMapper.setSqlSessionFactory(sqlSessionFactory);
		return userMapper;
	}*/

	/**
	 * 可用分隔符逗号设置多个包路径。mapper将在指定包中递归被搜索到，没有发现注解，
	 * 则使用mapper的非大写的非完全限定类名。有Component、Named注解则获取名称
	 */
	@Bean
	MapperScannerConfigurer mapperScannerConfigurer(){
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setBasePackage("spring.context.annotation.mapper");
		return mapperScannerConfigurer;
	}

}
