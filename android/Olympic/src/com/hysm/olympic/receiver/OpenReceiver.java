package com.hysm.olympic.receiver;

import com.hysm.olympic.activity.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OpenReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent){
		
		String ACTION = "android.intent.action.BOOT_COMPLETED";
		
		if(intent.getAction().equals(ACTION)){
			Intent intent2 = new Intent(context, MainActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
		} 

	}

}
