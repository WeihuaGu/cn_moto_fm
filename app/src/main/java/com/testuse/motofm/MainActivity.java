package com.testuse.motofm;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements IShizukuFMListener , IFMRadioListener{
    private static final String TAG = "MotoFMMain";
    private ShizukuFMClient mShizukuFMClient;
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

        mShizukuFMClient = new ShizukuFMClient(this);
        mShizukuFMClient.setListener(this);
        //updateStatus("正在初始化 Shizuku...");
        // 延迟连接，确保界面加载完成
        // mHandler.postDelayed(this::connectWithShizuku, 1000);

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
    private void connectWithShizuku() {
        updateStatus("检查 Shizuku 状态...");
	if (!mShizukuFMClient.hasShizukuPermission()){
            updateStatus("Shizuku 未授权");
	    return;
	}
        mShizukuFMClient.connect();
    }
    
    // ShizukuFMListener 实现
    @Override
    public void onShizukuStateChanged(boolean available, String reason) {
        Log.d(TAG, "Shizuku state: " + available + " - " + reason);
    }
    @Override
    public void onServiceConnected() {
        updateStatus("Shizuku服务已连接");
	try{
           IBinder fmradiobinder = mShizukuFMClient.getFMRadioUserServiceObject().getFMRadioService();
	   if(fmradiobinder != null){
	   }else{
                updateStatus("获取moto fmradio service失败");
	   }
	}catch (RemoteException e){
		Log.e(TAG, "get fmradio binder failed - RemoteException", e);
	}
    }
    @Override
    public void onServiceDisconnected() {
        updateStatus("Shizuku服务已断开");
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
        updateStatus("获取到remote fm Ibinder");
	mFMClient.tune(FIXED_FREQUENCY);
    }



    ///////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mShizukuFMClient != null) {
            mShizukuFMClient.disconnect();
        }
    }
}
