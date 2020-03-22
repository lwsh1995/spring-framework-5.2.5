package spring.context.annotation.domain;

import org.springframework.beans.factory.FactoryBean;

public class InfoFactoryBean implements FactoryBean<InfoBean> {
	private String info;

	public InfoFactoryBean(String info) {
		this.info = info;
	}

	@Override
	public InfoBean getObject() throws Exception {
		InfoBean infoBean = new InfoBean();
		String[] split = info.split(",");
		infoBean.setId(Integer.parseInt(split[0]));
		infoBean.setName(split[1]);
		return infoBean;
	}

	@Override
	public Class<InfoBean> getObjectType() {
		return InfoBean.class;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
