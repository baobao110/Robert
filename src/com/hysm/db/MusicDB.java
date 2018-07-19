package com.hysm.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.hysm.bean.DBbean;
import com.hysm.db.mongo.MongoUtil;
import com.mongodb.client.model.Filters;

@Component
public class MusicDB {
	
	private MongoUtil mu=MongoUtil.getThreadInstance();
	
	public int insertMess(Document doc) {
		try{
		 mu.insertOne(DBbean.T_MUSIC, doc);
		 return 1;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
		
	}

	public int ajaxListNum(Document doc) {
		// TODO Auto-generated method stub
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();
		
		if (doc.get("title") != null) {
			Pattern pattern = Pattern.compile("^.*" + doc.getString("title")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("title", pattern));
		}
		
		list.add(Filters.eq("state",1));
		
		return (int) mu.count(DBbean.T_MUSIC, Filters.and(list));

	}

	public List<Document> query_page(String db_name, Document doc,
			int pag, int limi) {
		// TODO Auto-generated method stub
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

	public Document query_it_one(String db_name, Document doc) {
		// TODO Auto-generated method stub

		Document back = mu.findOne(db_name, doc);
		return back;
	}

	public void replace_db_status(String db_name, Document doc, String _id) {
		// TODO Auto-generated method stub
		mu.replaceOne(db_name, Filters.eq("_id", _id), doc);
	}

	public JSONArray findAllmusic() {
		// TODO Auto-generated method stub
		List<Bson> all=new ArrayList<>();
		all.add(Filters.eq("state", 1));
		all.add(Filters.exists("mp3"));
		
		
		
		return mu.findJA(DBbean.T_MUSIC, Filters.and(all), null);
	}
}
