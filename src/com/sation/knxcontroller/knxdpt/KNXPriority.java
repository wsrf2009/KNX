package com.sation.knxcontroller.knxdpt;

public enum KNXPriority {
	 System(0), Normal(1), Urgent(2), Low(3);
	 
	 // 定义私有变量
	public int Priority;

	// 构造函数，枚举类型只能为私有
	private KNXPriority(int Priority) {
		this.Priority = Priority;
	}
} 