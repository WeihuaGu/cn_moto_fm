public class FMRadioHook implements IXposedHookLoadPackage {
    private static final String FM_DESCRIPTOR = "com.motorola.android.fmradio.IFMRadioService";
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // 仅在系统进程中工作
        if (!"android".equals(lpparam.packageName)) return;
        
        hookOnTransact();
    }
    
    private void hookOnTransact() {
        XposedHelpers.findAndHookMethod(
            "android.os.Binder",
            null,
            "onTransact",
            int.class, Parcel.class, Parcel.class, int.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        // 获取当前Binder的接口描述符
                        Object binder = param.thisObject;
                        String descriptor = (String) XposedHelpers.callMethod(binder, "getInterfaceDescriptor");
                        
                        // 只处理FM服务事务
                        if (FM_DESCRIPTOR.equals(descriptor)) {
                            int code = (int) param.args[0];
                            Parcel data = (Parcel) param.args[1];
                            
                            // 安全复制Parcel以避免影响原始数据
                            Parcel dataCopy = Parcel.obtain();
                            dataCopy.setDataPosition(0);
                            dataCopy.appendFrom(data, 0, data.dataSize());
                            
                            // 记录事务
                            logFmTransaction(code, dataCopy);
                            
                            dataCopy.recycle();
                        }
                    } catch (Throwable t) {
                        XposedBridge.log("FM Hook error: " + t.getMessage());
                    }
                }
            }
        );
    }
    
    private void logFmTransaction(int code, Parcel data) {
        try {
            // 跳过descriptor (已知是FM服务)
            data.readString();
            
            // 提取参数
            StringBuilder params = new StringBuilder();
            while (data.dataAvail() > 0) {
                params.append(data.readInt()).append(", ");
            }
            
            XposedBridge.log("FM TX: " + code + " | Params: [" + 
                (params.length() > 2 ? params.substring(0, params.length()-2) : "") + "]");
        } catch (Exception e) {
            XposedBridge.log("FM TX: " + code + " | 参数解析失败");
        }
    }
}
