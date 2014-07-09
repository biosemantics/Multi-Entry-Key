package edu.ucdavis.cs.cfgproject.shared;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StatesToSpeciesCreator {

	private List<Taxon> taxa;

	public StatesToSpeciesCreator(List<Taxon> taxa) {
		this.taxa = taxa;
	}
	
	// given a particular character, return a hashmap of states:species (statesToSpecies)
	public HashMap<String, List<Taxon>> createStatesToSpecies(String character) {
		HashMap<String, List<Taxon>> statesToSpecies = new HashMap<String, List<Taxon>>();
		for(Taxon taxon : taxa) {
			State statesOfTaxon = taxon.getAllStatesByCharacter(character);
			String[] stateValues = statesOfTaxon.getValues();
			for(String stateValue : stateValues) {
				List<Taxon> taxaBufferAtThisStateValue = new LinkedList<Taxon>();
				
				if(statesToSpecies.containsKey(stateValue)) {
					taxaBufferAtThisStateValue = statesToSpecies.get(stateValue);
					taxaBufferAtThisStateValue.add(taxon);	
					statesToSpecies.put(stateValue, taxaBufferAtThisStateValue);
				} else {
					taxaBufferAtThisStateValue.add(taxon);
					statesToSpecies.put(stateValue, taxaBufferAtThisStateValue);
				}
			}
		}
		return statesToSpecies;
	}
	
}
