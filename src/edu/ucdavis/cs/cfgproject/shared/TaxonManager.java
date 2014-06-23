package edu.ucdavis.cs.cfgproject.shared;

import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class TaxonManager implements Serializable{
	
	private String valueSeparator = "\\|";
	private CSVReader reader;
	private List<Taxon> taxa;
	private List<String> characters;
	private HashMap<String, List<String>> statesToSpecies = new HashMap<String, List<String>>();
	
	public TaxonManager() {
		
	}
	
	public TaxonManager(List<Taxon> taxa) {
		this.taxa = taxa;
	}
	
	public TaxonManager(List<Taxon> taxa, List<String> characters) {
		this.taxa = taxa;
		this.characters = characters;
	}
	
	public List<Taxon> getTaxa() {
		return taxa;
	}
	
	public List<String> getCharacters() {
		return characters;
	}
	
	public List<Taxon> readCSVFile(String filePath) throws Exception {
		List<Taxon> result = new LinkedList<Taxon>();
		reader = new CSVReader(new FileReader(filePath), ';', CSVWriter.NO_QUOTE_CHARACTER);
		
		String[] head = reader.readNext();
	    List<String[]> allLines = reader.readAll();
	    for(String[] line : allLines) {
	    	Taxon taxon = new Taxon(line[0]);
	    	for(int i=1; i<line.length; i++) {
	    		taxon.addCharStatePair(head[i], new State(getSplitedValues(line[i])));
	    	}
	    	result.add(taxon);
	    }
	    return result;
	}

	public String[] getSplitedValues(String multiValue) {
		return multiValue.split(valueSeparator);
	}
	
	public List<String> getAllCharactersName() {
		Taxon taxon = new Taxon();
		taxon = this.getTaxa().get(0);
		List<String> mainList = new ArrayList<>(taxon.getAllCharacters());
		return mainList;
	}
	
	
	// given a particular character, return a hashmap of states:species (statesToSpecies)
	public HashMap<String, List<Taxon>> createStatesToSpecies(String character) {
		HashMap<String, List<Taxon>> statesToSpecies = new HashMap<String, List<Taxon>>();
		for(Taxon taxon : this.getTaxa()) {
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
	
	public List<String> getSpeciesNamesbyCharacterAndState(String character, String state) {
		HashMap<String, List<Taxon>> statesToSpecies = new HashMap<String, List<Taxon>>();
		List<String> speciesNames = new LinkedList<String>();
		statesToSpecies = this.createStatesToSpecies(character);
		for(Taxon taxon : statesToSpecies.get(state)) {
			speciesNames.add(taxon.getName());
		}
		return speciesNames;
	}
	
	public List<List<String>> getStatesCombByNum(List<String> states, int num) {
//		System.out.println("**********************************************");
//	    List<String> test = new LinkedList<String>();
//	    test.add("a");
//	    test.add("b");
//	    test.add("c");
//	    test.add("d");
//	    ICombinatoricsVector<String> testVector = Factory.createVector(test);
//	    Generator<String> testGen = Factory.createSimpleCombinationGenerator(testVector, 2);
//	    for (ICombinatoricsVector<String> combination : testGen) {
//	        System.out.println(combination.getVector());
//	    }
//		Will PRINT
//		**********************************************
//		[a, b]
//		[a, c]
//		[a, d]
//		[b, c]
//		[b, d]
//		[c, d]
		List<List<String>> comb = new LinkedList<List<String>>();
		ICombinatoricsVector<String> combVector = Factory.createVector(states);
		Generator<String> combGen = Factory.createSimpleCombinationGenerator(combVector, num);
		for (ICombinatoricsVector<String> combination : combGen) {
			comb.add(combination.getVector());
	    }
		return comb;
	
	}
	
	public HashMap<Integer, List<List<List<String>>>> genDict(String character) {
		HashMap<String, List<Taxon>> statesToSpecies = new HashMap<String, List<Taxon>>();
		statesToSpecies = this.createStatesToSpecies(character);
		int numOfStates = statesToSpecies.size();
		HashMap<Integer, List<List<List<String>>>> dict = new HashMap<Integer, List<List<List<String>>>>();
		List<String> states = new LinkedList<String>(); //states now contains all states 
		for(String key : statesToSpecies.keySet()) {
			states.add(key);
		}
		for(int i=1; i<=numOfStates; i++){
			List<List<String>> combStateList = new LinkedList<List<String>>();
			List<List<List<String>>> combSpeciesList = new LinkedList<List<List<String>>>();
			combStateList = this.getStatesCombByNum(states, i);
			
			// now replace state names (combStateList) as species names (combSpeciesList) 
			for (List<String> tmpList : combStateList) {
				List<List<String>> newTmpList = new LinkedList<List<String>>();
				for (String tmpString : tmpList) {
					List<String> listOfSpecies = new LinkedList<String>();
					listOfSpecies = this.getSpeciesNamesbyCharacterAndState(character, tmpString);
					newTmpList.add(listOfSpecies);
				}
				combSpeciesList.add(newTmpList);
			}
			dict.put(i, combSpeciesList);			
		}
		return dict;	
	}
	
	public double calculateNDisc1Ig (String character) {
		HashMap<Integer, List<List<List<String>>>> dict = new HashMap<Integer, List<List<List<String>>>>();
		dict = this.genDict(character);
		double tmp = 0.0;
		double p = 0.0;
		double ig = 0.0;
		int combSize = 0;
		int numOfSpecies = this.getTaxa().size();
		
		for (Map.Entry<Integer, List<List<List<String>>>> entry : dict.entrySet()) {
			int key = entry.getKey();
			List<List<List<String>>> value = entry.getValue();
			tmp = 0.0;
			
			for (List<List<String>> comb : value) {
				// now calculate the size of the combination list
				if (comb.size() > 1) {
					for (int i=1;i<comb.size();i++) {
						comb.get(0).retainAll(comb.get(i));
			        }
				}
				combSize = comb.get(0).size();
				p = 1.0 / numOfSpecies * combSize;
				if (p != 0) {
					tmp += p * logBase2(p);
				} else {
					tmp += 0;
				}
			}
			ig += Math.pow((-1), key) * tmp;
		}
		
		return ig;

	}

	public static double logBase2(double x) {
		return Math.log(x)/Math.log(2.0d);
	}
		
}
