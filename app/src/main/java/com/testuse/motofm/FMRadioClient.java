package com.testuse.motofm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.motorola.android.fmradio.IFMRadioService;
import com.motorola.android.fmradio.IFMRadioServiceCallback;

public class FMRadioClient {
    private static final String TAG = "MotoFMClient";
    
    private Context mContext;
    private IFMRadioService mService;
    private ServiceConnection mConnection;
    private boolean mIsConnected = false;
    
    public FMRadioClient(Context context) {
        mContext = context;
    }
    
    public boolean tune(int frequency) {
	return false;

    }
}
