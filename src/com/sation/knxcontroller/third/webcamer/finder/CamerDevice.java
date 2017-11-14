package com.sation.knxcontroller.third.webcamer.finder;



import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.Frame;
import com.googlecode.javacv.cpp.opencv_core;
import com.sation.knxcontroller.third.webcamer.ipcammanager.IPCam;
import com.sation.knxcontroller.util.Log;


//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.Frame;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.UUID;

import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2RGBA;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;


/**
 * Created by wangchunfeng on 2017/9/28.
 */

public class CamerDevice implements IPCam {
    private static final String TAG = "CamerDevice";

    public UUID uuid;
    public String serviceURL;
    private int id;
    private String name;
    private String ipAddr;
    private boolean isOnline = false;
    public String rtspUri = "";
    private FFmpegFrameGrabber mGrabber;
    private OnCamerDeviceistener mListener;
//    private AudioTrack mAudioTrack;

    public int width;
    public int height;
    public int rate;
//    public boolean isConnected = false;

    public String username;
    public String password;

    public CamerDevice(UUID uuid, String serviceURL) {
        this.uuid = uuid;
        this.serviceURL = serviceURL;

        try {

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSecurity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setProperties(int width, int height, int rate) {
        this.width = width;
        this.height = height;
        this.rate = rate;
//        Log.i(TAG, "width:"+width+" height:"+height);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    @Override
    public void IPCamInit() {
//        if (null != mGrabber) {
        try {
            IPCamRelease();
//        }

            if (this.isOnline) {
                HttpSoap soap = new HttpSoap(this);
                soap.setOnHttpSoapListener(listener);
                soap.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setOnCamerDeviceistener(OnCamerDeviceistener listener) {
        mListener = listener;
    }

    private OnHttpSoapListener listener = new OnHttpSoapListener() {
        @Override
        public void OnHttpSoapDone(CamerDevice camer, String uri, boolean success) {
            if (success) {
                rtspUri = uri.substring(0, uri.indexOf("//") + 2) + camer.username
                        + ":" + camer.password + "@"
                        + uri.substring(uri.indexOf("//") + 2);
                Log.i(TAG, "rtspUri:"+rtspUri);

                synchronized (this) {
                    Log.i(TAG, "get the per!  this:"+this);
                    mGrabber = new FFmpegFrameGrabber(rtspUri);
//                mGrabber.setImageWidth(width);
//                mGrabber.setImageHeight(height);
                    try {
                        mGrabber.start();
//                        int m_out_buf_size = AudioTrack.getMinBufferSize(8000,
//                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                                AudioFormat.ENCODING_PCM_16BIT);
//                        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
//                                AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                                AudioFormat.ENCODING_PCM_16BIT,
//                                m_out_buf_size,
//                                AudioTrack.MODE_STREAM);
//                        mAudioTrack.play() ;

                        if (mListener != null) {
                            mListener.onSoapDone(CamerDevice.this, success);
                        }

//                        isConnected = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
//            if (mListener != null) {
//                mListener.onSoapDone(CamerDevice.this, success);
//            }
        }
    };

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getIpAddress() {
        return this.ipAddr;
    }

    @Override
    public boolean isOnline() {
        return this.isOnline;
    }

    @Override
    public Bitmap grabImage() {
        Bitmap bm = null;

        synchronized (this) {
            if (null != mGrabber) {
                try {
//                    Frame f = mGrabber.grabFrame();
//                    opencv_core.IplImage image = f.image;
                    opencv_core.IplImage image = mGrabber.grab();
//                    Buffer[] audio = f.samples;
//                    Log.i(TAG, "f:" + f);
                    if (null != image) {
                        opencv_core.IplImage dst = cvCreateImage(
                                new opencv_core.CvSize(this.width, this.height), image.depth(), 4);
                        cvCvtColor(image, dst, CV_BGR2RGBA);
                        bm = Bitmap.createBitmap(this.width,
                                this.height, Bitmap.Config.ARGB_8888);
                        bm.copyPixelsFromBuffer(dst.getByteBuffer());

                        cvReleaseImage(dst);
                    }

//                    if (null != audio) {
//                        ShortBuffer sb = (ShortBuffer) audio[0];
//                        short[] shorts = new short[sb.capacity()];
//                        sb.get(shorts);
//                        mAudioTrack.write(shorts, 0, shorts.length);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bm;
    }

    @Override
    public void IPCamRelease() {
        synchronized (this) {
            if (null != mGrabber) {
                try {
                    mGrabber.stop();
//                    mAudioTrack.stop();

//                    mGrabber = null;
//                    isConnected = false;

                    if (null != mListener) {
                        mListener.onDisconnected(this);
                    }
//                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Log.i(TAG, "this:"+this);

//                throw new Exception("IP Camer disconnected!");

//                mAudioTrack = null;

            }
        }
    }
}
