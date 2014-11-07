package edu.ucdavis.cs.cfgproject.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Taxon implements Serializable {
	
	private String name;
	private Map<String, State> characterStateMap = new HashMap<String, State>();
	
	public Taxon() {
	}
	
	public Taxon(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setState(String character, State state) {
		characterStateMap.put(character, state);
	}
	
	public State getState(String character) {
		return this.characterStateMap.get(character);
	}
	
	// return all characters of a this taxon
	public Set<String> getCharacters() {
		return characterStateMap.keySet();
	}
	
}