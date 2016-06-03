package com.sation.knxcontroller.models;


/**
 * 1-Bit - (DPT 1.*) Boolean  0 - 1  Value display, Blinds, Switch, Slider switch, Snapper switch, Button, Media button, Scene button  
 * 4-Bit - (DPT 3.*) Controlled/Dimming  Up 0 - 7, Down 0 - 7  Snapper, Snapper switch, Button, Media button  
 * 8-Bit - (DPT 5.*) Unsigned Value  0 - 255  Value display, Slider, Slider switch, RGB color light, Button, Media button, Scene button  
 * 8-Bit - (DPT 6.*) Signed Value  -128 - 127  Value display, Slider, Slider switch, Button, Media button, Scene button  
 * 16-Bit - (DPT 7.*) Unsigned Value  0 - 65535  Value display, Slider, Slider switch, Button, Media button, Scene button  
 * 16-Bit - (DPT 8.*) Signed Value  -32768 - 32767  Value display, Slider, Slider switch, Button, Media button, Scene button  
 * 16-Bit - (DPT 9.*) Float Value   Value display, Slider, Slider switch, Button, Media button, Scene button  
 * 32-Bit - (DPT 12.*) Unsigned Value  0 - 4294967295  Value display, Button, Media button  
 * 32-Bit - (DPT 13.*) Signed Value  -2147483648 - 2147483647  Value display, Button, Media button  
 * 32-Bit - (DPT 14.*) Float Value   Value display, Button, Media button  
 * 14-Byte - (DPT 16.*) String Value   Value display, Button, Media button 
 */
public enum KNXDataType {
	 Bit1, 
	 Bit2, 
	 Bit3,
	 Bit4, 
	 Bit5,
	 Bit6,
	 Bit7,
	 Bit8,
	 Bit16,
	 Bit24,
	 Bit32,
	 Bit48, 
	 Bit64, 
	 Bit80, 
	 Bit112
}
