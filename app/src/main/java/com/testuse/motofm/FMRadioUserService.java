package com.testuse.motofm;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.IInterface;
import android.util.Log;

import android.app.IServiceConnection;

import rikka.shizuku.Shizuku;

import com.motorola.android.fmradio.IFMRadioService;
import com.motorola.android.fmradio.IFMRadioServiceCallback;

/**
 * User Service 运行在 root/shell 进程，可以无限制访问系统服务
 * 这个服务在 Shizuku 的 root/shell 进程中运行
 */
public class FMRadioUserService extends IFMRadioUserService.Stub {
    private static final String TAG = "FMRadioUserService";
    private int userId = 0;
    private IFMRadioService mFMRadioService;
    private IFMRadioUserServiceCallback mCallback;

    private IBinder cachedBinder;
    private ServiceConnection fmradioServiceConnection;
    
    public FMRadioUserService() {
        Log.d(TAG, "FMRadioUserService created with default constructor");
    }
    
    public FMRadioUserService(Context context) {
        Log.d(TAG, "FMRadioUserService created with Context constructor");
    }
    
    @Override
    public void setCallback(IFMRadioUserServiceCallback callback) throws RemoteException {
        mCallback = callback;
        Log.d(TAG, "Callback set");
    }

    @Override
    public void destroy() throws RemoteException {
        System.exit(0);
    }

    @Override
    public void exit() throws RemoteException {
        destroy();
    }
    
    @Override
    public IBinder getFMRadioService() throws RemoteException {
        try {
	    return null;

        } catch (Exception e) {
            Log.e(TAG, "Failed to connect to FM Radio service", e);
            return null;
        }
    }
    
    
}
