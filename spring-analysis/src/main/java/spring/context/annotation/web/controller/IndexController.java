package spring.context.annotation.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.context.annotation.dao.InfoBeanDao;
import spring.context.annotation.domain.InfoBean;

import java.util.List;

@RestController
@RequestMapping("/spring")
public class IndexController {

	@Autowired
	InfoBeanDao infoBeanDao;

	@GetMapping("/index")
	public String index(){
		return "index";
	}

	@GetMapping("/data")
	public String data(){
		List<InfoBean> infoBeans = infoBeanDao.selectAll();
		return infoBeans.toString();
	}
}
