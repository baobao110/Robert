package com.hysm.olympic.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ÓïÒôµÄÎÄ±¾ÄÚÈÝÐ£Ñé
 * @author songkai 
 */
public class Word_tool {

	/**
	 * È¥³ý×Ö·û´®ÖÐµÄ¿Õ¸ñ
	 * @param str
	 * @return
	 */
	public static String RemoveAllKong(String str){ 
		return str.replaceAll("\\s*", "");
	}
	
	/**
	 * ÌáÈ¡×Ö·û´®ÖÐµÄºº×Ö
	 * @param str
	 * @return
	 */
	public static String GetHanZi(String str){
		String reg = "[^\u4e00-\u9fa5]";
		return str.replaceAll(reg, "");  
	}
	
	/**
	 * ÌáÈ¡×Ö·û´®ÖÐµÄÓ¢ÎÄ×ÖÄ¸
	 * @param str
	 * @return
	 */
	public static String GetChar(String str){
		String reg = "[^a-zA-Z]";
		return str.replaceAll(reg, ""); 
	}
	
	/**
	 * ÌáÈ¡×Ö·û´®ÖÐµÄÊý×Ö
	 * @param str
	 * @return
	 */
	public static String GetNum(String str){
		String reg = "[^0-9]";
		return str.replaceAll(reg, ""); 
	}
	
	/**
	 * Ð£ÑéÊý×Ö
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
	   Pattern pattern = Pattern.compile("[0-9]*"); 
	   Matcher isNum = pattern.matcher(str);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
	
	/**
	 * Ð£ÑéÑ§ÉúÐÕÃû(2-20¸ö³¤¶È)
	 * @param str
	 * @return
	 */
	public static boolean CheckStudentName(String str){ 
		 str = GetHanZi(str); 
		 if(str.length()>= 2 && str.length()<= 20){
			 return true;
		 }else{
			return false; 
		 }
	}
	
	/**
	 * Ð£Ñé¾º²Â´ð°¸
	 * @param str
	 * @return
	 */
	public static String CheckStudentChoose(String str){ 
		str = GetChar(str);  
		if(str.length() == 1){
			str = str.toUpperCase(); 
			return str;
		}else{
			return "";
		}
	}
	
	public static boolean check_A(String str){ 
		boolean back = false; 
		String[] arr = {"A","a","°®","°¥","°¦","°¬","àÈ","ºÙ"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	public static boolean check_B(String str){ 
		boolean back = false; 
		String[] arr = {"B","b","±È","±Æ","±Ê","±Ò","±Ï","±Õ","±Ú","±Ü","±Ë","±Ì","±Û","±Ç","ßÙ","±É","ßÁ"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	public static boolean check_C(String str){ 
		boolean back = false; 
		String[] arr = {"C","c","·Ç","·É","·Æ","·Ê","åú","Ëê","Ëæ","Ëä","Ëé","Ë­","Ë¯"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	public static boolean check_D(String str){ 
		boolean back = false; 
		String[] arr = {"D","d","µÚ","µØ","µ×","µÍ","µÎ","µØ","µÜ","µÏ","µÛ","µÝ","µÐ"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	 
}
