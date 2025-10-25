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
import android.os.Parcel;

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
	    return null;

    }


    private boolean bindServiceWithAIDL() {
	try{
             return false;

        } catch (Exception e) {
            Log.e(TAG, "AIDL 绑定失败", e);
            return false;
        }
    }


    /////////////////////////////////////////////////////////////////
    private IBinder getActivityManagerBinder() {
        try {
            // 仍然需要反射获取 ServiceManager，但这是唯一需要反射的地方
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerClass.getMethod("getService", String.class);
            return (IBinder) getServiceMethod.invoke(null, "activity");
        } catch (Exception e) {
            Log.e(TAG, "获取 ActivityManager Binder 失败", e);
            return null;
        }
    }

    private android.app.IActivityManager getActivityManagerAIDL() {
        try {
            // 获取 ActivityManagerService 的 Binder
            IBinder amBinder = getActivityManagerBinder();
            if (amBinder == null) {
                return null;
            }

            // 使用我们的 AIDL 接口
            return android.app.IActivityManager.Stub.asInterface(amBinder);

        } catch (Exception e) {
            Log.e(TAG, "获取 IActivityManager AIDL 失败", e);
            return null;
        }
    }

    private android.app.IServiceConnection createServiceConnectionAIDL(final IBinder[] binderHolder,
                                                                      final CountDownLatch latch) {
        return new android.app.IServiceConnection.Stub() {
            @Override
            public void connected(ComponentName name, IBinder service) throws RemoteException {
                Log.d(TAG, "服务连接成功: " + name);
                binderHolder[0] = service;
                latch.countDown();
            }

            @Override
            public void disconnected(ComponentName name) throws RemoteException {
                Log.d(TAG, "服务断开: " + name);
                binderHolder[0] = null;
                latch.countDown();
            }
        };
    }

    ///////////////////////////////////////////

    
}
