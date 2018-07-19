package com.hysm.controller.advertise;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hysm.bean.DBbean;
import com.hysm.controller.base.BaseController;
import com.hysm.dao.ajaxDAO;
import com.hysm.service.IMessageService;
import com.hysm.service.adService;
import com.hysm.util.StringUtil;

@Controller
@RequestMapping("/advertise")
public class adController extends BaseController {
	
	@Autowired
	private adService ad;
	
	@RequestMapping("save")
	@ResponseBody
	public String save(String data){
			if(StringUtil.bIsNotNull(data)){
				Document doc=Document.parse(data);
				try{
					int res=ad.save(doc);
					if(res>0){
						return "200";
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				return "400";
			}
			return "400";
			
		}
	
	@RequestMapping("change")
	@ResponseBody
	public String change(String data){
			if(StringUtil.bIsNotNull(data)){
				Document doc=Document.parse(data);
				try{
					ad.change(doc);
						return "200";
					}catch (Exception e) {
						e.printStackTrace();
					}
				return "400";
			}
			return "400";
			
		}
	
	/*@RequestMapping("query")
	@ResponseBody
	public ajaxDAO query(){
		
		try{
			Document doc=new Document();
			doc.put("state", 1);
			List<Document> list=ad.getALL(DBbean.T_advertise,doc);
			return ajaxDAO.success(list);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ajaxDAO.failure();
		
	}
	*/
	
	@RequestMapping("query")
	@ResponseBody
	public void  query(String page,String limit,String title,HttpServletResponse resp){
		Document doc=new Document();
		int pag=StringUtil.toNum(page);
		int limi=StringUtil.toNum(limit);
		if(pag<1){
			pag=0;
		}
		
		if(limi<30){
			limi=30;
		}
		if(!title.isEmpty()||title!=null){
			/*try {
				title=new String(title.getBytes("ISO-8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			doc.put("title",title);
		}
		Document info=ad.query(doc, pag,limi);
		if(info!=null){
			sendJSON2(info.toJson(), resp);
			
		}

	}
	
	@RequestMapping(value = "/free")
	@ResponseBody
	public ajaxDAO delete(String _id){
		Document doc=ad.select_ById(DBbean.T_advertise,_id);
			try {
				doc.put("state",0);
				ad.update(DBbean.T_advertise, doc, _id);
				return ajaxDAO.success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ajaxDAO.failure("删除失败");
			}
		}
	
	@RequestMapping(value = "/query_title")
	@ResponseBody
	public ajaxDAO query_title(String title){
			/*Document doc=ad.select_ByTitle(DBbean.T_advertise,title);*/
		List<Document> doc=ad.select_ByTitle(DBbean.T_advertise,title);
			if(null==doc){
				return ajaxDAO.failure();
			}
			else{
				/*if(doc.getInteger("state")==1){
					return ajaxDAO.success(doc);
				}
				else{
					return ajaxDAO.failure();
				}*/
				return ajaxDAO.success(doc);
			}
		}
	
	@RequestMapping(value = "/query_id")
	@ResponseBody
	public ajaxDAO query_id(String _id){
			Document doc=ad.select_ById(DBbean.T_advertise, _id);
			if(null==doc){
				return ajaxDAO.failure();
			}
			else{
				if(doc.getInteger("state")==1){
					return ajaxDAO.success(doc);
				}
				else{
					return ajaxDAO.failure();
				}
			}
		}
		
	}
	
		

