package com.testuse.motofm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Handler;
import android.os.RemoteException;
import android.os.Parcel;
import android.util.Log;

import com.motorola.android.fmradio.IFMRadioService;
import com.motorola.android.fmradio.IFMRadioServiceCallback;

public class FMRadioClient implements TransactionSearchListener{
    private static final String TAG = "MotoFMClient";
    private static final String TransactionTarget = "com.motorola.android.fmradio.IFMRadioService";
    
    private Context mContext;
    private Handler mHandler = new Handler();
    private IBinder mBinder;
    private IFMRadioService mService;
    private IFMRadioServiceCallback mCallback;

    private ServiceConnection fmRemoteCon;
    private boolean mIsConnected = false;
    private IFMRadioListener mListener;
    private boolean istesting = false;
    private TransactionFinder finder ;
    
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
                        mBinder = service;
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

    private boolean sendTr_0(int transactionId){
	Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
	try {
            data.writeInterfaceToken(TransactionTarget);
	    mBinder.transact(transactionId, data, reply, 0);
	    reply.readException();
            return reply.readInt() != 0;
	} catch (Exception e) {
            Log.d(TAG, "事务 " + transactionId + " 异常: " + e.getMessage());
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    private boolean sendTr_1(int transactionId, int param){
	Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
	try {
            data.writeInterfaceToken(TransactionTarget);
	    data.writeInt(param);
	    mBinder.transact(transactionId, data, reply, 0);
	    reply.readException();
            return reply.readInt() != 0;
	} catch (Exception e) {
            Log.d(TAG, "事务 " + transactionId + " 异常: " + e.getMessage());
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }

    }

    
    private boolean sendTr_2(int transactionId, int param1, int param2){
	Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
	try {
            data.writeInterfaceToken(TransactionTarget);
	    data.writeInt(param1);
	    data.writeInt(param2);
	    mBinder.transact(transactionId, data, reply, 0);
	    reply.readException();
            return reply.readInt() != 0;
	} catch (Exception e) {
            Log.d(TAG, "事务 " + transactionId + " 异常: " + e.getMessage());
            return false;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    public boolean TRANSACTION_setMute(int mode){
	return sendTr_1(37,mode);
    }

    public boolean TRANSACTION_setAudioMode(int mode){
	return sendTr_1(3,mode);
    }

    public boolean TRANSACTION_seek(){
	return sendTr_0(7);
    }
    public boolean TRANSACTION_scan(){
	return sendTr_0(12);
    }
    public boolean TRANSACTION_setFMRouting(int routing){
	return sendTr_1(0x20,routing);
    }
    public boolean TRANSACTION_isFmOn(){
	return sendTr_0(0x25);
    }
    public boolean TRANSACTION_isMute(){
	return sendTr_0(0x6);
    }

    public boolean startFM() {
      Log.d(TAG, "=== 启动FM收音机 ===");
      try {
        boolean enabled = mService.enable(FMConstants.FMRADIO_BAND_US_EUROPE); // US/EU band
        Log.d(TAG, "启用结果: " + enabled);	
        TRANSACTION_setAudioMode(FMConstants.FMRADIO_AUDIO_MODE_STEREO);
        boolean setmuteflag = TRANSACTION_setMute(FMConstants.FMRADIO_MUTE_AUDIO);
        Log.d(TAG, "setmute: " + setmuteflag);	
	mService.setVolume(9);
        TRANSACTION_scan();
	/**
        Log.d(TAG, "遍历查找: ");	
	finder = new TransactionFinder(mBinder,this);
	finder.startDiscovery(0, 40);
	**/


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
	    try{
	    mService.getCurrentFreq();
	    }catch(RemoteException e){}
	    
        }, 1000);

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "2秒到了");
    	    Log.d(TAG, "check scan() 应激发 FM_CMD_SCANNING = 25 ");
	    try{
	    mService.scan();
	    }catch(RemoteException e){}
        }, 2000);

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "7秒到了");
    	    Log.d(TAG, "getAudioType() 应激发 FM_CMD_GET_AUDIOTYPE_DONE = 11");
	    try{
	    mService.getAudioType();
	    }catch(RemoteException e){}
        }, 7000);

	mHandler.postDelayed(() -> {
    	    Log.d(TAG, "8秒到了");
    	    Log.d(TAG, "getAudioMode() 应激发 FM_CMD_SET_AUDIOMODE_DONE = 17");
	    try{
	    mService.getAudioType();
	    }catch(RemoteException e){}
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
        istesting = true;
    }

    @Override
    public void onTransactionFound(int transactionId, int cmd) {
	int NN = transactionId -1 ;
        Log.d(TAG, "发现事务N " + NN + " 对应命令 " + cmd);
        // 可以在这里记录或处理发现的映射
    }

    @Override
    public void onDiscoveryEnd() {
        Log.d(TAG, "事务ID发现结束");
        istesting = false;
	finder.printTransactionMap();
    }
}
