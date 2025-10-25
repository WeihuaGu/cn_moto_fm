package android.app;

import android.content.ComponentName;
import android.os.IBinder;

interface IServiceConnection {
    void connected(in ComponentName name, IBinder service);
    void disconnected(in ComponentName name);
}
