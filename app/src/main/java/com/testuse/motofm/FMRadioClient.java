package com.testuse.motofm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.motorola.android.fmradio.IFMRadioService;
import com.motorola.android.fmradio.IFMRadioServiceCallback;

public class FMRadioClient {
    private static final String TAG = "MotoFMClient";
    
    private Context mContext;
    private IFMRadioService mService;
    private IFMRadioServiceCallback mCallback;

    private ServiceConnection fmRemoteCon;
    private boolean mIsConnected = false;
    private IFMRadioListener mListener;
    
    public FMRadioClient(Context context) {
        mContext = context;
        mCallback = new IFMRadioServiceCallback.Stub() {
            @Override
            public void onCommandComplete(int cmd, int status, String value) {
	    	Log.d(TAG, "fm cmd: " + cmd);
	    	Log.d(TAG, "status: " + status);
	    	Log.d(TAG, "value result: " + value);
            }
       };
    }
    public void setListener(IFMRadioListener listener) {
        mListener = listener;
    }
    public void connect(){
	fmRemoteCon = new ServiceConnection() {
    	    @Override
    	    public void onServiceConnected(ComponentName name, IBinder service) {
		mListener.onFMServiceConnected();
        	mService = IFMRadioService.Stub.asInterface(service);
		if(mService!=null){
		    try{
		    	mService.registerCallback(mCallback);
		        mListener.onFMServiceBinderGet();
		    }catch(RemoteException e){
			e.printStackTrace();
		    }
		}

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
               // 处理服务断开
		mListener.onFMServiceDisconnected();
            }
        };

	Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.motorola.android.fmradio", "com.motorola.android.fmradio.FMRadioService"));
	boolean bound = mContext.bindService(intent, fmRemoteCon, Context.BIND_AUTO_CREATE);
	if (!bound) {
            Log.e(TAG, "Failed to bind service");
        }else {
	    Log.d(TAG, "bindService result: " + bound);
	}


    }

    
    public boolean tune(int frequency) {
	try{
		// 检查频率范围
        int minFreq = mService.getMinFrequence();
        int maxFreq = mService.getMaxFrequence();
        int step = mService.getStepUnit();
        Log.d(TAG, "频率范围: " + minFreq + " - " + maxFreq + ", 步进: " + step);

        if (frequency < minFreq || frequency > maxFreq) {
            Log.e(TAG, "频率 " + frequency + " 超出范围");
            return false;
        }

        // 检查当前波段
        int currentBand = mService.getBand();
        Log.d(TAG, "当前波段: " + currentBand);

        // 设置音频参数
        mService.setAudioMode(0); // 音频模式
        mService.setMute(0);      // 取消静音
        mService.setVolume(80);   // 设置音量

	   return mService.tune(frequency);
	}catch(RemoteException e){
		Log.d(TAG,"调频错误");
                e.printStackTrace();
		return false;
	}
    }
}
