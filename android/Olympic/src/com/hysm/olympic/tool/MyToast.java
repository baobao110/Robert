package com.hysm.olympic.tool;

import com.hysm.olympic.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
 

public class MyToast {

	private LayoutInflater inflater; 
	private Context context; 
	private View layout; 
	private TextView infotext; 
	private Toast toast;
	
	public MyToast(Context mycontext,String info){
		
		context = mycontext;
		inflater = LayoutInflater.from(context);
		layout = inflater.inflate(R.layout.mytoast, null);
		infotext = (TextView)layout.findViewById(R.id.mytoast_info);
		infotext.setText(info);
		
		toast = new Toast(context);
		
		toast.setGravity(Gravity.CENTER, 12, 20);//setGravity用来设置Toast显示的位置，相当于xml中的android:gravity或android:layout_gravity
		
		toast.setDuration(Toast.LENGTH_LONG);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
		toast.setView(layout); //添加视图文件
		
		toast.show();
	}
}
