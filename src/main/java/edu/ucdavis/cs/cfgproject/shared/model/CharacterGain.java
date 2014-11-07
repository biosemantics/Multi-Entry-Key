package edu.ucdavis.cs.cfgproject.shared.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CharacterGain implements Serializable, Comparable<CharacterGain> {

	private String character;
	private Double informationGain;
	
	public CharacterGain() { }
	
	public CharacterGain(String character, Double informationGain) {
		this.character = character;
		this.informationGain = informationGain;
	}
	
	public String getCharacter() {
		return character;
	}
	public Double getInformationGain() {
		return informationGain;
	}

	@Override
	public int compareTo(CharacterGain o) {
		return o.informationGain.compareTo(informationGain);
	}
	
	@Override
	public String toString() {
		return round(informationGain, 2) + " "+ character;
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
}
