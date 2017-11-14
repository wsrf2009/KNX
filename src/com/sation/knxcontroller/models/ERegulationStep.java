package com.sation.knxcontroller.models;

/**
 * Created by wangchunfeng on 2017/6/4.
 */

public enum ERegulationStep {
    PointZeroOne("0.01"),
    PointZeroFive("0.05"),
    PointOne("0.1"),
    PointFive("0.5"),
    One("1"),
    Five("5");

    private String description;
    private ERegulationStep(String str) {
        description = str;
    }
    public String getDescription() {
        return description;
    }
}
