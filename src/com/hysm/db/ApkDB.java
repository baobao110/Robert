package com.hysm.db;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.hysm.bean.DBbean;
import com.hysm.db.mongo.MongoUtil;
import com.mongodb.client.model.Filters;


@Component
public class ApkDB {
	
	private MongoUtil mu=MongoUtil.getThreadInstance();
	
	
	
	//查询数据
	public List<Document> query_db_all(String db_name){
		 
		List<Document> list = new ArrayList<Document>();
		
		
		 list= mu.find(db_name,null);
		
		return list;
		 
	}
	
	
	//删除数据
	public void delete_db_one(String db_name, Object _id) {
		mu.deleteMany(db_name, Filters.eq("_id", _id));
	}
	
	//增加一条数据
	public void add_db_one(String db_name, Document doc) {

		mu.insertOne(db_name, doc);
	}
	
	
}
