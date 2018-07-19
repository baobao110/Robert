package com.hysm.olympic.activity;

  

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
  

 



import com.hysm.olympic.R; 
import com.hysm.olympic.bean.PublicData;
import com.hysm.olympic.http.HttpTool;
import com.hysm.olympic.tool.Date_tool; 
import com.hysm.olympic.tool.MyToast;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.Speek; 
import com.hysm.olympic.tool.Word_tool;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult; 
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle; 
import android.os.Handler;
import android.os.Message; 
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener; 
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;   
/**
 * ��ҳ Activity
 * @author songkai
 * �û�������¼�˻�   ����> ������ѧ��   ����> �����ʴ�/����ҳ��
 * ��������   ����> �˻���ȫ��֤  ����> ��������ҳ��
 * �˳�  ����> �˳�app
 *
 */
public class MainActivity extends Activity{
	
	private LinearLayout login_view;  
	private TextView prompt_text;  
	private EditText student_edit; 
	private Button login_button;
	private ImageView exit_img;
	
	private TextView set_text;
	 
	// ����ʶ�����
    private SpeechRecognizer recognizer; 
    //�����ϳɶ���
    private SpeechSynthesizer speaker; 
    //ʶ������ľ���
    private StringBuilder sentence = new StringBuilder() ; 
    //handler
 	private Handler mHandler;
 	
 	// ʶ��ɹ�
 	public static final int SPEECH_SUCCESS = 0;
 	// ʶ��ʧ��
 	public static final int SPEECH_FAIL = -1;
 	// ��ʼʶ��
 	public static final int SPEECH_START = 1;
 	// ʶ�����
 	public static final int SPEECH_ERROR = 2; 
    // ʶ�����
  	public static final int SPEECH_END = 3;
  	// ʶ�����
  	public static final int SPEECH_Progress = 4;
  	 
  	//���Կ�ʼ����
  	public static final int SAY_START =10;
  	//���Բ��Ž���
  	public static final int SAY_END =11;
  	//�������Ž���
  	public static final int SAY_Progress =12;
  	
  	public static final int Login_ok =100;
  	public static final int Login_err =101;
  	
  	 
  	public static String say_info;
  	 
  	 
  	//�����������   0 ��ӭ��   1 ˵ѧ��  2 �ѵ�¼
  	private int work_code = 1;
  	
  	private String student_name="",student_id="";
  	
  	private String rid,robotid,schoolcode,schoolname;
  	
  	private SharedPreferences preferences; 
	private AlertDialog set_alert;
	
	private long old_time = 0;
	
	private int islogining = 0;
	 
  	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // ���Activity����ջ
		setContentView(R.layout.activity_main);
  
	} 
	  
	
	/**
	 * ��ʼ��ҳ��
	 */
	private void init_view() {
		login_view = (LinearLayout)findViewById(R.id.main_login_view); 
		   
		prompt_text = (TextView)findViewById(R.id.main_prompt_text);
		 
		student_edit = (EditText)findViewById(R.id.main_student_edit);
		login_button = (Button)findViewById(R.id.main_login_button); 
		exit_img = (ImageView)findViewById(R.id.main_exit_img); 
		  
		set_text = (TextView)findViewById(R.id.main_set_text);
		
		Net_tool.checkNet(MainActivity.this);
	}
	/**
	 * ��ʼ������ʶ��/�����ϳ�
	 */
	private void init_crate() {
		
		Speek speek = new Speek(MainActivity.this);
		 
		//��ʼ����������
        recognizer = speek.getRecognizer();
        speaker = speek.getSpeaker();
		 
	} 
	
	@Override
	protected void onResume(){ 
		super.onResume();
		
		old_time = 0;
		
		init_view(); 
		init_crate(); 
		init_handler(); 
		init_listener();
		
		init_data();
		
		islogining = 0;
		work_code = 1;  
		student_name = "";
		student_id = "";
		 
		 
	}
	
	/**
	 * ��ʼ������
	 */
	private void init_data() {
		//�ж��Ƿ����� 
		preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE); 
		rid = preferences.getString("rid", "");
		robotid = preferences.getString("robotid", "");
		schoolcode = preferences.getString("schoolcode", "");
		schoolname = preferences.getString("schoolname", "");
		
		if(schoolcode.equals("")){ 
			show_set(); 
		}else{
			//ȡ����ǰ��¼������û���Ϣ
			SharedPreferences.Editor editor = preferences.edit();  
			 
			editor.putString("studentid", "");
			editor.putString("studentname", "");
			editor.putString("grade", "");
			editor.putString("classname", "");
			editor.putString("parent", "");
			editor.putString("phone", ""); 
			editor.putString("sid", "");
			 
			editor.commit();
			
			start_work();
		}
	}


	/**
	 * ��ʼ�������¼�
	 */
	private void init_listener(){
		
		set_text.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				
				if(recognizer.isListening()){
					recognizer.stopListening();
				}
				if(speaker.isSpeaking()){
					speaker.stopSpeaking();
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SetAct.class);
				startActivity(intent);
			}
		});
		
		login_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0){
				
				recognizer.stopListening();
				speaker.stopSpeaking();
				
				student_id = student_edit.getText().toString(); 
				if(student_id.equals("")){
					 
					say_info = "��˵������д����ѧ�š�"; 
					 if(recognizer.isListening()){
	    				recognizer.stopListening();
	    			}
	    			if(speaker.isSpeaking()){
	    				speaker.stopSpeaking();
	    			}
					init_crate(); 
					speaker.startSpeaking(say_info,synthesizerListener);
				}else{
					//
					islogining = 1; 
					new Thread(msgrunnable).start();
				}
			}
		});
		
		exit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0){
				
				if(recognizer.isListening()){
					recognizer.stopListening();
				}
				if(speaker.isSpeaking()){
					speaker.stopSpeaking();
				}
				recognizer.destroy();
				speaker.destroy();
				BaseApplication.getInstance().exit();
			}
		});
		 
	}
	
	
	private void init_handler() {
		mHandler = new Handler() { 
			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg); 
				int what = msg.what;
				
				switch (what) {
				case SPEECH_SUCCESS:// ʶ��ɹ� 
					//����ʱ��
					if(work_code != 2 && islogining != 1){
						old_time = Date_tool.get_time(); 
						String result = (String) msg.obj;  
						word_check(result);
					} 
					break; 
				case SPEECH_FAIL:// ʶ��ʧ�� 
					//��ʼ¼�� 
					recognizer.startListening(recognizerListener); 
					break; 
				case SPEECH_START: 
					
					break;
				case SPEECH_END:
					 
					break;
				case SPEECH_ERROR: 
					 
					String error = (String) msg.obj; 
					//Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
					if(work_code != 2 && islogining != 1){
						//��ʼ¼��  
						if(old_time == 0){
							//����ʱ��
							old_time = Date_tool.get_time();
							recognizer.startListening(recognizerListener); 
						}else{
							
								//����ʱ���Ƿ񳬹�2���ӣ�������
							if(Date_tool.check_time(old_time)){ 
								//��¼����
								if(recognizer.isListening()){
									recognizer.stopListening(); 
								}
								recognizer.destroy();
								if(speaker.isSpeaking()){
									speaker.stopSpeaking(); 
								}
								speaker.destroy();
								
								Intent intent = new Intent();
								intent.setClass(MainActivity.this, AdvertAct.class);
								startActivity(intent); 
							}else{
								recognizer.startListening(recognizerListener); 
							} 
						} 
					}
					break;
				case SPEECH_Progress: 
					break;
				case SAY_END:
					 
					
					//����ʱ��
					old_time = Date_tool.get_time();
					 
					if(work_code != 2){
						//��ʼ¼�� 
						recognizer.startListening(recognizerListener);
					}else{
						//��¼���� 
						if(recognizer.isListening()){
							recognizer.stopListening(); 
						}
						recognizer.destroy();
						if(speaker.isSpeaking()){
							speaker.stopSpeaking(); 
						}
						speaker.destroy();
						
						PublicData.Choose_Speak_state = 1;
						
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, ChooseAct.class);
						startActivity(intent); 
					}
					 
					break;
				case SAY_START:
					
					//����˵������
					if(!say_info.equals("")){ 
						new MyToast(MainActivity.this, say_info); 
					}  
					break;
				case SAY_Progress:
					  
					break;
				case Login_ok: 
					work_code = 2;
					recognizer.stopListening();
					init_crate(); 
					speaker.startSpeaking(say_info,synthesizerListener); 
					break;
				case Login_err:
					recognizer.stopListening();
					init_crate(); 
					speaker.startSpeaking(say_info,synthesizerListener);
					break;
				}
			}
 
        	
        };
	}
	 

	//��д������
    private RecognizerListener recognizerListener = new RecognizerListener(){

    	/**
    	* ��ʼ¼��
    	*/
		@Override
		public void onBeginOfSpeech() {
			Message msg=mHandler.obtainMessage();
			msg.what=SPEECH_START;
			mHandler.sendMessage(msg);
		}

		 /**
		* ����¼��
		*/
		@Override
		public void onEndOfSpeech() {
			Message msg=mHandler.obtainMessage();
			msg.what=SPEECH_END;
			mHandler.sendMessage(msg);
		}

		/**
		* �Ự��������ص��ӿ�
		* @param speechError
		*/
		@Override
		public void onError(SpeechError error) {
			// �ػ���������ʱ�ص��ĺ���
			String merror = error.getPlainDescription(true);// ��ȡ����������
			Message msg=mHandler.obtainMessage();
			msg.what=SPEECH_ERROR;
			msg.obj=merror;
			mHandler.sendMessage(msg);
		}

		/**
		* ��չ�ýӿ�
		*/
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			 
		}

		 /**
		* ��д����ص��ӿ� , ����Json��ʽ���
		* @param recognizerResult  json�������
		* @param b                 ����trueʱ�Ự����
		*/
		@Override
		public void onResult(RecognizerResult recognizerResult, boolean isLast) {
			// һ������»�ͨ��onResults�ӿڶ�η��ؽ����������ʶ�������Ƕ�ν�����ۼӣ�
			// ���ڽ���Json�Ĵ���ɲμ�MscDemo��JsonParser�ࣻ
			// isLast����trueʱ�Ự������
			/***
			 * ���--{"sn":1,"ls":false,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[
			 * {"w":"����","sc":0.00}]}]}
			 * ���--{"sn":2,"ls":true,"bg":0,"ed":0,"ws"
			 * :[{"bg":0,"cw":[{"w":"��","sc":0.00}]}]}
			 */
			try{
				//��λ�ȡ�������д��������
				JSONObject mRsult = new JSONObject(recognizerResult.getResultString());
				
				if (!mRsult.getBoolean("ls")) {
					
					JSONArray data = mRsult.getJSONArray("ws");
					for (int i = 0; i < data.length(); i++) {
						JSONObject w = data.getJSONObject(i);
						JSONArray array = w.getJSONArray("cw");
						for (int k = 0; k < array.length(); k++) {
							JSONObject cwdata = array.getJSONObject(k);
							sentence.append(cwdata.getString("w"));
						}
					} 
					String rusult = sentence.toString();
					Message msgs = mHandler.obtainMessage();
					msgs.what = SPEECH_Progress;
					msgs.obj = rusult;
					mHandler.sendMessage(msgs);
					
					
				}else{
					//ֹͣ����
					recognizer.stopListening(); 
					//����������������
					 
					String rusult = sentence.toString();
					
					//������д������
					sentence= new StringBuilder();
					
					if(rusult!= null && !rusult.equals("")){
						Message msgs = mHandler.obtainMessage();
						msgs.what = SPEECH_SUCCESS;
						msgs.obj = rusult;
						mHandler.sendMessage(msgs);
					}else{
						Message msgs = mHandler.obtainMessage();
						msgs.what = SPEECH_FAIL;
						msgs.obj = rusult;
						mHandler.sendMessage(msgs);
					}
					 
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/*@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			
		}*/

  
    };
    
    
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
		public void onSpeakProgress(int percent, int beginPos, int endPos){
			Message msg=mHandler.obtainMessage();
			msg.what=SAY_Progress;
			msg.obj = endPos;
			mHandler.sendMessage(msg);
		}
		/**
         * �ָ����Żص��ӿ�
         */
		@Override
		public void onSpeakResumed() {
			 
		}
    	
    };
 
	 

    /**
     * Ӧ�ÿ�ʼ����
     */
    private void start_work(){ 
		say_samething();
	}


	private void say_samething(){ 
		say_info = "���ã����ǰ���С��ʦ����˵������д����ѧ�š�";  
		init_crate(); 
		speaker.startSpeaking(say_info,synthesizerListener);
	}
	
	 protected void word_check(String str){
		 
		 if(work_code!= 2 && islogining != 1){
			   if(str.contains("����")){
				 
					if(recognizer.isListening()){
						recognizer.stopListening(); 
					}
					recognizer.destroy();
					if(speaker.isSpeaking()){
						speaker.stopSpeaking(); 
					}
					speaker.destroy();
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, SetAct.class);
					startActivity(intent);
				}else if(str.contains("�˳�")){
					 
					if(recognizer.isListening()){
						recognizer.stopListening(); 
					}
					recognizer.destroy();
					if(speaker.isSpeaking()){
						speaker.stopSpeaking(); 
					}
					speaker.destroy();
					BaseApplication.getInstance().exit();
				}else{
					 
					//ѧ��У��
					str = Word_tool.GetNum(str);
					if(str.equals("")){
						 
						say_info = "��˵��ѧ�Ų�׼ȷ��������˵��";
						 if(recognizer.isListening()){
			    				recognizer.stopListening();
			    		 }
		    			if(speaker.isSpeaking()){
		    				speaker.stopSpeaking();
		    			}
						init_crate(); 
						speaker.startSpeaking(say_info,synthesizerListener);
					}else{
						student_edit.setText(str);
					} 	 
			  }
		 } 
	 }
	  	
	private void show_set() {
		set_alert = new AlertDialog.Builder(MainActivity.this)
		.setTitle("��ʾ")//���öԻ���ı���
        .setMessage("����û�����û����˵�ѧУ���룬ǰ�����ã�")//���öԻ��������
        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                dialog.dismiss();
                if(recognizer.isListening()){
    				recognizer.stopListening();
    			}
    			if(speaker.isSpeaking()){
    				speaker.stopSpeaking();
    			}
                
                Intent intent = new Intent();
    			intent.setClass(MainActivity.this, SetAct.class);
    			startActivity(intent);
            }
        }).create();
		
		set_alert.show();
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//��������¼� 
		if(keyCode == KeyEvent.KEYCODE_BACK){ 
			if(recognizer.isListening()){
				recognizer.stopListening();
			}
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			recognizer.destroy();
			speaker.destroy();
			BaseApplication.getInstance().exit();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	Runnable msgrunnable = new Runnable() { 
		@Override
		public void run() {
			Map<String,String> params = new HashMap<String,String>();
			params.put("rid", rid);
			params.put("robotid", robotid);
			params.put("schoolcode", schoolcode);
			params.put("studentid", student_id);
			
			String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.Login_url, params); 
			try {
				JSONObject json = new JSONObject(result);
				
				if(json.getString("backcode").equals("200")){
					
					//�����½��Ϣ
					if(preferences== null){
						preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE); 
					}
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
					
					student_name = json.getString("name"); 
					say_info = "��ϲ"+student_name+"��¼�ɹ���";
					work_code = 2; 
					
					Message msg = mHandler.obtainMessage();
					msg.what = Login_ok;
					mHandler.sendMessage(msg);
					
					
					
					
				}else{ 
					Message msg = mHandler.obtainMessage();
					msg.what = Login_err;
					msg.obj = json.getString("backmsg");
					say_info = json.getString("backmsg");
					mHandler.sendMessage(msg);
				}
				
				islogining = 0;
				
			}catch(JSONException e){ 
				e.printStackTrace();
			}
		}
	};


  
}
