  package com.hysm.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hysm.bean.DBbean;
import com.hysm.db.SchoolDB;
import com.hysm.game.GameUtil;
import com.hysm.service.ISchoolService;
import com.hysm.util.DateUtil;
import com.hysm.util.Excel_import;
import com.hysm.util.Ip_tool;
import com.hysm.util.StringUtil;
import com.hysm.util.UuidUtil;

@Service("schoolService")
public class ISchoolServiceImpl implements ISchoolService {

	@Autowired
	private SchoolDB schoolDB; 
	
	@Override
	public Map<String, Object> query_school(int state, int page, String schoolname) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		 
		int skip = 0;
		int limit = 50; 
		skip = (page-1)*limit;
		
		Document sort = new Document();
		sort.put("cdate", -1);
		
		Document search  = new Document();
		if(state!= 100){
			search.put("state", state);
		} 
		if(schoolname!= null && !schoolname.equals("")){
			Pattern pattern = Pattern.compile("^.*" + schoolname + ".*$", Pattern.CASE_INSENSITIVE); 
			search.put("schoolname", pattern);
		}
		
		int count = schoolDB.query_count(DBbean.T_school, search);
		
		List<Document> list = schoolDB.query_db_page_sort(DBbean.T_school, search, skip, limit, sort);
		
		int page_num = page;//默认第一页 
		int page_count = 1;//默认总页数：1 
		if(count % limit == 0){
			page_count = count/limit;
		}else{
			page_count = (count/limit)+1;
		}
		
		map.put("page_num", page_num);
		map.put("page_count", page_count);
		map.put("count", count);
		map.put("state", state);
		map.put("schoolname", schoolname);
		map.put("list", list);
		 
		return map;
	}

	@Override
	public Map<String, Object>  save_school(HttpServletRequest request){
		
		String schoolname = request.getParameter("schoolname");
		String school_id = request.getParameter("school_id");
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String county = request.getParameter("county");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		int back_code = 0;
		
		if(school_id.equals("")){
			
			synchronized (this) {
				
				//校验学院名称重复
				Document doc = new Document();
				doc.put("schoolname", schoolname);
				doc.put("city", city);
				
				int num = schoolDB.query_count(DBbean.T_school, doc);
				
				if(num < 1){
					String school_code = schoolDB.query_school_code();
					school_id = UuidUtil.getId();
					
					Document school = new Document();
					school.put("_id", school_id);
					school.put("schoolcode", school_code);
					school.put("schoolname", schoolname);
					school.put("province", province);
					school.put("city", city);
					school.put("county", county);
					school.put("cdate", new Date());
					school.put("ctime", DateUtil.fromDate24H());
					school.put("state", 1);
					
					schoolDB.add_db_one(DBbean.T_school, school);
					
					map.put("schoolcode", school_code);
					
					back_code = 200;
					
				}else{
					back_code = 300;
				}
				
				
			}
			 
		}else{
			
			Document school = schoolDB.query_db_one(DBbean.T_school, school_id);
			 
			if(school!= null){
				
				String old_name = school.getString("schoolname"); 
				String schoolcode = school.getString("schoolcode");
				
				Document doc = new Document();
				doc.put("schoolname", schoolname);
				doc.put("city", city); 
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("$ne", school_id);
				doc.put("_id", m);
				
				int num = schoolDB.query_count(DBbean.T_school, doc);
				
				if(num < 1){
					school.put("schoolname", schoolname);
					school.put("province", province);
					school.put("city", city);
					school.put("county", county);
					
					map.put("schoolcode", school.getString("schoolcode"));
					
					schoolDB.replace_db_byid(DBbean.T_school, school, school_id);
					
					
					//学校名称修改，学生表也修改
					
					if(!old_name.equals(schoolname)){
						
						Document docs = new Document();
						docs.put("schoolcode", schoolcode);
						
						List<Document> student_list = schoolDB.query_db_all(DBbean.T_student, docs);
						
						if(student_list!= null && student_list.size()>0){ 
							for(int i=0;i<student_list.size();i++){
								student_list.get(i).put("schoolname", schoolname); 
								schoolDB.replace_db_byid(DBbean.T_student, student_list.get(i), student_list.get(i).getString("_id"));
							}
							
						} 
					}
					
					
					
					back_code = 200;
					
				}else{
					back_code = 300; 
				} 
				
			}else{
				back_code = 400; 
			} 
			
		}
		
		map.put("back_code", back_code);
		 
		return map;
	}

	@Override
	public Document school_info(HttpServletRequest request) {
		
		String school_id = request.getParameter("school_id");
		
		Document school = schoolDB.query_db_one(DBbean.T_school, school_id);
		 
		return school;
	}

	@Override
	public int free_school(HttpServletRequest request) {
		
		String school_id = request.getParameter("school_id");
		
		Document school = schoolDB.query_db_one(DBbean.T_school, school_id);
		
		int state = 1-school.getInteger("state");
		
		school.put("state", state);
		
		schoolDB.replace_db_byid(DBbean.T_school, school, school_id);
		
		return 200;
	}

	@Override
	public Map<String, Object> upload_student(HttpServletRequest request) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		Iterator<String> iter = multiRequest.getFileNames();
		
		String file_code = String.valueOf(System.currentTimeMillis());
		
		String schoolcode = request.getParameter("schoolcode");
		file_code = schoolcode+"_"+file_code;
		
		
		String path = request.getRealPath(""); 
		String p_path = path + "/assets/excel/"; 
		String ext = "";
		String file_path ="";
		
		while (iter.hasNext()) {
			
			MultipartFile file = multiRequest.getFile((String) iter.next());
			
			// 有上传文件的话，容量是大于0的。
			if (file.getSize() > 0) {

				String fileName = file.getOriginalFilename();// 文件全名
				 
				ext = fileName.substring(fileName.lastIndexOf("."));// 后缀
				 
				File dir = new File(p_path);
				// 如果文件夹不存在则创建
				if (!dir.exists() && !dir.isDirectory()){ 
					dir.mkdir();
				}
				
				//保存文件 
				File localFile = new File(p_path, file_code + ext);// 指定文件名称后缀名  存原图
				file_path = "/assets/excel/"+ file_code + ext;
				
				if(ext.equals(".xls")){
					try {
						file.transferTo(localFile);
					}catch (Exception e) { 
						e.printStackTrace();
					} 
					
					//解析excel 文件
					
					map = new Excel_import().read_student(p_path+file_code + ext);
					
					if((map.get("back_code").toString()).equals("200")){
					
						//校验学号重复
						
						List<Document> list = (List<Document>) map.get("list");
						
						if(list.size()>0){
							for(int i=0;i<list.size();i++){
								String studentid = list.get(i).getString("studentid");
								
								Document doc = new Document();
								doc.put("schoolcode", schoolcode);
								doc.put("studentid", studentid);
								
								int num = schoolDB.query_count(DBbean.T_student, doc);
								
								if(num > 0){
									map.put("back_code", "700");
									map.put("studentid", list.get(i).getString("studentid"));
									
									return map;
								}
										
							}
							
							Document docs = new Document();
							docs.put("schoolcode", schoolcode); 
							Document school = schoolDB.query_it_one(DBbean.T_school, docs);
							
							String schoolname = school.getString("schoolname");
							
							Document sort = new Document();
							sort.put("passnum", 1); 
							Document pp = new Document();
							pp.put("state", 1); 
							List<Document> passlist = new SchoolDB().query_all_sort(DBbean.T_PASS, pp, sort);
							
							 
							
							//保存学生信息
							for(int i=0;i<list.size();i++){
								list.get(i).put("_id", UuidUtil.get32UUID());
								list.get(i).put("rank", 1);//等级
								list.get(i).put("score", 0);//积分
								list.get(i).put("star", 0);//星
								list.get(i).put("experience", 0);//经验
								
								list.get(i).put("pass", passlist.get(0).getString("pass"));
								list.get(i).put("passid", passlist.get(0).getString("_id"));
								list.get(i).put("passnum", passlist.get(0).getInteger("passnum")); 
								
								list.get(i).put("schoolcode", schoolcode);
								list.get(i).put("schoolname", schoolname);
								
								list.get(i).put("cdate", new Date());
								list.get(i).put("ctime", DateUtil.fromDate24H());
								list.get(i).put("state", 1);
								
								schoolDB.add_db_one(DBbean.T_student, list.get(i));
							}
							
							map.put("back_code", "200");
							
							
						}else{
							map.put("back_code", "150");//文件格式错误
						} 
					}
					
				}else{ 
					map.put("back_code", "100");//文件格式错误
				}
			}
			
		}
		 
		return map;
	}

	@Override
	public Map<String, Object> query_student(HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		int page = 1;
		if(request.getParameter("page")!= null){
			page = Integer.valueOf(request.getParameter("page"));
		}
		
		int state = 100;
		if(request.getParameter("state")!= null){
			state = Integer.valueOf(request.getParameter("state"));
		}
		
		String student_name = "";
		if(request.getParameter("student_name")!= null){
			student_name = request.getParameter("student_name");
		}
		
		String school_code = "";
		if(request.getParameter("school_code")!= null){
			school_code = request.getParameter("school_code");
		}
		
		int skip = 0;
		int limit = 50; 
		skip = (page-1)*limit;
		
		Document sort = new Document();
		sort.put("cdate", -1);
		
		Document search  = new Document();
		if(state!= 100){
			search.put("state", state);
		} 
		
		if(student_name!= null && !student_name.equals("")){
			Pattern pattern = Pattern.compile("^.*" + student_name + ".*$", Pattern.CASE_INSENSITIVE); 
			search.put("name", pattern);
		}
		
		if(school_code!= null && !school_code.equals("")){
			search.put("schoolcode", school_code);
		}
		 
		int count = schoolDB.query_count(DBbean.T_student, search);
		
		List<Document> list = schoolDB.query_db_page_sort(DBbean.T_student, search, skip, limit, sort);
		
		int page_num = page;//默认第一页 
		int page_count = 1;//默认总页数：1 
		if(count % limit == 0){
			page_count = count/limit;
		}else{
			page_count = (count/limit)+1;
		}
		
		map.put("page_num", page_num);
		map.put("page_count", page_count);
		map.put("count", count); 
		map.put("list", list);
		 
		return map;
	}

	@Override
	public int free_student(HttpServletRequest request) {
		 
		String sid = request.getParameter("sid");
		
		Document student = schoolDB.query_db_one(DBbean.T_student, sid);
		
		int state = 1-student.getInteger("state");
		
		student.put("state", state);
		
		schoolDB.replace_db_byid(DBbean.T_student, student, sid);
		
		return 200;
	}

	@Override
	public Document student_info(HttpServletRequest request) {
		String sid = request.getParameter("sid");
		
		Document ddd = schoolDB.query_db_one(DBbean.T_student, sid);
		 
		return ddd;
	}

	@Override
	public Map<String, Object> save_student(HttpServletRequest request) {
		
		String student_name = request.getParameter("student_name");
		String studentid = request.getParameter("studentid");
		String classname = request.getParameter("classname");
		String grade = request.getParameter("grade");
		String parent = request.getParameter("parent");
		String phone = request.getParameter("phone");
		String _id = request.getParameter("_id");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		int back_code = 0;
		
		Document doc = new Document();
		doc.put("studentid", studentid);
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("$ne", _id);
		doc.put("_id", m);
		
		int num = schoolDB.query_count(DBbean.T_student, doc);
		
		if(num <1){
			
			Document ddd = schoolDB.query_db_one(DBbean.T_student, _id);
			
			if(ddd != null){
				
				ddd.put("name", student_name);
				ddd.put("studentid", studentid);
				ddd.put("grade", grade);
				ddd.put("classname", classname);
				ddd.put("parent", parent);
				ddd.put("phone", phone);
				
				schoolDB.replace_db_byid(DBbean.T_student, ddd, _id);
				
				back_code = 200;
				
			}else{
				back_code = 100;
			}
			
		}else{
			back_code = 300;
		}
		 
		map.put("back_code", back_code); 
		return map;
	}

	@Override
	public Document setRobot(HttpServletRequest request) {
		
		Document map = new Document();
		
		String rid="";
		if(request.getParameter("rid")!= null){
			rid = request.getParameter("rid");
		} 
		String robotid="";
		if(request.getParameter("robotid")!= null){
			robotid=request.getParameter("robotid");
		}
		String schoolcode = "";
		if(request.getParameter("schoolcode")!= null){
			schoolcode = request.getParameter("schoolcode");
		}
		
		String backcode ="200";
		String backmsg ="";
		
		if(!robotid.equals("") && !schoolcode.equals("")){
			
			Document doc = new Document();
			doc.put("schoolcode", schoolcode); 
			
			Document school = schoolDB.query_it_one(DBbean.T_school, doc);
			
			if(school != null){
				
				if(school.getInteger("state") == 1){
					if(rid.equals("")){
						
						rid =DateUtil.getLocalByDate4();
						
						Document robot = new Document();
						robot.put("_id", rid);
						robot.put("robotid", robotid);
						robot.put("schoolcode", schoolcode);
						robot.put("schoolname", school.getString("schoolname"));
						
						robot.put("cdate", new Date());
						robot.put("ctime", DateUtil.fromDate24H());
						robot.put("state", 1);
						
						schoolDB.add_db_one(DBbean.T_robot, robot); 
						
						backcode ="200";
						backmsg ="设置成功！";
						
						map.put("rid", rid);
						map.put("robotid", robotid);
						map.put("schoolcode", schoolcode);
						map.put("schoolname", school.getString("schoolname"));
					}else{
						
						Document robot = schoolDB.query_db_one(DBbean.T_robot, rid);
						robot.put("robotid", robotid);
						robot.put("schoolcode", schoolcode);
						robot.put("schoolname", school.getString("schoolname"));
						
						schoolDB.replace_db_byid(DBbean.T_robot, robot, rid);
						
						backcode ="200";
						backmsg ="设置成功！";
						
						map.put("rid", rid);
						map.put("robotid", robotid);
						map.put("schoolcode", schoolcode);
						map.put("schoolname", school.getString("schoolname"));
						
					}
				}else{
					backcode ="400";
					backmsg ="您的学校已冻结，无法设置！";
				}
				 
			}else{
				backcode ="300";
				backmsg ="您输入的学校编号不存在，请正确输入您的学校编号！";
			}
		}else{
			backcode ="100";
			backmsg ="字段缺失";
		}
		 
		 map.put("backcode", backcode);
		 map.put("backmsg", backmsg);
		  
		
		return map;
	}

	@Override
	public Document studentLogin(HttpServletRequest request) {
		
		String rid="";
		if(request.getParameter("rid")!= null){
			rid = request.getParameter("rid");
		} 
		String robotid="";
		if(request.getParameter("robotid")!= null){
			robotid=request.getParameter("robotid");
		}
		String schoolcode = "";
		if(request.getParameter("schoolcode")!= null){
			schoolcode = request.getParameter("schoolcode");
		}
		
		String studentid = "";
		if(request.getParameter("studentid")!= null){
			studentid=request.getParameter("studentid"); 
		}
		 
		
		Document ddd = new Document(); 
		String backcode ="200";
		String backmsg ="";
		
		if(rid.equals("") || robotid.equals("") || schoolcode.equals("") ||studentid.equals("")){
			
			backcode ="100";
			backmsg ="抱歉，您登录失败了";
			
		}else{
			Document doc = new Document();
			doc.put("schoolcode", schoolcode);
			
			Document school = schoolDB.query_it_one(DBbean.T_school, doc);
			 
			if(school != null && school.getInteger("state") == 1){
				
				Document doc2 = new Document();
				doc2.put("studentid", studentid);
				doc2.put("schoolcode", schoolcode);
				
				String schoolname = school.getString("schoolname");
				
				Document student = schoolDB.query_it_one(DBbean.T_student, doc2);
				
				if(student!= null){
					
					if(student.getInteger("state") == 1){
						
						ddd.put("name", student.getString("name"));
						ddd.put("studentid", studentid);
						ddd.put("schoolcode", schoolcode);
						
						ddd.put("grade", student.getString("grade"));
						ddd.put("classname", student.getString("classname"));
						ddd.put("parent", student.getString("parent"));
						ddd.put("phone", student.getString("phone"));
						 
						
						ddd.put("rank", student.getInteger("rank"));//等级
						ddd.put("score", student.getInteger("score"));//积分
						ddd.put("star", student.getInteger("star"));//星
						ddd.put("experience", student.getInteger("experience"));//经验
						
						ddd.put("pass",    student.getString("pass"));
						ddd.put("passid",  student.getString("passid"));
						ddd.put("passnum", student.getInteger("passnum")); 
						
						ddd.put("schoolname", schoolname);
						ddd.put("sid", student.getString("_id"));
						
						backcode ="200";
						backmsg ="恭喜您，登录成功！";
						
						//登录日志
						
						String ip = Ip_tool.get_ip(request);
						
						Document log = new Document();
						log.put("_id", UuidUtil.getId());
						log.put("studentid", studentid);
						log.put("schoolcode", schoolcode);
						log.put("rid", rid);
						log.put("robotid", robotid);
						log.put("ip", ip);
						log.put("cdate", new Date());
						log.put("ctime", DateUtil.fromDate24H());
						
						schoolDB.add_db_one(DBbean.T_LOGIN_LOG, log);
						 
						
					}else{
						backcode ="500";
						backmsg ="您的账户已冻结，无法登录！";
					} 
					
				}else{
					backcode ="400";
					backmsg ="您的学号不存在，请重新说出您的学号！";
				}
				
			}else{
				backcode ="300";
				backmsg ="您的学校已冻结，无法登录！";
			}
		}
			
		
		
		
		ddd.put("backcode", backcode);
		ddd.put("backmsg", backmsg);
		
		System.out.println(ddd.toJson());
		  
		
		return ddd;
	}

	@Override
	public Document studentInfo(HttpServletRequest request) {
		
		String schoolcode = "";
		if(request.getParameter("schoolcode")!= null){
			schoolcode = request.getParameter("schoolcode");
		}
		
		String studentid = "";
		if(request.getParameter("studentid")!= null){
			studentid=request.getParameter("studentid"); 
		}
		
		Document ddd = new Document(); 
		String backcode ="200";
		String backmsg ="";
		
		Document doc = new Document();
		doc.put("schoolcode", schoolcode);
		
		Document school = schoolDB.query_it_one(DBbean.T_school, doc);
		
		Document doc2 = new Document();
		doc2.put("studentid", studentid);
		doc2.put("schoolcode", schoolcode);
		
		String schoolname = school.getString("schoolname");
		
		Document student = schoolDB.query_it_one(DBbean.T_student, doc2);
		
		ddd.put("name", student.getString("name"));
		ddd.put("studentid", studentid);
		ddd.put("schoolcode", schoolcode);
		
		ddd.put("grade", student.getString("grade"));
		ddd.put("classname", student.getString("classname"));
		ddd.put("parent", student.getString("parent"));
		ddd.put("phone", student.getString("phone"));
		 
		
		ddd.put("rank", student.getInteger("rank"));//等级
		ddd.put("score", student.getInteger("score"));//积分
		ddd.put("star", student.getInteger("star"));//星
		ddd.put("experience", student.getInteger("experience"));//经验
		
		ddd.put("pass",    student.getString("pass"));
		ddd.put("passid",  student.getString("passid"));
		ddd.put("passnum", student.getInteger("passnum")); 
		
		ddd.put("schoolname", schoolname);
		ddd.put("sid", student.getString("_id"));
		
		backcode ="200";
		backmsg ="成功！";
		 
		ddd.put("backcode", backcode);
		ddd.put("backmsg", backmsg); 
		
		return ddd;
	}
	
	
	@Override
	public Document studentpass(HttpServletRequest request){
		
		String schoolcode = "";
		if(request.getParameter("schoolcode")!= null){
			schoolcode = request.getParameter("schoolcode");
		}
		
		String studentid = "";
		if(request.getParameter("studentid")!= null){
			studentid=request.getParameter("studentid"); 
		}
		
		Document doc = new Document();
		doc.put("schoolcode", schoolcode);
		
		Document school = schoolDB.query_it_one(DBbean.T_school, doc);
		
		Document doc2 = new Document();
		doc2.put("studentid", studentid);
		doc2.put("schoolcode", schoolcode);
		
		String schoolname = school.getString("schoolname");
		
		Document student = schoolDB.query_it_one(DBbean.T_student, doc2);
		
		Document sort = new Document();
		sort.put("passnum", 1); 
		Document pp = new Document();
		pp.put("state", 1); 
		List<Document> passlist = new SchoolDB().query_all_sort(DBbean.T_PASS, pp, sort);
		
		Document ddd = new Document(); 
		String backcode ="200";
		String backmsg ="";
		ddd.put("name", student.getString("name"));
		ddd.put("studentid", studentid);
		ddd.put("schoolcode", schoolcode);
		
		ddd.put("grade", student.getString("grade"));
		ddd.put("classname", student.getString("classname"));
		ddd.put("parent", student.getString("parent"));
		ddd.put("phone", student.getString("phone"));
		 
		
		ddd.put("rank", student.getInteger("rank"));//等级
		ddd.put("score", student.getInteger("score"));//积分
		ddd.put("star", student.getInteger("star"));//星
		ddd.put("experience", student.getInteger("experience"));//经验
		
		ddd.put("pass",    student.getString("pass"));
		ddd.put("passid",  student.getString("passid"));
		ddd.put("passnum", student.getInteger("passnum")); 
		
		ddd.put("schoolname", schoolname);
		ddd.put("sid", student.getString("_id"));
		
		int next_exp = (int) GameUtil.countEXP(student.getInteger("rank")+1);
		
		//System.out.println(next_exp);
		
		ddd.put("next_exp", next_exp);
		
		backcode ="200";
		backmsg ="成功！";
		 
		ddd.put("backcode", backcode);
		ddd.put("backmsg", backmsg); 
		ddd.put("passlist", passlist); 
		 
		return ddd;
	}

	@Override
	public List<Document> query_advert() {
		
		Document doc = new Document();
		doc.put("state", 1);
		
		List<Document> list = schoolDB.query_db_all("c_advertise", doc);
		
		return list;
	}
	
	
	/**
	 * 排行榜
	 */
	@Override
	public Document query_sort(HttpServletRequest request){
		
		String sid = "";
		if(request.getParameter("sid")!= null){
			sid = request.getParameter("sid");
		}
		
		int kind = 1; 
		if(request.getParameter("kind")!= null){
			kind = Integer.valueOf(request.getParameter("kind"));
		}
		
		//String back_code = "0";
		
		Document back = new Document();
		
		Document student = schoolDB.query_db_one(DBbean.T_student, sid);
		 
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("$gt", student.getInteger("star"));
		
		Document doc1 = new Document();
		doc1.put("star", map); 
		
		Document doc2 = new Document();
		
		Document sort = new Document();
		sort.put("star", -1);
		
		if(kind == 1){
			//世界排名
			  
		}else{
			//全校排名
			doc2.put("schoolcode", student.getString("schoolcode"));
			doc1.put("schoolcode", student.getString("schoolcode"));
		}
		
		int mysort = schoolDB.query_count(DBbean.T_student, doc1);
		
		List<Document> sort_list = schoolDB.query_db_page_sort(DBbean.T_student, doc2, 0, 100, sort);
		
		back.put("mysort", mysort+1);
		back.put("sort_list", sort_list);
		 
		return back;
	}

	@Override
	public List<Document> query_pass() {
		
		Document doc = new Document();
		doc.put("state", 1);
		
		Document sort = new Document();
		sort.put("passnum", 1);
		
		List<Document> list = schoolDB.query_all_sort(DBbean.T_PASS, doc, sort);
		 
		return list;
	}

	@Override
	public int savepass(HttpServletRequest request) {
		
		String  passid = request.getParameter("passid");
		String  passname = request.getParameter("passname");
		int passnum = Integer.valueOf(request.getParameter("passnum"));
		int star = Integer.valueOf(request.getParameter("passstar"));
		
		int back = 100;
		
		if(passid.equals("")){ 
			//新增 
			Document doc = new Document();
			doc.put("passnum", passnum);
			
			int count = schoolDB.query_count(DBbean.T_PASS, doc);
			
			if(count < 1){
				
				Document pass = new Document();
				pass.put("_id", UuidUtil.getId());
				pass.put("pass",passname);
				pass.put("passnum",passnum);
				pass.put("star",star);
				pass.put("state",1);
				
				schoolDB.add_db_one(DBbean.T_PASS, pass);
				
				back = 200;
				
			}else{
				back = 300;
			}
		}else{
			
			Document doc = new Document();
			doc.put("passnum", passnum);
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("$ne", passid);
			doc.put("_id", map);
			
			int count = schoolDB.query_count(DBbean.T_PASS, doc);
			
			if(count < 1){
				Document pass = schoolDB.query_db_one(DBbean.T_PASS, passid);
				pass.put("pass",passname);
				pass.put("passnum",passnum);
				pass.put("star",star);
				
				schoolDB.replace_db_byid(DBbean.T_PASS, pass, passid);
				
				back = 200;
			}else{
				back = 300;
			} 
		}
		
		return back;
	}

	
	
	private Document initPage(HttpServletRequest req) {
		Document map=new Document();
		
		int page=StringUtil.toNum(req.getParameter("page"));
		int limit=StringUtil.toNum(req.getParameter("limit"));
		if(page<1){
			page=1;
		}
		if(limit<1){
			limit=30;
		}
		map.put("page", page);
		map.put("limit", limit);
		
		return map;
		
	}

	@Override
	public Document query(Document doc,int page,int limit) {
		// TODO Auto-generated method stub
		int sum=schoolDB.ajaxListNum(doc);
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", sum);
		resp.put("msg", "获取成功");
		List<Document> docs=schoolDB.query_db_page(DBbean.T_school, doc, page, limit);
		for(int i=0;i<docs.size();i++){
			if(docs.get(i).getInteger("state")==1){
				docs.get(i).put("status","有效");
			}
			else{
				docs.get(i).put("status","冻结");
			}
		}
		resp.put("data",docs);
		return resp;
	}
	
	public Document query_student(Document doc,int page,int limit) {
		// TODO Auto-generated method stub
		int sum=schoolDB.ajaxListNum_student(doc);
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", sum);
		resp.put("msg", "获取成功");
		List<Document> docs=schoolDB.query_student_page(DBbean.T_student, doc, page, limit);
		for(int i=0;i<docs.size();i++){
			if(docs.get(i).getInteger("state")==1){
				docs.get(i).put("status","有效");
			}
			else{
				docs.get(i).put("status","冻结");
			}
		}
		resp.put("data",docs);
		return resp;
	}

	@Override
	public int delete_student(HttpServletRequest request) {
		String sid = request.getParameter("sid");
		
		
		
		schoolDB.deleteSdu(sid);
		
		return 200;
	}

	@Override
	public int deleteThisPass(HttpServletRequest request) {
		
String passid = request.getParameter("passid");
		
		
		
		schoolDB.deleteThisPass(passid);
		
		return 200;
		
	}

	@Override
	public int reset_student(HttpServletRequest request) {
		
		String sid = request.getParameter("sid");
		
		Document student = schoolDB.query_db_one(DBbean.T_student, sid);
		
		Document sort = new Document();
		sort.put("passnum", 1); 
		Document pp = new Document();
		pp.put("state", 1); 
		List<Document> passlist = new SchoolDB().query_all_sort(DBbean.T_PASS, pp, sort);
		
		student.put("rank", 1);//等级
		student.put("score", 0);//积分
		student.put("star", 0);//星
		student.put("experience", 0);//经验
		
		student.put("pass", passlist.get(0).getString("pass"));
		student.put("passid", passlist.get(0).getString("_id"));
		student.put("passnum", passlist.get(0).getInteger("passnum"));
		
		schoolDB.replace_db_byid(DBbean.T_student, student, sid);
		 
		return 200;
	}



}
