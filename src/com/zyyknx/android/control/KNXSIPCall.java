package com.zyyknx.android.control;

public class KNXSIPCall extends KNXControlBase {
	private static final long serialVersionUID = 1L;
	
	// 标签
	private String Lable;
	public String getLable() {
		return Lable;
	}
	public void setLable(String lable) {
		Lable = lable;
	}
	
	//
	private String Note; 
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}  

	//用户名称
	private String UserName;
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	
	//SIP域名
	private String SIPDomain;
	public String getSIPDomain() {
		return SIPDomain;
	}
	public void setSIPDomain(String sIPDomain) {
		SIPDomain = sIPDomain;
	}
	
	//认证用户
	private String AuthenticationUserName;
	public String getAuthenticationUserName() {
		return AuthenticationUserName;
	}
	public void setAuthenticationUserName(String authenticationUserName) {
		AuthenticationUserName = authenticationUserName;
	}
	
	//认证密码
	private String AuthenticationUserPassword;
	public String getAuthenticationUserPassword() {
		return AuthenticationUserPassword;
	}
	public void setAuthenticationUserPassword(String authenticationUserPassword) {
		AuthenticationUserPassword = authenticationUserPassword;
	}
	
	//端口
	private int Port;
	public int getPort() {
		return Port;
	}
	public void setPort(int port) {
		Port = port;
	}
	
	//协议类型
	private Prococal Prococal;
	public Prococal getPrococal() {
		return Prococal;
	}
	public void setPrococal(Prococal prococal) {
		Prococal = prococal;
	}
	
	//代理地址
	private String ProxyAddressUrl;
	public String getProxyAddressUrl() {
		return ProxyAddressUrl;
	}
	public void setProxyAddressUrl(String proxyAddressUrl) {
		ProxyAddressUrl = proxyAddressUrl;
	}
	
	
} 
