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
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
	if ("android".equals(lpparam.packageName)){
        	hookViaSystemService();
	}
    }

    private void hookViaSystemService() {
        XposedHelpers.findAndHookMethod(
        	"com.android.server.am.ActiveServices",
        	null,
        	"bringUpServiceLocked",
        	ComponentName.class, int.class, int.class, boolean.class,
        	new XC_MethodHook() {
            	  @Override
            	  protected void beforeHookedMethod(MethodHookParam param) {
                	ComponentName component = (ComponentName) param.args[0];
                	// 使用 dumpsys 中确认的标识
                	if ("com.motorola.android.fmradio".equals(component.getPackageName()) && ".FMRadioService".equals(component.getClassName())) {
                    		XposedBridge.log("FM: 通过系统服务检测到 FM 服务");
                    		// 获取正在启动的服务实例
                    		Object serviceRecord = param.getResult();
                    		IBinder binder = (IBinder) XposedHelpers.getObjectField(
                        	serviceRecord, "binder"
                    	        );

                                if (binder != null) {
                        		hookExecTransactForFmService(binder);
                                }
                        }
                   }
                }
        );
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

    	// 保存 hook 引用以便后续移除
     }
    
}
