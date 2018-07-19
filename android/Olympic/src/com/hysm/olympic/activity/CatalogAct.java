package com.hysm.olympic.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException; 

import com.hysm.olympic.R;
import com.hysm.olympic.adapter.CatalogAdapter;
import com.hysm.olympic.bean.PublicData;
import com.hysm.olympic.http.HttpTool;
import com.hysm.olympic.tool.Date_tool;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.Speek; 
import com.iflytek.cloud.SpeechError; 
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView; 
import android.widget.TextView;

public class CatalogAct extends Activity {
	
	private TextView name_text,studentid_text;
	
	private ImageView exit_img;
	
	private ListView listview;
	
	private CatalogAdapter adapter;
	
	 
    //�����ϳɶ���
    private SpeechSynthesizer speaker; 
    
    //handler
 	private Handler mHandler;
 	
 	private Thread msg_thread; 
 	  
  	//���Կ�ʼ����
  	public static final int SAY_START =10;
  	//���Բ��Ž���
  	public static final int SAY_END =11;
  	
  	//���ݼ��سɹ�
  	public static final int LOAD_OK = 200;
  	//���ݼ���ʧ��
  	public static final int LOAD_ERR = 404;
  	 
  	private String student_name="",student_id="";
  	
  	private SharedPreferences preferences;
   
  	List<String> title_list =null,id_list= null; 
  	
  	private long old_time = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // ���Activity����ջ 
		setContentView(R.layout.catalog);
		
		
	}

	private void init_view() {
		name_text = (TextView)findViewById(R.id.catalog_name_text);
		studentid_text = (TextView)findViewById(R.id.catalog_studentid_text);
		listview = (ListView)findViewById(R.id.catalog_list);
		
		exit_img = (ImageView)findViewById(R.id.catalog_exit_img);
		
		Net_tool.checkNet(CatalogAct.this);
	}

	private void init_listener() {
		listview.setOnItemClickListener(new OnItemClickListener(){ 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if(speaker.isSpeaking()){
					speaker.stopSpeaking();
				}
				speaker.destroy();
				
				String catalog_id = id_list.get(position);
				Intent intent = new Intent();
				intent.setClass(CatalogAct.this, StudyAct.class);
				intent.putExtra("catalog_id", catalog_id);
				
				startActivity(intent);
				
			}
        });
		
		exit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(speaker.isSpeaking()){
					 speaker.stopSpeaking();
				 }
				 speaker.destroy(); 
				 finish();
			}
		});
	}

	private void init_crate() {
		Speek speek = new Speek(CatalogAct.this);
		 
		//��ʼ���������� 
        speaker = speek.getSpeaker();
	}

	private void init_handler() {
		mHandler = new Handler() { 
			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg); 
				int what = msg.what;
				
				switch (what){
				   
				case SAY_END:
					 
					if(old_time == 0){
						//����ʱ��
						old_time = Date_tool.get_time(); 
						//speaker.startSpeaking("��ѡ��ѧϰ���ݡ�",synthesizerListener);
						 
					}else{
						
						//����ʱ���Ƿ񳬹�2���ӣ�������
						if(Date_tool.check_time(old_time)){ 
							 
							speaker.stopSpeaking();
							speaker.destroy();
							
							Intent intent = new Intent();
							intent.setClass(CatalogAct.this, AdvertAct.class);
							startActivity(intent); 
						}else{
							speaker.startSpeaking("��ѡ��ѧϰ���ݡ�",synthesizerListener);
						}
					}
					break;
				case SAY_START:  
					break;
				case LOAD_OK:
					
					//���ݼ��سɹ�
					show_speek();
					
					break;
				}
			}
 
        	
        };
	}
	
	 

	@Override
	protected void onResume(){ 
		super.onResume();
		
		old_time = 0;
		
		init_view(); 
		init_listener(); 
		init_crate(); 
		init_handler();
		
		msg_thread = new Thread(msg_runnable);
		
		
		init_data(); 
		
		//���ݼ��سɹ������Ķ�
		 
	}

	private void init_data() {
		preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE);
		student_id = preferences.getString("studentid", "");
		student_name = preferences.getString("studentname", "");
		
		name_text.setText("������"+student_name);
		studentid_text.setText("ѧ�ţ�"+student_id);
		 
		
		//���ݼ��سɹ�����ʾ
		msg_thread.start();
		 
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
    
    
    Runnable msg_runnable = new Runnable(){ 
		@Override
		public void run() {
			 
			load_data();
		}
	};
	
	 protected void load_data(){
		 
		 Map<String,String> params = new HashMap<String,String>(); 
		 String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.Catalog_url, params);
		 
		 try {
			PublicData.CatalogList = new JSONArray(result);
		} catch (JSONException e){ 
			e.printStackTrace();
		} 
		 Message msg = mHandler.obtainMessage();
		 msg.what = LOAD_OK;
		 
		 mHandler.sendMessage(msg);
		 
	 }
	 
	 protected void show_speek() {
		
		if(PublicData.CatalogList != null){
			
			title_list = new ArrayList<String>();
			id_list = new ArrayList<String>();
			
			String str = "";
			
			try{ 
				for(int i=0;i<PublicData.CatalogList.length();i++){
					str = str +PublicData.CatalogList.getJSONObject(i).getString("title");
					title_list.add(PublicData.CatalogList.getJSONObject(i).getString("title"));
					id_list.add(PublicData.CatalogList.getJSONObject(i).getString("_id"));
				} 
			}catch(Exception e){
				
			} 
			adapter = new CatalogAdapter(CatalogAct.this, title_list); 
			listview.setAdapter(adapter);
			
			if(PublicData.Catalog_Speak_state == 1){
				speaker.startSpeaking("��ӭ���������֪ʶѧϰ����ѡ��ѧϰ���ݡ�"+str,synthesizerListener);
				PublicData.Catalog_Speak_state = 0;
			} 
			
		} 
		
	 }
    
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
 
}
