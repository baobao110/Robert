package com.hysm.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import com.hysm.bean.DBbean;
import com.hysm.db.mongo.MongoUtil;
import com.hysm.model.Admin;
import com.hysm.util.MD5;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

@Component
public class AdminDB {
	
	private MongoUtil mu=MongoUtil.getThreadInstance();

	public void inertOneUser(String db_name,Document doc) {
		 
		mu.insertOne(db_name, doc);
		
	}

	public List<Document> query_db_all(String db_name,Document doc){
		 
		List<Document> list = new ArrayList<Document>();
		
		
		 list= mu.find(db_name,doc);
		
		return list;
		 
	}
	
	public Document query_it_one(String db_name, Document doc) {

		Document back = mu.findOne(db_name, doc);
		return back;
	}
	
	
	
/*	public static void main(String[] args) {
		
		System.out.println(new AdminDB().query_db_all("c_manager")); 
		
		Document docv= new Document();
		docv.put("_id", System.currentTimeMillis()+"");
		docv.put("asdfg", System.currentTimeMillis()+"");
		
		new AdminDB().inertOneUser(docv);
	}*/
	
	public void replace_db_status(String db_name, Document doc, Object _id) {
		mu.replaceOne(db_name, Filters.eq("_id", _id), doc);
	}
	
	public Document query_db_one(String db_name,Object _id){ 
		Document doc = mu.findOne(db_name, Filters.eq("_id",_id));
		
		return doc;
	}
	
	public void add_db_one(String db_name,Document doc){
		
		mu.insertOne(db_name, doc);;
	}
	
	public void insert_one(String db_name,Document doc){
		mu.insertOne(db_name, doc);
	}
	
	public List<Document> regrex(String db_name,Document doc){
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		Pattern pattern =null;
		
		if (doc.get("name") != null) {
			 pattern = Pattern.compile("^.*" + doc.getString("name")
					+ ".*$", Pattern.CASE_INSENSITIVE);
			 list.add(Filters.regex("name", pattern));
		}
		list.add(Filters.eq("status",doc.getString("status")));
		

		return mu.find(db_name, Filters.and(list));
	}
		 
	
	public int ajaxListNum(Document doc) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		
		list.add(Filters.eq("status",1));
		
		
		if (doc.get("name") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("name")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("name", pattern));
		}
		
		return (int) mu.count(DBbean.T_user, Filters.and(list));
		
	}
	
	public List<Document> query_db_page(String db_name,Document doc,int page,int limit){
		 
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();
		
		if (doc.get("name") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("name")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("name", pattern));
		}
		
		list.add(Filters.eq("status",1));

		int pn = page;
		int ps = limit;


		return mu.findLimitSort(DBbean.T_user,
				Filters.and(list), (pn - 1) * ps, ps, null);
		 
	}

}
