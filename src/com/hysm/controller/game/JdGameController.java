package com.hysm.controller.game;


import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hysm.controller.base.BaseController;
import com.hysm.game.GameUtil;
import com.hysm.game.GameVO;
import com.hysm.service.IMessageService;

@Controller
@RequestMapping("/jdgame")
public class JdGameController extends BaseController {
	@Autowired
	private IMessageService meService;
	
	
	
	@RequestMapping("ajaxLogin")
	public void ajaxLogin(HttpServletRequest req, HttpServletResponse resp){
		
		
		
		String uid=req.getParameter("uid");
		
		//登录
		GameUtil.OnlineUsers.put(uid, System.currentTimeMillis());
		
		
		sendJSONP("200", resp, "ajaxLoginShow", 3);
	}
	
	
	@RequestMapping("ajaxData")
	public void getData(HttpServletRequest req, HttpServletResponse resp) {
		
		String uid=req.getParameter("uid");
		//登录
		GameUtil.OnlineUsers.put(uid, System.currentTimeMillis());
				
		int passnum=Integer.valueOf(req.getParameter("passnum"));
		
		GameVO game =null;
		
		if(req.getParameter("type").equals("login")){
			// 请求竞技用户
			 game = this.queryGame(req);
			if(game==null){
				GameUtil.addCuser(uid,passnum);
				System.out.println(GameUtil.cusers.toString());
			}
			
			
			
		}
		

		// 请求竞技用户
		if(game==null){
			 game = this.queryGame(req);
		}
	
		
		if(game!=null){
			
			if(!req.getParameter("type").equals("login")&&!game.hadItems()){
				//查找题目
				JSONArray items=meService.ajaxTestQuestions(game.getNum());
				
				game.setItems(items);
				
			
			}
			
			
			
			
			
			sendJSONP(game.getData().toJSONString(), resp, "initReq", 1);
			
			if(game.getStatus()==GameUtil.status[2]){
				
				meService.addJDres(game);
				
			}
		}else{
			
			sendJSONP("404", resp, "initReq", 3);
			
		}
		
		
		
		
	}

	private GameVO queryGame(HttpServletRequest req) {
		
		
		String uid = req.getParameter("uid");
		//登录
		GameUtil.OnlineUsers.put(uid, System.currentTimeMillis());
			

		return GameUtil.queryGameByuid(uid);

	}
		
	
	/**
	 * 答题上传
	 * @param req
	 * @param resp
	 */
	@RequestMapping("sendThisItem")
	public void sendThisItem(HttpServletRequest req, HttpServletResponse resp){
		
		GameVO game = this.queryGame(req);
		
		
		String uid=req.getParameter("uid");
		
		//登录
		GameUtil.OnlineUsers.put(uid, System.currentTimeMillis());
				
		
		
		String choose=req.getParameter("choose");
		int index=Integer.valueOf(req.getParameter("index"));
		long etime=System.currentTimeMillis();
		
		game.sendUserRes(uid,index,choose,etime);
		
		
		
		sendJSONP(game.getData().toJSONString(), resp, "sendThisItemShow", 1);
		
		
		
		
		if(game.getStatus()==GameUtil.status[2]){
			
			
			meService.addJDres(game);
			
			
		}
		
		
	}
	
	
	/**
	 * 竞答开始
	 * @param req
	 * @param resp
	 */
	@RequestMapping("startJD")
	public void startJD(HttpServletRequest req, HttpServletResponse resp){
		
		
		
		GameVO game = this.queryGame(req);
		
		String uid=req.getParameter("uid");
		//登录
		GameUtil.OnlineUsers.put(uid, System.currentTimeMillis());
				
		
		
		
		
		int index=Integer.valueOf(req.getParameter("index"));
		
		
		
		game.itemStart(uid,index);
		
		

     	boolean can=true;
     	JSONArray items=game.getData().getJSONArray(GameUtil.KEY_ITEMS);
     	JSONObject userres=items.getJSONObject(index-1).getJSONObject(GameUtil.userRes);
     	
		while(userres.keySet().size()!=2&&can){
			
			userres=items.getJSONObject(index-1).getJSONObject(GameUtil.userRes);
			
			
			if(!GameUtil.isNoExter(game.noMy(uid))){
				//对面已离开
				can=false;
				
				game.finished();
				
				break;
			}
			
		}
		

		
		
		if(game.getStatus()!=GameUtil.status[2]){
			
			//设置开始时间
			long stime=System.currentTimeMillis();
			Iterator<String> its=	userres.keySet().iterator();
			while(its.hasNext()){
				String key=its.next();
				JSONObject oneres=userres.getJSONObject(key);
				
				if(!oneres.containsKey("stime")){
					oneres.put("stime", stime);
					oneres.put("onestate", GameUtil.ONEITEM_STATE[0]);//未答题
				}
			
				
			}
			game.updateItems(items);
			
			sendJSONP(game.getData().toJSONString(), resp, "startJDshow", 1);
		  
		}else{
			//有人离开，竞答结束
			sendJSONP(game.getData().toJSONString(), resp, "startJDshow", 1);
			
			meService.addJDres(game);
		}
	
		
		
		
		
	}


}
