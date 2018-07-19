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
import com.hysm.olympic.tool.Music_player;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MusicAct extends Activity {

	private TextView music_name,music_now,music_all;
	private SeekBar music_bar;
	private ImageView music_prev,music_pause,music_next;
	
	private ImageView music_advert;
	private ImageView exit_img;
	
	private Handler handler;
	 
	private int this_num = 0;
	
	private Music_player music_player;
	
	String music_id = "";
	
	private static final int CHANGE_IMG= 6700;
	
	private static final int Load_ok = 6800;
	
	private static final int IMG_TIME = 6900;
	
	private boolean open = false;
	
	private Bitmap bitmap = null;
	
	private int img_num = 0;
	
	private JSONArray img_arr=null;
	
	private Timer mytimer = null;
	
	private boolean is_progress= false;

	@Override
	protected void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);
		
		
		
		Intent intent = getIntent();
		music_id = intent.getStringExtra("music_id");
		
		init_view();
		
		init_data();
		
		init_handler(); 
		
		init_music();
		
		init_listener();
		
		init_advert();
		
	}
 

	private void init_view() {
		music_name = (TextView)findViewById(R.id.music_name);
		music_now = (TextView)findViewById(R.id.music_now);
		music_all = (TextView)findViewById(R.id.music_time);
		
		music_bar = (SeekBar)findViewById(R.id.music_seekbar);
		
		music_prev = (ImageView)findViewById(R.id.music_prev);
		music_pause = (ImageView)findViewById(R.id.music_pause);
		music_next = (ImageView)findViewById(R.id.music_next);
		
		music_advert = (ImageView)findViewById(R.id.music_advert);
		exit_img = (ImageView)findViewById(R.id.music_exit_img);
	}
	
	private void init_data() {
		  if(PublicData.MusicList!= null){
			  
			  for(int i=0;i<PublicData.MusicList.length();i++){
				  try {
					if((PublicData.MusicList.getJSONObject(i).getString("_id")).equals(music_id)){
						this_num = i;
					  }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
		  }
	}

	private void init_handler() {
		 
		handler = new Handler(){ 
			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg); 
				int what = msg.what;
				
				switch (what) {
				case Music_player.Player_start: 
					try {
						music_name.setText(PublicData.MusicList.getJSONObject(this_num).getString("title"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					music_pause.setImageResource(R.drawable.music_pause);
					break;
				case Music_player.Player_pause: 
					music_pause.setImageResource(R.drawable.music_play);
					break;
				case Music_player.Player_playing: 
					music_pause.setImageResource(R.drawable.music_pause);
					break;
				case Music_player.Player_Progress: 
					
					//设置时间进度  
					music_now.setText(music_player.now_time);
					music_all.setText(music_player.all_time); 
					is_progress = true;
					music_bar.setProgress(music_player.progress); 
					is_progress = false;
					
					break;
				case Music_player.Player_end: 
					play_next();
					break;
				case Load_ok: 
					//前往加载图片
					if(img_num > 0){
						Thread imgthread = new Thread(imgrunnable);
						imgthread.start();
					}
					
					break;
				case CHANGE_IMG: 
					if(bitmap!= null){
						music_advert.setImageBitmap(bitmap);
					}
					
					if(mytimer==null){
						//初始化定时器
						mytimer=new Timer();
						mytimer.schedule(mytimerTask, 0, 10000); 
					}
					
					break;
				case IMG_TIME:
					//前往加载图片
					if(img_num > 0){
						Thread imgthread = new Thread(imgrunnable);
						imgthread.start();
					}
					break;
				default:
					break;
				}
				
			} 
		};
		
	}
 
	private void init_music() {
		music_player = new Music_player(handler); 
		
		try {
			music_player.playUrl(HttpTool.Service_url+PublicData.MusicList.getJSONObject(this_num).getString("mp3"));
			
			music_name.setText(PublicData.MusicList.getJSONObject(this_num).getString("title"));
		} catch (JSONException e) { 
			e.printStackTrace();
		} 
		music_pause.setImageResource(R.drawable.music_pause);
	}

	private void init_listener() {
		
		music_prev.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View arg0) {
				 play_prev(); 
			}
		});
		
		music_pause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(music_player != null){
					music_player.pause();
				}
				
			}
		});
		
		music_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				play_next();
			}
		});
		
		music_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				 
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				 
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(music_player != null && is_progress == false){
					music_player.seekToPositon(progress);
					music_pause.setImageResource(R.drawable.music_pause);
				}
				
			}
		});
		
		exit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(music_player != null){
					music_player.stop();
				} 
				 finish();
			}
		});
		
	}
	
	protected void play_prev(){
		if(music_player != null){
			music_player.stop();
			
			this_num --; 
			if(this_num < 0){
				this_num = PublicData.MusicList.length()-1;
			}
			try {
				music_player.playUrl(HttpTool.Service_url+PublicData.MusicList.getJSONObject(this_num).getString("mp3"));
				
				music_name.setText(PublicData.MusicList.getJSONObject(this_num).getString("title"));
			} catch (JSONException e) { 
				e.printStackTrace();
			} 
			music_pause.setImageResource(R.drawable.music_pause); 
		} 
	}

	protected void play_next() {
		if(music_player != null){
			music_player.stop();
			
			this_num ++; 
			if(this_num >= PublicData.MusicList.length()){
				this_num = 0;
			}
			try {
				music_player.playUrl(HttpTool.Service_url+PublicData.MusicList.getJSONObject(this_num).getString("mp3"));
				
				music_name.setText(PublicData.MusicList.getJSONObject(this_num).getString("title"));
			} catch (JSONException e) { 
				e.printStackTrace();
			}  
			music_pause.setImageResource(R.drawable.music_pause); 
		} 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//点击按键事件 
		if(keyCode == KeyEvent.KEYCODE_BACK){ 
			if(music_player != null){
				music_player.stop();
			} 
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void init_advert() {
		//加载广告图片
		
		Thread thread = new Thread(msgrunnable);
		thread.start();
	}
	
	private int get_img_id(){
		
		int n = (int)(Math.random()*img_num); 
		return n;
		 
	}
	
	Runnable msgrunnable = new Runnable(){ 
		@Override
		public void run() {
			 
			Map<String,String> params = new HashMap<String,String>();
			String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.Advert_data, params);
			
			try {
				img_arr = new JSONArray(result);
				
				if(img_arr!= null && img_arr.length()>0){
					img_num = img_arr.length();
				}
				
				Message msg = handler.obtainMessage();
				msg.what = Load_ok; 
				handler.sendMessage(msg);
				
			} catch (JSONException e){ 
				e.printStackTrace();
			}
		}
	};
	
	Runnable imgrunnable = new Runnable() {
		
		@Override
		public void run() {
			//加载图片 
			int n = get_img_id(); 
			try {
				String imgurl = HttpTool.Service_url+img_arr.getJSONObject(n).getJSONArray("uilist").getJSONObject(0).getString("img");
				
				bitmap = LoadImg.getImageBitmap(imgurl);
				
			} catch (JSONException e) { 
				e.printStackTrace();
			} 
			Message msg = handler.obtainMessage();
			msg.what = CHANGE_IMG; 
			handler.sendMessage(msg);
		}
	};
	
	TimerTask mytimerTask = new TimerTask() {
		
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = IMG_TIME; 
			handler.sendMessage(msg);
		}
	};
}
