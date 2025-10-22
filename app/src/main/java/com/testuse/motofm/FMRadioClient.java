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
    private ServiceConnection mConnection;
    private boolean mIsConnected = false;
    
    public FMRadioClient(Context context) {
        mContext = context;
    }
    
    public boolean connect() {
        Intent intent = new Intent("com.motorola.android.fmradio.FMRADIO_SERVICE");
        intent.setPackage("com.motorola.android.fmradio");
        
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "FM Radio Service connected");
                mService = IFMRadioService.Stub.asInterface(service);
                mIsConnected = true;
                
                try {
                    // 注册回调接收FM事件
                    mService.registerCallback(new IFMRadioServiceCallback.Stub() {
                        @Override
                        public void onCommandComplete(int cmd, int status, String value) {
                            Log.d(TAG, "FM Command Complete - cmd: " + cmd + ", status: " + status + ", value: " + value);
                        }
                    });
                    Log.d(TAG, "FM callback registered successfully");
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to register FM callback", e);
                }
            }
            
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "FM Radio Service disconnected");
                mService = null;
                mIsConnected = false;
            }
        };
        
        try {
            boolean result = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindService result: " + result);
            return result;
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException - Permission denied: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to bind FM service: " + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        if (mService != null) {
            try {
                mService.unregisterCallback(null);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to unregister callback", e);
            }
        }
        if (mConnection != null) {
            try {
                mContext.unbindService(mConnection);
                Log.d(TAG, "FM service unbound");
            } catch (Exception e) {
                Log.e(TAG, "Error unbinding service", e);
            }
        }
        mService = null;
        mIsConnected = false;
    }
    
    public boolean tune(int frequency) {
        if (mService != null && mIsConnected) {
            try {
                Log.d(TAG, "Tuning to frequency: " + frequency + " kHz (" + (frequency/1000.0) + " MHz)");
                return mService.tune(frequency);
            } catch (RemoteException e) {
                Log.e(TAG, "Tune failed - RemoteException", e);
            }
        } else {
            Log.w(TAG, "Tune failed - Service not connected");
        }
        return false;
    }
    
    public boolean isConnected() {
        return mIsConnected && mService != null;
    }
}
