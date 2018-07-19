package com.hysm.controller.app;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hysm.controller.base.BaseController;
import com.hysm.service.ISchoolService;

@Controller
@RequestMapping("/robot")
public class RobotController extends BaseController {

	@Autowired
	private ISchoolService schoolService;
	
	/**
	 * 机器人设置（机器人编号,学校编号）
	 * @param request
	 * @param response
	 */
	@RequestMapping("setRobot")
	@ResponseBody
	public void setRobot(HttpServletRequest request,HttpServletResponse response){
		
		Document doc = schoolService.setRobot(request);
		
		sendJSON2(doc.toJson(), response);
	}
	
	/**
	 * 学生登录
	 * @param request
	 * @param response
	 */
	@RequestMapping("studentLogin")
	@ResponseBody
	public void studentLogin(HttpServletRequest request,HttpServletResponse response){
		
		Document doc = schoolService.studentLogin(request);
		
		sendJSON2(doc.toJson(), response);
	}
	
	
	
	/**
	 * 学生登录
	 * @param request
	 * @param response
	 */
	@RequestMapping("studentInfo")
	@ResponseBody
	public void studentInfo(HttpServletRequest request,HttpServletResponse response){
		
		Document doc = schoolService.studentInfo(request);
		
		sendJSON2(doc.toJson(), response);
	}
	/**
	 * 学生登录
	 * @param request
	 * @param response
	 */
	@RequestMapping("studentpass")
	@ResponseBody
	public void studentpass(HttpServletRequest request,HttpServletResponse response){
		
		Document doc = schoolService.studentpass(request);
		
		//sendJSON2(doc.toJson(), response);
		
		sendJSONP(doc.toJson(), response, "initpage", 1);
	}
	
	 
	@RequestMapping("query_advert")
	@ResponseBody
	public void query_advert(HttpServletRequest request,HttpServletResponse response){
	
		List<Document> list =  schoolService.query_advert();
		
		sendjson(list, response);
	 
	}
	
	
	@RequestMapping("query_sort")
	@ResponseBody
	public void query_sort(HttpServletRequest request,HttpServletResponse response){
		
		Document doc = schoolService.query_sort(request);
		
		sendJSONP(doc.toJson(), response, "initpage", 1); 
	}
	
	
	@RequestMapping("reset_student")
	@ResponseBody
	public void reset_student(HttpServletRequest request,HttpServletResponse response){
	
		 int back = schoolService.reset_student(request);
		 
		 Document doc = new Document();
		 doc.put("backcode", back);
		 
		 sendJSON2(doc.toJson(), response);
	 
	}
	
	
	
	
	
	
}
