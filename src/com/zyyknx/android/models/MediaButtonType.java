package com.zyyknx.android.models;

public enum MediaButtonType {
	Back(0),
    Backward(1),
    Menu(2),
    Stop(3),
    BackwardSkip(4),
    Mute(5),
    Up(6),
    Okey(7),
    VolumeDown(8),
    Down(9),
    Pause(10),
    VolumeUp(11),
    Forward(12),
    Play(13),
    ForwardSkip(14),
    Power(15),
    Left(16),
    Righ(17);
	
	 // 定义私有变量
	public int mMediaButtonType; 
	// 构造函数，枚举类型只能为私有
	private MediaButtonType(int mediaButtonType) {
		this.mMediaButtonType = mediaButtonType;
	}
}
