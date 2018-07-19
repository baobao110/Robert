package com.hysm.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hysm.db.MessageDB;

@SuppressWarnings("serial")
public class GameVO implements Serializable{
	
	private JSONObject data;
	
	private int num=5;
	private int limittime=10;//每题限时
	private int eveScore=10;//每题可获得积分(获胜者)
	private int exp=10;//每题可获得经验experience
	private int star=1;//每题可获得星(关卡)
	
	
	
	private int onescore=200;//每题全部得分
	
	private String scoreRole="2:max|%|min:1";//每题得分规则,1秒以内满分，其他按比例
	
	
	
	
	


	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getLimittime() {
		return limittime;
	}

	public void setLimittime(int limittime) {
		this.limittime = limittime;
	}

	public int getOnescore() {
		return onescore;
	}

	public void setOnescore(int onescore) {
		this.onescore = onescore;
	}

	public String getScoreRole() {
		return scoreRole;
	}

	public void setScoreRole(String scoreRole) {
		this.scoreRole = scoreRole;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	

	

	public int getEveScore() {
		return eveScore;
	}

	public void setEveScore(int eveScore) {
		this.eveScore = eveScore;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public boolean hadItems() {
		if(this.data.get(GameUtil.KEY_ITEMS)!=null){
			return true;
		}
		return false;
	}

	public void setItems(JSONArray items) {
		// TODO Auto-generated method stub
		
		
		if(this.data.get(GameUtil.KEY_ITEMS)==null){
			
			this.data.put(GameUtil.GAMESTATE, GameUtil.status[1]);
			this.data.put(GameUtil.KEY_ITEMS, items);
		}
	}

	
	
	public synchronized  void sendUserRes(String uid, int index, String choose,long etime) {
		
		
		JSONArray items=this.data.getJSONArray(GameUtil.KEY_ITEMS);
		
		JSONObject one=items.getJSONObject(index-1);
		JSONObject ress=one.getJSONObject(GameUtil.userRes);
		if(ress!=null&&uid!=null){
			JSONObject myres=ress.getJSONObject(uid);
			
			if(myres!=null&&!myres.containsKey("etime")){
				long stime=myres.getLongValue("stime");

				
				String an=one.getString("answer");
				int sc=0;
				if(an.equals(choose)){
					//正确
					long cha=etime-stime;
					
					if(cha<=1500){
						
						sc=200;
						
					}else{
						int all=limittime*1000;
						
						if(all-cha>=0){
							
							sc=(int)((all-cha)*onescore/all);
						}else{
							
							sc=1;
						}
					}
					
				}
				
				myres.put("sc", sc);
				myres.put("etime", etime);
				myres.put("choose", choose);
				myres.put("onestate", GameUtil.ONEITEM_STATE[1]);//已答题
				this.addScore(sc,uid);
				
				//ress=this.addRes(myres,index,uid);
				
				ress.put(uid, myres);
				one.put(GameUtil.userRes,ress);
				this.updateItems(items);
			}
			
			
			
			if(this.checkItemRes(ress)){
				//有两个答案
				//该题目结束
				one.put("hadtask", 1);
			}
			
			
			this.updateItems(items);
			
			
			if(index==items.size()&&one.containsKey("hadtask")){
				
				
				//全部结束
				this.finished();
				
			}
			
		
			
		}
		
		
		
	}

	
	private boolean checkItemRes(JSONObject ress) {
		
		Iterator<String> it=ress.keySet().iterator();
		int i=0;
		while(it.hasNext()){
			String key=it.next();
			JSONObject onres=ress.getJSONObject(key);
			
			if(onres.containsKey("etime")){
				i++;
				if(i==2){
					return true;
				}
			}
			
		}
		
		
		return false;
	}

	private void addScore(int sc, String uid) {
		
		JSONArray users=this.data.getJSONArray(GameUtil.USERS);
		JSONObject user1=users.getJSONObject(0);
		if(user1.getString(GameUtil.USER_ID).equals(uid)){
			
			int ysc=user1.getIntValue(GameUtil.USER_SCORE);
			ysc+=sc;
			user1.put(GameUtil.USER_SCORE, ysc);
			int num=user1.getIntValue(GameUtil.USER_SUC_NUM);
			if(sc>0){
				num++;
			}
			user1.put(GameUtil.USER_SUC_NUM, num);
			
		}else{
			 user1=users.getJSONObject(1);
			int ysc=user1.getIntValue(GameUtil.USER_SCORE);
			ysc+=sc;
			user1.put(GameUtil.USER_SCORE, ysc);
			
			int num=user1.getIntValue(GameUtil.USER_SUC_NUM);
			if(sc>0){
				num++;
			}
			user1.put(GameUtil.USER_SUC_NUM, num);
			
		}
		this.data.put(GameUtil.USERS, users);
		
		
	}

	//答题开始
	public synchronized  boolean itemStart(String uid, int index) {
		
		
		
	    JSONArray items=this.data.getJSONArray(GameUtil.KEY_ITEMS);
		
		
		JSONObject one=items.getJSONObject(index-1);
		
		JSONObject userres=null;
		if(!one.containsKey(GameUtil.userRes)){
			//不包含，创建
			 userres=new JSONObject();
				
			 
		}else{
			
			userres=one.getJSONObject(GameUtil.userRes);
		}
		
		if(!userres.containsKey(uid)){
			
			JSONObject res=new JSONObject();
			res.put("uid", uid);
			res.put("index", index);
			
			userres.put(uid, res);	
			one.put(GameUtil.userRes, userres);
			this.updateItems(items);
		}
		
		
	
		
	
		
		
		
		
	
		
		return true;
		
		
	}
	
	public  void updateItems(JSONArray items) {
		this.data.put(GameUtil.KEY_ITEMS, items);
		
	}

	

	
	/**
	 * 任务 结束
	 * 	private int eveScore=10;//每题可获得积分(获胜者)
	private int exp=10;//每题可获得经验experience
	private int star=1;//每题可获得星(关卡)
	
	 */
	public  void finished() {
		
		
		
		//全部结束
		this.data.put(GameUtil.GAMESTATE,GameUtil.status[2]);//初始
		
		
		//结算积分、经验、星，
		JSONArray users=this.data.getJSONArray(GameUtil.USERS);
		JSONObject one=users.getJSONObject(0);
		JSONObject two=users.getJSONObject(1);
		int sc1=one.getIntValue(GameUtil.USER_SCORE);
		int num1=one.getIntValue(GameUtil.USER_SUC_NUM);
		int sc2=two.getIntValue(GameUtil.USER_SCORE);
		int num2=two.getIntValue(GameUtil.USER_SUC_NUM);
		if(sc1>sc2){
			//获胜方获得星
			one.put(GameUtil.KEY_STAR, star);
			
			//获胜方获得积分
			one.put(GameUtil.KEY_JF, eveScore*num1);
			
			
			//失败方
			two.put(GameUtil.KEY_STAR, -1);
			
			two.put(GameUtil.KEY_JF, 0);
			
		}else if(sc1<sc2){
			//失败方
			one.put(GameUtil.KEY_STAR, -1);
			one.put(GameUtil.KEY_JF, 0);
			
			two.put(GameUtil.KEY_STAR, star);//获胜方获得星
			//获胜方获得积分
			two.put(GameUtil.KEY_JF, eveScore*num2);
			
		}
		one.put(GameUtil.KEY_EXP, exp*num1);
		two.put(GameUtil.KEY_EXP, exp*num2);
		
		
		
		
	}

	public int getStatus() {
		
		return this.data.getIntValue(GameUtil.GAMESTATE);
		
	}

	public String getUser(String uid) {
		JSONArray users=this.data.getJSONArray(GameUtil.USERS);
		if(users.getJSONObject(0).getString(GameUtil.USER_ID).equals(uid)){
			return users.getJSONObject(0).getString(GameUtil.USER_NAME);
		}else{
			return users.getJSONObject(1).getString(GameUtil.USER_NAME);
		}
		
	}

	public void setJDRole(Document jdrole) {
		
		//多少题
		this.setNum(Integer.valueOf(jdrole.getString("num")));
		//每题多少分
		this.setOnescore(Integer.valueOf(jdrole.getString("onescore")));
		
		this.setScoreRole(jdrole.getString("scoreRole"));
		this.setExp(Integer.valueOf(jdrole.getString("exp")));
		
		this.setEveScore(Integer.valueOf(jdrole.getString("eveScore")));
		this.setStar(Integer.valueOf(jdrole.getString("star")));
		
		this.setLimittime(Integer.valueOf(jdrole.getString("limittime")));
	}

	/**
	 * 比赛中对方
	 * @param uid
	 * @return
	 */
	public String noMy(String uid) {
		
		
		JSONArray users=this.data.getJSONArray(GameUtil.USERS);
		if(users.getJSONObject(0).getString(GameUtil.USER_ID).equals(uid)){
			return users.getJSONObject(1).getString(GameUtil.USER_ID);
		}else{
			return users.getJSONObject(0).getString(GameUtil.USER_ID);
		}
		
		
		
	}

	public synchronized void stop(MessageDB mdb) {
		
		if(this.data.getInteger(GameUtil.GAMESTATE)==2){
			
			data=mdb.addGames(data);
			this.data.put(GameUtil.GAMESTATE,GameUtil.status[3]);//初始
			
		    List<Document> logs=new ArrayList<Document>();
		    
		   JSONArray users=data.getJSONArray(GameUtil.USERS);
			JSONArray items=data.getJSONArray(GameUtil.KEY_ITEMS);
			for(int i=0;i<users.size();i++){
				JSONObject one=users.getJSONObject(i);
				Document onedoc=new Document();
				onedoc.putAll(one);
			
				//修改用户数据:经验,积分，星级，	// 等级,已闯关数
				int res=mdb.updateUserInfo(onedoc);
				
				
				onedoc.put("jdinfo", items.toJSONString());
				onedoc.put("gid", data.get("gid"));
				logs.add(onedoc);
				
				
				
			}
		    
		    mdb.addJdres(logs);
		}
		
		
	} 
	
	
	
	//比赛进入最后流程
	/*	private class Task extends TimerTask{
			private GameVO game=null;
			Task(GameVO game){
				this.game=game;
			}
			//必须实现方法run()
			        public void run(){
			        	//对方超过10秒未有反应，结束任务
			        	this.game.finished();
			        	
			        	this.cancel();
			        
			        }
		}*/
	
}




