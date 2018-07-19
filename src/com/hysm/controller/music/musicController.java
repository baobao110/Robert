package com.hysm.controller.music;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.hysm.bean.DBbean;
import com.hysm.controller.base.BaseController;
import com.hysm.dao.ajaxDAO;
import com.hysm.service.musicService;
import com.hysm.util.StringUtil;

@Controller
@RequestMapping("/music")
public class musicController extends BaseController {
	
	@Autowired
	private  musicService  mc;
		
	@RequestMapping("save")
	@ResponseBody
	public String change(String data){
		System.out.println(data);
			if(StringUtil.bIsNotNull(data)){
				Document doc=Document.parse(data);
				System.out.println(doc);
				try{
					mc.save(doc);
						return "200";
					}catch (Exception e) {
						e.printStackTrace();
					}
				return "400";
			}
			return "400";
			
		}
	
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
		doc.put("state", 1);
		Document info=mc.query(doc, pag,limi);
		if(info!=null){
			sendJSON2(info.toJson(), resp);
			
		}

	}
	
	@RequestMapping(value = "/free")
	@ResponseBody
	public ajaxDAO delete(String _id){
		Document doc=mc.select_ById(DBbean.T_MUSIC,_id);
			try {
				doc.put("state",0);
				mc.update(DBbean.T_MUSIC, doc, _id);
				return ajaxDAO.success();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ajaxDAO.failure("删除失败");
			}
		}
	
	@RequestMapping(value = "/ajaxAllMusic")
	public void ajaxAllMusic(HttpServletRequest req,HttpServletResponse resp){
			
		
		JSONArray allmu=mc.findAllmusic();
		
		sendJSON2(allmu.toJSONString(), resp);
	}
}
