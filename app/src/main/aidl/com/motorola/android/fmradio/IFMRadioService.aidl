package com.motorola.android.fmradio;

import com.motorola.android.fmradio.IFMRadioServiceCallback;

interface IFMRadioService {
    boolean tune(int freq)=0;                    // TRANSACTION_tune = 0x1 -> 0
    boolean getCurrentFreq()=1;                  // TRANSACTION_getCurrentFreq = 0x2 -> 1
    boolean setAudioMode(int mode)=2;            // TRANSACTION_setAudioMode = 0x3 -> 2
    int getAudioMode()=3;                        // TRANSACTION_getAudioMode = 0x4 -> 3
    boolean setMute(int mode)=4;                 // TRANSACTION_setMute = 0x5 -> 4
    boolean isMute()=5;                          // TRANSACTION_isMute = 0x6 -> 5
    boolean seek(int direction)=6;               // TRANSACTION_seek = 0x7 -> 6
    boolean scan()=7;                            // TRANSACTION_scan = 0x8 -> 7
    boolean stopSeek()=8;                        // TRANSACTION_stopSeek = 0x9 -> 8
    boolean stopScan()=9;                        // TRANSACTION_stopScan = 0xa -> 9
    boolean setVolume(int volume)=10;            // TRANSACTION_setVolume = 0xb -> 10
    boolean getVolume()=11;                      // TRANSACTION_getVolume = 0xc -> 11
    int getBand()=12;                            // TRANSACTION_getBand = 0xd -> 12
    boolean setBand(int band)=13;                // TRANSACTION_setBand = 0xe -> 13
    int getMinFrequence()=14;                    // TRANSACTION_getMinFrequence = 0xf -> 14
    int getMaxFrequence()=15;                    // TRANSACTION_getMaxFrequence = 0x10 -> 15
    int getStepUnit()=16;                        // TRANSACTION_getStepUnit = 0x11 -> 16
    void registerCallback(IFMRadioServiceCallback cb)=17; // TRANSACTION_registerCallback = 0x12 -> 17
    void unregisterCallback(IFMRadioServiceCallback cb)=18; // TRANSACTION_unregisterCallback = 0x13 -> 18
    boolean setRdsEnable(boolean flag, int mode)=19; // TRANSACTION_setRdsEnable = 0x14 -> 19
    boolean isRdsEnable()=20;                    // TRANSACTION_isRdsEnable = 0x15 -> 20
    boolean getAudioType()=21;                   // TRANSACTION_getAudioType = 0x16 -> 21
    boolean getRSSI()=22;                        // TRANSACTION_getRSSI = 0x17 -> 22
    String getRdsPS()=23;                        // TRANSACTION_getRdsPS = 0x18 -> 23
    String getRdsRT()=24;                        // TRANSACTION_getRdsRT = 0x19 -> 24
    String getRdsRTPLUS()=25;                    // TRANSACTION_getRdsRTPLUS = 0x1a -> 25
    int getRdsPI()=26;                           // TRANSACTION_getRdsPI = 0x1b -> 26
    int getRdsPTY()=27;                          // TRANSACTION_getRdsPTY = 0x1c -> 27
    boolean setRSSI(int rssi)=28;                // TRANSACTION_setRSSI = 0x1d -> 28
    String getRDSStationName()=29;               // TRANSACTION_getRDSStationName = 0x1e -> 29
    void notifyFMStatus(boolean status)=30;      // TRANSACTION_notifyFMStatus = 0x1f -> 30
    void setFMRouting(int routing)=31;           // TRANSACTION_setFMRouting = 0x20 -> 31
    boolean isSpeakerSupported()=32;             // TRANSACTION_isSpeakerSupported = 0x21 -> 32
    int getFMStreamType()=33;                    // TRANSACTION_getFMStreamType = 0x22 -> 33
    boolean enable(int band)=34;                 // TRANSACTION_enable = 0x23 -> 34
    boolean disable()=35;                        // TRANSACTION_disable = 0x24 -> 35
    boolean isFmOn()=36;                         // TRANSACTION_isFmOn = 0x25 -> 36
    boolean setCustomBand(int minFreq, int maxFreq, int defaultFreq, int step)=37; // TRANSACTION_setCustomBand = 0x26 -> 37
    boolean isCustomBandSupported()=38;          // TRANSACTION_isCustomBandSupported = 0x27 -> 38
    void recordingAudioOnPrepare()=39;           // TRANSACTION_recordingAudioOnPrepare = 0x28 -> 39
    void recordingAudioOffPrepare()=40;          // TRANSACTION_recordingAudioOffPrepare = 0x29 -> 40
}
