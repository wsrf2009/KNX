package com.zyyknx.android.models;

public enum StatusCode {
	OK(1),
	BusinessDataException(0), // 业务数据异常
	NoPrivilige(-2), // 没有权限
	SessionTimeOut(-3), // Session 过期
	Fail(-1);

	// 定义私有变量
	public int Code;

	// 构造函数，枚举类型只能为私有
	private StatusCode(int code) {
		this.Code = code;
	}
}
