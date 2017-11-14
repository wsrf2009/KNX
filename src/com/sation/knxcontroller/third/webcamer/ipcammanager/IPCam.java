package com.sation.knxcontroller.third.webcamer.ipcammanager;



import android.graphics.Bitmap;

import com.googlecode.javacv.Frame;

//import org.bytedeco.javacv.Frame;

/**
 * Created by wangchunfeng on 2017/9/28.
 */

public interface IPCam {
    //Initial the network connection of the IP camera.
    void IPCamInit();

    //Get the unique identification for this IP camera.
    int getId();

    //Get the IP camera name set by user in smart tv system ui.
    String getName();

    //Get the IP address of this IP camera with XXX.XXX.XXX.XXX format.
    String getIpAddress();

    //Get the connect status of this IP camera.
    boolean isOnline();

    //Get the frame from IP camera. I will call this method on non-ui thread.
    Bitmap grabImage();

    //Release the network connection of the IP camera.
    void IPCamRelease();
}
