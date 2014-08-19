package edu.ucdavis.cs.cfgproject.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Taxon implements Serializable {
	String name;
	Map<String, State> characterStates = new HashMap<String, State>();
	
	public Taxon() {
	}
	
	public Taxon(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addCharStatePair(String character, State state) {
		characterStates.put(character, state);
	}
	
	public State getAllStatesByCharacter(String character) {
		return this.characterStates.get(character);
	}
	
	// return all characters of a this taxon
	public Set<String> getAllCharacters() {
		return characterStates.keySet();
	}
	
}