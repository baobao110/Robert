package com.hysm.db;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;







import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hysm.bean.DBbean;
import com.hysm.db.mongo.MongoUtil;
import com.hysm.game.GameUtil;
import com.mongodb.client.model.Filters;

@Component
public class MessageDB {
	private MongoUtil mu=MongoUtil.getThreadInstance();

	public int replaceMess(Document mess) {
		mu.clear();
		return mu.replaceOne(DBbean.T_MESSAGES, Filters.eq("_id", mess.get("_id")), mess);
	}

	public int insertMess(Document mess) {
		mu.clear();
		try{
		 mu.insertOne(DBbean.T_MESSAGES, mess);
		 return 1;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
		
	}

	public int ajaxMessageListNum(Document map) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		
	
		if (map.get("likename") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("likename")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("title", pattern));
		}


		/*
		 * if(map.get("fwdate")!= null){ String fw=map.getString("fwdate");
		 * String mindate=fw.substring(0,10); String maxdate=fw.substring(13);
		 * 
		 * Date start = DateUtil.getDateByString(mindate+" 00:00:00");
		 * list.add(Filters.gte("ctime", start)); Date end =
		 * DateUtil.getDateByString(maxdate+" 23:59:59");
		 * list.add(Filters.lte("ctime", end));
		 * 
		 * }
		 */

		

		return (int) mu.count(DBbean.T_MESSAGES, Filters.and(list));
		
		
		
	
	}

	public List<Document> ajaxMessageList(Document map) {
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		
		
		if (map.get("likename") != null) {
			
			Pattern pattern = Pattern.compile("^.*" + map.getString("likename")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("title", pattern));
		}

		
		Document sort = new Document();
		sort.put("orderby", -1); // 按时间 倒序

		int pn = (Integer) map.get("page");
		int ps = (Integer) map.get("limit");


		return mu.findLimitSort(DBbean.T_MESSAGES,
				Filters.and(list), (pn - 1) * ps, ps, sort);

	}

	public Document queryMessById(String id) {
		mu.clear();
		
		return mu.findOne(DBbean.T_MESSAGES, Filters.eq("_id", id));
	}

	public JSONArray ajaxKcTitleList() {
		mu.clear();
		Document columns=new Document();
		columns.append("_id", 1);
		columns.append("title", 1);
		

		Document sort = new Document();
		sort.put("orderby", -1); // 按时间 倒序
		
		return mu.findSortJA(DBbean.T_MESSAGES, Filters.eq("state", 1),columns,sort);
	}

	public void updateMessState(String id, int state) {
		
		mu.clear();
		Document set=new Document();
		Document bson =new Document();
		bson.put("state", state);
		
		set.put("$set", bson);
		
		mu.updateOne(DBbean.T_MESSAGES, Filters.eq("_id", id),set);
		
	}

	public int replaceTM(Document tm) {
		mu.clear();
		return mu.replaceOne(DBbean.T_TM, Filters.eq("_id", tm.get("_id")), tm);
	}

	public int insertTM(Document tm) {
		mu.clear();
		try{
		 mu.insertOne(DBbean.T_TM, tm);
		 return 1;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
	}

	public int ajaxTMListNum(Document map) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		
		
		if (map.get("likename") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("likename")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("kcname", pattern));
			/*list.add(Filters.eq("kcname", map.get("likename")));
			System.out.println(list);*/
		}
		
		if (map.get("question") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("question")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("question", pattern));
			/*list.add(Filters.eq("kcname", map.get("likename")));
			System.out.println(list);*/
		}
		if (map.get("kcid") != null) {
			

			list.add(Filters.eq("kcid", map.get("kcid")));
		}

		/*
		 * if(map.get("fwdate")!= null){ String fw=map.getString("fwdate");
		 * String mindate=fw.substring(0,10); String maxdate=fw.substring(13);
		 * 
		 * Date start = DateUtil.getDateByString(mindate+" 00:00:00");
		 * list.add(Filters.gte("ctime", start)); Date end =
		 * DateUtil.getDateByString(maxdate+" 23:59:59");
		 * list.add(Filters.lte("ctime", end));
		 * 
		 * }
		 */

		

		return (int) mu.count(DBbean.T_TM, Filters.and(list));
	}

	public List<Document> ajaxTMList(Document map) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		
		
		if (map.get("likename") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("likename")
					+ ".*$", Pattern.CASE_INSENSITIVE);
			list.add(Filters.regex("kcname", pattern));
				/*List<Bson> orlist=new ArrayList<Bson>();
				orlist.add(Filters.regex("kcname", pattern));
				orlist.add(Filters.eq("kcname", map.get("likename")));
			list.add(Filters.or(orlist));*/
		}
		
		if (map.get("question") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("question")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("question", pattern));
			/*list.add(Filters.eq("kcname", map.get("likename")));
			System.out.println(list);*/
		}
		
		if (map.get("kcid") != null) {
			

			list.add(Filters.eq("kcid", map.get("kcid")));
		}
		
		Document sort = new Document();
		sort.put("ctime", -1); // 按时间 倒序

		int pn = (Integer) map.get("page");
		int ps = (Integer) map.get("limit");


		return mu.findLimitSort(DBbean.T_TM,
				Filters.and(list), (pn - 1) * ps, ps, sort);

	
	}

	public List<Document> queryTmsByKcId(String kcid) {
		
		mu.clear();

		List<Bson> tjs = new ArrayList<Bson>();
		tjs.add(Filters.eq("state", 1));
		tjs.add(Filters.eq("kcid", kcid));
		
		return mu.find(DBbean.T_TM, Filters.and(tjs));
	}

	

	public int countAllQuestions() {
		// TODO Auto-generated method stub
		return (int)mu.count(DBbean.T_TM, Filters.eq("state", 1));
	}

	public Document queryTMByIndex(Integer oneindex) {
	
		return mu.findLimit(DBbean.T_TM, Filters.eq("state", 1), oneindex, 1).get(0);
	}

	public void updateTMState(String id, int state) {
		// TODO Auto-generated method stub
		mu.clear();
		Document set=new Document();
		Document bson =new Document();
		bson.put("state", state);
		
		set.put("$set", bson);
		
		mu.updateOne(DBbean.T_TM, Filters.eq("_id", id),set);
	}

	public int deleteTm(String id) {
		mu.clear();
		List<Bson> tjs = new ArrayList<Bson>();
		tjs.add(Filters.eq("state", -99));
		tjs.add(Filters.eq("_id", id));
		return mu.deleteMany(DBbean.T_TM, Filters.and(tjs));
	}

	public Document queryTmByid(String id) {
		
		return mu.findOne(DBbean.T_TM, Filters.eq("_id", id));
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public Document queryJdById(String id) {
	
		return mu.findOne(DBbean.T_JD, Filters.eq("_id", id));
	}

	public int replaceJD(Document ymess) {
		mu.clear();
		return mu.replaceOne(DBbean.T_JD, Filters.eq("_id", ymess.get("_id")), ymess);
	}

	public int insertJD(Document mess) {
		mu.clear();
		try{
		 mu.insertOne(DBbean.T_JD, mess);
		 return 1;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
	}

	public List<Document> ajaxJdList(Document map) {

		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		
		
		if (map.get("likename") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("likename")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("name", pattern));
		}
		if (map.get("kcid") != null) {
			

			list.add(Filters.eq("kcid", map.get("kcid")));
		}
		
		Document sort = new Document();
		sort.put("ctime", -1); // 按时间 倒序

		int pn = (Integer) map.get("page");
		int ps = (Integer) map.get("limit");


		return mu.findLimitSort(DBbean.T_JD,
				Filters.and(list), (pn - 1) * ps, ps, sort);
	}

	public int ajaxJdListNum(Document map) {
		mu.clear();

		List<Bson> list = new ArrayList<Bson>();

		int state=map.getInteger("state");
		if(state==99){
			//全部
			
		}else{
			
			list.add(Filters.eq("state", state));
		}
		
		
		if (map.get("likename") != null) {
			Pattern pattern = Pattern.compile("^.*" + map.getString("likename")
					+ ".*$", Pattern.CASE_INSENSITIVE);

			list.add(Filters.regex("name", pattern));
		}
		if (map.get("kcid") != null) {
			

			list.add(Filters.eq("kcid", map.get("kcid")));
		}

		/*
		 * if(map.get("fwdate")!= null){ String fw=map.getString("fwdate");
		 * String mindate=fw.substring(0,10); String maxdate=fw.substring(13);
		 * 
		 * Date start = DateUtil.getDateByString(mindate+" 00:00:00");
		 * list.add(Filters.gte("ctime", start)); Date end =
		 * DateUtil.getDateByString(maxdate+" 23:59:59");
		 * list.add(Filters.lte("ctime", end));
		 * 
		 * }
		 */

		

		return (int) mu.count(DBbean.T_JD, Filters.and(list));
	}

	public void updateJdState(String id, int state) {
		// TODO Auto-generated method stub
		mu.clear();
		Document set=new Document();
		Document bson =new Document();
		bson.put("state", state);
		
		set.put("$set", bson);
		
		mu.updateOne(DBbean.T_JD, Filters.eq("_id", id),set);
	}

	public int countAllKC() {
		// TODO Auto-generated method stub
		return (int)mu.count(DBbean.T_MESSAGES, Filters.eq("state", 1));
	}

	public void addJdres(List<Document> logs) {
		mu.clear();
		mu.insertMany(DBbean.T_USER_JDRES, logs);
		
	}

	
	/**
	 * 修改用户积分，经验，星数
	 * 
	 * 之后才是等级和关数
	 * @param onedoc
	 * @return
	 */
	public int updateUserInfo(Document onedoc) {
		mu.clear();
		Document inc=new Document();
		inc.append("experience", onedoc.getInteger(GameUtil.KEY_EXP,0));
		inc.append("score", onedoc.getInteger(GameUtil.KEY_JF,0));
		inc.append("star", onedoc.getInteger(GameUtil.KEY_STAR,0));
		Document update=new Document();
		update.append("$inc", inc);
		String uid=onedoc.getString(GameUtil.USER_ID);
		int res=mu.updateOne(DBbean.T_student, Filters.eq("_id", uid), update);
		if(res>0){
			Document user=mu.findOne(DBbean.T_student, Filters.eq("_id", uid));
			
			int rank=user.getInteger("rank", 1);
			long exp=user.getInteger("experience");
			int  sjrank=GameUtil.getRankByEXP(exp);
			Document setdata=new Document();
			
			setdata.put("rank", sjrank);
			
			int star=user.getInteger("star", 0);
			if(star<0){
				star=0;
			}
			
			//校验关卡
			Document pass=checkStarPass(star);
			setdata.put("pass", pass.getString("pass"));
			setdata.put("passid", pass.getString("_id"));
			setdata.put("passnum", pass.getInteger("passnum"));
			setdata.put("star",star);
			Document set=new Document();
			set.put("$set", setdata);
			
			
			mu.updateOne(DBbean.T_student, Filters.eq("_id", uid), set);
			
			
			return res;
			
		}
		
		
		return 0;
		
	}

	
	/**
	 * 校验关卡
	 * @param star
	 * @return
	 */
	public  Document checkStarPass(int star) {
		Document sort=new Document();
		sort.put("passnum", 1);//正序
		List<Document> allgk=mu.findSort(DBbean.T_PASS, Filters.eq("state", 1),sort);
		
		int all=0;
		for(int i=0,len=allgk.size();i<len;i++){
			Document one=allgk.get(i);
			all+=one.getInteger("star");
			if(all==star){
				return allgk.get(i+1);
			}
			
			if(all>star){
				return one;
			}
			
		}
		
		return null;
	}

	public Document queryUserInfo(String id) {
		Document st= mu.findOne(DBbean.T_student, Filters.eq("_id", id));
		if(st!=null&&st.containsKey("schoolcode")){
			Document sc=mu.findOne(DBbean.T_school, Filters.eq("schoolcode", st.getString("schoolcode")));
			st.put("schoolname", sc.getString("schoolname"));
			st.put("province", sc.getString("province"));
			st.put("city", sc.getString("city"));
			st.put("county", sc.getString("county"));
		}
		
		return st;
	}

	public JSONObject addGames(JSONObject game) {
		
		Document doc= Document.parse(game.toJSONString());
		
		mu.insertOne(DBbean.T_GAMES,doc);
		game.put("gid", doc.get("_id").toString());
		return game;
	}

	public static void main(String[] args) {
		MessageDB mdb=new MessageDB();
		System.out.println(mdb.checkStarPass(0).toJson());
	}

	public Document queryJdRole() {
		// TODO Auto-generated method stub
		return mu.findOne(DBbean.T_JD, null);
	}

	public int deleteKC(String id) {
		// TODO Auto-generated method stub
		mu.clear();
		List<Bson> tjs = new ArrayList<Bson>();
		tjs.add(Filters.eq("state", -99));
		tjs.add(Filters.eq("_id", id));
		return mu.deleteMany(DBbean.T_MESSAGES, Filters.and(tjs));
	}
	
}
