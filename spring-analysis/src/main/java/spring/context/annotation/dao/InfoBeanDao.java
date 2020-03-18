package spring.context.annotation.dao;

import spring.context.annotation.domain.InfoBean;

import java.util.List;

public interface InfoBeanDao {

	void insertUser(InfoBean infoBean);

	void deleteById(Integer id);

	List<InfoBean> selectAll();
}
