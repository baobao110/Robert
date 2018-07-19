package com.hysm.service.impl;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;










import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hysm.db.MessageDB;
import com.hysm.db.SchoolDB;
import com.hysm.db.UserDB;
import com.hysm.game.GameUtil;
import com.hysm.game.GameVO;
import com.hysm.service.IMessageService;
import com.hysm.util.DateUtil;
import com.hysm.util.StringUtil;
@Service("messageService")
public class IMessageServiceImpl implements IMessageService {
	
	
	@Autowired
	private MessageDB mdb;
	
	@Autowired
	private UserDB udb;
	
	@Autowired
	private SchoolDB schoolDB;

	@Override
	public int saveMessage(Document mess) {
	
		long ctime=System.currentTimeMillis();
		if(mess.get("_id")!=null){
			
			mess.put("mtime",ctime);
			Document ymess=mdb.queryMessById(mess.getString("_id"));
			ymess.putAll(mess);
			if(ymess.get("orderby")!=null){
				ymess.put("orderby",Integer.valueOf( String.valueOf(ymess.get("orderby"))));
			}else{
				ymess.put("orderby",0);
			}
			return mdb.replaceMess(ymess);
		}else{
			
			String id=DateUtil.getLocalByDate4()+StringUtil.getRandomString(3)+"0"+mess.getString("merid");
			
			mess.put("_id", id);
			mess.put("ctime",ctime);
			mess.put("mtime",ctime);
			mess.put("ctimestr",DateUtil.fromDate24H());
			
			mess.put("state", 1);
			if(mess.get("orderby")!=null){
				mess.put("orderby",Integer.valueOf( String.valueOf(mess.get("orderby"))));
			}else{
				mess.put("orderby",0);
			}
			
			return mdb.insertMess(mess);
		}
		
		
		
	}

	@Override
	public Document ajaxMessageList(HttpServletRequest req) {

		Document map=initPage(req);
		String likename=req.getParameter("likename");
		if(StringUtil.bIsNotNull(likename)){
			map.put("likename", likename);
		}
		
		String s=req.getParameter("state");
		int state=99;
		if(StringUtil.bIsNotNull(s)){
			
			state=StringUtil.toNum(s);
		}
		map.put("state", state);
		
		int rc=mdb.ajaxMessageListNum(map);
		
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", rc);
		resp.put("msg", "获取成功");
		List<Document> docs=mdb.ajaxMessageList(map);
		resp.put("data",docs);
		
		return resp;
	
	}
	
	/**
	 * 初始化分页查询条件
	 * @param req
	 * @return
	 */
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
	public Document queryMessById(String id) {
		// TODO Auto-generated method stub
		return mdb.queryMessById(id);
	}

	@Override
	public Document queryMessDetailById(String id) {
		try{
		Document mess=mdb.queryMessById(id);
		
	
		
		
		
		return mess;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
		
	}



	private Document createNewUM(String id,String messid,String uid,long ctime,String ctimestr,int isRead) {
		Document  userMsg=new Document();
		 userMsg.put("_id", id);
		 userMsg.put("messid", messid);
		 userMsg.put("uid", uid);
		 
		 userMsg.put("ctime",ctime );
		 userMsg.put("ctimestr",ctimestr);
		 userMsg.put("isRead",isRead);//0未阅读1已阅读
		 userMsg.put("isHY",0);//未加入会员
		 
		 userMsg.put("isGZ",0);//未关注
		 
		 userMsg.put("isSHARE",0);//未分享
		 
		 userMsg.put("isZAN",0);//未点赞1点赞2感兴趣3有点意思4拉黑 5收藏
		 
		 userMsg.put("isFLAG",0);//未标记1蓝色标记 2红色标记 3黄色标记
		 
		 userMsg.put("hadMoney",0);//未获得金额，>0 ，获得的金额(单位:分)
		 return userMsg;
	}

	@Override
	public JSONArray ajaxKcTitleList() {
		// TODO Auto-generated method stub
		return mdb.ajaxKcTitleList();
	}

	@Override
	public void updateMessState(String id, int state) {


		mdb.updateMessState(id,state);
		
	}


	@Override
	public int saveTM(Document tm) {
		//1商家消息2用户消息
		long ctime=System.currentTimeMillis();
		if(tm.get("_id")!=null){
			tm.put("mtime",ctime);
			Document ytm=mdb.queryTmByid(tm.getString("_id"));
			ytm.putAll(tm);
			
			return mdb.replaceTM(ytm);
		}else{
			
			String id="tm"+DateUtil.getLocalByDate4()+StringUtil.getRandomString(3);
			
			tm.put("_id", id);
			tm.put("ctime",ctime);
			tm.put("mtime",ctime);
			tm.put("ctimestr",DateUtil.fromDate24H());
			
			tm.put("state", 1);
			
			
			return mdb.insertTM(tm);
		}
		
		
		
	}

	@Override
	public Document ajaxTmList(HttpServletRequest req) {


		Document map=initPage(req);
		String likename=req.getParameter("likename");
		/*try {
			likename=new String(likename.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String question=req.getParameter("question");
		/*try {
			question=new String(question.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		if(StringUtil.bIsNotNull(likename)){
			map.put("likename", likename);
		}
		if(StringUtil.bIsNotNull(question)){
			map.put("question", question);
		}
		String kcid=req.getParameter("kcid");
		if(StringUtil.bIsNotNull(kcid)){
			map.put("kcid", kcid);
		}
		
		
		String s=req.getParameter("state");
		int state=99;
		if(StringUtil.bIsNotNull(s)){
			
			state=StringUtil.toNum(s);
		}
		map.put("state", state);
		int rc=mdb.ajaxTMListNum(map);
		
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", rc);
		resp.put("msg", "获取成功");
		List<Document> docs=mdb.ajaxTMList(map);
		resp.put("data",docs);
		
		return resp;
	
	
	}

	@Override
	public Document ajaxOneKcInfo(String kcid) {
		Document kc=mdb.queryMessById(kcid);
		List<Document> tm=mdb.queryTmsByKcId(kcid);
		if(kc!=null&&tm!=null){
			kc.put("questionlist", tm);
		}
		
		return kc;
	}

	@Override
	public JSONArray ajaxTestQuestions(int num) {
		try{
		MessageDB mdb=new MessageDB();
		int count=mdb.countAllQuestions();
		
		Set<Integer> shu = new HashSet<Integer>();
        
        while(shu.size()<num){
        	int i=(int) (Math.random() * count);
        	shu.add(i);
        	if(shu.size()==count){
        		break;
        	}
        }
        
        
     	JSONArray all=new JSONArray();
      
       for(Integer oneindex:shu){
    	   
    	   Document one=mdb.queryTMByIndex(oneindex);
    	   all.add(new JSONObject(one));
       }
       
       return all;
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return null;
	}

	@Override
	public void updateTMState(String id, int state) {
		// TODO Auto-generated method stub
		mdb.updateTMState(id,state);
	}

	@Override
	public void deleteTm(String id) {
		// TODO Auto-generated method stub
		mdb.deleteTm(id);
	}

	@Override
	public Document queryTmByid(String id) {
		// TODO Auto-generated method stub
		return mdb.queryTmByid(id);
	}

	@Override
	public int saveJD(Document mess) {

		//1商家消息2用户消息
		long ctime=System.currentTimeMillis();
		if(mess.get("_id")!=null){
			
			mess.put("mtime",ctime);
			Document ymess=mdb.queryJdById(mess.getString("_id"));
			ymess.putAll(mess);
			return mdb.replaceJD(ymess);
		}else{
			
			String id="m"+DateUtil.getLocalByDate4()+StringUtil.getRandomString(3);
			
			mess.put("_id", id);
			mess.put("ctime",ctime);
			mess.put("mtime",ctime);
			mess.put("ctimestr",DateUtil.fromDate24H());
			
			mess.put("state", 1);
			
			
			return mdb.insertJD(mess);
		}
		
		
		
	
	}

	@Override
	public Document ajaxJdList(HttpServletRequest req) {


		Document map=initPage(req);
		
		String likename=req.getParameter("likename");
		if(StringUtil.bIsNotNull(likename)){
			map.put("likename", likename);
		}
		
		String s=req.getParameter("state");
		int state=99;
		if(StringUtil.bIsNotNull(s)){
			
			state=StringUtil.toNum(s);
		}
		map.put("state", state);
		int rc=mdb.ajaxJdListNum(map);
		
		Document resp=new Document();
		resp.put("code", 0);
		resp.put("count", rc);
		resp.put("msg", "获取成功");
		List<Document> docs=mdb.ajaxJdList(map);
		resp.put("data",docs);
		
		return resp;
	}

	@Override
	public Document ajaxOneJDByid(String id) {
		// TODO Auto-generated method stub
		return mdb.queryJdById(id);
	}

	@Override
	public void updateJdState(String id, int state) {
		// TODO Auto-generated method stub

		mdb.updateJdState(id,state);
	}

	@Override
	public Document countNum() {
		//学习课题数量
		int num1=mdb.countAllKC();
				//题目数量
		int num2=mdb.countAllQuestions();
		
		int num3 = schoolDB.query_count("s_school", null);
		
		int num4 = schoolDB.query_count("s_student", null);
		
		Document doc=new Document();
		doc.put("num1", num1);
		doc.put("num2", num2);
		doc.put("num3", num3);
		doc.put("num4", num4);
		return doc;
	}

	@Override
	public  void addJDres(GameVO game) {
		
		
		game.stop(mdb);
		
		
		
	    try {
			Thread.sleep(2000);
			   GameUtil.removeGame(game);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	 
	}

	@Override
	public void deleteKC(String id) {
		// TODO Auto-generated method stub
		mdb.deleteKC(id);
	}
	
}
