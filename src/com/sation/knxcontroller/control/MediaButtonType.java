package com.sation.knxcontroller.control;

import java.io.Serializable;

public enum MediaButtonType implements Serializable {
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
	 Right(17);
	 
	 private int value; 
	 private MediaButtonType(int value) {
        this.value = value;
	 } 
	 public int getMediaButtonType() {
	     return value;
	 }   
}
