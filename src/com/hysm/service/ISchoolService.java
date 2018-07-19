package com.hysm.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;

public interface ISchoolService {

	public Map<String,Object> query_school(int state,int page,String schoolname);
	
	public Map<String, Object> save_school(HttpServletRequest request);
	 
	public Document school_info(HttpServletRequest request);
	
	public int free_school(HttpServletRequest request);
	
	public Map<String, Object> upload_student(HttpServletRequest request);
	
	public Map<String, Object> query_student(HttpServletRequest request);
	
	public int free_student(HttpServletRequest request);
	
	public Document student_info(HttpServletRequest request);
	
	public Map<String, Object> save_student(HttpServletRequest request);
	
	
	public Document setRobot(HttpServletRequest request);
	
	public Document studentLogin(HttpServletRequest request);
	
	public Document studentInfo(HttpServletRequest request);
	
	
	public Document studentpass(HttpServletRequest request);
	
	
	public List<Document> query_advert();
	
	
	public Document query_sort(HttpServletRequest request);
	
	
	public List<Document> query_pass();
	
	
	public int savepass(HttpServletRequest request);
	
	public Document query(Document doc,int page,int limit);

	public Document query_student(Document doc, int pag, int limi);

	public int delete_student(HttpServletRequest request);

	public int deleteThisPass(HttpServletRequest request);
	
	public int reset_student(HttpServletRequest request);
	
	
	
}
