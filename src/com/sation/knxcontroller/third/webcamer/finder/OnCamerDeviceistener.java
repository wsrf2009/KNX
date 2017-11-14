package com.sation.knxcontroller.third.webcamer.finder;

/**
 * Created by wangchunfeng on 2017/9/29.
 */

public interface OnCamerDeviceistener {
    void onSoapDone(CamerDevice device, boolean success);
    void onDisconnected(CamerDevice device);
}
