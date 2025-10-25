package com.testuse.motofm;

interface IFMRadioUserServiceCallback {
    void onServiceConnected();
    void onServiceDisconnected();
    void onCommandComplete(int cmd, int status, String value);
}
