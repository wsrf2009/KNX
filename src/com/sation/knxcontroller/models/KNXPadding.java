package com.sation.knxcontroller.models;

import java.io.Serializable;

/**
 * Created by wangchunfeng on 2017/8/15.
 */

public class KNXPadding implements Serializable {
    private int Left;
    public int getLeft() {
        return this.Left;
    }

    private int Top;
    public int getTop() {
        return this.Top;
    }

    private int Right;
    public int getRight() {
        return this.Right;
    }

    private int Bottom;
    public int getBottom() {
        return this.Bottom;
    }
}
