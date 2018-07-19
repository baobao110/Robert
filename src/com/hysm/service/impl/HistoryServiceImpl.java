package com.hysm.service.impl;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hysm.db.HistroryDB;
import com.hysm.service.HistoryService;
import com.hysm.util.StringUtil;

@Service("HistoryService")
public class HistoryServiceImpl implements HistoryService{
	
	@Autowired
	private HistroryDB history;

	@Override
	public List<Document> query_db_all(String db_name, Document doc) {
		// TODO Auto-generated method stub
		return history.query_db_all(db_name, doc);
	}

	@Override
	public Document ajaxJdList(HttpServletRequest req) {
		
		Document map=initPage(req);
		String likename=(String)req.getParameter("likename");
	/*	try {
			likename=new String(likename.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*System.out.println(likename);*/
		if(StringUtil.bIsNotNull(likename)){
			map.put("username", likename);
		}
		
		String s=req.getParameter("state");
		int state=99;
		if(StringUtil.bIsNotNull(s)){
			
			state=StringUtil.toNum(s);
		}
		/*map.put("state", state);*/
		
		int rc=history.ajaxMessageListNum(map);
		
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", rc);
		resp.put("msg", "获取成功");
		List<Document> docs=history.ajaxMessageList(map);
	/*	System.out.println(docs);*/
		for(int i=0;i<docs.size();i++){
			long t=docs.get(i).getLong("usertime");
			Date date=new Date(t);
			String time=new SimpleDateFormat("yyyy-MM-dd").format(date);
			docs.get(i).put("time",time);
			
		}
		resp.put("data",docs);
		
		return resp;
	
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


}
