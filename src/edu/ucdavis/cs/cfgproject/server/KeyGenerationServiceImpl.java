package edu.ucdavis.cs.cfgproject.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ucdavis.cs.cfgproject.client.KeyGenerationService;
import edu.ucdavis.cs.cfgproject.shared.Taxon;
import edu.ucdavis.cs.cfgproject.shared.TaxonManager;

public class KeyGenerationServiceImpl extends RemoteServiceServlet implements KeyGenerationService {
	
	@Override
	public List<Taxon> retrieveTaxa(List<Taxon> taxaPool, List<String> characters) {
		//get the pool of the taxa, if the pool is empty, read the input CSV file
		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/AchlilleaSp2.csv";
//		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/AchlilleaSp3.csv";
//		final String filePath = "/Users/Ryan/Documents/workspace/CfgProject/war/mapQueryNew.csv";
		
		TaxonManager tMgr = new TaxonManager(taxaPool, characters);
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
	public HashMap<String, String> retrieveCharactersAndIg(List<Taxon> taxa, List<String> characters) {
		HashMap<String, String> characterIgPairs = new HashMap<String, String>();
		double ig = 0.0;
		TaxonManager tMgr = new TaxonManager(taxa, characters);

		//get the current pool of characters
		List<String> charactersNames = new LinkedList<String>();
		if (characters.isEmpty()) {
			// first time run
			charactersNames = tMgr.getAllCharactersName();
		} else {
			charactersNames = characters;
		}
		
		
		for (String character : charactersNames) {
			ig = tMgr.calculateNDisc1Ig(character);
			characterIgPairs.put(character, String.valueOf(ig));
		}
		return characterIgPairs;
	}
	
	@Override
	public HashMap<String, List<Taxon>> retreieveStatesAndSpecies(List<Taxon> taxa, List<String> characters, String character) {
		HashMap<String, List<Taxon>> statesAndSpeciesPairs = new HashMap<String, List<Taxon>>();
		TaxonManager tMgr = new TaxonManager(taxa, characters);
		statesAndSpeciesPairs = tMgr.createStatesToSpecies(character);
		return statesAndSpeciesPairs;
	}
}
