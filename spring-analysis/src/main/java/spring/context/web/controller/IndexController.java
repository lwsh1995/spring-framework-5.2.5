package spring.context.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import spring.context.annotation.dao.InfoBeanDao;
import spring.context.annotation.domain.InfoBean;

import javax.servlet.ServletContext;
import java.util.List;

@Controller
@RequestMapping("/spring")
public class IndexController {

	@Autowired
	InfoBeanDao infoBeanDao;

	@GetMapping("/index")
	public String index(){
/*
		ServletContext servletContext = ContextLoader.getCurrentWebApplicationContext().getServletContext();
		Object attribute = servletContext.getAttribute("spring-analysis");*/
		return "index";
	}

	@GetMapping("/data")
	@ResponseBody
	public String data(){
		List<InfoBean> infoBeans = infoBeanDao.selectAll();
		return infoBeans.toString();
	}
}
