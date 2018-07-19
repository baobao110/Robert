package com.hysm.olympic.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException; 

import com.hysm.olympic.R;
import com.hysm.olympic.bean.PublicData;
import com.hysm.olympic.http.HttpTool;
import com.hysm.olympic.http.LoadImg;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.Speek;  
import com.hysm.olympic.tool.Word_tool; 
import com.iflytek.cloud.SpeechError; 
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity; 
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent; 
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * �ӵ�һ�⵽���һ��
 * ѡ����ABCD���ش�һ�����һ�η���
 * @author songkai 
 */
public class GuessAct extends Activity {
	
	private TextView name_text,studentid_text,score_text,time_text;  
	private TextView question_text,a_text,b_text,c_text,d_text;
	private TextView answer_text,content_text; 
	
	private ImageView tm_img; 
	private ImageView exit_img;
	private LinearLayout tm_layout;
	
	private int score= 0,question_num = 0,second = 0;
	
	private String type ="0";//1�κ�ϰ�� 2��ϰ3����
	
	private String question_str ="";
	private String a_str ="";
	private String b_str ="";
	private String c_str ="";
	private String d_str ="";
	
	private String true_answer = ""; 
	private String student_answer = "";
	
	private String content_info ="";
	
	private int work_code = 0;//0������   1���������   2 ��ȫ������ 3��ʱ
	 
    //�����ϳɶ���
    private SpeechSynthesizer speaker; 
    //ʶ������ľ���
    private StringBuilder sentence = new StringBuilder(); 
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
  	//���ⳬʱ
  	public static final int OVER_TIME = 500; 
  //����ʱ��
  	public static final int SECOND_TIME = 550;
  	
  	private static final int IMG_OK = 60;
  	
  	private SharedPreferences preferences;
  	private String student_name="",student_id="";
  	
  	private Thread msgthread; //timethread
  	
  	private int time_state = 0; //0����ʱ1��ʱ
  	
  	private String img_url ="";
  	
  	private Timer mTimer;
  	 
  	

	@Override
	protected void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // ���Activity����ջ
		setContentView(R.layout.guessing);
		
		Intent myintent = getIntent();
		type = myintent.getStringExtra("type");
		
		init_data();
		init_view(); 
		init_listener(); 
		init_crate(); 
		init_handler();
		
		load_data(); 
	}
  

	private void init_data() {
		preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE);
		student_id = preferences.getString("studentid", "");
		student_name = preferences.getString("studentname", ""); 
	}

	private void init_view() {
		
		name_text = (TextView)findViewById(R.id.guess_name_text);
		studentid_text = (TextView)findViewById(R.id.guess_studentid_text);
		
		score_text = (TextView)findViewById(R.id.guess_score_text);
		time_text = (TextView)findViewById(R.id.guess_time_text);
		
		exit_img = (ImageView)findViewById(R.id.guree_exit_img);
		
		question_text = (TextView)findViewById(R.id.guree_question_text);
		a_text =(TextView)findViewById(R.id.guree_a_text);
		b_text =(TextView)findViewById(R.id.guree_b_text);
		c_text =(TextView)findViewById(R.id.guree_c_text);
		d_text =(TextView)findViewById(R.id.guree_d_text);
		
		tm_layout = (LinearLayout)findViewById(R.id.guessing_tmimg_layout);
		tm_img = (ImageView)findViewById(R.id.guessing_tmimg);
		
		answer_text = (TextView)findViewById(R.id.guree_answer_text);
		content_text = (TextView)findViewById(R.id.guree_content_text);
		
		name_text.setText("������"+student_name);
		studentid_text.setText("ѧ�ţ�"+student_id);
		
		tm_layout.setVisibility(View.GONE);
		
		Net_tool.checkNet(GuessAct.this);
	}

	private void init_listener() {
		
		//�ֶ�ѡ���
		a_text.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View arg0){
				 
				//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="A"; 
    			check_answer(); 
			}
		});
		
		b_text.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View arg0){
				 
				//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="B"; 
    			check_answer(); 
			}
		});
		
		c_text.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View arg0){
				 
				//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="C"; 
    			check_answer(); 
			}
		});
		
		d_text.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View arg0){
				 
				//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="D"; 
    			check_answer(); 
			}
		});
		
		exit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(speaker.isSpeaking()){
					 speaker.stopSpeaking();
				 }
				 speaker.destroy();
				 
				 Intent intent = new Intent();
				 intent.setClass(GuessAct.this, ChooseAct.class);
				 startActivity(intent);
			}
		});
		
	}
	
	/**
	 * ��ʼ������ʶ��/�����ϳ�
	 */
	private void init_crate() {
		
		Speek speek = new Speek(GuessAct.this);
		 
		//��ʼ���������� 
        speaker = speek.getSpeaker();
		 
	}
	
	private void init_handler() {
		mHandler = new Handler() { 
			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg); 
				int what = msg.what;
				
				switch (what) { 
				case SAY_END: 
					  
					if(work_code == 0){
						//��Ŀ˵������
						//��ʼ¼�� 
						second = 10;
						time_text.setText("ʣ��10��");
						
						//��ʼ��ʱ
						time_state = 1;
						
						//recognizer.startListening(recognizerListener); 
					}else if(work_code == 1){
						//һ��ش����
						next_question();
					}else if(work_code == 2){
						//ȫ���������
						//recognizer.destroy();
						speaker.destroy();
						
						Intent intent = new Intent();
						intent.setClass(GuessAct.this, ChooseAct.class);
						startActivity(intent);
					}
					break;
				case SAY_START:
					
					break;
				case LOAD_OK:
					question_frist(); 
					break;
				case OVER_TIME:
					//recognizer.stopListening();
					speaker.stopSpeaking();
					 
					//������ʱ
	    			time_state = 0;
	    			work_code = 1;
	    			student_answer=""; 
	    			check_answer(); 
					break;
				case SECOND_TIME:
					time_text.setText("ʣ��"+second+"��");
					break;
				
				case IMG_OK:
					//ͼƬ���سɹ�
					
					 Bitmap bmp=(Bitmap)msg.obj;
					 tm_img.setImageBitmap(bmp);
					 
					 tm_layout.setVisibility(View.VISIBLE);
					 
					 load_question();
					
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
			 
			 Intent intent = new Intent();
			 intent.setClass(GuessAct.this, ChooseAct.class);
			 startActivity(intent);
			 
			
			//����ѡ��Ҳû
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void load_data() {
		
		time_state = 0;
		/*timethread = new Thread(time_runnable);
		timethread.start();*/
		
		mTimer=new Timer(); 
		mTimer.schedule(mTimerTask, 0, 1000);  
		 
		if(type.equals("1")){
			question_frist();
		}else{
			msgthread = new Thread(msg_runnable);
			msgthread.start();
		}
	}
	
	private void question_frist() {
		score = 0;
		question_num = 0;
		score_text.setText("0��");
		
		if(PublicData.QuestionList!= null && PublicData.QuestionList.length() > question_num){
			show_question();
		}
	} 
	
	//�ж��Ƿ���Ҫ����ͼƬ
	private void show_question() {
		work_code = 0;
		second = 10;
		time_text.setText("ʣ��10��"); 
		 
		try {
			if(PublicData.QuestionList.getJSONObject(question_num).has("img")){ 
				
				img_url = PublicData.QuestionList.getJSONObject(question_num).getString("img"); 
				new Thread(img_runnable).start(); 
				
			}else{
				tm_layout.setVisibility(View.GONE);
				load_question();
			}
		} catch (JSONException e) {
			 
			e.printStackTrace();
		} 
	}
	
	//��ʾ��������
	private void load_question() {
		
		int num = question_num+1;
		try {
			question_str = PublicData.QuestionList.getJSONObject(question_num).getString("question");
			a_str ="A."+PublicData.QuestionList.getJSONObject(question_num).getString("chooseA");
			b_str ="B."+PublicData.QuestionList.getJSONObject(question_num).getString("chooseB");
			c_str ="C."+PublicData.QuestionList.getJSONObject(question_num).getString("chooseC");
			d_str ="D."+PublicData.QuestionList.getJSONObject(question_num).getString("chooseD");
			
			content_info = PublicData.QuestionList.getJSONObject(question_num).getString("info");
			true_answer = PublicData.QuestionList.getJSONObject(question_num).getString("answer");
			
		} catch (JSONException e){ 
			e.printStackTrace();
		}
		 
		question_text.setText("��"+num+"�⣺"+question_str);
		a_text.setText(a_str);
		b_text.setText(b_str);
		c_text.setText(c_str);
		d_text.setText(d_str);
		
		answer_text.setText("");
		content_text.setText("");
		
		//������
		String  str = "��"+num+"�⣺"+question_str+" "+a_str+" "+b_str+" "+c_str+" "+d_str+"��ش�";
		
		//ֹͣ���� 
		if(speaker.isSpeaking()){
			speaker.stopSpeaking(); 
		} 
		init_crate(); 
		speaker.startSpeaking(str,synthesizerListener);
	}


	/**
	 * �ش�����
	 * @param result
	 */
	protected void answer_question(String result){
		if(result.equals("��һ��")){
			//������ʱ
			time_state = 0;
    		work_code = 1;
			student_answer=""; 
			check_answer();
    	}else{
    		 
    		if(Word_tool.check_A(result)){
    			//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="A"; 
    			check_answer(); 
    			
    		}else if(Word_tool.check_B(result)){
    			//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="B"; 
    			check_answer(); 
    		}else if(Word_tool.check_C(result)){
    			//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="C"; 
    			check_answer(); 
    		}else if(Word_tool.check_D(result)){
    			//������ʱ
    			time_state = 0;
    			work_code = 1;
    			student_answer="D"; 
    			check_answer(); 
    		}  
    	}
	}
	
	/**
     * У����Ŀ�𰸣���ʾ������������һ��
     */
    private void check_answer() {
    	if(true_answer.equals(student_answer)){ 
    		score = score+10;
			score_text.setText(score+"��");
		}
    	try {
			PublicData.QuestionList.getJSONObject(question_num).put("student_answer", student_answer);
		}catch(JSONException e){ 
			e.printStackTrace();
		}
    	 
		String str ="";
		if(true_answer.equals(student_answer)){
			str ="��ϲ���ش���ȷ��";
		}else{
			str ="��Ǹ������ˡ�";
		}
		content_text.setText(content_info+"���Դ��ǣ�"+true_answer); 
		
		str = content_info+"���Դ��ǣ�"+true_answer+str;
		
		//ֹͣ����
		 
		if(speaker.isSpeaking()){
			speaker.stopSpeaking();
		}
		init_crate(); 
		speaker.startSpeaking(str,synthesizerListener);
    	 
	}

	private void next_question() {
		 
		question_num++; 
		if(PublicData.QuestionList!= null && PublicData.QuestionList.length() > question_num){
			show_question();
		}else{
			
			work_code = 2;//ȫ���������
			
			String str="ȫ�������ѻش��������"+score+"�֡�";
			//ֹͣ����
			 
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			init_crate(); 
			speaker.startSpeaking(str,synthesizerListener);
			 
		}
	}


	/**
	 * �����½���Ϣ�߳�
	 */
	Runnable msg_runnable = new Runnable() {
		
		@Override
		public void run() {
			
			Map<String,String> params = new HashMap<String,String>();
		 
			String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.Test_url, params);
			
			try {
				JSONArray jarr = new JSONArray(result); 
				PublicData.QuestionList = jarr;
			}catch(JSONException e){ 
				e.printStackTrace();
			}
			
			Message msg = mHandler.obtainMessage();
			msg.what = LOAD_OK;
			mHandler.sendMessage(msg);
		}
	};
	
	
	 /******************************************************* 
		ͨ����ʱ����Handler�����½����� 
	  ******************************************************/
	TimerTask mTimerTask = new TimerTask(){

		@Override
		public void run() {
			if(time_state == 1){ 
				Message msg = mHandler.obtainMessage();
				if(second>0){
					second --;
					msg.what=SECOND_TIME;
				}else{
					msg.what=OVER_TIME;
				} 
				mHandler.sendMessage(msg);
			}
		}
		
	};
	 
	
	Runnable img_runnable = new Runnable(){
		
		@Override
		public void run() {  
			Bitmap bitmap = LoadImg.getImageBitmap(HttpTool.Service_url+img_url); 
			Message msg = mHandler.obtainMessage();
			msg.what = IMG_OK;
			msg.obj = bitmap;
			mHandler.sendMessage(msg); 
		}
	};
	

     

}
