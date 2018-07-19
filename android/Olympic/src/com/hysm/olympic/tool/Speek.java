package com.hysm.olympic.tool;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;

/**
 * ����ҵ�����߼�
 * @author songkai
 *
 */
public class Speek {

	// ����ʶ�����
    private SpeechRecognizer recognizer; 
    //�����ϳɶ���
    private SpeechSynthesizer speaker; 
    
    public Speek(Context context){
    	//��ʼ����������
        recognizer = SpeechRecognizer.createRecognizer(context,mInitListener);
        speaker = SpeechSynthesizer.createSynthesizer(context,mInitListener);
		
		//������д����
        recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        //����Ϊ����
        recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //����Ϊ��ͨ��
        recognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        // �ͻ�������ѡ��ģʽ --�ƶ�msc
        recognizer.setParameter(SpeechConstant.ENGINE_MODE, "msc");
		// �ͻ����������� --�ƶ�msc
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// ���ݽ��뷽ʽ --utf-8
        recognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
         
         
      //���÷�����
        speaker.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");//nannan//xiaoqi//xiaolin
        //��������
        speaker.setParameter(SpeechConstant.SPEED, "40");
        //�������
        speaker.setParameter(SpeechConstant.PITCH, "50");
        //������������Χ0~100
        speaker.setParameter(SpeechConstant.VOLUME, "100");
        //�����ƶ�
        speaker.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // ���ò�������Ƶ������
        speaker.setParameter(SpeechConstant.STREAM_TYPE, "3");
        //���ò��źϳ���Ƶ������ֲ��ţ�Ĭ��Ϊtrue
        speaker.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }
    
    
    
    
    public SpeechRecognizer getRecognizer() {
		return recognizer;
	}
 
	public SpeechSynthesizer getSpeaker() {
		return speaker;
	}
 
	//��ʼ����������
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) { 
            if(code != ErrorCode.SUCCESS){ 
            	
            }
        }
    };
}
