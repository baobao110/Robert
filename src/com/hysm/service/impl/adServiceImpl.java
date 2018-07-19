package com.hysm.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hysm.bean.DBbean;
import com.hysm.db.AdvertiseDB;
import com.hysm.db.MessageDB;
import com.hysm.service.adService;
import com.hysm.util.DateUtil;
import com.hysm.util.StringUtil;

@Service("adService")
public class adServiceImpl implements adService{
	
	@Autowired
	private AdvertiseDB ad;

	@Override
	public int save(Document doc) {
		// TODO Auto-generated method stub
		long ctime=System.currentTimeMillis();
		String id=DateUtil.getLocalByDate4()+StringUtil.getRandomString(3)+"0"+doc.getString("merid");
		
		doc.put("_id", id);
		doc.put("ctime",ctime);
		doc.put("ctimestr",DateUtil.fromDate24H());
		doc.put("state", 1);
		return ad.insertMess(doc);
	}
	
	public void  change(Document doc) {
		// TODO Auto-generated method stub
		long ctime=System.currentTimeMillis();
		String id=doc.getString("_id");
		
		/*doc.put("_id", id);*/
		doc.put("ctime",ctime);
		doc.put("ctimestr",DateUtil.fromDate24H());
		doc.put("state", 1);
		ad.replace_db_status(DBbean.T_advertise, doc, id);;
	}
	
	public List<Document> getALL(String db_name,Document doc){
		List<Document> list=ad.query_db_all(db_name,doc);
		return list;
		
	}
	
	public Document select_ById(String db_name, String _id) {
		// TODO Auto-generated method stub
		
		Document doc=new Document();
		doc.put("_id",_id);
		return ad.query_it_one(db_name, doc);
	}
	
	public void update(String db_name, Document doc,String _id) {
		// TODO Auto-generated method stub
		ad.replace_db_status(db_name, doc, _id);
	}

	/*@Override
	public Document select_ByTitle(String db_name, String title) {
		Document doc=new Document();
		doc.put("title",title);
		Document docu=ad.query_it_one(db_name, doc);
		return docu;
	}*/
	
	@Override
	public List<Document> select_ByTitle(String db_name, String title) {
		Document doc=new Document();
		doc.put("title",title);
		/*List<Document> list=(List<Document>) ad.query_it_one(db_name, doc);*/
		List<Document> list=ad.query_regrex(db_name, doc);
	/*	System.out.println("list"+list);*/
		return list;
	}

	@Override
	public Document query(Document doc, int pag, int limi) {
		// TODO Auto-generated method stub
		int sum=ad.ajaxListNum_student(doc);
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", sum);
		resp.put("msg", "获取成功");
		List<Document> docs=ad.query_advertise_page(DBbean.T_advertise, doc, pag, limi);
		for(int i=0;i<docs.size();i++){
			if(docs.get(i).getInteger("state")==1){
				docs.get(i).put("status","在使用中");
			}
			else{
				docs.get(i).put("status","暂停使用");
			}
			docs.get(i).put("time", docs.get(i).getString("ctimestr"));
		}
		resp.put("data",docs);
		return resp;
	}

	
	

}
