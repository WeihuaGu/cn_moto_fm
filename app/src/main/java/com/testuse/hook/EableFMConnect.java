package com.testuse.hook;

import android.content.pm.ServiceInfo;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EableFMConnect implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        // 新增：Hook Motorola FM Radio APK
        if ("com.motorola.android.fmradio".equals(lpparam.packageName)) {
            hookFMRadioServicePermission(lpparam);
        }
    }

    private void hookFMRadioServicePermission(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> packageParserClass = lpparam.classLoader.loadClass("android.content.pm.PackageParser");
            
            // 尝试 hook generateServiceInfo (Android 10+)
            try {
                java.lang.reflect.Method generateServiceInfo = packageParserClass.getDeclaredMethod(
                    "generateServiceInfo",
                    packageParserClass.getClasses()[0], // PackageParser$Service
                    int.class
                );
                XposedBridge.hookMethod(generateServiceInfo, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object result = param.getResult();
                        if (result instanceof ServiceInfo) {
                            ServiceInfo si = (ServiceInfo) result;
                            if ("com.motorola.android.fmradio.FMRadioService".equals(si.name)) {
                                XposedBridge.log("RadioFramework: Clearing permission for FMRadioService");
                                si.permission = null; // 关键：移除权限限制
                            }
                        }
                    }
                });
                return;
            } catch (NoSuchMethodException ignored) {}

        } catch (Throwable e) {
            XposedBridge.log("RadioFramework: Error hooking FM Radio: " + e);
        }
    }
}

