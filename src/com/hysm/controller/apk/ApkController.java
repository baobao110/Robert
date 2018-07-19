
package com.hysm.controller.apk;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hysm.bean.DBbean;
import com.hysm.controller.base.BaseController;
import com.hysm.db.ApkDB;
import com.hysm.util.StringUtil;

@Controller
@RequestMapping("/apk")
public class ApkController extends BaseController{
	
	
	@Autowired
	private ApkDB apk;
	
	@RequestMapping(value = "/toPage")
	public String toPage(){
		return "/apk/add.html";
		
	}
	
	@RequestMapping("upload")
	@ResponseBody
	public  void upload_mymp4(HttpServletRequest request,HttpServletResponse response){
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		String title=request.getParameter("title");
		System.out.println("title======="+title);
		
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		Iterator<String> iter = multiRequest.getFileNames();
		
		String file_code = String.valueOf(System.currentTimeMillis());
		
		String path = request.getRealPath(""); 
		System.out.println("path"+path);
		String p_path = path + "/assets/uploadapk/"; 
		String ext = "";
		String file_path ="";
		String last_sub=".apk";
		
		String back_code = "200";
		
		while (iter.hasNext()) {
			
			MultipartFile file = multiRequest.getFile((String) iter.next());
			
			// 有上传文件的话，容量是大于0的。
			if (file.getSize() > 0) {

				String fileName = file.getOriginalFilename();// 文件全名
				 
				/*ext = fileName.substring(fileName.lastIndexOf("."));// 后缀
				 
*/		
				ext=fileName.split("\\.")[0];
				 
				File dir = new File(p_path);
				// 如果文件夹不存在则创建
				if (!dir.exists() && !dir.isDirectory()){ 
					dir.mkdir();
				}
				
				//保存文件 
				File localFile = new File(p_path, title+file_code+last_sub);// 指定文件名称后缀名  存原图
				file_path = "/assets/uploadapk/"+title+file_code+last_sub;
				
				try {
					file.transferTo(localFile);
				}catch (Exception e) { 
					e.printStackTrace();
				} 
				back_code = "200";
				map.put("file_path", file_path);
				
				/*if(ext.equals(".mp3")){
					
					
					
				}else{
					back_code = "300";
				}*/
				
			}else{
				back_code = "300";
			}
		}
		
		map.put("back_code", back_code);
		
		sendjson(map, response);
		
	}
	
	@RequestMapping("save")
	@ResponseBody
	public String save(String data){
		System.out.println("data"+data);
		List<Document> list=apk.query_db_all(DBbean.T_APK);
		System.out.println(list);
		if(list.size()>0){
			for(Document i:list){
				Object id=i.get("_id");
				System.out.println("id"+id);
				apk.delete_db_one(DBbean.T_APK, id);
			}
		}
		
		if(StringUtil.bIsNotNull(data)){
			Document doc=Document.parse(data);
			System.out.println(doc);
			String tit=doc.getString("title");
			int title=Integer.parseInt(tit);
			doc.put("title", title);
			try{
				apk.add_db_one(DBbean.T_APK, doc);
				return "200";
				}catch (Exception e) {
					e.printStackTrace();
				}
			return "400";
		}
		return "400";
	}

}
