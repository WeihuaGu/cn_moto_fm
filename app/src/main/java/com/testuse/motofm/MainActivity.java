package com.testuse.motofm;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements IFMRadioListener{
    private static final String TAG = "MotoFMMain";
    private FMRadioClient mFMClient;

    private TextView mTvStatus;
    private Handler mHandler;
    
    private static final int FIXED_FREQUENCY = 102600;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTvStatus = findViewById(R.id.tv_status);
        mHandler = new Handler();

        mFMClient = new FMRadioClient(this);
        mFMClient.setListener(this);
        mHandler.postDelayed(this::connectWithFMClient, 1000);
    }
    
    private void updateStatus(String message) {
        runOnUiThread(() -> mTvStatus.setText(message));
    }
    /////////////////////////////////////////////////////////////
    private void connectWithFMClient() {
	mFMClient.connect();
    }
    
    // IFMRadioListener 实现
    @Override
    public void onFMServiceConnected() {
        updateStatus("FM服务已连接");
    }
    
    @Override
    public void onFMServiceDisconnected() {
        updateStatus("FM服务已断连");
    }
    
    @Override
    public void onFMServiceBinderGet() {
	mFMClient.startFM();
	mFMClient.tune(FIXED_FREQUENCY);
    }



    ///////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mFMClient != null) {
            //mFMClient.stopFM();
        }
    }
}
