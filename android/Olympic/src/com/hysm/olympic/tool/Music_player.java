package com.hysm.olympic.tool;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;

public class Music_player {
 
	public MediaPlayer mediaPlayer;  
	 private Handler handler;
	 private Timer mTimer;
	 
	 public static final int Player_start = 4000; 
	 public static final int Player_err = 4001;
	 public static final int Player_perpare = 4002;
	 public static final int Player_update = 4003;
	 public static final int Player_Progress = 4004;
	 public static final int Player_end = 4005;
	 
	 public static final int Player_pause = 4006;
	 public static final int Player_playing = 4007;
	 
	 public int progress = 0;
	 public String now_time ="00:00";
	 public String all_time ="00:00";
	 
	 public Music_player(Handler myhandler){
		 
		 handler= myhandler; 
		 mediaPlayer = new MediaPlayer();  
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        mTimer=new Timer();
        mTimer.schedule(mTimerTask, 0, 1000); 
        
        init_listener();
	 }

	private void init_listener() {
		
		mediaPlayer.setOnErrorListener(new OnErrorListener() { 
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				
				Message msg = handler.obtainMessage();
				msg.what = Player_update;
				msg.obj = what; 
				handler.sendMessage(msg);
				
				return false;
			}
		});
		
		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() { 
			@Override
			public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
				Message msg = handler.obtainMessage();
				msg.what = Player_err; 
				handler.sendMessage(msg);
			}
		});
		
	   mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer arg0) {
			Message msg = handler.obtainMessage();
			msg.what = Player_perpare; 
			handler.sendMessage(msg); 
		} 
	   });
	   
	   mediaPlayer.setOnCompletionListener(new OnCompletionListener(){ 
		@Override
		public void onCompletion(MediaPlayer arg0) {
			Message msg = handler.obtainMessage();
			msg.what = Player_end; 
			handler.sendMessage(msg);
		}
	   }); 
	}
	
	public void playUrl(String videoUrl)  
	{  
			if(mediaPlayer == null){
				 mediaPlayer = new MediaPlayer();  
		         mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			}
	        try {  
	            mediaPlayer.reset();  
	            mediaPlayer.setDataSource(videoUrl);  
	            mediaPlayer.prepare();//prepare之后自动播放  
	            mediaPlayer.start();
	            
	            Message msg = handler.obtainMessage();
				msg.what = Player_start; 
				handler.sendMessage(msg);
				
				get_progress();
	             
	        } catch (IllegalArgumentException e) {   
	            e.printStackTrace();  
	        } catch (IllegalStateException e) {   
	            e.printStackTrace();  
	        } catch (IOException e){     
	            e.printStackTrace();  
	        }  
	 }
	
	public void pause()  
   {  
		 if(mediaPlayer != null){  
			 if(mediaPlayer.isPlaying()){
				 mediaPlayer.pause();
				 Message msg = handler.obtainMessage();
				 msg.what = Player_pause; 
				 handler.sendMessage(msg);
			}else{
				 mediaPlayer.start(); 
				 Message msg = handler.obtainMessage();
				 msg.what = Player_playing; 
				 handler.sendMessage(msg);
			}
		 } 
     
   }
	
	public void stop()  
	{  
	      if(mediaPlayer != null) { 
	    	  
	    	  mediaPlayer.stop(); 
	          mediaPlayer.release();   
	          mediaPlayer = null;   
	      }   
	 }
	   
	 public void get_progress(){
	   	if(mediaPlayer != null){  
	   		   int position = mediaPlayer.getCurrentPosition();  
	           int duration = mediaPlayer.getDuration();
	           
	           progress = 0; 
	           if (duration > 0) { 
	        	   progress = 100*position/duration;
	           } 
	           now_time = get_time(position);
	           all_time = get_time(duration);
	           
	           
	           
	           Message msg = handler.obtainMessage();
			   msg.what = Player_Progress; 
			   handler.sendMessage(msg);
	           
	   	} 
	   	 
	  }
	 
	 public void seekToPositon(int num){
		 if(mediaPlayer != null){ 
			 int duration = mediaPlayer.getDuration();
			 
			 int position = num*duration/100;
			 mediaPlayer.seekTo(position);
			 
			 if(mediaPlayer.isPlaying()== false){
				 mediaPlayer.start();
			 }
			 
		 }
	 }
	 
	 
	 public String get_time(int num){
		 
		 num = num/1000;
		 
		 int a= 60;
		 
		 int b= num%a;
		 
		 String second = "";
		 if(b<10){
			 second = "0"+b;
		 }else{
			 second = ""+b;
		 }
		 
		 int c = num/a;
		 
		 String min ="";
		 if(c<10){
			 min = "0"+c;
		 }else{
			 min = ""+c;
		 }
		 
		 return min+":"+second;
	 }
	 
	 /******************************************************* 
	 	通过定时器和Handler来更新进度条 
	 ******************************************************/  
	TimerTask mTimerTask = new TimerTask() {  
	    @Override  
	    public void run() {  
	        if(mediaPlayer==null){
	        	 return; 
	        }else{
	        	if (mediaPlayer.isPlaying()) { 
	            	//播放进度
	        		get_progress(); 
	            }
	        }
	            
	          
	    }  
	}; 
}
