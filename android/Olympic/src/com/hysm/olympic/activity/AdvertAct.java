package com.hysm.olympic.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import com.hysm.olympic.R;
import com.hysm.olympic.http.HttpTool;
import com.hysm.olympic.http.LoadImg;
import com.hysm.olympic.tool.Net_tool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AdvertAct extends Activity {
	
	private ImageView img_view;
	
	private Handler handler;
	
	private static final int CHANGE_IMG= 1;
	
	private static final int Load_ok = 10;
	
	private static final int IMG_TIME = 6900;
	    
	private Bitmap bitmap = null;
	
	private int img_num = 0;
	
	private JSONArray img_arr=null;
	
	private Timer mytimer = null;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // 添加Activity到堆栈
		
		setContentView(R.layout.advert);
		
		img_view =(ImageView)findViewById(R.id.advert_img);
		
		Net_tool.checkNet(AdvertAct.this);
		 
		img_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 finish();
			}
		});
		
		init_handler();
		
		new Thread(msgrunnable).start(); 
		
		 
	}


	private void init_handler() {
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg);
				
				if(msg.what == CHANGE_IMG){
					if(bitmap!= null){
						img_view.setImageBitmap(bitmap); 
						//set_img(bitmap);
					}
					
					if(mytimer == null){
						//初始化定时器
						mytimer=new Timer();
						mytimer.schedule(mytimerTask, 0, 10000); 
					}
					
				}else if(msg.what == Load_ok){ 
					if(img_num > 0){
						//开始加载图片 
						new Thread(runnable).start(); 
					}
				}else if(msg.what == IMG_TIME){
					if(img_num > 0){
						//开始加载图片 
						new Thread(runnable).start(); 
					}
				}
			}
			 
		};
	}
	
	protected void set_img(Bitmap bmp) {
		WindowManager manager = this.getWindowManager();  
		DisplayMetrics outMetrics = new DisplayMetrics();  
		manager.getDefaultDisplay().getMetrics(outMetrics); 
		int height = outMetrics.heightPixels;
		
		height = height-20;
		
		int h = bmp.getHeight();
		int w = bmp.getWidth();
		
		int width = height*w/h;
		
		LayoutParams lp = img_view.getLayoutParams(); 
		lp.height = height;
		lp.width = width;
		
		img_view.setLayoutParams(lp); 
		img_view.setImageBitmap(bmp); 
	}



	private int get_img_id(){
		
		int n = (int)(Math.random()*img_num);
		 
		return n;
		 
	}
	
	
	/**
	 * 按时切换线程
	 */
	Runnable runnable = new Runnable() {
		
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
	
	Runnable msgrunnable = new Runnable() {
		
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
	
	TimerTask mytimerTask = new TimerTask() {
		
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = IMG_TIME; 
			handler.sendMessage(msg);
		}
	};
 
}
