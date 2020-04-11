package spring.context.annotation.dao;

import org.springframework.jdbc.core.RowMapper;
import spring.context.annotation.domain.InfoBean;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoBeanMapper implements RowMapper<InfoBean> {
	@Override
	public InfoBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		InfoBean infoBean = new InfoBean();
		infoBean.setId(rs.getInt("id"));
		infoBean.setName(rs.getString("name"));
		return infoBean;
	}
}
