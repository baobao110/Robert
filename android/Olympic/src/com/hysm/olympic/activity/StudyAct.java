package com.hysm.olympic.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
   




import com.hysm.olympic.R;
import com.hysm.olympic.bean.PublicData;
import com.hysm.olympic.http.HttpTool;
import com.hysm.olympic.http.LoadImg;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.Player;
import com.hysm.olympic.tool.Speek;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity; 
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;  
import android.view.KeyEvent; 
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener; 
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView; 
import android.widget.VideoView;

public class StudyAct extends Activity {
	
	private ImageView imgview;
	
	private ImageView exit_img1,exit_img2;
	
	private LinearLayout img_layout,video_layout,video_control;
	
	private FrameLayout frame_layout;
	
	private ImageView video_prev,video_play,video_next;
	
	private TextView video_info;
	
	private VideoView videoview;
	 
	private Handler handler; 
	
	private Thread msgthread;
	 
	private int study_num = 0;
	
	private String catalog_id ="";//章节目录id
	  
	//语音合成对象
    private SpeechSynthesizer speaker; 
    
  //语言开始播放
  	public static final int SAY_START =10;
  	//语言播放结束
  	public static final int SAY_END =11; 
  	
  //资料图片加载成功
  	public static final int IMG_LOAD_OK = 100;
  	//资料图片加载失败
  	public static final int IMG_LOAD_ERR = 101; 
   //数据加载成功
  	public static final int LOAD_OK = 200;
  	//数据加载失败
  	public static final int LOAD_ERR = 404;
  	
  	public static final int Video_End = 789;
  	
  	private int work_code = 0;//0学习  1进入课后练习
  	
  	private Player myplayer=null; //音乐播放器
  	
  	private float mPosX,mCurPosX;
  	
  	private boolean Can_Mave = false;//是否可以滑动
   

	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // 添加Activity到堆栈 
		setContentView(R.layout.study);
		
		Intent myintent = getIntent();
		catalog_id = myintent.getStringExtra("catalog_id");
		
		init_view(); 
		init_create();
		init_handler(); 
		init_data();
		
		init_listener();
		 
	}
 
	private void init_data() {
		work_code = 0;
		msgthread = new Thread(msg_runnable);
		
		msgthread.start();
	}

	private void init_view() {
		imgview = (ImageView)findViewById(R.id.study_img);
		img_layout = (LinearLayout)findViewById(R.id.study_img_view);
		video_layout = (LinearLayout)findViewById(R.id.study_video_view);
		
		video_control = (LinearLayout)findViewById(R.id.study_video_control);
		videoview = (VideoView)findViewById(R.id.study_video);
		
	    frame_layout = (FrameLayout)findViewById(R.id.study_vodeo_frame); 
	    video_info = (TextView)findViewById(R.id.study_video_txt);
	    video_prev = (ImageView)findViewById(R.id.study_video_prev);
	    video_next = (ImageView)findViewById(R.id.study_video_next);
	    video_play = (ImageView)findViewById(R.id.study_video_play);
	    
	    exit_img1 = (ImageView)findViewById(R.id.study_exit_img1);
	    exit_img2 = (ImageView)findViewById(R.id.study_exit_img2);
		
		videoview.setMediaController(new MediaController(StudyAct.this));
		videoview.setOnCompletionListener(new MyPlayerOnCompletionListener());
		videoview.setOnPreparedListener(new OnPreparedListener(){ 
			@Override
			public void onPrepared(MediaPlayer mp){ 
				videoview.pause(); 
				//取消自动播放 
			}
		});
		 
		//显示图片
		img_layout.setVisibility(View.VISIBLE);
		
		//关闭视频
		frame_layout.setVisibility(View.GONE);
		video_layout.setVisibility(View.VISIBLE);
		video_control.setVisibility(View.VISIBLE);
		
		Can_Mave = false;
		
		init_touch(); 
		 
		Net_tool.checkNet(StudyAct.this);
	}
	 
	 
	private void init_touch() {
		
		//图片的触屏事件
		imgview.setOnTouchListener(new OnTouchListener() { 
			@Override
			public boolean onTouch(View v, MotionEvent event){
				
				if(Can_Mave){
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN://按下
						 mPosX = event.getX(); 
	                     break;
					case MotionEvent.ACTION_MOVE://滑动
	                    mCurPosX = event.getX();
	                    
	                    break;
					 case MotionEvent.ACTION_UP://离开
						
						 if(mCurPosX - mPosX > 25){ 
							 //左滑 
							 Can_Mave = false; 
							 //加载上一个
							 into_prve(); 
						 }else if(mCurPosX - mPosX < -25){
							 //右滑 
							 Can_Mave = false; 
							 //加载 下一个
							 into_nextimg();
						 }
						 
						break;
					}
				}
				
				 
				return true;
			}
		});
		
		exit_img1.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				 if(speaker!= null){
					 if(speaker.isSpeaking()){
						 speaker.stopSpeaking();
					 }
					 speaker.destroy(); 
				 } 
				 if(myplayer != null){
					 myplayer.stop();
				 }
				 if(videoview!= null){
					 videoview.stopPlayback(); 
				 }
				  
				 finish();
				
			}
		});
		
		exit_img2.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				if(speaker!= null){
					 if(speaker.isSpeaking()){
						 speaker.stopSpeaking();
					 }
					 speaker.destroy(); 
				 } 
				 if(myplayer != null){
					 myplayer.stop();
				 }
				 if(videoview!= null){
					 videoview.stopPlayback(); 
				 }
				  
				 finish();
			}
		});
		
		
	}
 
	//视频监听事件
	class MyPlayerOnCompletionListener implements OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
        	 //视频播放结束 
			Message msg=handler.obtainMessage();
			msg.what=Video_End;
			handler.sendMessage(msg);
        }
    }

	private void init_create() {
		Speek speek = new Speek(StudyAct.this); 
		//初始化语音对象 
        speaker = speek.getSpeaker();
	}

	private void init_handler() {
		handler=new Handler(){

			@Override
			public void handleMessage(Message msg) { 
				super.handleMessage(msg);
				int what = msg.what; 
				switch (what) {
				case SAY_END: 
					
					if(work_code == 0){
						//加载下一个界面图片 
						into_nextimg(); 
					}else{
						//进入课后练习 
						speaker.destroy();
						Intent intent = new Intent(); 
						if(PublicData.QuestionList!= null && PublicData.QuestionList.length()> 0){
							intent.setClass(StudyAct.this, GuessAct.class); 
							intent.putExtra("type", "1");
						}else{
							intent.setClass(StudyAct.this, ChooseAct.class); 
						} 
						startActivity(intent);
					} 
					break;
				case SAY_START: 
					break; 
				
				case Video_End:
					//视频播放结束
					if(work_code == 0){
						//加载下一个界面图片 
						into_nextimg(); 
					}else{
						//进入课后练习 
						speaker.destroy();
						Intent intent = new Intent(); 
						if(PublicData.QuestionList!= null && PublicData.QuestionList.length()> 0){
							intent.setClass(StudyAct.this, GuessAct.class); 
							intent.putExtra("type", "1");
						}else{
							intent.setClass(StudyAct.this, ChooseAct.class); 
						} 
						startActivity(intent);
					} 
					break; 
				case IMG_LOAD_ERR: 
					 break;
				case IMG_LOAD_OK:
					
					 Bitmap bmp=(Bitmap)msg.obj; 
					//显示图片
					img_layout.setVisibility(View.VISIBLE);
					
					//关闭视频
					frame_layout.setVisibility(View.GONE);
					video_layout.setVisibility(View.VISIBLE);
					video_control.setVisibility(View.VISIBLE);
					 
					 imgview.setImageBitmap(bmp);
					 
					 //图片加载成功可以滑动
					 Can_Mave = true;
					  
					 //阅读内容 如果有语音，播放音频，没有合成语音 
					 try { 
						 if(PublicData.UiList.getJSONObject(study_num).has("audio")){
							 myplayer = new Player(handler);
							 myplayer.playUrl(HttpTool.Service_url+PublicData.UiList.getJSONObject(study_num).getString("audio"));
						 }else{
							 if(speaker.isSpeaking()){
								 speaker.stopSpeaking();
							 }
							 init_create();
							 speaker.startSpeaking(PublicData.UiList.getJSONObject(study_num).getString("info"),synthesizerListener); 
						 } 
					} catch (JSONException e){ 
						e.printStackTrace();
					}
					 break;
				 case LOAD_OK:
					 //加载第一个
					 into_frist();
					 
					 break;
				 case Player.Player_end: 
					 //清空播放器
					 if(myplayer!= null){
						myplayer.stop();
						myplayer = null;
					 } 
					 if(work_code == 0){
							//加载下一个界面图片 
							into_nextimg(); 
					  }else{ 
						//进入课后练习 
						  speaker.destroy();
							Intent intent = new Intent(); 
							if(PublicData.QuestionList!= null && PublicData.QuestionList.length()> 0){
								intent.setClass(StudyAct.this, GuessAct.class); 
								intent.putExtra("type", "1");
							}else{
								intent.setClass(StudyAct.this, ChooseAct.class); 
							} 
							startActivity(intent);
					 } 
					    break; 
				 case Player.Player_err: 
						break;
				 case Player.Player_update: 
						break;
			     case Player.Player_perpare: 
						break;
				 case Player.Player_start: 
						break;
				 case Player.Player_Progress: 
						break;
				}
			} 
			
		};
	}
	
	private void init_listener(){
		 
		video_prev.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) { 
				into_prve();
			}
		});
		
		video_next.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				 into_nextimg();
			}
		});
		
		video_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//显示图片
				img_layout.setVisibility(View.GONE); 
				//关闭视频
				frame_layout.setVisibility(View.VISIBLE);
				video_layout.setVisibility(View.VISIBLE);
				video_control.setVisibility(View.GONE);
				
				videoview.start();
			}
		});
	}
	  

	//语音合成监听器
    private SynthesizerListener synthesizerListener = new SynthesizerListener() {

    	 /**
         * 缓冲进度回调
         */
		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			 
		}
		/**
         * 会话结束回调接口，没有错误时，error为null
         */
		@Override
		public void onCompleted(SpeechError error) {
			 
			Message msg=handler.obtainMessage();
			msg.what=SAY_END;
			handler.sendMessage(msg);
		}

		 /**
	     * 会话事件回调接口
	     */
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			 
		}

		/**
         * 开始播放
         */
		@Override
		public void onSpeakBegin() {
			Message msg=handler.obtainMessage();
			msg.what=SAY_START;
			handler.sendMessage(msg);
		}
		/**
         * 暂停播放
         */
		@Override
		public void onSpeakPaused() {
			 
		}

		 /**
         * 播放进度回调
         */
		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			  
		}
		/**
         * 恢复播放回调接口
         */
		@Override
		public void onSpeakResumed() {
			 
		}
    	
    };
    
    /**
     * 加载图片线程
     */
    Runnable img_runnable = new Runnable(){ 
		@Override
		public void run(){
			 
			try {
				Bitmap bitmap = LoadImg.getImageBitmap(HttpTool.Service_url+PublicData.UiList.getJSONObject(study_num).getString("img"));
				
				Message msg = handler.obtainMessage();
				msg.what = IMG_LOAD_OK;
				msg.obj = bitmap;
				handler.sendMessage(msg);
				
			} catch (JSONException e) { 
				e.printStackTrace();
			}
			 
		}
	};
	
	/**
	 * 加载章节消息线程
	 */
	Runnable msg_runnable = new Runnable() {
		
		@Override
		public void run() {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("id", catalog_id); 
			String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.Study_url, params);
			
			try {
				JSONObject json = new JSONObject(result);
				PublicData.UiList = json.getJSONArray("uilist");
				PublicData.QuestionList = json.getJSONArray("questionlist");
			}catch(JSONException e){ 
				e.printStackTrace();
			}
			
			Message msg = handler.obtainMessage();
			msg.what = LOAD_OK;
			handler.sendMessage(msg);
		}
	};
	
	protected void into_frist() {
		 study_num = 0; 
		 Can_Mave = false;
		 if(PublicData.UiList!=null&& PublicData.UiList.length()>study_num){ 
			 try {
			   if(PublicData.UiList.getJSONObject(study_num).has("video")){
					
				   String video_url = PublicData.UiList.getJSONObject(study_num).getString("video");
				   video_info.setText(PublicData.UiList.getJSONObject(study_num).getString("info"));
				   video_play(video_url);
				   
			    }else{
					 Thread imgthread = new Thread(img_runnable);
					 imgthread.start();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 }
	}
 

	//进入下一个界面
	protected void into_nextimg() {
		study_num++;
		Can_Mave = false;
		if(PublicData.UiList!=null&& PublicData.UiList.length()>study_num){
			
			//清空播放器
			 if(myplayer!= null){
				myplayer.stop();
				myplayer = null;
			 }
			 if(speaker.isSpeaking()){
				 speaker.stopSpeaking();
			 }
			
			try {
			   if(PublicData.UiList.getJSONObject(study_num).has("video")){ 
				   String video_url = PublicData.UiList.getJSONObject(study_num).getString("video");
				   video_info.setText(PublicData.UiList.getJSONObject(study_num).getString("info"));
				   video_play(video_url);
				   
			    }else{
					 Thread imgthread = new Thread(img_runnable);
					 imgthread.start();
				}
			}catch(JSONException e){ 
				e.printStackTrace();
			} 
		}else{
			//全部课程学完，进入课后练习 
			 Can_Mave = false;
			work_code = 1; 
			if(PublicData.QuestionList!= null && PublicData.QuestionList.length()>0){
				 if(speaker.isSpeaking()){
					 speaker.stopSpeaking();
				 }
				init_create();
				speaker.startSpeaking("下面进入课后练习。",synthesizerListener);
			}else{
				 if(speaker.isSpeaking()){
					 speaker.stopSpeaking();
				 }
				init_create();
				speaker.startSpeaking("课程已学完。",synthesizerListener);
			}
			
			 
		}
	}
	
	//进入上一个界面
	protected void into_prve(){ 
		study_num --; 
		Can_Mave = false;
		if(study_num>= 0){
			//进入播放
			if(PublicData.UiList!=null){ 
				//清空播放器
				 if(myplayer!= null){
					myplayer.stop();
					myplayer = null;
				 }
				 if(speaker.isSpeaking()){
					 speaker.stopSpeaking();
				 }
				 
			    try{
				   if(PublicData.UiList.getJSONObject(study_num).has("video")){ 
					   String video_url = PublicData.UiList.getJSONObject(study_num).getString("video"); 
					   video_info.setText(PublicData.UiList.getJSONObject(study_num).getString("info"));
					   video_play(video_url);
					   
				    }else{
						 Thread imgthread = new Thread(img_runnable);
						 imgthread.start();
					}
				}catch(JSONException e){ 
					e.printStackTrace();
				} 
				
			}else{
				speaker.destroy(); 
				if(speaker.isSpeaking()){
					speaker.stopSpeaking();
				}
				speaker.destroy(); 
				finish();
			}
		}else{
			speaker.destroy(); 
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			speaker.destroy(); 
			finish();
		}
		
	}
	
	/**
	 * 播放视频
	 * @param video_url
	 */
	private void video_play(String video_url){
		
		//显示视频
		img_layout.setVisibility(View.GONE); 
		//关闭图片
		frame_layout.setVisibility(View.VISIBLE);
		video_layout.setVisibility(View.VISIBLE);
		video_control.setVisibility(View.VISIBLE);
		
		if(videoview!= null){
			Uri uri = Uri.parse(HttpTool.Service_url + video_url);  
			videoview.setVideoURI(uri); 
		}else{
			videoview = (VideoView)findViewById(R.id.study_video); 
			videoview.setMediaController(new MediaController(StudyAct.this));
			videoview.setOnCompletionListener(new MyPlayerOnCompletionListener());
			videoview.setOnPreparedListener(new OnPreparedListener(){ 
				@Override
				public void onPrepared(MediaPlayer mp){ 
					videoview.pause(); 
					//取消自动播放 
				}
			});
			
			Uri uri = Uri.parse(HttpTool.Service_url + video_url); 
			videoview.setVideoURI(uri); 
			
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//点击按键事件 
		if(keyCode == KeyEvent.KEYCODE_BACK){ 
			if(speaker!= null){
				 if(speaker.isSpeaking()){
					 speaker.stopSpeaking();
				 }
				 speaker.destroy(); 
			 } 
			 if(myplayer != null){
				 myplayer.stop();
			 }
			 if(videoview!= null){
				 videoview.stopPlayback(); 
			 }
			 
			 
			 finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	  
	 
}
