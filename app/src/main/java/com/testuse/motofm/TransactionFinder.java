package com.testuse.motofm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.motorola.android.fmradio.IFMRadioService;
import com.motorola.android.fmradio.IFMRadioServiceCallback;


public class TransactionFinder {
    private static final String TAG = "TransactionFinder";
    private final IBinder mBinder;
    private IFMRadioService mService;
    private final TransactionSearchListener mListener;
    private volatile boolean mDiscovering = false;
    
    // 存储事务ID到命令的映射关系
    private final Map<Integer, Integer> transactionToCmdMap = new HashMap<>();
    // 存储已知的事务ID（无论是否有回调）
    private final Set<Integer> knownTransactions = new HashSet<>();
    // 存储事务ID到参数类型的映射
    private final Map<Integer, ParamType> transactionToParamType = new HashMap<>();
    // 参数类型枚举
    public enum ParamType {
        NO_PARAM,      // 无参数
        INT_PARAM,     // 单个int参数
        BOOLEAN_INT,   // boolean和int参数 (setRdsEnable)
        FOUR_INTS      // 四个int参数 (setCustomBand)
    }
    
    private final Object lock = new Object();
    
    public TransactionFinder(IBinder binder, TransactionSearchListener listener) {
        this.mBinder = binder;
	this.mService = IFMRadioService.Stub.asInterface(binder);
        this.mListener = listener;
        initKnownTransactions();
    }
    
     private void initParamTypeMapping() {
        // 根据AIDL定义设置参数类型
        // 已验证正确的方法
        transactionToParamType.put(0, ParamType.INT_PARAM);     // tune(int freq)
        transactionToParamType.put(10, ParamType.INT_PARAM);    // setVolume(int volume)
        transactionToParamType.put(34, ParamType.INT_PARAM);    // enable(int band)

        // 其他方法（根据AIDL定义，但事务ID可能不正确）
        transactionToParamType.put(1, ParamType.NO_PARAM);      // getCurrentFreq()
        transactionToParamType.put(2, ParamType.INT_PARAM);     // setAudioMode(int mode)
        transactionToParamType.put(3, ParamType.NO_PARAM);      // getAudioMode()
        transactionToParamType.put(4, ParamType.INT_PARAM);     // setMute(int mode)
        transactionToParamType.put(5, ParamType.NO_PARAM);      // scan()
        transactionToParamType.put(6, ParamType.INT_PARAM);     // seek(int direction)
        transactionToParamType.put(7, ParamType.NO_PARAM);      // isMute()
        transactionToParamType.put(8, ParamType.NO_PARAM);      // stopSeek()
        transactionToParamType.put(9, ParamType.NO_PARAM);      // stopScan()
        transactionToParamType.put(11, ParamType.NO_PARAM);     // getVolume()
        transactionToParamType.put(12, ParamType.NO_PARAM);     // getBand()
        transactionToParamType.put(13, ParamType.INT_PARAM);    // setBand(int band)
        transactionToParamType.put(14, ParamType.NO_PARAM);     // getMinFrequence()
        transactionToParamType.put(15, ParamType.NO_PARAM);     // getMaxFrequence()
        transactionToParamType.put(16, ParamType.NO_PARAM);     // getStepUnit()
        transactionToParamType.put(17, ParamType.NO_PARAM);     // registerCallback(IFMRadioServiceCallback cb)
        transactionToParamType.put(18, ParamType.NO_PARAM);     // unregisterCallback(IFMRadioServiceCallback cb)
        transactionToParamType.put(19, ParamType.BOOLEAN_INT);  // setRdsEnable(boolean flag, int mode)
        transactionToParamType.put(20, ParamType.NO_PARAM);     // isRdsEnable()
        transactionToParamType.put(21, ParamType.NO_PARAM);     // getAudioType()
        transactionToParamType.put(22, ParamType.NO_PARAM);     // getRSSI()
        transactionToParamType.put(23, ParamType.NO_PARAM);     // getRdsPS()
        transactionToParamType.put(24, ParamType.NO_PARAM);     // getRdsRT()
        transactionToParamType.put(25, ParamType.NO_PARAM);     // getRdsRTPLUS()
        transactionToParamType.put(26, ParamType.NO_PARAM);     // getRdsPI()
        transactionToParamType.put(27, ParamType.NO_PARAM);     // getRdsPTY()
        transactionToParamType.put(28, ParamType.INT_PARAM);    // setRSSI(int rssi)
        transactionToParamType.put(29, ParamType.NO_PARAM);     // getRDSStationName()
        transactionToParamType.put(30, ParamType.INT_PARAM);    // notifyFMStatus(boolean status) - 注意：boolean在Parcel中写为int
        transactionToParamType.put(31, ParamType.INT_PARAM);    // setFMRouting(int routing)
        transactionToParamType.put(32, ParamType.NO_PARAM);     // isSpeakerSupported()
        transactionToParamType.put(33, ParamType.NO_PARAM);     // getFMStreamType()
        transactionToParamType.put(35, ParamType.NO_PARAM);     // disable()
        transactionToParamType.put(36, ParamType.NO_PARAM);     // isFmOn()
        transactionToParamType.put(37, ParamType.FOUR_INTS);    // setCustomBand(int minFreq, int maxFreq, int defaultFreq, int step)
        transactionToParamType.put(38, ParamType.NO_PARAM);     // isCustomBandSupported()
        transactionToParamType.put(39, ParamType.NO_PARAM);     // recordingAudioOnPrepare()
        transactionToParamType.put(40, ParamType.NO_PARAM);     // recordingAudioOffPrepare()
    }
    private void initKnownTransactions() {
        // 添加已知正确的事务ID
        knownTransactions.add(0);   // tune
        knownTransactions.add(10);  // setVolume
        knownTransactions.add(13);  // getBand
        knownTransactions.add(14);  // getMinFrequence
        knownTransactions.add(15);  // getMaxFrequence
        knownTransactions.add(16);  // getStepUnit
        knownTransactions.add(34);  // enable
				    
        initParamTypeMapping();
    }
    
    private boolean sendTransaction(int transactionId) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken("com.motorola.android.fmradio.IFMRadioService");
            // 根据参数类型写入参数
            ParamType paramType = transactionToParamType.get(transactionId);
            if (paramType == null) {
                // 未知参数类型，使用默认int参数
                data.writeInt(0);
            } else {
                switch (paramType) {
                    case NO_PARAM:
                        // 不写入参数
                        break;
                    case INT_PARAM:
                        data.writeInt(0); // 默认参数值
                        break;
                    case BOOLEAN_INT:
                        data.writeInt(1); // boolean: true
                        data.writeInt(0); // int: 0
                        break;
                    case FOUR_INTS:
                        data.writeInt(87500);  // minFreq
                        data.writeInt(108000); // maxFreq
                        data.writeInt(101700); // defaultFreq
                        data.writeInt(100);    // step
                        break;
                }
            }
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
    
    public void startDiscovery(int startTrans, int endTrans) {
        new Thread(() -> {
            try {
                Log.d(TAG, "开始发现事务ID (范围: " + startTrans + "-" + endTrans + ")");
                mDiscovering = true;
                // 通知开始发现
                if (mListener != null) {
                    mListener.onDiscoveryStart();
                }
		IFMRadioServiceCallback callback = new IFMRadioServiceCallback.Stub(){
                    @Override
                    public void onCommandComplete(int cmd, int arg, String data) {
                        synchronized (lock) {
                            if (mDiscovering && currentTestingTrans != -1) {
                                // 记录当前测试事务ID对应的命令
                                transactionToCmdMap.put(currentTestingTrans, cmd);
                                knownTransactions.add(currentTestingTrans);
                                Log.d(TAG, "发现事务ID " + currentTestingTrans + " -> 命令 " + cmd);
                                // 通知找到映射
                                if (mListener != null) {
                                    mListener.onTransactionFound(currentTestingTrans, cmd);
                                }
                                currentTestingTrans = -1;
                                lock.notifyAll();
                            }
                        }
                    }
                };
                // 注册回调
		mService.registerCallback(callback);
                
                // 遍历范围内所有未知事务ID
                for (int trans = startTrans; trans <= endTrans; trans++) {
                    if (knownTransactions.contains(trans)) {
                        Log.d(TAG, "跳过已知事务ID: " + trans);
                        continue;
                    }
                    
                    synchronized (lock) {
                        currentTestingTrans = trans;
                        Log.d(TAG, "测试事务ID: " + trans);
                        // 使用抽离的sendTransaction方法
                        boolean result = sendTransaction(trans);
                        // 等待回调
                        lock.wait(5000);
                        
                        // 如果没有收到回调，标记为无回调事务
                        if (currentTestingTrans != -1) {
                            knownTransactions.add(trans);
                            Log.d(TAG, "事务ID " + trans + " 无回调");
                        }
                    }
                }
                
                // 注销回调
		mService.unregisterCallback(callback);
                mDiscovering = false;
                // 通知发现结束
                if (mListener != null) {
                    mListener.onDiscoveryEnd();
                }
                Log.d(TAG, "事务ID发现完成");
                printTransactionMap();
            } catch (Exception e) {
                Log.e(TAG, "发现失败", e);
                mDiscovering = false;
                if (mListener != null) {
                    mListener.onDiscoveryEnd();
                }
            }
        }).start();
    }
    
    /**
     * 获取事务ID到命令的映射关系
     */
    public Map<Integer, Integer> getTransactionToCmdMap() {
        return new HashMap<>(transactionToCmdMap);
    }
    
    /**
     * 根据事务ID获取命令
     */
    public Integer getCmdForTransaction(int transactionId) {
        return transactionToCmdMap.get(transactionId);
    }
    
    /**
     * 获取已知事务ID集合
     */
    public Set<Integer> getKnownTransactions() {
        return new HashSet<>(knownTransactions);
    }
    
    /**
     * 添加参数类型映射
     */
    public void addParamType(int transactionId, ParamType paramType) {
        transactionToParamType.put(transactionId, paramType);
    }
    
    /**
     * 打印事务映射表
     */
    public void printTransactionMap() {
        Log.d(TAG, "=== 事务ID到命令映射表 ===");
        for (Map.Entry<Integer, Integer> entry : transactionToCmdMap.entrySet()) {
            Log.d(TAG, "事务ID " + entry.getKey() + " -> 命令 " + entry.getValue());
        }
    }
    
    private int currentTestingTrans = -1;
}
