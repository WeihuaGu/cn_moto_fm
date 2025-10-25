package com.testuse.motofm;

import com.testuse.motofm.IFMRadioUserServiceCallback;

interface IFMRadioUserService {
    void setCallback(IFMRadioUserServiceCallback callback) = 2;
    void destroy() = 16777114;
    void exit() = 1;
    IBinder getFMRadioService() = 3;
}
