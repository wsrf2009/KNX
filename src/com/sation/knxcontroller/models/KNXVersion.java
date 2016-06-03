package com.sation.knxcontroller.models;

import java.io.Serializable;

public class KNXVersion  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//Version
	private int Version;  
	public int getVersion() {
		return Version;
	}
	public void setVersion(int version) {
		Version = version;
	}
	
	//editorVersion
	private String EditorVersion;  
	public String getEditorVersion() {
		return EditorVersion;
	}
	public void setEditorVersion(String editorVersion) {
		EditorVersion = editorVersion;
	}
	
	//LastModified
	private String LastModified;  
	public String getLastModified() {
		return LastModified;
	}
	public void setLastModified(String editorVersion) {
		LastModified = editorVersion;
	}
	
	//SerialNumber
	private String SerialNumber;  
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}


}
