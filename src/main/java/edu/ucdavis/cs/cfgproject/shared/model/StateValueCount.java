package edu.ucdavis.cs.cfgproject.shared.model;

import java.io.Serializable;

public class StateValueCount implements Serializable {

	private String value;
	private int count = 1;
		
	public StateValueCount() {
		super();
	}

	public StateValueCount(String value, int count) {
		super();
		this.value = value;
		this.count = count;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
