package com.hysm.service.impl;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.hysm.bean.DBbean;
import com.hysm.db.AdvertiseDB;
import com.hysm.db.MusicDB;
import com.hysm.service.musicService;
import com.hysm.util.DateUtil;
import com.hysm.util.StringUtil;


@Service("musicService")
public  class musicServiceImpl implements musicService {
	
	@Autowired
	private MusicDB mc;
	
	@Override
	public int save(Document doc) {
		// TODO Auto-generated method stub
		long ctime=System.currentTimeMillis();
		String id=DateUtil.getLocalByDate4()+StringUtil.getRandomString(3)+"0"+doc.getString("merid");
		
		doc.put("_id", id);
		doc.put("ctime",ctime);
		doc.put("ctimestr",DateUtil.fromDate24H());
		doc.put("state", 1);
		return mc.insertMess(doc);
	}

	@Override
	public Document query(Document doc, int pag, int limi) {
		// TODO Auto-generated method stub
		int sum=mc.ajaxListNum(doc);
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", sum);
		resp.put("msg", "获取成功");
		List<Document> docs=mc.query_page(DBbean.T_MUSIC, doc, pag, limi);
		for(int i=0;i<docs.size();i++){
			if(docs.get(i).getInteger("state")==1){
				docs.get(i).put("status","在使用中");
			}
			else{
				docs.get(i).put("status","暂停使用");
			}
		}
		resp.put("data",docs);
		return resp;
	}

	@Override
	public Document select_ById(String db_name, String _id) {
		// TODO Auto-generated method stub
		Document doc=new Document();
		doc.put("_id",_id);
		return mc.query_it_one(db_name, doc);
	}

	@Override
	public void update(String db_name, Document doc, String _id) {
		// TODO Auto-generated method stub
		mc.replace_db_status(db_name, doc, _id);
	}

	@Override
	public JSONArray findAllmusic() {
		// TODO Auto-generated method stub
		return mc.findAllmusic();
	}

}
