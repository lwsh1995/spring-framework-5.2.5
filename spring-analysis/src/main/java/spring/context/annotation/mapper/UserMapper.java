package spring.context.annotation.mapper;

import spring.context.annotation.domain.UserBean;

public interface UserMapper {

	UserBean getUser(long id);
}
