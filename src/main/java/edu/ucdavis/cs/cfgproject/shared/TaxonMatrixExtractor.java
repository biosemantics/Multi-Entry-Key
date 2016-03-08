package edu.ucdavis.cs.cfgproject.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;
import edu.ucdavis.cs.cfgproject.shared.model.State;
import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class TaxonMatrixExtractor {
	
	public static Map<String, Set<String>> extractCharacterToStateValuesMap(TaxonMatrix taxonMatrix) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		for(String character : taxonMatrix.getCharacters()) {
			result.put(character, extractCharacterStateValues(taxonMatrix, character));
		}
		return result;
	}
	
	public static Set<String> extractCharacterStateValues(TaxonMatrix taxonMatrix, String character) {
		Set<String> result = new HashSet<String>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			State state = taxon.getState(character);
			result.addAll(state.getValues());
		}
		return result;
	}
	
	public static TaxonMatrix extractTaxonMatrix(TaxonMatrix taxonMatrix, Set<CharacterStateValue> characterStateValues) {
		Set<Taxon> taxa = new HashSet<Taxon>(taxonMatrix.getTaxa());
		for(Taxon taxon : taxonMatrix.getTaxa()) 
			for(CharacterStateValue characterStateValue : characterStateValues) 
				if(!taxon.getState(characterStateValue.getCharacter()).contains(characterStateValue.getStateValue()))
					taxa.remove(taxon);
		return new TaxonMatrix(taxa, taxonMatrix.getCharacters());
	}
	
	public static Map<String, Set<Taxon>> extractStateToTaxaMap(TaxonMatrix taxonMatrix, String character) {
		HashMap<String, Set<Taxon>> statesToSpecies = new HashMap<String, Set<Taxon>>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			State state = taxon.getState(character);
			Set<String> stateValues = state.getValues();
			for(String stateValue : stateValues) {
				Set<Taxon> taxa = new HashSet<Taxon>();
				
				if(statesToSpecies.containsKey(stateValue)) {
					taxa = statesToSpecies.get(stateValue);
					taxa.add(taxon);	
					statesToSpecies.put(stateValue, taxa);
				} else {
					taxa.add(taxon);
					statesToSpecies.put(stateValue, taxa);
				}
			}
		}
		return statesToSpecies;
	}
	
	public static Map<State, Set<Taxon>> extractStateObjToTaxaMap(TaxonMatrix taxonMatrix, String character) {
		HashMap<State, Set<Taxon>> stateObjToSpecies = new HashMap<State, Set<Taxon>>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			State state = taxon.getState(character);
			
			if (state.getValues().size() > 1) {
				Set<Taxon> taxa = new HashSet<Taxon>();
				if (stateObjToSpecies.containsKey(state)) {
					taxa = stateObjToSpecies.get(state);
					taxa.add(taxon);
					stateObjToSpecies.put(state, taxa);
				} else {
					taxa.add(taxon);
					stateObjToSpecies.put(state, taxa);
				}
			}
		}
		return stateObjToSpecies;
	}
	
}
