package com.testuse.hook;

import android.content.ComponentName;
import android.content.pm.ServiceInfo;
import android.os.Build;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EableFMConnect implements IXposedHookLoadPackage {
    private static final String TARGET_PACKAGE = "com.motorola.android.fmradio";
    private static final String TARGET_SERVICE = "com.motorola.android.fmradio.FMRadioService";

    // Android 13 特定常量
    private static final int PERMISSION_FLAG_PRIVILEGED = 0x00000004;
    private static final int PERMISSION_FLAG_PRE23 = 0x00000008;
    private static final int PERMISSION_FLAG_INSTALLER = 0x00000010;
    private static final int PERMISSION_FLAG_SYSTEM_FIXED = 0x00000020;
    private static final int PERMISSION_FLAG_RUNTIME_ONLY = 0x00000040;
    private static final int PERMISSION_FLAG_DEVELOPMENT = 0x00000080;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        // 新增：Hook Motorola FM Radio APK
        if (!"android".equals(lpparam.packageName)) {
            return;
        }
        hookFMRadioServicePermission(lpparam);
    }

    private void hookFMRadioServicePermission(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
	    Class<?> computerEngineClass = getComputerEngineClass(lpparam.classLoader);
            if (computerEngineClass == null) {
                XposedBridge.log("ServiceBypass: Failed to find ComputerEngine class");
                return;
            }
	    XposedBridge.hookAllMethods(
                computerEngineClass,
                "getServiceInfo",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                            nullFMServicePermisson(param);
		    }
		}
	    );
            
        } catch (Throwable e) {
            XposedBridge.log("RadioFramework: Error hooking FM Radio: " + e);
        }
    }


    private void nullFMServicePermisson(MethodHookParam param){
	    try {
                            if (param.args == null || param.args.length < 1 ||
                                !(param.args[0] instanceof ComponentName)) {
                                return;
                            }

                            ComponentName component = (ComponentName) param.args[0];
                            // 检查是否是目标服务
                            if (!isTargetService(component)) {
                                return;
                            }

                            // 获取并验证 ServiceInfo
                            ServiceInfo serviceInfo = (ServiceInfo) param.getResult();
                            if (serviceInfo == null) {
                                XposedBridge.log("ServiceBypass: ServiceInfo is null for " + component);
                                return;
                            }
                            // 绕过权限检查的核心逻辑
                            bypassPermissionChecks(serviceInfo);
                            XposedBridge.log("ServiceBypass: SUCCESS! Bypassed permissions for " + component);
                            XposedBridge.log("ServiceBypass: New permission: " +
                                (serviceInfo.permission != null ? serviceInfo.permission : "null") +
                                ", exported: " + serviceInfo.exported);

	    }catch(Throwable t){
                            XposedBridge.log("ServiceBypass: Error in getServiceInfo hook");

	    }


    }
     private Class<?> getComputerEngineClass(ClassLoader classLoader) {
        try {
            // Android AOSP 标准路径
            return XposedHelpers.findClass(
                "com.android.server.pm.ComputerEngine", classLoader);
        } catch (Throwable ignored) { return null; }
     }

     private boolean isTargetService(ComponentName component) {
        return TARGET_PACKAGE.equals(component.getPackageName()) &&
               TARGET_SERVICE.equals(component.getClassName());
     }

     private void bypassPermissionChecks(ServiceInfo serviceInfo) {
        try {
            // 1. 清除所有权限要求
            serviceInfo.permission = null;

            // 2. 强制服务可导出 (即使 manifest 中 exported=false)
            serviceInfo.exported = true;

	}
	catch (Throwable t) {
        }
     }
}

