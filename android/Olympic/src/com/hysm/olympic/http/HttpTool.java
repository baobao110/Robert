package com.hysm.olympic.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * APP�������ͨ��
 * @author songkai
 *
 */
public class HttpTool {

	public static final String ENCODE ="utf-8";
	
	public static final String Service_url ="http://www.chuange.cn/ay/";
	
	//public static final String Service_url ="http://192.168.0.108:8080/ay/";
	
	public static String Catalog_url ="message/ajaxKcList.do";
	
	public static String Study_url ="message/ajaxOneKcInfo.do";
	
	public static String Test_url ="message/ajaxTestQuestions.do";
	
	public static String Set_url ="robot/setRobot.do";
	
	public static String Login_url = "robot/studentLogin.do";
	
	public static String Student_info = "robot/studentInfo.do";
	
	public static String Advert_data = "robot/query_advert.do";
	
	public static String All_music = "music/ajaxAllMusic.do";
	
	/*
     * Function  :   ����Post���󵽷�����
     * Param     :   params���������ݣ�encode�����ʽ
     */
    public static String send_Post(String strUrlPath,Map<String, String> params) {
        
        byte[] data = getRequestData(params, ENCODE).toString().getBytes();//���������
        try {            
             
            URL url = new URL(strUrlPath);  
             
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(6000);     //�������ӳ�ʱʱ��
            httpURLConnection.setDoInput(true);                  //�����������Ա�ӷ�������ȡ����
            httpURLConnection.setDoOutput(true);                 //����������Ա���������ύ����
            httpURLConnection.setRequestMethod("POST");     //������Post��ʽ�ύ����
            httpURLConnection.setUseCaches(false);               //ʹ��Post��ʽ����ʹ�û���
            //������������������ı�����
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //����������ĳ���
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //�����������������д������
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
            
            int response = httpURLConnection.getResponseCode();            //��÷���������Ӧ��
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //�������������Ӧ���
            }
        } catch (IOException e) {
             
            return "-1";
        }
        return "-1";
    }
    
    /*
     * Function  :   ��װ��������Ϣ
     * Param     :   params���������ݣ�encode�����ʽ
     */
   public static StringBuffer getRequestData(Map<String, String> params, String encode) {
      StringBuffer stringBuffer = new StringBuffer();        
      try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                      .append("=")
                      .append(URLEncoder.encode(entry.getValue(), encode))
                      .append("&");
            }
           stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
           e.printStackTrace();
       }
       return stringBuffer;
    }
    
   /*
    * Function  :   �������������Ӧ�������������ת�����ַ�����
    * Param     :   inputStream����������Ӧ������
    */
   public static String dealResponseResult(InputStream inputStream) {
      String resultData = null;      
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] data = new byte[1024];
      int len = 0;
       try {
          while((len = inputStream.read(data)) != -1) {
             byteArrayOutputStream.write(data, 0, len);
          }
     } catch (IOException e) {
         e.printStackTrace();
        }
       resultData = new String(byteArrayOutputStream.toByteArray());    
       return resultData;
   } 
}
