package com.motorola.android.fmradio;

import com.motorola.android.fmradio.IFMRadioServiceCallback;

interface IFMRadioService {
    boolean tune(int freq)=0;                    // ok FM_CMD_TUNE_COMPLETE = 0
    boolean getCurrentFreq()=1;                  // ok FM_CMD_GET_FREQ_DONE = 12
    boolean setAudioMode(int mode)=2;            // ok may
    int getAudioMode()=3;                        // x
    int getBand()=4;                            // TRANSACTION_getBand = 0xd -> 12
    boolean stopSeek()=5;                        // x
    boolean seek(int direction)=6;               // ok may FM_CMD_SEEK_COMPLETE = 1
    boolean setMute(int mode)=7;                 // ok may FM_CMD_SET_AUDIOMUTE_DONE = 18
    boolean setAbort()=8;                        // ok
    boolean getVolume()=9;                       // x
    boolean setVolume(int volume)=10;            // ok FM_CMD_SET_VOLUME_DONE = 22
    boolean scan()=11;                           // ok may FM_CMD_SCANNING = 25
    boolean stopScan()=12;                        // TRANSACTION_stopScan = 0xa -> 9
    boolean setBand(int band)=13;                // ok FM_CMD_SET_BAND_DONE = 19
    int getMinFrequence()=14;                    // ok 
    int getMaxFrequence()=15;                    // ok
    int getStepUnit()=16;                        // ok
    void registerCallback(IFMRadioServiceCallback cb)=17; // ok
    void unregisterCallback(IFMRadioServiceCallback cb)=18; // TRANSACTION_unregisterCallback = 0x13 -> 18
    boolean setRdsEnable(boolean flag, int mode)=19; // TRANSACTION_setRdsEnable = 0x14 -> 19
    boolean isRdsEnable()=20;                    // TRANSACTION_isRdsEnable = 0x15 -> 20
    boolean getAudioType()=21;                   // x
    boolean getRSSI()=22;                        // ok may FM_CMD_GET_RSSI_DONE = 16
    String getRdsPS()=23;                        // ok
    String getRdsRT()=24;                        // ok
    String getRdsRTPLUS()=25;                    // ok
    int getRdsPI()=26;                           // TRANSACTION_getRdsPI = 0x1b -> 26
    int getRdsPTY()=27;                          // TRANSACTION_getRdsPTY = 0x1c -> 27
    boolean setRSSI(int rssi)=28;                // TRANSACTION_setRSSI = 0x1d -> 28
    String getRDSStationName()=29;               // ok
    void notifyFMStatus(boolean status)=30;      // TRANSACTION_notifyFMStatus = 0x1f -> 30
    void setFMRouting(int routing)=31;           // TRANSACTION_setFMRouting = 0x20 -> 31
    boolean isSpeakerSupported()=32;             // TRANSACTION_isSpeakerSupported = 0x21 -> 32
    int getFMStreamType()=33;                    // TRANSACTION_getFMStreamType = 0x22 -> 33
    boolean enable(int band)=34;                 // ok FM_CMD_ENABLE_COMPLETE = 9
    boolean disable()=35;                        // ok may FM_CMD_DISABLE_COMPLETE = 10
    boolean isMute()=36;                          // TRANSACTION_isMute = 0x6 -> 5
    boolean setCustomBand(int minFreq, int maxFreq, int defaultFreq, int step)=37; // TRANSACTION_setCustomBand = 0x26 -> 37
    boolean isCustomBandSupported()=38;          // TRANSACTION_isCustomBandSupported = 0x27 -> 38
    void recordingAudioOnPrepare()=39;           // TRANSACTION_recordingAudioOnPrepare = 0x28 -> 39
    void recordingAudioOffPrepare()=40;          // TRANSACTION_recordingAudioOffPrepare = 0x29 -> 40
    boolean isFmOn()=41;                         // x
}
