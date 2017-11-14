package com.sation.knxcontroller.third.webcamer.finder;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * Created by wangchunfeng on 2017/9/28.
 */

public class CamerFinder {
    public static final String DISCOVERY_PROBE_TDS = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Envelope xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns=\"http://www.w3.org/2003/05/soap-envelope\"><Header><wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">uuid:5101931c-dd3e-4f14-a8aa-c46144af3433</wsa:MessageID><wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To><wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action></Header><Body><Probe xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><Types>dn:NetworkVideoTransmitter</Types><Scopes /></Probe></Body></Envelope>";
    public static final int BROADCAST_SERVER_PORT = 3702;
    public static final String TAG = "CamerFinder";
    private DatagramSocket mSocket;
    private DatagramPacket mPacket;
    private Context mContext;
    private boolean mIsSearching = false;
    private OnCamerFinderListener mListener;
    private List<CamerDevice> mCamerList;
    private List<CamerDevice> mListCopy;

    public CamerFinder(Context context) {
        mContext = context;
        mCamerList = new ArrayList<CamerDevice>();
        mListCopy = new ArrayList<CamerDevice>();

        startWorking();
    }

    public void startWorking() {
        initSocket();
        mIsSearching = true;
        new Thread(mSearchingRunnable).start();

        /* 每隔十秒搜索一次IPC */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsSearching) {
                    try {
                        sendProbe();

                        Thread.sleep(10000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                Log.i(TAG, "exit probe loop!");
            }
        }).start();
    }

    public void stopWorking() {
        mIsSearching = false;

//        if (!mSocket.isClosed()) {
//            mSocket.close();
//        }
    }

    public List<CamerDevice> getCamerList() {
        return mCamerList;
    }

    public void setOnCamerFinderListener(OnCamerFinderListener listener) {
        mListener = listener;
    }

    private Runnable mSearchingRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] Buff = new byte[4096];
            DatagramPacket packet = new DatagramPacket(Buff,
                    Buff.length);

            while (mIsSearching) {
                try {
                    mSocket.receive(packet);
                    if (packet.getLength() > 0) {
                        String strPacket = new String(packet.getData(), 0,
                                packet.getLength());
                        Log.v("ws-discovery", " receive packets:" + strPacket );
                        processReceivedPacket(strPacket);

                        if (mListener != null) {
                            mListener.OnCamerFound();
                        }
                    }
                } catch (InterruptedIOException e) {
//                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            if (!mSocket.isClosed()) {
                mSocket.close();
            }

            Log.i(TAG, "exit receive loop!");
        }
    };


    private String getMid(String src, String head, String foot) {
        int headIndex = src.indexOf(head);
        if (headIndex == -1) {
            return null;
        }
        String tmp = src.substring(headIndex + head.length());
        int footIndex = tmp.indexOf(foot);
        if (footIndex == -1) {
            return null;
        }
        return tmp.substring(0, footIndex);
    }

    private void processReceivedPacket(String packet) {
        String address = getMid(packet, "Address>", "<");
        String uuid = null;
        if (null != address) {
            int i = address.indexOf("uuid:");
            if (-1 != i) {
                uuid = address.substring(i+5);
                int e = address.indexOf(" ");
                if (-1 != e) {
                    uuid = uuid.substring(0, e);
                }
            }
        }
        String url = getMid(packet, "XAddrs>", "<").split(" ")[0];
        if (uuid != null && url != null) {
            UUID myUUID = UUID.fromString(uuid);

            boolean isExists = false;
            for (CamerDevice device : mListCopy) {
                if (device.uuid.equals(myUUID)) {
                    mCamerList.add(device);
                    isExists = true;
                    break;
                }
            }

            if (!isExists) {
                CamerDevice cd = new CamerDevice(
                        UUID.fromString(uuid), url);
                cd.setOnline(true);
                String ip = url.substring(url.indexOf("//") + 2);
                ip = ip.substring(0, ip.indexOf("/"));
                int i = ip.indexOf(":");
                if (-1 != i) {
                    cd.setIpAddr(ip.substring(0, i));
                } else {
                    cd.setIpAddr(ip);
                }
                mCamerList.add(cd);
            }

            if (mListener != null) {
                mListener.OnCamerListUpdated();
            }
        }
    }

    private boolean isUuidExistInCameraList(UUID uuid) {
        boolean result = false;
        for (CamerDevice device : mListCopy) {
            if (device.uuid.equals(uuid)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    public static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface
                .getNetworkInterfaces(); niEnum.hasMoreElements();) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni
                        .getInterfaceAddresses()) {
                    if (interfaceAddress.getBroadcast() != null) {
                        return interfaceAddress.getBroadcast().toString()
                                .substring(1);
                    }
                }
            }
        }
        return null;
    }

    private void initSocket() {
        try {
            mSocket = new DatagramSocket();
            mSocket.setBroadcast(true);
            mSocket.setSoTimeout(3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendProbe() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mCamerList) {

                    mListCopy.clear();
                    for (CamerDevice c : mCamerList) {
                        mListCopy.add(c);
                    }
                    mCamerList.clear();
                    byte[] buf = DISCOVERY_PROBE_TDS.getBytes();
                    try {
                        String strBroadcast = getBroadcast();
                        if (strBroadcast == null) {
                            throw new IOException("Broadcast is null");
                        }
                        Log.d("ws-discovery", "Broadcast to:" + strBroadcast);
                        mPacket = new DatagramPacket(buf, buf.length,
                                InetAddress.getByName(strBroadcast), BROADCAST_SERVER_PORT);
                        mSocket.send(mPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }).start();
    }
}


