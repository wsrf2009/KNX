package com.sation.knxcontroller.models;

/**
 * Created by wangchunfeng on 2017/6/3.
 */

public enum EDecimalDigit {
    Zero("None"),
    One(".x"),
    Two(".xx");

    private String description;
    private EDecimalDigit(String str) {
        description = str;
    }
    public String getDescription() {
        return description;
    }
}
