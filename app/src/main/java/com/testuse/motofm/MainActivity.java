package com.testuse.motofm;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MotoFMMain";
    
    private FMRadioClient mFMRadio;
    private TextView mTvStatus;
    
    // 固定频率 102.6 MHz = 102600 kHz
    private static final int FIXED_FREQUENCY = 102600;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG, "MainActivity created - Starting FM Radio at " + (FIXED_FREQUENCY/1000.0) + " MHz");
        
        mTvStatus = findViewById(R.id.tv_status);
        mFMRadio = new FMRadioClient(this);
        
        // 自动连接并播放固定频率
        connectAndPlay();
    }
    
    private void connectAndPlay() {
        updateStatus("正在启动FM收音机...");
        
        if (mFMRadio.connect()) {
            updateStatus("FM服务连接中...");
            
            // 延迟执行调谐，确保服务已连接
            new android.os.Handler().postDelayed(() -> {
                if (mFMRadio.tune(FIXED_FREQUENCY)) {
                    updateStatus("正在播放: " + (FIXED_FREQUENCY/1000.0) + " MHz");
                    Log.d(TAG, "Successfully tuned to " + (FIXED_FREQUENCY/1000.0) + " MHz");
                } else {
                    updateStatus("调谐失败，请检查权限");
                    Log.e(TAG, "Failed to tune to " + (FIXED_FREQUENCY/1000.0) + " MHz");
                }
            }, 1000); // 延迟1秒确保服务连接
        } else {
            updateStatus("FM服务连接失败");
            Log.e(TAG, "Failed to connect to FM service");
        }
    }
    
    private void updateStatus(String message) {
        runOnUiThread(() -> mTvStatus.setText(message));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFMRadio != null) {
            mFMRadio.disconnect();
            Log.d(TAG, "FM service disconnected on destroy");
        }
    }
}
