package com.hysm.olympic.tool;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

 

public class SoundPool_tool{

	  
	 public static void SoundPool_play1(Context context){
		 
		 final SoundPool sp;
		  
		 final int soundID_1; 
		 sp=new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
		 
		 AssetManager assetManager = context.getAssets(); 
		 
		 String  voicename = "1.mp3";  
		 
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(voicename);
			 
			soundID_1 = sp.load(fileDescriptor, 1); 
			
			sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
					sp.play(soundID_1, 1, 1, 0, 0, 1);
					 
				}
			});
			
		} catch (IOException e) { 
			e.printStackTrace();
		} 
		
	 }
	 
	 public static void SoundPool_play2(Context context){
		 
		 final SoundPool sp;
		  
		 final int soundID_1; 
		 sp=new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
		 
		 AssetManager assetManager = context.getAssets(); 
		 
		 String  voicename = "2.mp3";  
		 
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(voicename);
			 
			soundID_1 = sp.load(fileDescriptor, 1); 
			
			sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
					sp.play(soundID_1, 1, 1, 0, 0, 1);
					 
				}
			});
			
		} catch (IOException e) { 
			e.printStackTrace();
		} 
		
	 }
	 
	 
	 
	 
}
