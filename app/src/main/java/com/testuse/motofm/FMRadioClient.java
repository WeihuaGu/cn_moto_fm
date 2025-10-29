package com.testuse.motofm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.motorola.android.fmradio.IFMRadioService;
import com.motorola.android.fmradio.IFMRadioServiceCallback;

public class FMRadioClient implements TransactionSearchListener{
    private static final String TAG = "MotoFMClient";
    
    private Context mContext;
    private Handler mHandler = new Handler();
    private IFMRadioService mService;
    private IFMRadioServiceCallback mCallback;

    private ServiceConnection fmRemoteCon;
    private boolean mIsConnected = false;
    private IFMRadioListener mListener;
    private istesting = false;
    
    public FMRadioClient(Context context) {
        mContext = context;
        mCallback = new IFMRadioServiceCallback.Stub() {
            @Override
            public void onCommandComplete(int cmd, int status, String value) {
		if(istesting){
		    return;
		}
	    	Log.d(TAG, "FM Service Command Complete: ");
	    	Log.d(TAG, "cmd: " + cmd);
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

    
    public boolean startFM() {
      Log.d(TAG, "=== 启动FM收音机 ===");
      try {
        boolean enabled = mService.enable(FMConstants.FMRADIO_BAND_US_EUROPE); // US/EU band
        Log.d(TAG, "启用结果: " + enabled);	

	return true;
      } catch (Exception e) {
        Log.e(TAG, "启动FM异常: " + e.getMessage());
        e.printStackTrace();
        return false;
      }
    }

    public boolean stopFM() {
      try {
	    if (mService.isFmOn()) {
                mService.disable();
                mService.unregisterCallback(mCallback);
            }
	    return true;
      } catch (Exception e) {
        Log.e(TAG, "停止FM异常: " + e.getMessage());
        e.printStackTrace();
        return false;
      }
    }

    public void debugFMState() {
    try {
        Log.d(TAG, "=== FM状态调试 ===");
        mService.tune(102600);
    	    Log.d(TAG, "调频102600");
    	    Log.d(TAG, "激发 FM_CMD_TUNE_COMPLETE = 0");

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "1秒到了");
    	    Log.d(TAG, "check getCurrentFreq() 应激发 FM_CMD_GET_FREQ_DONE = 12");
	    mService.getCurrentFreq();
        }, 1000);

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "2秒到了");
    	    Log.d(TAG, "check scan() 应激发 FM_CMD_SCANNING = 25 ");
	    mService.scan();
        }, 2000);

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "7秒到了");
    	    Log.d(TAG, "getAudioType() 应激发 FM_CMD_GET_AUDIOTYPE_DONE = 11");
	    mService.getAudioType();
        }, 7000);

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "8秒到了");
    	    Log.d(TAG, "getAudioMode() 应激发 FM_CMD_SET_AUDIOMODE_DONE = 17");
	    mService.getAudioType();
        }, 8000);

        Log.d(TAG, "=== 调试结束 ===");

      } catch (RemoteException e) {
        Log.e(TAG, "调试失败", e);
      }
    }
        
    public boolean tune(int frequency) {
	try{
        // 检查当前波段
        int currentBand = mService.getBand();
        Log.d(TAG, "当前波段: " + currentBand);

		// 检查频率范围
        int minFreq = mService.getMinFrequence();
        int maxFreq = mService.getMaxFrequence();
        int step = mService.getStepUnit();
        Log.d(TAG, "频率范围: " + minFreq + " - " + maxFreq + ", 步进: " + step);

        if (frequency < minFreq || frequency > maxFreq) {
            Log.e(TAG, "频率 " + frequency + " 超出范围");
            return false;
        }
	boolean bootune = mService.tune(frequency);
	if(!bootune){
            Log.e(TAG, "tune调用失败 ");
	}

	return mService.tune(frequency);

	}catch(RemoteException e){
		Log.d(TAG,"调频错误");
                e.printStackTrace();
		return false;
	}
    }


    @Override
    public void onDiscoveryStart() {
        Log.d(TAG, "开始发现事务ID");
        mDiscovering = true;
    }

    @Override
    public void onTransactionFound(int transactionId, int cmd) {
        Log.d(TAG, "发现事务ID " + transactionId + " 对应命令 " + cmd);
        // 可以在这里记录或处理发现的映射
    }

    @Override
    public void onDiscoveryEnd() {
        Log.d(TAG, "事务ID发现结束");
        mDiscovering = false;

        // 打印最终映射表
        if (mTransactionFinder != null) {
            mTransactionFinder.printTransactionMap();
        }
    }
}
