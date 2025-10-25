package android.app;

import android.content.Intent;
import android.content.ComponentName;
import android.os.IBinder;

interface IActivityManager {
    int bindService(IBinder caller, IBinder token, in Intent service, 
                   String resolvedType, IBinder connection, int flags, 
                   String callingPackage, int userId);
    
    ComponentName startService(IBinder caller, in Intent service, 
                              String resolvedType, boolean requireForeground, 
                              String callingPackage, int userId);
    
    IBinder getService(String name);
}
