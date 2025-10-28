/**
 * FM Radio 常量定义
 */
package com.testuse.motofm;

public class FMConstants {
    
    // ==================== 频段常量 ====================
    public static final int FMRADIO_BAND_US_EUROPE = 0;
    public static final int FMRADIO_BAND_JAPAN = 1;
    public static final int FMRADIO_BAND0 = 0;
    public static final int FMRADIO_BAND1 = 1;
    public static final int FMRADIO_BAND2 = 2;
    public static final int FMRADIO_BAND3 = 3;
    public static final int FMRADIO_BAND4 = 4;
    
    // 频段频率范围 (kHz)
    public static final int FMRADIO_BAND0_MIN_FREQ = 87500;  // 87.5 MHz
    public static final int FMRADIO_BAND0_MAX_FREQ = 108000; // 108.0 MHz
    public static final int FMRADIO_BAND0_STEP = 200;        // 0.2 MHz
    
    public static final int FMRADIO_BAND1_MIN_FREQ = 87500;  // 87.5 MHz
    public static final int FMRADIO_BAND1_MAX_FREQ = 108000; // 108.0 MHz
    public static final int FMRADIO_BAND1_STEP = 100;        // 0.1 MHz
    
    public static final int FMRADIO_BAND2_MIN_FREQ = 87500;  // 87.5 MHz
    public static final int FMRADIO_BAND2_MAX_FREQ = 108000; // 108.0 MHz
    public static final int FMRADIO_BAND2_STEP = 50;         // 0.05 MHz
    
    public static final int FMRADIO_BAND3_MIN_FREQ = 76000;  // 76.0 MHz (日本频段)
    public static final int FMRADIO_BAND3_MAX_FREQ = 95000;  // 95.0 MHz (日本频段)
    public static final int FMRADIO_BAND3_STEP = 100;        // 0.1 MHz
    
    // ==================== 音频模式 ====================
    public static final int FMRADIO_AUDIO_MODE_MONO = 0;
    public static final int FMRADIO_AUDIO_MODE_STEREO = 1;
    
    // ==================== 音频路由 ====================
    public static final int FMRADIO_ROUTING_HEADSET = 0;
    public static final int FMRADIO_ROUTING_SPEAKER = 1;
    
    // ==================== 静音控制 ====================
    public static final int FMRADIO_MUTE_NOT = 0;
    public static final int FMRADIO_MUTE_AUDIO = 1;
    
    // ==================== RDS模式 ====================
    public static final int FMRADIO_RDS_MODE_RDS = 0;
    public static final int FMRADIO_RDS_MODE_RBDS = 1;
    
    // ==================== 搜索方向 ====================
    public static final int FMRADIO_SEEK_DIRECTION_UP = 0;
    public static final int FMRADIO_SEEK_DIRECTION_DOWN = 1;
    public static final int FMRADIO_SEEK_DIRECTION_NONE = -1;
    
    // ==================== 收音机类型 ====================
    public static final int FMRADIO_TYPE_ANALOG = 0;
    public static final int FMRADIO_TYPE_DIGITAL = 1;
    
    // ==================== 音量控制 ====================
    public static final int FMRADIO_VOLUME_MIN = 0;
    public static final int FMRADIO_VOLUME_MAX = 15; // 0xf
    
    // ==================== 状态码 ====================
    public static final int FMRADIO_STATUS_FAIL = 0;
    public static final int FMRADIO_STATUS_OK = 1;
    
    // ==================== FM命令常量 ====================
    public static final int FM_CMD_NONE = -1;
    public static final int FM_CMD_SEEK_COMPLETE = 1;
    public static final int FM_CMD_SCAN_COMPLETE = 2;
    public static final int FM_CMD_ABORT_COMPLETE = 3;
    
    // RDS相关命令
    public static final int FM_CMD_RDS_PS_AVAILABLE = 4;    // PS节目服务名称可用
    public static final int FM_CMD_RDS_RT_AVAILABLE = 5;     // RT广播文本可用
    public static final int FM_CMD_RDS_PI_AVAILABLE = 6;     // PI节目标识可用
    public static final int FM_CMD_RDS_PTY_AVAILABLE = 7;    // PTY节目类型可用
    public static final int FM_CMD_RDS_RTPLUS_AVAILABLE = 8; // RT+增强广播文本可用
    
    // 启用/禁用相关命令
    public static final int FM_CMD_ENABLE_COMPLETE = 9;
    public static final int FM_CMD_DISABLE_COMPLETE = 10;
    
    // 获取状态命令完成
    public static final int FM_CMD_GET_AUDIOTYPE_DONE = 11;
    public static final int FM_CMD_GET_FREQ_DONE = 12;
    public static final int FM_CMD_GET_MUTE_DONE = 13;
    public static final int FM_CMD_GET_VOLUME_DONE = 14;
    public static final int FM_CMD_GET_AUDIOMODE_DONE = 15;
    public static final int FM_CMD_GET_RSSI_DONE = 16;
    
    // 设置命令完成
    public static final int FM_CMD_SET_AUDIOMODE_DONE = 17;
    public static final int FM_CMD_SET_AUDIOMUTE_DONE = 18;
    public static final int FM_CMD_SET_BAND_DONE = 19;
    public static final int FM_CMD_SET_VOLUME_DONE = 22;
    public static final int FM_CMD_SET_RSSI_DONE = 23;
    
    // RDS启用/禁用完成
    public static final int FM_CMD_ENABLE_RDS_DONE = 20;
    public static final int FM_CMD_DISABLE_RDS_DONE = 21;
    
    // 音频模式改变
    public static final int FM_CMD_AUDIO_MODE_CHANGED = 24;
    
    // 扫描中状态
    public static final int FM_CMD_SCANNING = 25;
    
    // 调谐完成
    public static final int FM_CMD_TUNE_COMPLETE = 0; // 注意：这个可能需要确认，从上下文看可能是0
    
    // ==================== 工具方法 ====================
    
    /**
     * 根据频率选择合适的频段
     */
    public static int selectBandForFrequency(int frequency) {
        if (frequency >= FMRADIO_BAND3_MIN_FREQ && frequency <= FMRADIO_BAND3_MAX_FREQ) {
            return FMRADIO_BAND_JAPAN; // 日本频段
        } else if (frequency >= FMRADIO_BAND0_MIN_FREQ && frequency <= FMRADIO_BAND0_MAX_FREQ) {
            return FMRADIO_BAND_US_EUROPE; // 美国/欧洲频段
        } else {
            return FMRADIO_BAND_US_EUROPE; // 默认
        }
    }
    
    /**
     * 将MHz转换为kHz
     */
    public static int mhzToKhz(double mhz) {
        return (int)(mhz * 1000);
    }
    
    /**
     * 将kHz转换为MHz
     */
    public static double khzToMhz(int khz) {
        return khz / 1000.0;
    }
    
    /**
     * 检查频率是否在有效范围内
     */
    public static boolean isValidFrequency(int frequency, int band) {
        switch (band) {
            case FMRADIO_BAND0:
            case FMRADIO_BAND1:
            case FMRADIO_BAND2:
                return frequency >= FMRADIO_BAND0_MIN_FREQ && frequency <= FMRADIO_BAND0_MAX_FREQ;
            case FMRADIO_BAND3:
                return frequency >= FMRADIO_BAND3_MIN_FREQ && frequency <= FMRADIO_BAND3_MAX_FREQ;
            default:
                return false;
        }
    }
}
