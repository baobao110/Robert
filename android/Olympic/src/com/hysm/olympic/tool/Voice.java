package com.hysm.olympic.tool;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

public class Voice {
	
	private MediaPlayer mediaPlayer;
	private Context context;
	
	public Voice(Context mycontext){
		 context = mycontext;
		 mediaPlayer = new MediaPlayer();  
         mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
         
         AssetManager assetManager = context.getAssets(); 
 		try {
 			
 			String  voicename = "1.mp3";  
 			//Toast.makeText(context, voicename, Toast.LENGTH_SHORT).show();
 			AssetFileDescriptor fileDescriptor = assetManager.openFd(voicename); 
 			 
 			mediaPlayer.reset();
 			mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getStartOffset());
 			mediaPlayer.prepare();//prepare之后自动播放  
             mediaPlayer.start();
 			
 		} catch (IOException e){ 
 			e.printStackTrace();
 		} 
	}
	
	 
	
	 
}
