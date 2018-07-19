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
		alert.setView(layout, 0, 0, 0, 0);//ȥ�����
		
		alert.getWindow().setDimAmount(0);//���û谵��Ϊ0 
		/*����հ״��������˳��Ի���Ҫ������ؼ��Ż��˳�*/ 
		alert.setCanceledOnTouchOutside(false);
		
	}
	
	public void show_info(String info){ 
		if(infotext!= null){
			infotext.setText(info);  
			alert.show();
			
			// ��ȡ��Ļ�ܶȣ�����2��  
			DisplayMetrics dm = new DisplayMetrics();  
			dm = context.getResources().getDisplayMetrics();
			
			WindowManager.LayoutParams  lp= alert.getWindow().getAttributes();
			
			float density  = dm.density; 
			int w = (int)(400*density); 
			
		    lp.width = w;//������  
		    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;//����߶� 
		    alert.getWindow().setAttributes(lp);
		}  
	}
	
	public void hide(){ 
		if(alert!= null){
			alert.dismiss();
		}
	}
	
	
	
}