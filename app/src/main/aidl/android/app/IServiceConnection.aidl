package android.app;

interface IServiceConnection {
    void connected(in ComponentName name, IBinder service);
    void disconnected(in ComponentName name);
}
