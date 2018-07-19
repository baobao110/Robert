package com.hysm.olympic.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
  
 




import com.hysm.olympic.R;
import com.hysm.olympic.bean.PublicData; 
import com.hysm.olympic.http.HttpTool;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.SoundPool_tool;
import com.hysm.olympic.tool.Speek;
import com.hysm.olympic.tool.Voice;
import com.hysm.olympic.tool.Voice2;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context; 
import android.content.SharedPreferences;
import android.graphics.Bitmap;  
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar; 

public class GameAct extends Activity{

	private WebView webview;
	private ProgressBar progressBar;
	
	private SharedPreferences preferences;
	private JSONObject student_info;
	
	//�����ϳɶ���
    private SpeechSynthesizer speaker; 
    //handler
 	private Handler mHandler;
 	
 	//���Կ�ʼ����
  	public static final int SAY_START =10;
  	//���Բ��Ž���
  	public static final int SAY_END =11; 
  	//���ݼ��سɹ�
  	public static final int LOAD_OK = 200;
  	//���ݼ���ʧ��
  	public static final int LOAD_ERR = 404;
  	
  	private String schoolcode,studentid,rid,robotid;
  	
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // ���Activity����ջ 
		setContentView(R.layout.game);
		
		init_view();
		init_data();
		
		init_crate(); 
		
		init_hander();
		
		init_web(); 
		init_listener();
		
		
	}
   
	private void init_view(){
		webview = (WebView)findViewById(R.id.game_webview);
		progressBar = (ProgressBar)findViewById(R.id.game_progress);
		
		Net_tool.checkNet(GameAct.this);
	}
	private void init_data(){
		 
		preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE); 
		
		rid = preferences.getString("rid", "");
		robotid = preferences.getString("robotid", "");
		
		schoolcode = preferences.getString("schoolcode", "");
		String schoolname = preferences.getString("schoolname", "");
		
		String sid = preferences.getString("sid", "");
		studentid = preferences.getString("studentid", "");
		String studentname = preferences.getString("studentname", "");
		String grade = preferences.getString("grade", "");
		String classname = preferences.getString("classname", "");
		String parent = preferences.getString("parent", "");
		String phone = preferences.getString("phone", "");
		 
		 
		String rank = preferences.getString("rank", "1");
		String score = preferences.getString("score", "0");
		String star = preferences.getString("star", "0");
		String experience = preferences.getString("experience", "0");
		
		String pass = preferences.getString("pass", "");
		String passid = preferences.getString("passid", "");
		String passnum = preferences.getString("passnum", "0");
		
		try {
			
			student_info = new JSONObject();
			
			student_info.put("rid", rid);
			student_info.put("robotid", robotid);
			student_info.put("schoolcode", schoolcode);
			student_info.put("schoolname", schoolname);
			
			student_info.put("sid", sid);
			student_info.put("studentid", studentid);
			student_info.put("studentname", studentname);
			student_info.put("grade", grade);
			student_info.put("classname", classname);
			student_info.put("parent", parent);
			student_info.put("phone", phone);
			
			student_info.put("rank", Integer.valueOf(rank));
			student_info.put("score", Integer.valueOf(score));
			student_info.put("star", Integer.valueOf(star));
			student_info.put("experience", Integer.valueOf(experience));
			
			student_info.put("pass", pass);
			student_info.put("passid", passid); 
			student_info.put("passnum", Integer.valueOf(passnum));
			
			
		} catch (JSONException e){ 
			e.printStackTrace();
		}
		
	}
	
	private void init_web(){
		WebSettings webSettings=webview.getSettings();
        webSettings.setJavaScriptEnabled(true);//����ʹ��js

        /**
         * LOAD_CACHE_ONLY: ��ʹ�����磬ֻ��ȡ���ػ�������
         * LOAD_DEFAULT: ��Ĭ�ϣ�����cache-control�����Ƿ��������ȡ���ݡ�
         * LOAD_NO_CACHE: ��ʹ�û��棬ֻ�������ȡ����.
         * LOAD_CACHE_ELSE_NETWORK��ֻҪ�����У������Ƿ���ڣ�����no-cache����ʹ�û����е����ݡ�
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//��ʹ�û��棬ֻ�������ȡ����. 
        //��֧��֧����Ļ����
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false); 
        
       
        
        //����ʾwebview���Ű�ť
        //webSettings.setDisplayZoomControls(false);
         
        webview.addJavascriptInterface(this,"OlymlpicAndroid");//���js���� ����html���ܵ��ÿͻ���
         
        webview.loadUrl("file:///android_asset/ay/pass.html");
         
	}
	
	 private void init_listener() {
		  webview.setWebChromeClient(webChromeClient);
	      webview.setWebViewClient(webViewClient);
		
	 }
	
	 //WebViewClient��Ҫ����WebView�������֪ͨ�������¼�
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//ҳ��������
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//ҳ�濪ʼ����
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //�����صĵ�ַ 
            return super.shouldOverrideUrlLoading(view, url);
        }

    }; 
    //WebChromeClient��Ҫ����WebView����Javascript�ĶԻ�����վͼ�ꡢ��վtitle�����ؽ��ȵ�
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //��֧��js��alert��������Ҫ�Լ�����Ȼ��ͨ��dialog����
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result){
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setTitle("��ʾ");
            localBuilder.setMessage(message).setPositiveButton("ȷ��",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //ע��:
            //����Ҫ��һ�����:result.confirm()��ʾ:
            //������Ϊȷ��״̬ͬʱ����WebCore�߳�
            //�����ܼ��������ť
            result.confirm();
            return true;
        }

        //��ȡ��ҳ����
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title); 
        }

        //���ؽ��Ȼص�
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };
	
	
	 @Override
     protected void onDestroy() {
        super.onDestroy(); 
        //�ͷ���Դ
        webview.destroy();
        webview=null;
    }
	 
	 
	 private void init_crate() {
		 Speek speek = new Speek(GameAct.this); 
		 //��ʼ���������� 
	     speaker = speek.getSpeaker();
	      
	 }
 
	 private void init_hander(){
		 mHandler = new Handler() { 
				@Override
				public void handleMessage(Message msg){ 
					super.handleMessage(msg); 
					int what = msg.what;
					
					switch (what){ 
						case SAY_END: 
							
							speek_end();
							break;
						case SAY_START:  
							break;
						case LOAD_OK: 
						   //���ݼ��سɹ� 
						   sync_data_ok();
						 break;
					}
				}
	 
	        	
	        };
	 }
	  
	//�����ϳɼ�����
    private SynthesizerListener synthesizerListener = new SynthesizerListener() {

    	 /**
         * ������Ȼص�
         */
		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			 
		}
		/**
         * �Ự�����ص��ӿڣ�û�д���ʱ��errorΪnull
         */
		@Override
		public void onCompleted(SpeechError arg0) {
			Message msg=mHandler.obtainMessage();
			msg.what=SAY_END;
			mHandler.sendMessage(msg);
		}

		 /**
	     * �Ự�¼��ص��ӿ�
	     */
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			 
		}

		/**
         * ��ʼ����
         */
		@Override
		public void onSpeakBegin() {
			Message msg=mHandler.obtainMessage();
			msg.what=SAY_START;
			mHandler.sendMessage(msg);
		}
		/**
         * ��ͣ����
         */
		@Override
		public void onSpeakPaused() {
			 
		}

		 /**
         * ���Ž��Ȼص�
         */
		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			 
		}
		/**
         * �ָ����Żص��ӿ�
         */
		@Override
		public void onSpeakResumed() {
			 
		}
    	
    };
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//��������¼� 
		if(keyCode == KeyEvent.KEYCODE_BACK){ 
			
			 if(speaker.isSpeaking()){
				 speaker.stopSpeaking();
			 }
			 speaker.destroy(); 
			 finish();
		}
		return super.onKeyDown(keyCode, event);
	}
    
    
    /**
     * android֪ͨjs ����ͬ���ɹ�
     */
    protected void sync_data_ok() {
    	webview.loadUrl("javascript:SyncDataOK()");
	}

    /**
     * android֪ͨjs �������Ž���
     */
	protected void speek_end() {
		  webview.loadUrl("javascript:SpeekEnd()");
	} 
	 
	 /**
     * JS����android�ķ���
     * @param str
     * @return
     */
	 
	 /**
	  * ��ȡѧ����Ϣ
	  * @return
	  */
	 @JavascriptInterface
	 public String GetStudent(){ 
		 return student_info.toString();
	 } 
	 /**
	  * ͬ��ѧ����Ϣ
	  */
	 @JavascriptInterface
	 public void SyncStudent(){ 
		 new Thread(msgrunnable).start();
	 } 
	 /**
	  * �����ϳ�
	  * @param str
	  */
	 @JavascriptInterface
	 public void SpeekWord(String str){
		  
		 if(speaker.isSpeaking()){
			 speaker.stopSpeaking();
		 }
		 
		 init_crate();  
	     speaker.startSpeaking(str,synthesizerListener);
		 
	 } 
	 /**
	  * ��������
	  * @param str
	  */
	 @JavascriptInterface
	 public void SpeekStop(){
		 if(speaker.isSpeaking()){
			 speaker.stopSpeaking();
		 }
	 }
	 
	 /**
	  * ����˵�ѡ��
	  */
	 @JavascriptInterface
	 public void IntoChoose(){
		
		 if(speaker.isSpeaking()){
			 speaker.stopSpeaking();
		 }
		 speaker.destroy();
		 
		 finish();
	 }
	 
	 /**
	  * ������ȷ
	  */
	 @JavascriptInterface
	 public void video_suc(){ 
		 //new Voice(GameAct.this);
		 SoundPool_tool.SoundPool_play1(GameAct.this);
	 }
	 
	 /**
	  * ������ȷ
	  */
	 @JavascriptInterface
	 public void video_error(){  
		//new Voice2(GameAct.this); 
		SoundPool_tool.SoundPool_play2(GameAct.this);
	 }
	 
	 
	  
	 /**
	  * ѧ����Ϣͬ��
	  */ 
	 Runnable msgrunnable = new Runnable(){
		
		@Override
		public void run() {
			 
			Map<String,String> params = new HashMap<String,String>(); 
			params.put("schoolcode", schoolcode);
			params.put("studentid", studentid);
			
			String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.Student_info, params);
			
			try {
				JSONObject json = new JSONObject(result);
				
				if(json.getString("backcode").equals("200")){
					
					//������Ϣ
					SharedPreferences.Editor editor = preferences.edit();
					
					editor.putString("studentid", json.getString("studentid"));
					editor.putString("studentname", json.getString("name"));
					editor.putString("grade", json.getString("grade"));
					editor.putString("classname", json.getString("classname"));
					editor.putString("parent", json.getString("parent"));
					editor.putString("phone", json.getString("phone"));
					
					editor.putString("sid", json.getString("sid"));
					editor.putString("schoolname", json.getString("schoolname"));
					
					editor.putString("rank", json.getInt("rank")+"");//�ȼ�
					editor.putString("score", json.getInt("score")+"");//����
					editor.putString("star", json.getInt("star")+"");//��
					editor.putString("experience", json.getInt("experience")+"");//����
					
					editor.putString("pass",    json.getString("pass"));
					editor.putString("passid",  json.getString("passid"));
					editor.putString("passnum", json.getInt("passnum")+""); 
					
					editor.commit();
					
					//ѧ����Ϣͬ��
					
					try {
						
						student_info = new JSONObject();
						
						student_info.put("rid", rid);
						student_info.put("robotid", robotid);
						student_info.put("schoolcode", schoolcode);
						student_info.put("schoolname", json.getString("schoolname"));
						
						student_info.put("sid", json.getString("sid"));
						student_info.put("studentid", studentid);
						student_info.put("studentname", json.getString("name"));
						student_info.put("grade", json.getString("grade"));
						student_info.put("classname", json.getString("classname"));
						student_info.put("parent", json.getString("parent"));
						student_info.put("phone", json.getString("phone"));
						
						student_info.put("rank", json.getInt("rank"));
						student_info.put("score", json.getInt("score"));
						student_info.put("star", json.getInt("star"));
						student_info.put("experience", json.getInt("experience"));
						
						student_info.put("pass", json.getString("pass"));
						student_info.put("passid", json.getString("passid")); 
						student_info.put("passnum", json.getInt("passnum"));
						
						
					} catch (JSONException e){ 
						e.printStackTrace();
					}
					 
					Message msg = mHandler.obtainMessage();
					msg.what = LOAD_OK;
					mHandler.sendMessage(msg); 
					 
					
				}else{ 
					Message msg = mHandler.obtainMessage();
					msg.what = LOAD_ERR;
					msg.obj = json.getString("backmsg"); 
					mHandler.sendMessage(msg);
				}
				
			}catch(JSONException e){ 
				e.printStackTrace();
			}
		}
	};

}
