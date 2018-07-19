package com.hysm.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
 

import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import com.hysm.bean.DBbean;
import com.hysm.db.mongo.MongoUtil;
import com.hysm.util.UuidUtil;
import com.mongodb.client.model.Filters;

@Component
public class SchoolDB {

	MongoUtil mu = MongoUtil.getThreadInstance();
	
	/**
	 * 新增一条记录
	 */
	public void add_db_one(String db_name,Document doc){
		
		mu.insertOne(db_name, doc);
	}
	
	/**
	 * 修改一条记录
	 * @param db_name
	 * @param doc
	 * @param _id
	 */
	public void replace_db_byid(String db_name,Document doc,String _id){
		mu.replaceOne(db_name, Filters.eq("_id", _id), doc);
	}
	
	/**
	 * 删除一条记录
	 */
	public void delete_db_one(String db_name,String _id){
		mu.deleteMany(db_name, Filters.eq("_id", _id));
	}
	
	/**
	 * 查询一条记录根据_id
	 * @param db_name
	 * @param _id
	 * @return
	 */
	
	public Document query_db_one(String db_name,String _id){ 
		Document back = mu.findOne(db_name, Filters.eq("_id", _id));
		
		return back;
	}
	
	/**
	 * 查询一条数据
	 * @param db_name
	 * @param doc
	 * @return
	 */
	public Document query_it_one(String db_name,Document doc){
		
		Document back = mu.findOne(db_name, doc); 
		return back;
	}
	
	/**
	 * 
	 * @param db_name
	 * @return
	 */
	public Document get_one(String db_name){
		
		Document back = mu.findOne(db_name, null); 
		return back;
	}
	
	/**
	 * 查询全部
	 * @param db_name
	 * @param doc
	 * @return
	 */
	public List<Document> query_db_all(String db_name,Document doc){
		 
		return mu.find(db_name, doc);
		 
	}
	 
	
	public int query_count(String db_name,Document doc){
		
		long count = mu.count(db_name, doc);
		
		return (int)count;
	}
	
	
	public List<Document> query_db_page(String db_name,Document doc,int page,int limit){
		 
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

	/*	int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		*/
		
		if (doc.get("schoolname") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("schoolname")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("schoolname", pattern));
		}
		
		if(doc.getInteger("state")==1){
			list.add(Filters.eq("state",1));
		}
		else{
			if(doc.getInteger("state")==-99){
				list.add(Filters.eq("state",0));
			}
		}

		int pn = page;
		int ps = limit;


		return mu.findLimitSort(DBbean.T_school,
				Filters.and(list), (pn - 1) * ps, ps, null);
		 
	}
	
	public List<Document> query_student_page(String db_name,Document doc,int page,int limit){
		 
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

	/*	int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		*/
		
		if (doc.get("student_name") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("student_name")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("name", pattern));
		}
		
		if (doc.get("school_code") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("school_code")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("schoolcode", pattern));
		}
		
		if(doc.getInteger("state")==1){
			list.add(Filters.eq("state",1));
		}
		else{
			if(doc.getInteger("state")==-99){
				list.add(Filters.eq("state",0));
			}
		}

		int pn = page;
		int ps = limit;


		return mu.findLimitSort(db_name,
				Filters.and(list), (pn - 1) * ps, ps, null);
		 
	}
	
	
	
	public List<Document> query_db_page_sort(String db_name,Document doc,int page,int limit,Document sort){
		 
		return mu.findLimitSort(db_name, doc, page, limit, sort);
		 
	}
	
	public List<Document> query_all_sort(String db_name,Document doc,Document sort){
		 
		return  mu.findSort(db_name,  doc, sort); 
		 
	}
	
	
	public String query_school_code(){
		
		long count = mu.count("s_school", null);
		
		int num = (int)(count+1);
		
		String code = num+"";
		
		if(code.length()==1){
			code = "00000"+code;
		}else if(code.length()==2){
			code = "0000"+code;
		}else if(code.length()==3){
			code = "000"+code;
		}else if(code.length()==4){
			code = "00"+code;
		}else if(code.length()==5){
			code = "0"+code;
		}  
		
		return code;
	}
	
	
	public int ajaxListNum(Document doc) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		/*int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		*/
		/*System.out.println("schoolname"+ doc.getString("schoolname"));*/
		
		if(doc.getInteger("state")==1){
			list.add(Filters.eq("state",1));
		}
		else{
			if(doc.getInteger("state")==-99){
				list.add(Filters.eq("state",0));
			}
		}
		
		if (doc.get("schoolname") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("schoolname")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("schoolname", pattern));
		}
		
		return (int) mu.count(DBbean.T_school, Filters.and(list));
		
	}
	
	public int ajaxListNum_student(Document doc) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		/*int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		*/
		/*System.out.println("schoolname"+ doc.getString("schoolname"));*/
		
		if(doc.getInteger("state")==1){
			list.add(Filters.eq("state",1));
		}
		else{
			if(doc.getInteger("state")==-99){
				list.add(Filters.eq("state",0));
			}
		}
		
		if (doc.get("student_name") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("student_name")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("name", pattern));
		}
		if (doc.get("school_code") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("school_code")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("schoolcode", pattern));
		}
		
		return (int) mu.count(DBbean.T_student, Filters.and(list));
		
	}

	public void deleteSdu(String sid) {
		mu.deleteMany(DBbean.T_student, Filters.eq("_id", sid));
		
	}

	public void deleteThisPass(String passid) {
		mu.deleteMany(DBbean.T_PASS, Filters.eq("_id", passid));
		
	}
}
