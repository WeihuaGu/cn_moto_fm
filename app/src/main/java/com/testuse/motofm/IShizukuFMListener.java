package com.testuse.motofm;

public interface IShizukuFMListener {
        void onServiceConnected();
        void onServiceDisconnected();
        void onShizukuStateChanged(boolean available, String reason);
}


