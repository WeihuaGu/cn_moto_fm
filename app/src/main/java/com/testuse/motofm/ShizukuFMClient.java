package com.testuse.motofm;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.content.ServiceConnection;
import android.util.Log;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuBinderWrapper;

public class ShizukuFMClient {
    private static final String TAG = "ShizukuFMClient";
    private Context mContext;
    private IFMRadioUserService mUserService = null;
    private IShizukuFMListener mListener;
    private ServiceConnection shizukuServiceConnection;
    private final Shizuku.UserServiceArgs userServiceArgs =
    new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, FMRadioUserService.class.getName()))
        .daemon(false)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE);
    
    public ShizukuFMClient(Context context) {
        mContext = context;
        // 监听 Shizuku 状态
        Shizuku.addBinderReceivedListenerSticky(this::onBinderReceived);
        Shizuku.addRequestPermissionResultListener(this::onRequestPermissionResult);
        Shizuku.addBinderDeadListener(this::onBinderDead);
    }

    public IFMRadioUserService getFMRadioUserServiceObject(){
	    return mUserService;
    }

    private void connectShizukuService(){
       shizukuServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.i(TAG, " Shizuku 服务已连接");
            if (binder != null && binder.pingBinder()) {
              mUserService = IFMRadioUserService.Stub.asInterface(binder);

              if (mListener != null) {
                 mListener.onServiceConnected();
              }
            } else {
              Log.i(TAG, " Shizuku binder 为 null 或者 binder.pingBinder() 有问题");
            }
       }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
           Log.i(TAG, " Shizuku 服务已断开");
           mUserService = null;
           mListener.onServiceDisconnected();
        }
       };
       Shizuku.bindUserService(userServiceArgs, shizukuServiceConnection);
    }
    
    private void onBinderReceived() {
        if (mListener != null) {
            mListener.onShizukuStateChanged(true, "Shizuku is ready");
        }
    }
    
    private void onBinderDead() {
        if (mListener != null) {
            mListener.onShizukuStateChanged(false, "Shizuku service died");
        }
    }
    
    private void onRequestPermissionResult(int requestCode, int grantResult) {
        Log.d(TAG, "Shizuku permission result: " + grantResult);
        boolean granted = grantResult == PERMISSION_GRANTED;
        if (mListener != null) {
            mListener.onShizukuStateChanged(granted, 
                granted ? "Permission granted" : "Permission denied");
        }
    }
    
    public void setListener(IShizukuFMListener listener) {
        mListener = listener;
        // 立即通知当前状态
        if (Shizuku.pingBinder()) {
            if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) {
                listener.onShizukuStateChanged(true, "Shizuku is ready");
            } else {
                listener.onShizukuStateChanged(false, "Need Shizuku permission");
            }
        } else {
            listener.onShizukuStateChanged(false, "Shizuku not available");
        }
    }

    public void connect(){
        connectShizukuService();
    }

    public void disconnect(){
    }
    
    
    public boolean isShizukuAvailable() {
        return Shizuku.pingBinder();
    }
    
    public boolean hasShizukuPermission() {
        return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PERMISSION_GRANTED;
    }
}
