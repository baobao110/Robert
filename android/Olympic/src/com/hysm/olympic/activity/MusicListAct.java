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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicListAct extends Activity{
	
	private ListView listview;
	private ImageView exit_img;
	
	private CatalogAdapter adapter;
	
	//数据加载成功
  	public static final int LOAD_OK = 200;
  	//数据加载失败
  	public static final int LOAD_ERR = 404;
  	
  	List<String> title_list =null,id_list= null; 
  	
  	 //handler
 	private Handler handler;
 	
 	private Thread msg_thread;  
 	
 	@Override
	protected void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // 添加Activity到堆栈 
		setContentView(R.layout.music_list);
		
		
		
	}
 	
 	@Override
	protected void onResume(){ 
		super.onResume();
		
		listview = (ListView)findViewById(R.id.music_list); 
		exit_img = (ImageView)findViewById(R.id.musiclist_exit_img);
		init_handler(); 
		init_listener();
		
		init_data(); 
		 
	}
 


	private void init_handler() {
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg);
				
				int what = msg.what;
				
				switch (what) {
				case LOAD_OK: 
					show_list(); 
					break; 
				default:
					break;
				}
			}
			
		};
	}
	
	private void init_data() {
		msg_thread = new Thread(msg_runnable);
		msg_thread.start();
	}
	
	private void init_listener() {
		listview.setOnItemClickListener(new OnItemClickListener(){ 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			 
				
				String _id = id_list.get(position);
				Intent intent = new Intent();
				intent.setClass(MusicListAct.this, MusicAct.class);
				intent.putExtra("music_id", _id);
				
				startActivity(intent);
				
			}
        });
		
		exit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 finish();
			}
		});
	}
	
	 Runnable msg_runnable = new Runnable(){ 
		@Override
		public void run() {
			 
			load_data();
		}
	};
	

	protected void show_list() {
		 if(PublicData.MusicList != null){
			 
			 title_list = new ArrayList<String>();
			 id_list = new ArrayList<String>();
			 
			 try{ 
				for(int i=0;i<PublicData.MusicList.length();i++){ 
					title_list.add(PublicData.MusicList.getJSONObject(i).getString("title"));
					id_list.add(PublicData.MusicList.getJSONObject(i).getString("_id"));
				} 
			}catch(Exception e){
				
			} 
			adapter = new CatalogAdapter(MusicListAct.this, title_list); 
			listview.setAdapter(adapter);
		 }
	}

	protected void load_data() {
		 Map<String,String> params = new HashMap<String,String>(); 
		 String result = HttpTool.send_Post(HttpTool.Service_url+HttpTool.All_music, params);
		 
		 try {
			PublicData.MusicList = new JSONArray(result);
		} catch (JSONException e){ 
			e.printStackTrace();
		} 
		 Message msg = handler.obtainMessage();
		 msg.what = LOAD_OK;
		 
		 handler.sendMessage(msg);		
	}

}
