package com.hysm.olympic.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hysm.olympic.R;
import com.hysm.olympic.tool.Speek;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AskAct extends Activity {
	
	private TextView question_text;
	private LinearLayout img_view,text_view;
	private ImageView imgview;
	private TextView img_info,text_info;
	
	//private Thread thread;
	
	private Bitmap bitmap; 
	private Handler handler;
	
	private String content_info ="2022�걱���������˻ᣨXXIV Olympic Winter Games����24�춬������ƥ���˶��ᣬ����2022��2��4����2022��2��20�����л����񹲺͹������к��żҿ������Ͼ��С������й���ʷ�ϵ�һ�ξٰ춬�����˻ᣬ�������żҿ�ͬΪ������У�Ҳ���й��̱������˻ᡢ�Ͼ���»���й������ξٰ�İ������¡�";
		
	 
	// ����ʶ�����
    private SpeechRecognizer recognizer; 
    //�����ϳɶ���
    private SpeechSynthesizer speaker; 
    //ʶ������ľ���
    private StringBuilder sentence = new StringBuilder() ; 
     
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
  	
  //����ͼƬ���سɹ�
  	public static final int IMG_LOAD_OK = 100;
  	//����ͼƬ����ʧ��
  	public static final int IMG_LOAD_ERR = 101;
  	  
	@Override
	protected void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState); 
		BaseApplication.getInstance().addActivity(this); // ���Activity����ջ
		setContentView(R.layout.ask);
		 
		init_data();
		init_view(); 
		init_listener();
		 
		init_crate(); 
		//thread = new Thread(runnable); 
		init_handler();
		
		 
	}
  
	private void init_data() {
		 
	}

	private void init_view() {
		 
		question_text =(TextView)findViewById(R.id.ask_question_text);
		img_view = (LinearLayout)findViewById(R.id.ask_img_view);
		text_view = (LinearLayout)findViewById(R.id.ask_text_view);
		imgview = (ImageView)findViewById(R.id.ask_img_img);
		
		img_info =(TextView)findViewById(R.id.ask_img_info);
		text_info =(TextView)findViewById(R.id.ask_text_info);
		 
	}

	private void init_listener(){
		 
	}
	
	/**
	 * ��ʼ������ʶ��/�����ϳ�
	 */
	private void init_crate() {
		
		Speek speek = new Speek(AskAct.this);
		 
		//��ʼ����������
        recognizer = speek.getRecognizer();
        speaker = speek.getSpeaker();
		 
	}
	
	private void init_handler() {
		handler=new Handler(){ 
			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg);
				 
				int what = msg.what; 
				switch (what) {
				case SPEECH_SUCCESS:// ʶ��ɹ�
					 
					String result = (String) msg.obj;  
					word_check(result);
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
					 
					//��ʼ¼�� 
					recognizer.startListening(recognizerListener); 
					break;
				case SPEECH_Progress:
					question_text.setText((String) msg.obj);
					break; 
				case SAY_END: 
					  
					//��ʼ¼�� 
					recognizer.startListening(recognizerListener); 
					break;
				case SAY_START: 
					break;
				 case IMG_LOAD_ERR:
					 bitmap = null;
					 imgview.setVisibility(View.GONE);
					 break;
				 case IMG_LOAD_OK:
					 imgview.setVisibility(View.VISIBLE);
					 imgview.setImageBitmap(bitmap);
					 break;
				}
			}
			
		};
	}
  
	 
	
	
	
	
	@Override
	protected void onResume() { 
		super.onResume();
		
		speaker.startSpeaking("��������С��ʦ��ѯһЩ����֪ʶ,Ҳ�����˳������ʴ�",synthesizerListener);
	}


	//��д������
    private RecognizerListener recognizerListener = new RecognizerListener(){

    	/**
    	* ��ʼ¼��
    	*/
		@Override
		public void onBeginOfSpeech() {
			Message msg=handler.obtainMessage();
			msg.what=SPEECH_START;
			handler.sendMessage(msg);
		}

		 /**
		* ����¼��
		*/
		@Override
		public void onEndOfSpeech() {
			Message msg=handler.obtainMessage();
			msg.what=SPEECH_END;
			handler.sendMessage(msg);
		}

		/**
		* �Ự��������ص��ӿ�
		* @param speechError
		*/
		@Override
		public void onError(SpeechError error) {
			// �ػ���������ʱ�ص��ĺ���
			String merror = error.getPlainDescription(true);// ��ȡ����������
			Message msg=handler.obtainMessage();
			msg.what=SPEECH_ERROR;
			msg.obj=merror;
			handler.sendMessage(msg);
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
					Message msgs = handler.obtainMessage();
					msgs.what = SPEECH_Progress;
					msgs.obj = rusult;
					handler.sendMessage(msgs);
					
				}else{
					//ֹͣ����
					recognizer.stopListening(); 
					//����������������
					 
					String rusult = sentence.toString();
					
					//������д������
					sentence= new StringBuilder();
					
					if(rusult!= null && !rusult.equals("")){
						Message msgs = handler.obtainMessage();
						msgs.what = SPEECH_SUCCESS;
						msgs.obj = rusult;
						handler.sendMessage(msgs);
					}else{
						Message msgs = handler.obtainMessage();
						msgs.what = SPEECH_FAIL;
						msgs.obj = rusult;
						handler.sendMessage(msgs);
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
		public void onCompleted(SpeechError error) {
			 
			Message msg=handler.obtainMessage();
			msg.what=SAY_END;
			handler.sendMessage(msg);
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
			Message msg=handler.obtainMessage();
			msg.what=SAY_START;
			handler.sendMessage(msg);
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
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			  
		}
		/**
         * �ָ����Żص��ӿ�
         */
		@Override
		public void onSpeakResumed() {
			 
		}
    	
    };
  
    
   /* private void load_img() {
		 thread.start();
	}*/
 
    protected void word_check(String result) {
		 question_text.setText(result);
		 
		 if(result.contains("�˳�")){
			 recognizer.stopListening();
			 speaker.stopSpeaking();
			 finish();
		 }else{
			 imgview.setVisibility(View.VISIBLE);
			 img_info.setText(content_info); 
			 speaker.startSpeaking(content_info,synthesizerListener);
		 }
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//��������¼� 
		if(keyCode == KeyEvent.KEYCODE_BACK){ 
			BaseApplication.getInstance().exit();
		}
		return super.onKeyDown(keyCode, event);
	}


	
}
