package com.hysm.olympic.activity;

import java.util.ArrayList;
import java.util.List;
  


import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import android.app.Activity;
import android.app.Application;

public class BaseApplication extends Application {

	private static String  APPID ="5486584a";//Ѷ�ɵ�Ӧ��  //5486584a   59215193
	
	@Override
	public void onCreate() {
		
		//����Ӧ��
		SpeechUtility.createUtility(this, SpeechConstant.APPID +"="+APPID);
		 
		super.onCreate();
		instance = this;
	}
	
	/**�򿪵�activity**/
    private List<Activity> activities = new ArrayList<Activity>();
    /**Ӧ��ʵ��**/
    private static BaseApplication instance;
    /**
     *  ���ʵ��
     * @return
     */
    public static BaseApplication getInstance(){
        return instance;
    }
    /**
     * �½���һ��activity
     * @param activity
     */
    public void addActivity(Activity activity){
        activities.add(activity);
    }
    /**
     *  ����ָ����Activity
     * @param activity
     */
    public void finishActivity(Activity activity){
        if (activity!=null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    /**
     * Ӧ���˳����������е�activity
     */
    public void exit(){
        for (Activity activity : activities) {
            if (activity!=null) {
                activity.finish();
            }
        }
        System.exit(0);
    }
    /**
     * �ر�Activity�б��е�����Activity*/
    public void finishActivity(){
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
        //ɱ����Ӧ�ý���  
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
