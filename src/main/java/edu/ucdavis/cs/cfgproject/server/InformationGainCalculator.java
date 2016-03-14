package edu.ucdavis.cs.cfgproject.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import edu.ucdavis.cs.cfgproject.shared.TaxonMatrixExtractor;
import edu.ucdavis.cs.cfgproject.shared.model.State;
import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class InformationGainCalculator {
	
	public double calculateNDisc1Ig(TaxonMatrix taxonMatrix, String character) {
		Map<String, Set<Taxon>> stateToTaxaMap = TaxonMatrixExtractor.extractStateToTaxaMap(taxonMatrix, character);
		Map<Set<String>, Set<Taxon>> stateObjToTaxaMap = TaxonMatrixExtractor.extractStateObjToTaxaMap(taxonMatrix, character);
//		HashMap<Integer, List<List<String>>> dict = new HashMap<Integer, List<List<String>>>();
//		dict = this.genDict(stateToTaxaMap);
		double tmp = 0.0;
		double p = 0.0;
		double ig = 0.0;
		int combSize = 0;
		int numberOfTaxa = taxonMatrix.size();
		
		// test
//		System.out.println("size="+ stateToTaxaMap.size());
//		for (Map.Entry<String, Set<Taxon>> entry : stateToTaxaMap.entrySet()) {
//			System.out.println("key="+entry.getKey());
//			Set<Taxon> tmptaxa = new HashSet<Taxon>();
//			tmptaxa = entry.getValue();
//			for (Taxon taxon : tmptaxa) {
//				System.out.println(taxon.getName());
//			}
//		}
//		System.out.println("size="+ stateObjToTaxaMap.size());
//		for (Map.Entry<Set<String>, Set<Taxon>> entry : stateObjToTaxaMap.entrySet()) {
//			System.out.println(entry.getKey());
//			Set<Taxon> tmptaxa = new HashSet<Taxon>();
//			tmptaxa = entry.getValue();
//			for (Taxon taxon : tmptaxa) {
//				System.out.println(taxon.getName());
//			}
//		}
		
		// state size = 1
		List<String> states = new LinkedList<String>(); //states now contains all states 
		for (String key : stateToTaxaMap.keySet()) {
			states.add(key);
		}
//		System.out.println(states);
		
		for (String state : states) {
			List<String> listOfSpecies = new LinkedList<String>();
			listOfSpecies = this.getTaxonNamesbyState(stateToTaxaMap, state);
			combSize = listOfSpecies.size();
			p = 1.0 / numberOfTaxa * combSize;
			if (p != 0) {
				tmp += p * logBase2(p);
			} else {
				tmp += 0;
			}
		}
		ig += (-1) * tmp;
		
		// state size > 1
		for (Map.Entry<Set<String>, Set<Taxon>> entry : stateObjToTaxaMap.entrySet()) {
			int stateSize = entry.getKey().size();
			int speciesSize = entry.getValue().size();
			p = 1.0 / numberOfTaxa * speciesSize;
			if (p != 0) {
				tmp = p * logBase2(p);
			} else {
				tmp = 0;
			}
			ig += Math.pow((-1), stateSize) * tmp;
		}
		
//		for (Map.Entry<Integer, List<List<String>>> entry : dict.entrySet()) {
//			int key = entry.getKey();
//			List<List<String>> value = entry.getValue();
//			tmp = 0.0;
//			
//			for (List<String> comb : value) {
//				// now calculate the size of the combination list
//				List<String> listOfSpecies = new LinkedList<String>();
//				if (comb.size() > 1) {
//					listOfSpecies = this.getTaxonNamesbyState(stateToTaxaMap, comb.get(0));
//					for (int i=1;i<comb.size();i++) {
//						List<String> tmpSpecies = new LinkedList<String>();
//						tmpSpecies = this.getTaxonNamesbyState(stateToTaxaMap, comb.get(i));
//						listOfSpecies.retainAll(tmpSpecies);
//			        }
//				} else {
//					listOfSpecies = this.getTaxonNamesbyState(stateToTaxaMap, comb.get(0));
//				}
//				combSize = listOfSpecies.size();
//				p = 1.0 / numberOfTaxa * combSize;
//				if (p != 0) {
//					tmp += p * logBase2(p);
//				} else {
//					tmp += 0;
//				}
//			}
//			ig += Math.pow((-1), key) * tmp;
//		}
		System.out.println("IG is " + ig);
		return ig;

	}

	public static double logBase2(double x) {
		return Math.log(x)/Math.log(2.0d);
	}
	
	public HashMap<Integer, List<List<String>>> genDict(Map<String, Set<Taxon>> stateToTaxaMap) {
		int numOfStates = stateToTaxaMap.size();
		HashMap<Integer, List<List<String>>> dict = new HashMap<Integer, List<List<String>>>();
		List<String> states = new LinkedList<String>(); //states now contains all states 
		for(String key : stateToTaxaMap.keySet()) {
			states.add(key);
		}
		for(int i=1; i<=numOfStates; i++){
			List<List<String>> combStateList = new LinkedList<List<String>>();
//			List<List<List<String>>> combSpeciesList = new LinkedList<List<List<String>>>();
			combStateList = this.getStatesCombByNum(states, i);
			
			// now replace state names (combStateList) as species names (combSpeciesList) 
//			for (List<String> tmpList : combStateList) {
//				List<List<String>> newTmpList = new LinkedList<List<String>>();
//				for (String tmpString : tmpList) {
//					List<String> listOfSpecies = new LinkedList<String>();
//					listOfSpecies = this.getTaxonNamesbyState(stateToTaxaMap, tmpString);
//					newTmpList.add(listOfSpecies);
//				}
//				combSpeciesList.add(newTmpList);
//			}
//			System.out.println(character + ", " + i + ", " + combStateList + ", " + combSpeciesList);
			dict.put(i, combStateList);			
		}
		return dict;	
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
	
	public List<String> getTaxonNamesbyState(Map<String, Set<Taxon>> stateToTaxaMap, String state) {
		List<String> taxonNames = new LinkedList<String>();
		for(Taxon taxon : stateToTaxaMap.get(state)) {
			taxonNames.add(taxon.getName());
		}
		return taxonNames;
	}
	
}

