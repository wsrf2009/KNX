package com.zyyknx.android.models;

public class RequestEntity {

	public RequestEntity(String key, Object value) {
		this.Key = key;
		this.Value = value;
	}

	public String Key;

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public Object Value;

	public Object getValue() {
		return Value;
	}

	public void setValuse(Object value) {
		Value = value;
	}
}
