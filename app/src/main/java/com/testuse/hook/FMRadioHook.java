package com.testuse.hook;

import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Method;

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
		long dataObj = (long) param.args[1];
		String params = dumpParams(dataObj);
		XposedBridge.log(String.format("FM 事务,参数: %d | %s", code, params));
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

    private String dumpParams(long dataObj) {
      Parcel data = null;
      try {
        // 正确指定参数类型为 long.class
	data = (Parcel) XposedHelpers.callStaticMethod(
    		Parcel.class, 
    		"obtain", 
    		new Class<?>[]{long.class},  // 参数类型数组
    		dataObj                     // 参数值
	);
	XposedBridge.log(String.format(
    		"DEBUG: size=%d, pos=%d, avail=%d",
    		data.dataSize(),
    		data.dataPosition(),
    		data.dataAvail()
	));

        Parcel dataCopy = Parcel.obtain();
        dataCopy.setDataPosition(0);
        dataCopy.appendFrom(data, 0, data.dataSize());

            String hexDump = getHexDump(dataCopy);
            XposedBridge.log("FM DEBUG: HEX DUMP: " + hexDump);

        StringBuilder params = new StringBuilder("[");
        while (dataCopy.dataAvail() > 0) {
            params.append(dataCopy.readInt()).append(", ");
        }
        if (params.length() > 1){
		params.setLength(params.length() - 2);
	}
        return params.append("]").toString();
      }catch (Throwable t) {
        return "PARSE_ERROR: " + t.getClass().getSimpleName();
      } finally {
            data.recycle();
      }
  }
  // 输出十六进制数据
  private String getHexDump(Parcel data) {
    // 保存原始位置
    int originalPos = data.dataPosition();
    try {
        // 重置到起始位置
        data.setDataPosition(104);

        // 正确获取原始字节 - 使用 marshall()
        byte[] bytes = data.marshall();

        // 转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, 64); i++) {
            sb.append(String.format("%02X ", bytes[i]));
            if ((i + 1) % 8 == 0) sb.append(" ");
            if ((i + 1) % 16 == 0 && i < bytes.length - 1) sb.append("\nFM DEBUG: ");
        }
        if (bytes.length > 64) sb.append("...");
        return sb.toString();
    } finally {
        // 恢复原始位置
        data.setDataPosition(originalPos);
    }
}

    
}
