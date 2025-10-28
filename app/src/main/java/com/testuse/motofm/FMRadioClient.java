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

	boolean isOn = mService.isFmOn();
        Log.d(TAG, "FM开启状态: " + isOn);

        Log.d(TAG, "配置音频输出...");
        mService.setFMRouting(FMConstants.FMRADIO_ROUTING_SPEAKER); // 扬声器
        mService.setVolume(10);   // 中等音量
        mService.setMute(FMConstants.FMRADIO_MUTE_NOT);      // 取消静音 - 关键！
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

    public void testAudioOutput() {
    try {
        Log.d(TAG, "=== 专门测试音频输出 ===");

        // 1. 测试扬声器支持
        boolean speakerSupported = mService.isSpeakerSupported();
        Log.d(TAG, "扬声器支持: " + speakerSupported);

        // 2. 强制设置音频路由到扬声器
        Log.d(TAG, "设置音频路由到扬声器...");
        mService.setFMRouting(FMConstants.FMRADIO_ROUTING_SPEAKER);
        Thread.sleep(200);

        // 3. 多次取消静音
        Log.d(TAG, "取消静音...");
        for (int i = 0; i < 5; i++) {
            mService.setMute(FMConstants.FMRADIO_MUTE_NOT);
            Thread.sleep(50);
        }

        // 4. 设置较大音量
        Log.d(TAG, "设置音量为15...");
        mService.setVolume(15); // 最大音量
        Thread.sleep(200);

        // 5. 检查当前状态
        boolean isMute = mService.isMute();
        int audioMode = mService.getAudioMode();
        boolean hasVolume = mService.getVolume();

        Log.d(TAG, "最终音频状态:");
        Log.d(TAG, "  静音: " + isMute);
        Log.d(TAG, "  音频模式: " + audioMode);
        Log.d(TAG, "  音量状态: " + hasVolume);

        // 6. 如果还静音，尝试不同的取消静音方式
        if (isMute) {
            Log.w(TAG, "检测到仍然静音，尝试强制取消...");
            mService.setMute(0); // 直接使用0
            mService.setMute(FMConstants.FMRADIO_MUTE_NOT);
        }

        Log.d(TAG, "=== 音频测试完成 ===");

    } catch (Exception e) {
        Log.e(TAG, "音频测试失败", e);
    }
}

    public void debugFMState() {
    try {
        Log.d(TAG, "=== FM状态调试 ===");

        // 检查FM是否开启
        boolean isFmOn = mService.isFmOn();
        Log.d(TAG, "FM是否开启: " + isFmOn);

        // 检查静音状态
        boolean isMute = mService.isMute();
        Log.d(TAG, "是否静音: " + isMute);

        // 检查当前频率
        boolean hasFreq = mService.getCurrentFreq();
        Log.d(TAG, "是否有当前频率: " + hasFreq);

        // 检查音量
        boolean hasVolume = mService.getVolume();
        Log.d(TAG, "是否有音量: " + hasVolume);

        // 检查音频模式
        int audioMode = mService.getAudioMode();
        Log.d(TAG, "音频模式: " + audioMode +
              " (0=单声道, 1=立体声)");

        // 检查音频类型
        boolean audioType = mService.getAudioType();
        Log.d(TAG, "音频类型: " + audioType);

        // 检查RSSI信号强度
        boolean hasRssi = mService.getRSSI();
        Log.d(TAG, "是否有RSSI: " + hasRssi);

	String stationName = mService.getRDSStationName();
        String rdsPS = mService.getRdsPS();
        String rdsRT = mService.getRdsRT();
        String rdsRTPLUS = mService.getRdsRTPLUS();

        Log.d(TAG, "RDS Station Name: '" + stationName + "'");
        Log.d(TAG, "RDS PS: '" + rdsPS + "'");
        Log.d(TAG, "RDS RT: '" + rdsRT + "'");
        Log.d(TAG, "RDS RT+: '" + rdsRTPLUS + "'");

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
}
