package com.testuse.hook;

import java.util.concurrent.atomic.AtomicInteger;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.content.Intent;
import android.content.ComponentName;



import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FMRadioHook implements IXposedHookLoadPackage {
    private static final String FM_DESCRIPTOR = "com.motorola.android.fmradio.IFMRadioService";
    private static final AtomicInteger logCount = new AtomicInteger(0);
    private ClassLoader fmClassLoader = null; // 声明成员变量保存类加载器

    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
	if ("com.motorola.android.fmradio".equals(lpparam.packageName)){
		fmClassLoader = lpparam.classLoader;
		if(fmClassLoader!=null){
                	XposedBridge.log("FM: 获取到FM应用的类加载器");
			hookFmService();
		}
	}
    }

    private void hookFmService() {
        try {
            XposedHelpers.findAndHookMethod(
                "com.motorola.android.fmradio.FMRadioService",
                fmClassLoader,
                "onBind",
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        IBinder binder = (IBinder) param.getResult();
                        if (binder != null) {
                            XposedBridge.log("FM: 获取到 FM 服务 Binder");
                            hookExecTransactForFmService(binder);
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("FM: Hook 失败: " + t.getMessage());
        }
    }

    private void hookExecTransactForFmService(final IBinder fmBinder) {
    	XC_MethodHook hook = new XC_MethodHook() {
          @Override
          protected void beforeHookedMethod(MethodHookParam param) {
            // 仅当 thisObject 是我们的 FM Binder 时才处理
            if (param.thisObject == fmBinder) {
                int code = (int) param.args[0];
                XposedBridge.log("FM: 事务代码: " + code);
                // 记录参数等...
            }
          }
        };
        XposedHelpers.findAndHookMethod(
        	"android.os.Binder",
        	null,
        	"execTransact",
        	int.class, long.class, long.class, int.class,
        	hook
    	);
     }
    
}
