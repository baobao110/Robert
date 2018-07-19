package com.hysm.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import com.hysm.bean.DBbean;
import com.hysm.db.mongo.MongoUtil;
import com.mongodb.client.model.Filters;

@Component
public class AdvertiseDB {
	
	private MongoUtil mu=MongoUtil.getThreadInstance();
	
	public int insertMess(Document doc) {
		try{
		 mu.insertOne(DBbean.T_advertise, doc);
		 return 1;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
		
	}
	
	public List<Document> query_db_all(String db_name,Document doc){
		 
		List<Document> list = new ArrayList<Document>();
		
		
		 list= mu.find(db_name,doc);
		
		return list;
		 
	}
	
	public List<Document> query_regrex(String db_name, Document doc) {
		
		mu.clear();
		List<Bson> list = new ArrayList<Bson>();
		/*System.out.println("title"+doc.getString("title"));*/
		Pattern pattern = Pattern.compile("^.*" +doc.getString("title")
				+ ".*$", Pattern.CASE_INSENSITIVE);
		list.add( Filters.regex("title",pattern));
		list.add(Filters.eq("state", 1));
		
		return mu.find(db_name, Filters.and(list));

	}
	
	
	public Document query_it_one(String db_name, Document doc) {

		Document back = mu.findOne(db_name, doc);
		return back;
	}
	
	public void replace_db_status(String db_name, Document doc, String _id) {
		mu.replaceOne(db_name, Filters.eq("_id", _id), doc);
	}

	public int ajaxListNum_student(Document doc) {
		// TODO Auto-generated method stub
		
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();
		if (doc.get("title") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("title")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("title", pattern));
		}
		
		list.add(Filters.eq("state",1));
		
		return (int) mu.count(DBbean.T_advertise, Filters.and(list));
	}

	public List<Document> query_advertise_page(String  db_name, Document doc,
			int pag, int limi) {
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();
		if (doc.get("title") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("title")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("title", pattern));
		}
		
		list.add(Filters.eq("state",1));

		int pn = pag;
		int ps = limi;


		return mu.findLimitSort(db_name,
				Filters.and(list), (pn - 1) * ps, ps, null);
	}
}
