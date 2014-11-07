package edu.ucdavis.cs.cfgproject.shared.model;

public class CharacterStateValue {

	private String character;
	private String stateValue;
	
	public CharacterStateValue(String character, String stateValue) {
		super();
		this.character = character;
		this.stateValue = stateValue;
	}

	public String getCharacter() {
		return character;
	}

	public String getStateValue() {
		return stateValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((character == null) ? 0 : character.hashCode());
		result = prime * result
				+ ((stateValue == null) ? 0 : stateValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CharacterStateValue other = (CharacterStateValue) obj;
		if (character == null) {
			if (other.character != null)
				return false;
		} else if (!character.equals(other.character))
			return false;
		if (stateValue == null) {
			if (other.stateValue != null)
				return false;
		} else if (!stateValue.equals(other.stateValue))
			return false;
		return true;
	}
	
	
	
}
