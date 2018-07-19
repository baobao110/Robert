package com.hysm.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hysm.bean.DBbean;
import com.hysm.controller.base.BaseController;
import com.hysm.dao.ajaxDAO;
import com.hysm.service.AdminService;
import com.hysm.service.ILoginService;
import com.hysm.service.impl.AdminServiceImpl;
import com.hysm.util.AppUtil;
import com.hysm.util.PageData;
import com.hysm.util.StringUtil;
import com.hysm.util.mobile.HYSM_TEMPCacher;
import com.hysm.util.mobile.Mobile_Bind;

@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
	
	@Autowired
	private ILoginService loginService;
	
	@Autowired
	private AdminService admin;
	
	@RequestMapping(value = "/toLogin")
	@ResponseBody
	public void toLogin(HttpServletRequest req,HttpServletResponse response,HttpSession session){
		
		String back_code ="100";
		
		if(session.getAttribute("uuid")!= null){
			
			String uid = req.getParameter("uuid");
			
			String uuid = session.getAttribute("uuid").toString();
			
			if(uid.equals(uuid)){
				back_code ="200";
			}
			
		}
		
		
		sendMessage(back_code, response);
	}
	
	
	/**
	 * 用户app用户名密码校验
	 */
	@RequestMapping(value = "/userloginAuth",method= { RequestMethod.POST})
	@ResponseBody
	public void userloginAuth(HttpServletRequest req,HttpServletResponse resp,HttpSession session)  {
		try{
			//type:1 
			int type=StringUtil.toNum(req.getParameter("type"));
			String phone=req.getParameter("phone");
			
			if(type==1){
				//手机验证码
				String code=req.getParameter("code");

				Mobile_Bind mb=(Mobile_Bind)HYSM_TEMPCacher.getCache(phone+"_MAUTH");
				if(mb!=null&&(mb.getCode()).equals(code)){
					//验证通过
					Document user=loginService.queryUserByPhone(phone);
					if(user!=null){
						sendJSON2(user.toJson(),resp);
						
					}else{
						//该手机不存在
						sendJSON2("400",resp);
						
					}
					
				}else{
					HYSM_TEMPCacher.cache(phone+"_MAUTH", mb);
					
					//验证码出错
					sendJSON2("400",resp);
					
				}
				
			}else{
				//密码登录
				String pwd=req.getParameter("pwd");
				
				Document user=loginService.queryUserByPhoneAndPwd(phone,pwd);
				if(user!=null){
					sendJSON2(user.toJson(),resp);
					
				}else{
					//该手机不存在
					sendJSON2("400",resp);
					
				}
				
			}
			
		
			
			
			}catch (Exception e) {
				e.printStackTrace();
				//注册时出错
				sendJSON2("600",resp);
			}
	
	}
	
	
	
	/**
	 * 平台请求登录，验证用户
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ptloginAuth",method= { RequestMethod.POST})
	@ResponseBody
	public Object login() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "";
		String KEYDATA[] = pd.getString("KEYDATA").replaceAll(
				"526870237bigzone", "").replaceAll("bigzone526870237", "")
				.split(",fh,");
		if (null != KEYDATA /*&& KEYDATA.length == 3*/) { 
			
			/*String sessionCode = (String) session
					.getAttribute(Const.SESSION_SECURITY_CODE); */// 获取session中的验证码
			String code = KEYDATA[2];
			if (null == code || "".equals(code)) {// 判断效验码
				errInfo = "nullcode"; // 效验码为空
			} else {
				
				String USERNAME = KEYDATA[0]; // 登录过来的用户名
				String PASSWORD = KEYDATA[1]; // 登录过来的密码
				
				
				if (USERNAME.equals("admin")&&PASSWORD.equals("c123456")) {
					
					
				
				} else {
					errInfo = "usererror"; // 用户名或密码有误
					logBefore(logger, USERNAME + "登录系统密码或用户名错误");
				}
			}
		} else {
			errInfo = "error"; // 缺少参数
		}
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}
	
	@RequestMapping(value = "/check",method= { RequestMethod.POST})
	@ResponseBody
	public void check(HttpServletRequest req,HttpServletResponse resp){
		
		String  name = req.getParameter("name");
		String password = req.getParameter("password");
		 
		Document doc=admin.select_ByName(DBbean.T_user, name);
		
		
		String back_code = "0";
		String msg ="";
		 
		if(null==doc){
			back_code = "100";
			msg ="用户名或者密码不正确";
			//return ajaxDAO.failure("用户名或者密码不正确");
		}
	try{
		String pwd=doc.getString("password");
		 
		if(null==pwd||"".equals(pwd)){
			//return ajaxDAO.failure("用户名或者密码不正确");
			back_code = "100";
			msg ="用户名或者密码不正确";
		}
		if(pwd.equals(DigestUtils.md5Hex(password))){
			
			
			//保存
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			req.getSession().setAttribute("uuid", uuid);
			
			back_code = "200";
			msg =uuid;
			
			//return ajaxDAO.success();
		} else{
			back_code = "100";
			msg ="用户名或者密码不正确";
		}
	}
	catch(Exception e){
		Document ddd = new Document();
		ddd.put("back_code",500);
		sendJSON2(ddd.toJson(), resp);
	}
		
		Document ddd = new Document();
		ddd.put("back_code", back_code);
		ddd.put("msg", msg);
		
		sendJSON2(ddd.toJson(), resp);
	}
	

}
