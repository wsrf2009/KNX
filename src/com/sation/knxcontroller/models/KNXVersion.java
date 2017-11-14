package com.sation.knxcontroller.models;

import java.io.Serializable;

/**
 * Created by wangchunfeng on 2017/4/20.
 */

public class KNXVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int Version;
    public int getVersion() {
        return Version;
    }

    private String EditorVersion;
    public String getEditorVersion() {
        return EditorVersion;
    }

    private String LastModified;
    public String getLastModified() {
        return LastModified;
    }

    private String SerialNumber;
    public String getSerialNumber() {
        return SerialNumber;
    }
}
