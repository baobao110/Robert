package com.hysm.olympic.tool;
 
import com.hysm.olympic.activity.BaseApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface; 
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Net_tool {

	//�ж�����
    public static int getNetType(Context context) {
        //����һ��������״ֵ̬
        int noState = -1;
        //��ȡϵͳ���������
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //�õ�NetWorkInfo
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //���activeNetworkInfoΪ��,˵��û����
        if (activeNetworkInfo == null) {
            return noState;
        }
        //����ͻ�ȡ��·����
        int type = activeNetworkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {//����������WiFi
            return 1;
        } else if (type == connectivityManager.TYPE_MOBILE) {//�����������ֻ�����
            return 0;
        }

        return noState;
    }
    
    
    public static void checkNet(Context context){
    	
    	/*int nettype = getNetType(context);
    	
    	if(nettype == -1){
    		//�������������� 
    		AlertDialog alert = new AlertDialog.Builder(context)
    		.setTitle("��ʾ")//���öԻ���ı���
            .setMessage("Ӧ����Ҫ����ſ���ʹ�ã����ȿ�ͨ�����wifi��")//���öԻ��������
            .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { 
                    dialog.dismiss();
                    BaseApplication.getInstance().exit();
                }
            }).create();
    		
    		alert.show();
    	} */
    }

}
