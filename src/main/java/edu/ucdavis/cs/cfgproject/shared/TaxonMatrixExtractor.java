package edu.ucdavis.cs.cfgproject.shared;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	

	public static TaxonMatrix extractTaxonMatrix(TaxonMatrix taxonMatrix, String character, String state) {
		List<Taxon> taxa = new LinkedList<Taxon>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			if(taxon.getState(character).contains(state))
				taxa.add(taxon);
		}
		return new TaxonMatrix(taxa);
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
	
}
