package com.hysm.olympic.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �������ı�����У��
 * @author songkai 
 */
public class Word_tool {

	/**
	 * ȥ���ַ����еĿո�
	 * @param str
	 * @return
	 */
	public static String RemoveAllKong(String str){ 
		return str.replaceAll("\\s*", "");
	}
	
	/**
	 * ��ȡ�ַ����еĺ���
	 * @param str
	 * @return
	 */
	public static String GetHanZi(String str){
		String reg = "[^\u4e00-\u9fa5]";
		return str.replaceAll(reg, "");  
	}
	
	/**
	 * ��ȡ�ַ����е�Ӣ����ĸ
	 * @param str
	 * @return
	 */
	public static String GetChar(String str){
		String reg = "[^a-zA-Z]";
		return str.replaceAll(reg, ""); 
	}
	
	/**
	 * ��ȡ�ַ����е�����
	 * @param str
	 * @return
	 */
	public static String GetNum(String str){
		String reg = "[^0-9]";
		return str.replaceAll(reg, ""); 
	}
	
	/**
	 * У������
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
	 * У��ѧ������(2-20������)
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
	 * У�龺�´�
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
		String[] arr = {"A","a","��","��","��","��","��","��"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	public static boolean check_B(String str){ 
		boolean back = false; 
		String[] arr = {"B","b","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	public static boolean check_C(String str){ 
		boolean back = false; 
		String[] arr = {"C","c","��","��","��","��","��","��","��","��","��","˭","˯"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	public static boolean check_D(String str){ 
		boolean back = false; 
		String[] arr = {"D","d","��","��","��","��","��","��","��","��","��","��","��"}; 
		for(String c:arr){ 
			if(str.contains(c)){
				back = true; 
			}
		} 
		return back; 
	}
	
	 
}
