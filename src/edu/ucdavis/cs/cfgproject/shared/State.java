package edu.ucdavis.cs.cfgproject.shared;

import java.io.Serializable;

public class State implements Serializable {
	
	private String[] values;

	public State() {
	}
	
	public State(String[] values) {
		this.values = values;
	}
	
	public boolean isPolymorphic() {
		return values.length > 1;
	}
	
	public String[] getValues() {
		return values;
	}
	
	public String getValue(int i) {
		return values[i];
	}
	
}
