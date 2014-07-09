package edu.ucdavis.cs.cfgproject.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.*;
import java.util.*;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ucdavis.cs.cfgproject.client.KeyGenerationService;
import edu.ucdavis.cs.cfgproject.shared.State;
import edu.ucdavis.cs.cfgproject.shared.StatesToSpeciesCreator;
import edu.ucdavis.cs.cfgproject.shared.Taxon;

public class KeyGenerationServiceImpl extends RemoteServiceServlet implements KeyGenerationService {
	
	@Override
	public List<Taxon> retrieveCSV(List<Taxon> taxa) {
		//get the pool of the taxa, if the pool is empty, read the input CSV file
//		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/AchlilleaSp2.csv";
//		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/AchlilleaSp3.csv";
		final String filePath = "mapQueryNew.csv";
		
		TaxonManager tMgr = new TaxonManager(taxa);
		
		// read the input CSV file
		List<Taxon> allTaxa = null;
		try {
			allTaxa = tMgr.readCSVFile(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allTaxa;
		
	}
	
	@Override
	public List<Taxon> retrieveTaxa(List<Taxon> taxaPool) {
		//get the pool of the taxa, if the pool is empty, read the input CSV file
		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/AchlilleaSp2.csv";
//		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/AchlilleaSp3.csv";
//		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/mapQueryNew.csv";
		
		TaxonManager tMgr = new TaxonManager(taxaPool);
		if (taxaPool.isEmpty()) {
			// read the input CSV file
			List<Taxon> allTaxa = null;
			try {
				allTaxa = tMgr.readCSVFile(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return allTaxa;
		} else {
			// return the current taxonPool with the state			
			return taxaPool;
				
		}
	}

	@Override
	public HashMap<String, String> retrieveCharactersAndIg(List<Taxon> taxa) {
		
		if (taxa.isEmpty()) {
			return null;
		}
		
		HashMap<String, String> characterIgPairs = new HashMap<String, String>();
		double ig = 0.0;
		TaxonManager tMgr = new TaxonManager(taxa);

		//get the current pool of characters
		List<String> charactersNames = new LinkedList<String>();
//		if (characters.isEmpty()) {
//			// first time run
//			charactersNames = tMgr.getAllCharactersName();
//		} else {
//			charactersNames = characters;
//		}
		charactersNames = tMgr.getAllCharactersName();
		
		
		for (String character : charactersNames) {
			ig = tMgr.calculateNDisc1Ig(character);
			characterIgPairs.put(character, String.valueOf(ig));
		}
		return characterIgPairs;
	}
	
	@Override
	public HashMap<String, List<Taxon>> retrieveStatesAndSpecies(List<Taxon> taxa, String character) {
		HashMap<String, List<Taxon>> statesAndSpeciesPairs = new HashMap<String, List<Taxon>>();
		StatesToSpeciesCreator statesToSpeciesCreator = new StatesToSpeciesCreator(taxa);
		statesAndSpeciesPairs = statesToSpeciesCreator.createStatesToSpecies(character);
		return statesAndSpeciesPairs;
	}
	
	@Override
	public List<Taxon> retrieveTaxaByCheckBoxes(List<Taxon> allTaxa, HashMap<String, HashMap<String, CheckBox>> characterStateCheckBoxMap) {
		
		StatesToSpeciesCreator statesToSpeciesCreator = new StatesToSpeciesCreator(allTaxa);
		
		List<Taxon> deletedTaxa = new LinkedList<Taxon>();
		List<Taxon> remainingTaxa = new LinkedList<Taxon>();
		
		
		// iterate through the fancy data structure
		for (Map.Entry<String, HashMap<String, CheckBox>> entry : characterStateCheckBoxMap.entrySet()) {
			String character = entry.getKey();							//key
			HashMap<String, CheckBox> stateCbMap = entry.getValue();	//value
			
			// for each character, create HashMap statesToSpecies
			HashMap<String, List<Taxon>> statesToSpecies = new HashMap<String, List<Taxon>>();
			statesToSpecies = statesToSpeciesCreator.createStatesToSpecies(character);
			List<Taxon> taxonList = new LinkedList<Taxon>();
			
			// By this character go through every state
			for (Map.Entry<String, CheckBox> innerEntry : stateCbMap.entrySet()) {
				String state = innerEntry.getKey();						//inner key
				CheckBox cb = innerEntry.getValue();					//inner value
				
				//if cb is checked, find all species that are in this state
				if (cb.getValue()) {
					List<Taxon> tmpTaxa = statesToSpecies.get(state);
					for (Taxon taxon : tmpTaxa) {
						taxonList.add(taxon);
					}
				}
				
			}
			HashSet<Taxon> tmpHs = new HashSet<Taxon>();
			tmpHs.addAll(taxonList);
			taxonList.clear();
			taxonList.addAll(tmpHs);
			
			// if the taxon is not in taxonList, add it to deletedTaxa
			for (Taxon taxon : allTaxa) {
				if (taxonList.contains(taxon) == false) {
					deletedTaxa.add(taxon);
				}
			}
			
			
		}
		
		HashSet<Taxon> tmpHs = new HashSet<Taxon>();
		tmpHs.addAll(deletedTaxa);
		deletedTaxa.clear();
		deletedTaxa.addAll(tmpHs);
		
		// update remainingTaxa
		for (Taxon taxon : allTaxa) {
			if (deletedTaxa.contains(taxon) == false) {
				remainingTaxa.add(taxon);
			}
		}
		
		return remainingTaxa;
		

	}
}
