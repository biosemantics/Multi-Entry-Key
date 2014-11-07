package edu.ucdavis.cs.cfgproject.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class State implements Serializable {
	
	private Set<String> values = new HashSet<String>();

	public State() {
	}
	
	public State(Set<String> values) {
		this.values = values;
	}
	
	public boolean isPolymorphic() {
		return values.size() > 1;
	}
	
	public Set<String> getValues() {
		return values;
	}

	public boolean contains(String value) {
		return values.contains(value);
	}	
}
