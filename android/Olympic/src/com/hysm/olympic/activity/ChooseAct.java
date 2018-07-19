package com.hysm.olympic.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hysm.olympic.R;
import com.hysm.olympic.bean.PublicData;
import com.hysm.olympic.tool.Date_tool; 
import com.hysm.olympic.tool.MyToast;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.Speek; 
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult; 
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent; 
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView; 

public class ChooseAct extends Activity {

	private TextView name_text,studentid_text;
	private TextView ask_text,guess_text,pk_text;
	private ImageView bookimg,lximg,pkimg,musicimg;
	
	private ImageView exit_img;
	
	
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
  	 
  	private String student_name="",student_id="";
  	
  	private SharedPreferences preferences;
  	
  	private long old_time = 0; 
  	
  	private int img_show = 0;
  	
  	private String say_info="";
  	
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // ���Activity����ջ
		setContentView(R.layout.choose);
		  
	}
  

	private void init_view() {
		name_text = (TextView)findViewById(R.id.choose_name_text);
		studentid_text = (TextView)findViewById(R.id.choose_studentid_text);
		
		bookimg = (ImageView)findViewById(R.id.choose_book_img);
		pkimg = (ImageView)findViewById(R.id.choose_pk_img);
		lximg = (ImageView)findViewById(R.id.choose_lx_img);
		musicimg = (ImageView)findViewById(R.id.choose_music_img);
		
		exit_img = (ImageView)findViewById(R.id.choose_exit_img);
		
		ask_text = (TextView)findViewById(R.id.choose_ask_text);
		guess_text = (TextView)findViewById(R.id.choose_guess_text);
		pk_text = (TextView)findViewById(R.id.choose_pk_text);
		img_show = 0;
		 
		
		Net_tool.checkNet(ChooseAct.this);
	}

	private void init_listener() {
		bookimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				PublicData.Catalog_Speak_state = 1;
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, CatalogAct.class);
				startActivity(intent);
				 
			}
		});
		
		lximg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, GuessAct.class);
				intent.putExtra("type", "2"); 
				startActivity(intent);
				 
			}
		});
		
		pkimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, GameAct.class); 
				startActivity(intent); 
				
			}
		});
		
		musicimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, MusicListAct.class); 
				startActivity(intent); 
				
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
				Intent intent = new Intent(ChooseAct.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
	
	@Override
	protected void onResume(){ 
		super.onResume();
		
		old_time = 0;
		
		init_view(); 
		init_listener(); 
		init_crate(); 
		init_handler();  
		
		init_data();
		
		if(PublicData.Choose_Speak_state == 1){
			say_info = "������ѡ�����֪ʶѧϰ������֪ʶ����Ҳ����ѡ������������͡�"; 
			speaker.startSpeaking(say_info,synthesizerListener); 
			PublicData.Choose_Speak_state = 0;
		}else{
			
			//ֱ�ӽ�������ʶ��
			
			recognizer.startListening(recognizerListener);
			
		}
		
		
	}
	
	private void init_data() {
		preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE);
		student_id = preferences.getString("studentid", "");
		student_name = preferences.getString("studentname", "");
		
		name_text.setText(student_name);
		studentid_text.setText(student_id);
		
		if(img_show == 0){
			img_show = 1;
			bookimg.setVisibility(View.VISIBLE);
			ask_text.setVisibility(View.VISIBLE);
			
			lximg.setVisibility(View.GONE);
			guess_text.setVisibility(View.GONE);
			
			pkimg.setVisibility(View.VISIBLE);
			pk_text.setVisibility(View.VISIBLE);
		}
	}


	/**
	 * ��ʼ������ʶ��/�����ϳ�
	 */
	private void init_crate() {
		
		Speek speek = new Speek(ChooseAct.this);
		 
		//��ʼ����������
        recognizer = speek.getRecognizer();
        speaker = speek.getSpeaker();
		 
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
					old_time = Date_tool.get_time(); 
					
					String result = (String) msg.obj;  
					word_check(result);
					break; 
				case SPEECH_FAIL:// ʶ��ʧ��
					recognizer.startListening(recognizerListener); 
					break; 
				case SPEECH_START:
					 
					break;
				case SPEECH_END:
					 
					break;
				case SPEECH_ERROR: 
					String error = (String) msg.obj; 
					
					if(old_time == 0){
						//����ʱ��
						old_time = Date_tool.get_time();
						recognizer.startListening(recognizerListener); 
					}else{
						
						//����ʱ���Ƿ񳬹�2���ӣ�������
						if(Date_tool.check_time(old_time)){ 
							if(recognizer.isListening()){
								recognizer.stopListening(); 
							}
							recognizer.destroy();
							if(speaker.isSpeaking()){
								speaker.stopSpeaking(); 
							}
							speaker.destroy();
							
							Intent intent = new Intent();
							intent.setClass(ChooseAct.this, AdvertAct.class);
							startActivity(intent); 
						}else{
							recognizer.startListening(recognizerListener); 
						}
					}
					  
					break;
				case SPEECH_Progress: 
					 
					break;
				case SAY_END:
					
					 
					//����ʱ��
					old_time = Date_tool.get_time();
					 
					//��ʼ¼�� 
					recognizer.startListening(recognizerListener);
					
					break;
				case SAY_START:
					
					if(say_info!= null){
						 new MyToast(ChooseAct.this, say_info);
					}
					 
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
				JSONArray data = mRsult.getJSONArray("ws");
				for (int i = 0; i < data.length(); i++) {
					JSONObject w = data.getJSONObject(i);
					JSONArray array = w.getJSONArray("cw");
					for (int k = 0; k < array.length(); k++) {
						JSONObject cwdata = array.getJSONObject(k);
						sentence.append(cwdata.getString("w"));
					}
				}
				
				if (!mRsult.getBoolean("ls")) {
					 
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
			if(recognizer.isListening()){
				recognizer.stopListening();
			}
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			recognizer.destroy();
			speaker.destroy(); 
			Intent intent = new Intent(ChooseAct.this, MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

  
    
    protected void word_check(String str) {
		 
    	if(str.contains("�˳�")){
    		if(recognizer.isListening()){
				recognizer.stopListening();
			}
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			recognizer.destroy();
			speaker.destroy(); 
			BaseApplication.getInstance().exit();
		}else if(str.contains("ѧϰ") ){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			
			PublicData.Catalog_Speak_state = 1;
			
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, CatalogAct.class);
			startActivity(intent);
		}/*else if(str.contains("��ϰ")){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, GuessAct.class);
			intent.putExtra("type", "2"); 
			startActivity(intent);
		}*/else if(str.contains("pk") || str.contains("����")){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, GameAct.class); 
			startActivity(intent);
		}else if(str.contains("����") || str.contains("����")){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, MusicListAct.class); 
			startActivity(intent);
		}else{ 
			
			init_crate();
			if(PublicData.Choose_Speak_state == 1){
				say_info = "�����С��ʦ������ѡ�����֪ʶѧϰ������֪ʶ���𣬻���ѡ������������͡�"; 
				speaker.startSpeaking(say_info,synthesizerListener);
				PublicData.Choose_Speak_state = 0;
			}else{
				//ֱ�ӽ�������ʶ�� 
				recognizer.startListening(recognizerListener);
			}
			
		}
	}
    
    
   

	
}
