package com.hysm.olympic.tool;

import com.hysm.olympic.R;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View; 
import android.view.WindowManager;
import android.widget.TextView;

public class MyShow {

	private LayoutInflater inflater; 
	private Context context; 
	private View layout; 
	private TextView infotext;
	
	 
	private AlertDialog alert;
	
	public MyShow(Context mycontext){
		
		context = mycontext;
		inflater = LayoutInflater.from(context);
		layout = inflater.inflate(R.layout.mytoast, null);
		infotext = (TextView)layout.findViewById(R.id.mytoast_info);
		 
		alert = new AlertDialog.Builder(context).create(); 
		//alert.setView(layout);
		alert.setView(layout, 0, 0, 0, 0);//去除外框
		
		alert.getWindow().setDimAmount(0);//设置昏暗度为0 
		/*点击空白处，不会退出对话框，要点击返回键才会退出*/ 
		alert.setCanceledOnTouchOutside(false);
		
	}
	
	public void show_info(String info){ 
		if(infotext!= null){
			infotext.setText(info);  
			alert.show();
			
			// 获取屏幕密度（方法2）  
			DisplayMetrics dm = new DisplayMetrics();  
			dm = context.getResources().getDisplayMetrics();
			
			WindowManager.LayoutParams  lp= alert.getWindow().getAttributes();
			
			float density  = dm.density; 
			int w = (int)(400*density); 
			
		    lp.width = w;//定义宽度  
		    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;//定义高度 
		    alert.getWindow().setAttributes(lp);
		}  
	}
	
	public void hide(){ 
		if(alert!= null){
			alert.dismiss();
		}
	}
	
	
	
}