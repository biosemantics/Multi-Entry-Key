package edu.ucdavis.cs.cfgproject.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;
import edu.ucdavis.cs.cfgproject.shared.model.State;
import edu.ucdavis.cs.cfgproject.shared.model.StateValueCount;
import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class TaxonMatrixExtractor {
	
	public static Map<String, Set<StateValueCount>> extractCharacterToStateValuesMap(TaxonMatrix taxonMatrix) {
		Map<String, Set<StateValueCount>> result = new HashMap<String, Set<StateValueCount>>();
		for(String character : taxonMatrix.getCharacters()) {
			
			result.put(character, extractCharacterStateValues(taxonMatrix, character));
		}
		return result;
	}
	
	public static Set<StateValueCount> extractCharacterStateValues(TaxonMatrix taxonMatrix, String character) {
		Set<StateValueCount> result = new HashSet<StateValueCount>();
		Map<String, Integer> valueCount = new HashMap<String, Integer>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			State state = taxon.getState(character);
			
			for(String value : state.getValues()) {
				if(!valueCount.containsKey(value))
					valueCount.put(value, 0);
				valueCount.put(value, valueCount.get(value) + 1);
			}
		}
		
		for(String value : valueCount.keySet())
			result.add(new StateValueCount(value, valueCount.get(value)));
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
	
	public static Map<Set<String>, Set<Taxon>> extractStateObjToTaxaMap(TaxonMatrix taxonMatrix, String character) {
		HashMap<Set<String>, Set<Taxon>> stateObjToSpecies = new HashMap<Set<String>, Set<Taxon>>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			State state = taxon.getState(character);
			Set<String> stateValues = state.getValues();
			
			if (stateValues.size() > 1) {
				Set<Taxon> taxa = new HashSet<Taxon>();
				if (stateObjToSpecies.containsKey(stateValues)) {
					taxa = stateObjToSpecies.get(stateValues);
					taxa.add(taxon);
					stateObjToSpecies.put(stateValues, taxa);
				} else {
					taxa.add(taxon);
					stateObjToSpecies.put(stateValues, taxa);
				}
			}
		}
		return stateObjToSpecies;
	}
	
}
