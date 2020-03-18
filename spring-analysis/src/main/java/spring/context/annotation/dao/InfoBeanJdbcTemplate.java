package spring.context.annotation.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import spring.context.annotation.domain.InfoBean;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class InfoBeanJdbcTemplate implements InfoBeanDao {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertUser(InfoBean infoBean) {
		String sql = "insert into info_bean (id,name) values(?,?)";
		jdbcTemplate.update(sql,infoBean.getId(),infoBean.getName());
		System.out.println("create record : "+infoBean.toString());
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		String sql="delete from info_bean where id = ?";
		jdbcTemplate.update(sql,id);
		System.out.println("delete record : "+id);
		// 抛出异常，让操作回滚
		throw new RuntimeException("aa");
	}

	@Override
	public List<InfoBean> selectAll() {
		String sql="select * from info_bean";
		List query = jdbcTemplate.query(sql, new InfoBeanMapper());
		return query;
	}
}
