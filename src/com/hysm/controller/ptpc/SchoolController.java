package com.hysm.controller.ptpc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.hysm.bean.DBbean;
import com.hysm.controller.base.BaseController;
import com.hysm.dao.ajaxDAO;
import com.hysm.service.HistoryService;
import com.hysm.service.ISchoolService;
import com.hysm.util.Excel_exprot;
import com.hysm.util.StringUtil;

@Controller
@RequestMapping("/school")
public class SchoolController extends BaseController{

	@Autowired
	private ISchoolService schoolService;
	
	@Autowired
	private HistoryService history;
	
	@RequestMapping("querySchool")
	@ResponseBody
	public void querySchool(HttpServletRequest request,HttpServletResponse response){
		
		int page =1;
		if(request.getParameter("page")!= null){
			page = Integer.valueOf(request.getParameter("page"));
		}
		
		int state = 100;
		
		if(request.getParameter("state")!= null){
			state = Integer.valueOf(request.getParameter("state"));
		}
		
		String schoolname = "";
		if(request.getParameter("schoolname")!= null){
			schoolname = request.getParameter("schoolname");
		}
		
		Map<String, Object> map = schoolService.query_school(state, page, schoolname);
		
		sendjson(map, response); 
	}
	
	@RequestMapping("querySchool2pt")
	@ResponseBody
	public void query(int state,String page,String limit,String schoolname,HttpServletResponse resp){
			Document doc=new Document();
			doc.put("state",state);
			int pag=StringUtil.toNum(page);
			int limi=StringUtil.toNum(limit);
			/*try {
				schoolname=new String(schoolname.getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			if(pag<1){
				pag=0;;
			}
			
			if(limi<30){
				limi=30;
			}
			if(!schoolname.isEmpty()){
				doc.put("schoolname",schoolname);
			}
			Document info=schoolService.query(doc, pag,limi);
			if(info!=null){
				sendJSON2(info.toJson(), resp);
				
			}
		
		}
	
	@RequestMapping("save_school")
	@ResponseBody
	public void save_school(HttpServletRequest request,HttpServletResponse response){
		
		 
		Map<String, Object> map = schoolService.save_school(request);
		  
		sendjson(map, response); 
	}
	
	
	@RequestMapping("school_info")
	@ResponseBody
	public void school_info(HttpServletRequest request,HttpServletResponse response){
		
		Document school = schoolService.school_info(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		map.put("school", school);
		
		
		sendjson(map, response); 
	}
	
	@RequestMapping("free_school")
	@ResponseBody
	public void free_school(HttpServletRequest request,HttpServletResponse response){
		
		int back_code = schoolService.free_school(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("back_code", back_code);
		
		sendjson(map, response); 
	}
	
	
	@RequestMapping("download_student")
	@ResponseBody
	public void download_student(HttpServletRequest request,HttpServletResponse response){
		
		HSSFWorkbook wb = Excel_exprot.exportfile();
		response.setContentType("applicationnd.ms-excel");  
        response.setHeader("Content-disposition", "attachment;filename=student.xls");  
        OutputStream ouputStream;
		try {
			ouputStream = response.getOutputStream();
			wb.write(ouputStream);  
			ouputStream.flush();  
			ouputStream.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	@RequestMapping(value = { "/upload_student" }, method = { RequestMethod.POST })
	public void upload_student(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model){
		
		Map<String, Object> map = schoolService.upload_student(request);
		
		sendjson(map, response); 
	}
	
	/*@RequestMapping(value = { "/query_student" }, method = { RequestMethod.POST })
	public void query_student(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> model){
		
		Map<String, Object> map = schoolService.query_student(request);
		
		sendjson(map, response); 
	}
	*/
	
	@RequestMapping("query_student")
	@ResponseBody
	public void query_student(int state,String page,String limit,String student_name,String school_code,HttpServletResponse resp){
		Document doc=new Document();
		doc.put("state",state);
		int pag=StringUtil.toNum(page);
		int limi=StringUtil.toNum(limit);
		if(pag<1){
			pag=0;;
		}
		
		if(limi<30){
			limi=30;
		}
		if(!student_name.isEmpty()){
			/*try {
				student_name=new String(student_name.getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			doc.put("student_name",student_name);
		}
		if(!school_code.isEmpty()){
			/*try {
				school_code=new String(school_code.getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			doc.put("school_code",school_code);
		}
		Document info=schoolService.query_student(doc, pag,limi);
		if(info!=null){
			
			sendJSON2(info.toJson(), resp);
			
		}
	}
	
	@RequestMapping("free_student")
	@ResponseBody
	public void free_student(HttpServletRequest request,HttpServletResponse response){
		
		int back_code = schoolService.free_student(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("back_code", back_code);
		
		sendjson(map, response); 
	}
	
	
	@RequestMapping("delete_student")
	@ResponseBody
	public void delete_student(HttpServletRequest request,HttpServletResponse response){
		
		int back_code = schoolService.delete_student(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("back_code", back_code);
		
		sendjson(map, response); 
	}
	
	@RequestMapping("student_info")
	@ResponseBody
	public void student_info(HttpServletRequest request,HttpServletResponse response){
		
		Document student = schoolService.student_info(request);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("student", student);
		
		sendjson(map, response); 
	}
	
	@RequestMapping("save_student")
	@ResponseBody
	public void save_student(HttpServletRequest request,HttpServletResponse response){
		 
		Map<String, Object> map = schoolService.save_student(request);
		  
		sendjson(map, response); 
	}
	
	@RequestMapping("query_all")
	@ResponseBody
	public ajaxDAO query_all(){
		List<Document> list=history.query_db_all(DBbean.T_student_jdres,null);
		System.out.println(list.size());
		return ajaxDAO.success(list);
		
	}
	
	@RequestMapping("query_history")
	@ResponseBody
	public ajaxDAO query_history(String username,String province,String fromwhere){
		System.out.println(username+"----"+province+"-----"+fromwhere);
		Document doc=new Document();
		doc.put("username", username);
		doc.put("province",province);
		doc.put("fromwhere",fromwhere);
		List<Document> list=history.query_db_all(DBbean.T_student_jdres, doc);
		System.out.println(list.size());
		return ajaxDAO.success(list);
		
	}
	
	@RequestMapping("query")
	@ResponseBody
	public void ajaxMessageList(HttpServletRequest req,HttpServletResponse resp){
		
	/*	String name = req.getParameter("likename");
		
		System.out.println(name);
		
		try {
			name=new String(name.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(name);*/
		
		Document info= history.ajaxJdList(req);
		
		
		if(info!=null){
			
			sendJSON2(info.toJson(), resp);
			
		}
		
		
		
	}
	
	
	@RequestMapping("allpass")
	@ResponseBody
	public void allpass(HttpServletRequest request,HttpServletResponse response){
		
		List<Document> list = schoolService.query_pass();
		
		sendjson(list, response);
	}
	
	
	@RequestMapping("savepass")
	@ResponseBody
	public void savepass(HttpServletRequest request,HttpServletResponse response){
		
		 
		int back = schoolService.savepass(request);
		
		sendMessage(back+"", response);
		 
	}
	
	
	@RequestMapping("deleteThisPass")
	@ResponseBody
	public  void deleteThisPass(HttpServletRequest request,HttpServletResponse response){
		
		 
		int back = schoolService.deleteThisPass(request);
		
		sendMessage(back+"", response);
		 
	}
}
