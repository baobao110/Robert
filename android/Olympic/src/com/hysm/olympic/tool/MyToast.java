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
		
		toast.setGravity(Gravity.CENTER, 12, 20);//setGravity��������Toast��ʾ��λ�ã��൱��xml�е�android:gravity��android:layout_gravity
		
		toast.setDuration(Toast.LENGTH_LONG);//setDuration���������ó���ʱ�䣬�Ժ���Ϊ��λ���÷��������ò��䶯��ʱ�䳤�ȵ���Ҫ����
		toast.setView(layout); //�����ͼ�ļ�
		
		toast.show();
	}
}
